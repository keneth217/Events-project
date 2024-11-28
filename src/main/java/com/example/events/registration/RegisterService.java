package com.example.events.registration;

import com.example.events.category.EventCategory;
import com.example.events.dto.ResponseDto;
import com.example.events.emails.EmailDetails;
import com.example.events.emails.EmailService;
import com.example.events.event.EventRepository;
import com.example.events.event.EventResponse;
import com.example.events.event.MyEvent;
import com.example.events.event.MyEventType;
import com.example.events.exceptions.AlreadyRegisteredToEventException;
import com.example.events.exceptions.EventAlreadyScannedException;
import com.example.events.exceptions.EventNotFoundException;

import com.example.events.exceptions.EventSoldOutException;
import com.example.events.user.MyRegisteredEventsResponse;
import com.example.events.user.User;
import com.example.events.user.UserRepository;
import com.example.events.user.UserResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegisterMapper registermapper;
    private final RegistrationRepository registrationRepository;
    private final EmailService emailService;

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // Utility method to get the logged-in user's details (shopId, shopCode, username) from token
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Assuming your `User` implements `UserDetails`
    }

    ///get  details of the logged-in user
    public User getLoggedInUserDetails() {
        return getLoggedInUser(); // Directly return the logged-in user
    }

    public RegisterResponse registerForEvent(RegisterRequest request) {
        // Get the logged-in user
        User loggedInUser = getLoggedInUser();

        // Fetch user by ID
        User user = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new EventNotFoundException("User not found with the given ID: " + request.getUserId()));

        // Fetch event by ID
        MyEvent event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found with the given ID: " + request.getEventId()));

        // Check if the user is already registered for the event
        boolean alreadyRegistered = registrationRepository.existsByUserAndEvent(loggedInUser, event);
        if (alreadyRegistered) {
            throw new AlreadyRegisteredToEventException("You have already registered for this event.");
        }

        // Check the event type and registration limits
        if (event.getEventType() == MyEventType.SOLD_OUT_EVENT) {
            // If it's a sold-out event, check the number of registered attendees
            int totalRegisteredAttendees = registrationRepository.countByEvent(event);

            // Assuming the event has a field for max attendees or capacity (soldOut limit)
            if (totalRegisteredAttendees >= event.getSoldOUt()) {
                throw new EventSoldOutException("The event is sold out. No more registrations are allowed.");
            }
        }
        BigDecimal cost = event.getEventCost().multiply(BigDecimal.valueOf(request.getTicketQuantity()));

        // Create a new EventRegistration (without the QR code yet)
        EventRegistration eventRegistration = EventRegistration.builder()
                .regDate(request.getRegDate())
                .regTime(request.getRegTime())
                .user(loggedInUser)
                .event(event)
                .ticketQuantity(request.getTicketQuantity())
                .paidAmount(BigDecimal.valueOf(0.0))
                .eventCost(cost)

                .transactionId(UUID.randomUUID().toString()) // Generate a unique transaction ID
                .ticketQuantity(request.getTicketQuantity())
                .scanned(false) // Initial value, will be marked true upon scan
                .status(RegStatus.PENDING)
                .build();

        // Save the event registration to generate a registration ID
        EventRegistration registeredEvent = registrationRepository.save(eventRegistration);

        // Now that the registration is saved, generate QR code content with numbering and line breaks
        String qrCodeContent = "1. Registration ID: " + registeredEvent.getRegistrationId() + "\n" +
                "2. User: " + loggedInUser.getFirstName()+"," +" Tickets: " + request.getTicketQuantity()+ "\n" +
                "3. Paid: " + request.getPaidAmount()+ "\n" +
                "4. Event: " + event.getEventName();


        // Generate QR code as a Base64 string
        String qrCodeBase64 = registermapper.generateQRCodeAsBase64(qrCodeContent, 300, 300);

        // Send email with the embedded QR code (with registration ID)
        registermapper.sendEmailAlertWithAttachmentQRCode(loggedInUser.getEmail(), event, qrCodeBase64);

        // Update the EventRegistration with the QR code (Base64 string)
        registeredEvent.setUniqueCode(qrCodeBase64);
        registrationRepository.save(registeredEvent); // Save with updated QR code

        // Convert to response and return
        return registermapper.toRegister(registeredEvent);
    }


    public AttendeesResponse getAllAttendeesForEvent(UUID eventId) {
        // Fetch event by ID
        MyEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with the given ID: " + eventId));

        // Fetch all registrations for the specific event
        List<EventRegistration> registrations = registrationRepository.findByEvent(event);

        // Map each registration to UserResponse using a separate method
        List<UserResponse> attendees = registrations.stream()
                .map(registermapper::mapToUserResponse)  // Using a separate mapping method
                .collect(Collectors.toList());

        // Return the AttendeesResponse
        return AttendeesResponse.builder()
                .attendees(attendees)
                .build();
    }

    // Get registered events for a single user
    public MyRegisteredEventsResponse getMyEvents(User user) {
        // Get the logged-in user (assuming you have a method to fetch the logged-in user)
        User loggedInUser = getLoggedInUser();

        // Fetch event registrations for the logged-in user
        List<EventRegistration> eventRegistrations = registrationRepository.findByUser(loggedInUser);

        // If no registrations are found, throw an exception
        if (eventRegistrations.isEmpty()) {
            throw new EventNotFoundException("No registered events for you: " + loggedInUser.getLastName());
        }

        //  convert event registrations to event responses
        List<EventResponse> eventResponses = registermapper.toEventResponses(eventRegistrations);

        // Return the response
        return registermapper.toMyRegisteredEventsResponse(eventRegistrations);
    }


    public ResponseDto processQRCodeScan(UUID registrationId) {
        // Find the registration by ID
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new EventNotFoundException("No registration found for the provided QR code."));
        String name = registration.getUser().getFirstName();
        // Check if already scanned
        if (registration.isScanned()) {
            throw new EventAlreadyScannedException("Hey !! " + name + ", Your QR code has already been scanned.");
        }

        // Mark as scanned
        registration.setScanned(true);
        registration.setStatus(RegStatus.PUBLISHED);
        registrationRepository.save(registration);

        // Return a structured response using ResponseDto
        String message = "QR code scanned successfully. Registration ID " + registrationId + " marked as scanned.";
        return new ResponseDto("200", message);  // "200" is a placeholder for success status code
    }

}

package com.example.events.registration;

import com.example.events.category.EventCategory;
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

        // Generate QR code content
        String qrCodeContent = "User: " + loggedInUser.getFirstName() + ", " +
                "Event: " + event.getEventName();

        // Generate QR code as a Base64 string
        String qrCodeBase64 = registermapper.generateQRCodeAsBase64(qrCodeContent, 300, 300);

        // Send email with embedded QR code
        registermapper.sendEmailAlertWithAttachmentQRCode(loggedInUser.getEmail(), event, qrCodeBase64);

        // Create a new EventRegistration
        EventRegistration eventRegistration = EventRegistration.builder()
                .regDate(request.getRegDate())
                .regTime(request.getRegTime())
                .user(loggedInUser)
                .event(event)
                .transactionId(UUID.randomUUID().toString()) // Generate a unique transaction ID
                .ticketQuantity(request.getTicketQuantity())
                .scanned(false) // Initial value, will be marked true upon scan
                .uniqueCode(qrCodeBase64)
                .status(RegStatus.PENDING)
                .build();

        // Save the event registration
        EventRegistration registeredEvent = registrationRepository.save(eventRegistration);

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







    public void scanQRCode(String qrCodeContent) {
        // Fetch the event registration based on the QR code content (uniqueCode)
        EventRegistration registration = registrationRepository.findByUniqueCode(qrCodeContent)
                .orElseThrow(() -> new EventNotFoundException("No registration found for the provided QR code."));

        // If the QR code is already scanned, return or throw an exception if necessary
        if (registration.isScanned()) {
            throw new EventAlreadyScannedException("This QR code has already been scanned.");
        }

        // Update the scanned status to true
        registration.setScanned(true);

        // Save the updated registration status
        registrationRepository.save(registration);

        // Optionally, you can add a success message or any other relevant logic
        System.out.println("QR code scanned successfully, registration updated.");
    }


}

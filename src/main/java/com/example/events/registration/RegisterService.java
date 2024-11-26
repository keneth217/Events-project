package com.example.events.registration;

import com.example.events.category.EventCategory;
import com.example.events.event.EventRepository;
import com.example.events.event.MyEvent;
import com.example.events.exceptions.AlreadyRegisteredToEventException;
import com.example.events.exceptions.EventNotFoundException;

import com.example.events.user.User;
import com.example.events.user.UserRepository;
import com.example.events.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public RegisterResponse registerForEvent(RegisterRequest request) {
        // Fetch user by ID
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EventNotFoundException("User not found with the given ID: " + request.getUserId()));

        // Fetch event by ID
        MyEvent event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found with the given ID: " + request.getEventId()));

        // Check if the user is already registered for the event
        boolean alreadyRegistered = registrationRepository.existsByUserAndEvent(user, event);
        if (alreadyRegistered) {
            throw new AlreadyRegisteredToEventException("You have already registered for this event.");
        }

        // Create a new EventRegistration
        EventRegistration eventRegistration = EventRegistration.builder()
                .regDate(request.getRegDate())
                .regTime(request.getRegTime())
                .user(user)
                .event(event)
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


}

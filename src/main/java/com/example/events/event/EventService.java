package com.example.events.event;

import com.example.events.category.CategoryRepository;
import com.example.events.category.EventCategory;
import com.example.events.exceptions.EventNotFoundException;
import com.example.events.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper mapper;
    private final CategoryRepository categoryRepository;

    // Utility method to get the logged-in user's details (shopId, shopCode, username) from token
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Assuming your `User` implements `UserDetails`
    }

    ///get  details of the logged-in user
    public User getLoggedInUserDetails() {
        return getLoggedInUser(); // Directly return the logged-in user
    }
    public EventResponse createEvent(EventRequest eventRequest) {

        User loggedInUser = getLoggedInUser();
        // Retrieve the category by ID
        EventCategory category = categoryRepository.findById(eventRequest.getCategoryId())
                .orElseThrow(() -> new EventNotFoundException("Invalid category ID: " + eventRequest.getCategoryId()));

        // Validate event name
        if (eventRequest.getEventName() == null || eventRequest.getEventName().isEmpty()) {
            throw new EventNotFoundException("Event name is required");
        }

        byte[] eventImage = null;  // For storing the image bytes
        if (eventRequest.getEventImage() != null && !eventRequest.getEventImage().isEmpty()) {
            // Process the uploaded image if it's not empty
            try {
                eventImage = eventRequest.getEventImage().getBytes();  // Convert the image file to byte array
            } catch (IOException e) {
                throw new EventNotFoundException("Error processing uploaded image file");
            }
        }

        // Build the event object with the retrieved category
        MyEvent event = MyEvent.builder()
                .eventName(eventRequest.getEventName())
                .description(eventRequest.getDescription())
                .endTime(eventRequest.getEndTime())
                .startDate(eventRequest.getStartDate())
                .startTime(eventRequest.getStartTime())
                .category(category) // Assign the retrieved category object
                .endDate(eventRequest.getEndDate())
                .eventImage(eventImage)  // Store the image as a byte array
                .soldOUt(eventRequest.getSoldOUt())
                .creatorName(loggedInUser.getFirstName())
                .eventType(MyEventType.FREE_EVENT)
                .eventCost(eventRequest.getEventCost())
                .status(EventStatus.ONGOING)
                .location(eventRequest.getLocation())
                .build();

        // Save the event and map to response
        MyEvent savedEvent = eventRepository.save(event);
        return mapper.toEvent(savedEvent);
    }




    public List<EventResponse> getAllEvents() {
        return eventRepository
                .findAll()
                .stream()
                .map(mapper::toEvent)
                .collect(Collectors.toList());
    }


    public EventResponse getEventById(UUID eventId) {
        MyEvent event= eventRepository.findById(eventId).
                orElseThrow(()-> new EventNotFoundException("Event Not found with the given id"+ eventId));


        return mapper.toEvent(event);
    }


    public String deleteEvent(UUID eventId) {
        MyEvent event= eventRepository.findById(eventId).
                orElseThrow(()-> new EventNotFoundException("Event Not found with the given id"+ eventId));
        eventRepository.delete(event);
        return "Event deleted Successfully";
    }

    public EventResponse updateEvent(UUID eventId,EventRequest eventRequest) {
        // Find the event to update
        MyEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventRequest.getEventId()));

        // Update the event fields
        event.setEventName(eventRequest.getEventName());
        event.setDescription(eventRequest.getDescription());
        event.setLocation(eventRequest.getLocation());
        event.setStartDate(eventRequest.getStartDate());
        event.setEndDate(eventRequest.getEndDate());
        event.setStartTime(eventRequest.getStartTime());
        event.setEndTime(eventRequest.getEndTime());
        event.setLocation(eventRequest.getLocation());


        // If category is present in the request, update it as well
        if (eventRequest.getCategoryId() != null) {
            EventCategory category = categoryRepository.findById(eventRequest.getCategoryId())
                    .orElseThrow(() -> new EventNotFoundException("Invalid category ID: " + eventRequest.getCategoryId()));
            event.setCategory(category);
        }

        // Save the updated event
        MyEvent updatedEvent = eventRepository.save(event);

        // Return the response
        return mapper.toEvent(updatedEvent);
    }


//    public List<EventResponse> filterEventByDateLocationCategory(LocalDate startDate, String location, String category) {
//        List<MyEvent> events = eventRepository.findAllByStartDateAndLocationAndCategory(startDate, location, category);
//        return events.stream()
//                .map(mapper::toEvent)
//                .collect(Collectors.toList());
//    }

    public List<EventResponse> filterEventByDateBetween(LocalDate startDate, LocalDate endDate) {
        List<MyEvent> events = eventRepository.findByStartDateBetween(startDate, endDate);
        return events.stream()
                .map(mapper::toEvent)
                .collect(Collectors.toList());
    }

    public List<EventResponse> upcomingEvents() {
        LocalDate currentDate = LocalDate.now();
        List<MyEvent> events = eventRepository.findByStartDateAfterOrderByStartDateAsc(currentDate);
        return events.stream()
                .limit(5) // Get only the next 5 upcoming events
                .map(mapper::toEvent)
                .collect(Collectors.toList());
    }
}

package com.example.events.event;

import com.example.events.category.CategoryRepository;
import com.example.events.category.EventCategory;
import com.example.events.exceptions.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper mapper;
    private final CategoryRepository categoryRepository;
    public EventResponse createEvent(EventRequest eventRequest) {
        // Retrieve the category by ID
        EventCategory category = categoryRepository.findById(eventRequest.getCategoryId())
                .orElseThrow(() -> new EventNotFoundException("Invalid category ID: " + eventRequest.getCategoryId()));

        // Build the event object with the retrieved category
        MyEvent event = MyEvent.builder()
                .eventName(eventRequest.getEventName())
                .description(eventRequest.getDescription())
                .endTime(eventRequest.getEndTime())
                .startDate(eventRequest.getStartDate())
                .startTime(eventRequest.getStartTime())
                .category(category) // Assign the retrieved category object
                .endDate(eventRequest.getEndDate())
                .soldOUt(eventRequest.getSoldOUt())
                .eventType(MyEventType.FREE_EVENT)
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
}

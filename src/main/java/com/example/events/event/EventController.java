package com.example.events.event;

import com.example.events.configs.RateLimitingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService service;
    private final RateLimitingService rateLimitingService;
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return  ResponseEntity.ok("hello spring user");
    }
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@ModelAttribute  EventRequest  eventRequest){
        return ResponseEntity.ok(service.createEvent(eventRequest));
    }
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(  HttpServletRequest request) {

        // Check if the request is within the rate limit
        rateLimitingService.checkRateLimit(request);
        List <EventResponse> allEvents = service.getAllEvents();
        if (allEvents == null || allEvents.isEmpty()) {
            // Return 204 No Content for an empty list
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allEvents);
    }
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable UUID eventId) {
        var event = service.getEventById(eventId);
        if (event == null) {
            // Return 404 Not Found for a missing event
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }
    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable UUID eventId) {
        service.deleteEvent(eventId);

        return  ResponseEntity.ok("Event Deleted successfully" );
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable UUID eventId, @RequestBody EventRequest  eventRequest){
        return ResponseEntity.ok(service.updateEvent(eventId,eventRequest));
    }



//    @GetMapping
//    public ResponseEntity<List<EventResponse>> filterEvents(@PathVariable LocalDate startDate, String Location, String CategoryName , HttpServletRequest request) {
//
//        // Check if the request is within the rate limit
//        rateLimitingService.checkRateLimit(request);
//        List <EventResponse> allEvents = service.filterEventByDateLocationCategory(startDate,Location,CategoryName);
//        if (allEvents == null || allEvents.isEmpty()) {
//            // Return 204 No Content for an empty list
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.ok(allEvents);
//    }

    @GetMapping("/{startDate}/{endDate}")
    public ResponseEntity<List<EventResponse>> filterEventsByDate(
            @PathVariable LocalDate startDate,
            @PathVariable LocalDate endDate,
            HttpServletRequest request
    ) {
        // Check if the request is within the rate limit
        rateLimitingService.checkRateLimit(request);

        // Fetch events within the date range
        List<EventResponse> allEvents = service.filterEventByDateBetween(startDate, endDate);


        // If no events found, return 204 No Content
        if (allEvents == null || allEvents.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Return the list of events with 200 OK
        return ResponseEntity.ok(allEvents);
    }





}

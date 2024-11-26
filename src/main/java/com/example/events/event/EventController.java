package com.example.events.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService service;
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return  ResponseEntity.ok("hello spring user");
    }
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest  eventRequest){
        return ResponseEntity.ok(service.createEvent(eventRequest));
    }
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
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



}

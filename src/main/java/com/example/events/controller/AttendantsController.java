package com.example.events.controller;

import com.example.events.configs.RateLimitingService;
import com.example.events.event.EventResponse;
import com.example.events.event.EventService;
import com.example.events.registration.RegisterRequest;
import com.example.events.registration.RegisterResponse;
import com.example.events.registration.RegisterService;
import com.example.events.registration.TicketResponse;
import com.example.events.user.MyRegisteredEventsResponse;
import com.example.events.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/attendant")
@RequiredArgsConstructor
public class AttendantsController {

    private final RegisterService registerService;


    private final EventService eventService;
    private final RateLimitingService rateLimitingService;
    @GetMapping("test")
    public ResponseEntity<String> user(){
        return  ResponseEntity.ok("welcome attendant");
    }



    @PostMapping
    public ResponseEntity<TicketResponse> registerEvent(@RequestBody RegisterRequest request) throws Exception {
        return ResponseEntity.ok(registerService.registerForEvent(request));
    }
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(HttpServletRequest request) {

        // Check if the request is within the rate limit
        rateLimitingService.checkRateLimit(request);
        List <EventResponse> allEvents = eventService.getAllEvents();
        if (allEvents == null || allEvents.isEmpty()) {
            // Return 204 No Content for an empty list
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allEvents);
    }
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable UUID eventId) {
        var event = eventService.getEventById(eventId);
        if (event == null) {
            // Return 404 Not Found for a missing event
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    @GetMapping("/events")
    public ResponseEntity <MyRegisteredEventsResponse> myEventsRegistered(User user) {
        MyRegisteredEventsResponse attendants = registerService.getMyEvents(user);
        if (attendants == null ) {
            // Return 204 No Content for an empty list
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(attendants);
    }
}

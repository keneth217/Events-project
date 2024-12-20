package com.example.events.controller;

import com.example.events.configs.RateLimitingService;
import com.example.events.event.EventResponse;
import com.example.events.event.EventService;
import com.example.events.registration.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/all-events")
@RequiredArgsConstructor
public class AdminController {

    private final EventService eventService;
    private final RateLimitingService rateLimitingService;
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

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        List<EventResponse> upcomingEvents = eventService.upcomingEvents();
        return ResponseEntity.ok(upcomingEvents);
    }
}

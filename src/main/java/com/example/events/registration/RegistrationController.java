package com.example.events.registration;

import com.example.events.event.EventResponse;
import com.example.events.exceptions.EventAlreadyScannedException;
import com.example.events.exceptions.EventNotFoundException;
import com.example.events.user.MyRegisteredEventsResponse;
import com.example.events.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/register")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<RegisterResponse> registerEvent(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(registerService.registerForEvent(request));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity <AttendeesResponse> eventAttendees(@PathVariable UUID eventId) {
       AttendeesResponse attendants = registerService.getAllAttendeesForEvent(eventId);
        if (attendants == null ) {
            // Return 204 No Content for an empty list
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(attendants);
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

    @PostMapping("/scan")
    public ResponseEntity<String> scanQRCode(@RequestParam String qrCodeContent) {
        try {
            // Call service method to handle scanning and update the status
            registerService.scanQRCode(qrCodeContent);

            // Return success response
            return ResponseEntity.ok("QR code scanned successfully, registration updated.");
        } catch (EventNotFoundException e) {
            // Handle case where no registration is found for the QR code
            return ResponseEntity.status(404).body("Registration not found for the provided QR code.");
        } catch (EventAlreadyScannedException e) {
            // Handle case where the QR code was already scanned
            return ResponseEntity.status(400).body("This QR code has already been scanned.");
        } catch (Exception e) {
            // Generic error handler for any other exceptions
            return ResponseEntity.status(500).body("An error occurred while scanning the QR code.");
        }
    }
}

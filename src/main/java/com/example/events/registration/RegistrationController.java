package com.example.events.registration;

import com.example.events.dto.ResponseDto;
import com.example.events.event.EventResponse;
import com.example.events.exceptions.EventAlreadyScannedException;
import com.example.events.exceptions.EventNotFoundException;
import com.example.events.user.MyRegisteredEventsResponse;
import com.example.events.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ResponseDto> scanQRCode(@RequestParam UUID registrationId) {
        ResponseDto response = registerService.processQRCodeScan(registrationId);
        return new ResponseEntity<>(response, HttpStatus.OK);  // Return response with HTTP 200
    }
}

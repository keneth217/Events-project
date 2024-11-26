package com.example.events.registration;

import com.example.events.dto.UserDto;
import com.example.events.event.EventResponse;
import com.example.events.user.User;
import com.example.events.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RegisterResponse {
    private UUID id;
    private LocalDate regDate;
    private LocalTime regTime;
    private UserResponse users;
    private EventResponse events;
    private RegStatus status;
}

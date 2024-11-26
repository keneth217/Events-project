package com.example.events.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private LocalDate regDate;
    private LocalTime regTime;
}

package com.example.events.registration;

import com.example.events.event.MyEvent;
import com.example.events.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class RegisterRequest {
    private LocalDate regDate;
    private LocalTime regTime;
    private UUID userId;
    private UUID eventId;
    private boolean scanned;
    private String transactionId;
    private String ticketQuantity;
}

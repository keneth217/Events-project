package com.example.events.registration;

import com.example.events.event.MyEvent;
import com.example.events.payments.Payment;
import com.example.events.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
    private Long ticketQuantity;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal eventCost;
    private BigDecimal remainingAmount;

    // One Ticket can have many Payments
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<Payment> payments; // List of payments for this ticket
}

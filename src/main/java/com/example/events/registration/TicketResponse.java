package com.example.events.registration;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
@Data
@Builder
public class TicketResponse {


    private UUID ticketId;
    private LocalDate regDate;
    private LocalTime regTime;
    private UUID userId;
    private UUID eventId;
    private String eventName;
    private String userName;
    private boolean scanned;
    private String transactionId;
    private Long ticketQuantity;
    private BigDecimal paidAmount;
    private BigDecimal eventCost;
    private BigDecimal remainingAmount;
}

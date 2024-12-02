package com.example.events.payments;

import com.example.events.registration.EventRegistration;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String reference;
    private BigDecimal amount; // Amount in kobo
    private String status; // pending, success, failed
    private String createdAt;
    private String paidAt;
    private String channel;
    private String currency;
    private String ipAddress;
    private String metadata;
    private Long fees;
    private String feesBreakdown;
    private String log;

    @ManyToOne(fetch = FetchType.LAZY)  // LAZY to avoid unnecessary loading
    @JoinColumn(name = "ticket_id", nullable = false)  // Define join column for ticket_id
    private EventRegistration ticket;  // Foreign key to EventRegistration

    // Utility methods to parse dates
    public LocalDateTime getCreatedAtAsLocalDateTime() {
        return LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);
    }

    public LocalDateTime getPaidAtAsLocalDateTime() {
        return LocalDateTime.parse(paidAt, DateTimeFormatter.ISO_DATE_TIME);
    }
}

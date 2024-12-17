package com.example.events.registration;

import com.example.events.event.MyEvent;
import com.example.events.payments.Payment;
import com.example.events.payments.PaymentStatus;
import com.example.events.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID registrationId;

    private LocalDate regDate;
    private LocalTime regTime;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean scanned;
    private LocalDate scannedDate;
    private LocalTime scannedTime;
    private String transactionId;
    private Long ticketQuantity;
    private Long ticketCount;
    private BigDecimal paidAmount;
    private BigDecimal eventCost;
    private BigDecimal remainingAmount;

    @Column(name = "unique_code", length = 1000)  // Unique code for this registration
    private String uniqueCode;

    @Enumerated(EnumType.STRING)
    private RegStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)  // LAZY to avoid loading unnecessary data
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)  // LAZY fetch type for optimization
    @JoinColumn(name = "event_id", nullable = false)
    private MyEvent event;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;  // Payments associated with this registration
}

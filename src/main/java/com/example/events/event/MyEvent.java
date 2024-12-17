package com.example.events.event;

import com.example.events.category.EventCategory;
import com.example.events.registration.EventRegistration;
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
@Table(name="events")
public class MyEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String eventName;

    private String location;
    private String description;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int totalAttendants;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int soldOUt;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String creatorName;
    private BigDecimal eventCost;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int attendants;
    private BigDecimal amountToPay;
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    @Enumerated(EnumType.STRING)
    private MyEventType eventType;




    @Lob
    //  @Column(columnDefinition = "BYTEA")
    @Basic(fetch = FetchType.LAZY)
    private byte[] eventImage;  // Storing images as byte arrays



    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private EventCategory category;


    @OneToMany(mappedBy = "event")
    private List<EventRegistration> registrations;


}

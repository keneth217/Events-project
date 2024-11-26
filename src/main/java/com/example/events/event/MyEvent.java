package com.example.events.event;

import com.example.events.category.EventCategory;
import com.example.events.registration.EventRegistration;
import jakarta.persistence.*;
import lombok.*;


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

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private EventCategory category;


    @OneToMany(mappedBy = "event")
    private List<EventRegistration> registrations;


}

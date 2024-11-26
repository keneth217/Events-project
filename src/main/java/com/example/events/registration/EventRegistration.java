package com.example.events.registration;



import com.example.events.event.MyEvent;
import com.example.events.user.User;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalTime;
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
    @Enumerated(EnumType.STRING)
    private RegStatus status;
    @ManyToOne
   // @MapsId("userId")  // Maps to the "userId" of the composite key
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    //@MapsId("eventId")  // Maps to the "eventId" of the composite key
    @JoinColumn(name = "events_id")
    private MyEvent event;
}

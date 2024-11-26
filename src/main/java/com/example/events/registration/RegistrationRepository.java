package com.example.events.registration;

import com.example.events.event.MyEvent;
import com.example.events.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RegistrationRepository extends JpaRepository<EventRegistration, UUID> {
    boolean existsByUserAndEvent(User user, MyEvent event);

    List<EventRegistration> findByEvent(MyEvent event);
}

package com.example.events.registration;

import com.example.events.event.MyEvent;
import com.example.events.payments.Payment;
import com.example.events.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationRepository extends JpaRepository<EventRegistration, UUID> {
    EventRegistration findByTransactionId(String transactionId);

    boolean existsByUserAndEvent(User user, MyEvent event);

    List<EventRegistration> findByEvent(MyEvent event);

    int countByEvent(MyEvent event);

    List<EventRegistration> findByUser(User user);

    Optional<EventRegistration> findByUniqueCode(String qrCodeContent);

    Optional<EventRegistration> findByUserId(User user);
}

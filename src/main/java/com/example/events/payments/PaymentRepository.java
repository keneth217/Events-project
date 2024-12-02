package com.example.events.payments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Payment findByReference(String reference);


    @Query("SELECT t.status FROM Payment t WHERE t.reference = :reference")
    String findStatusByReference(@Param("reference") String reference);

    // Repository method for fetching successful payments for a given transaction ID
    List<Payment> findByTicket_RegistrationIdAndStatus(UUID registrationId, String status);

    List<Payment> findByReferenceAndStatus(String reference, String success);
}

package com.example.events.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<MyEvent, UUID> {

    List<MyEvent> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    List<MyEvent> findByStartDateBetweenOrderByStartDateDesc(LocalDate startDate, LocalDate endDate);
    List<MyEvent> findByStartDateAfterOrderByStartDateAsc(LocalDate currentDate);

}

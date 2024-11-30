package com.example.events.event;

import com.example.events.category.EventCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
@Data
@Builder
public class EventResponse {
    private UUID eventId;
    private String eventName;
    private String location;
    private String description;
    private UUID categoryId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private EventStatus status;

    private String creatorName;
    private BigDecimal eventCost;
    private int soldOUt;
}

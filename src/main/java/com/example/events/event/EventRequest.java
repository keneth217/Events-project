package com.example.events.event;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
@Data
@Builder
public class EventRequest {
    private UUID eventId;
    private String eventName;
    private String Location;
    private String description;
    private UUID categoryId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int soldOUt;
    private MultipartFile eventImage;

    private String creatorName;
    private BigDecimal eventCost;
}

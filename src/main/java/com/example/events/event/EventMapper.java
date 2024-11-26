package com.example.events.event;

import com.example.events.category.EventCategory;
import org.springframework.stereotype.Service;

@Service
public class EventMapper {
    public EventResponse toEvent(MyEvent event) {
        return EventResponse.builder()
                .eventId(event.getId())
                .eventName(event.getEventName())
                .description(event.getDescription())
                .category(event.getCategory() != null ? EventCategory.builder()
                        .id(event.getCategory().getId())
                        .categoryName(event.getCategory().getCategoryName())
                        .categoryDescription(event.getCategory().getCategoryDescription())
                        .build() : null)
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .Location(event.getLocation())
                .status(event.getStatus())
                .build();
    }




}

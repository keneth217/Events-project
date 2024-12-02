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
                .categoryId(event.getCategory().getId())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .creatorName(event.getCreatorName())
                .eventCost(event.getEventCost())
                .eventImage(event.getEventImage())
                .location(event.getLocation())
                .status(event.getStatus())
                .build();
    }




}

package com.example.events.category;

import com.example.events.event.EventResponse;
import com.example.events.event.MyEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryMapper {
    public CategoryResponse toCategory(EventCategory eventCategory) {
        CategoryResponse categoryResponse=CategoryResponse.builder()
                .id(eventCategory.getId())
                .categoryDescription(eventCategory.getCategoryDescription())
                .categoryName(eventCategory.getCategoryName())
                .build();
        return categoryResponse;
    }

    public CategoryEventResponse toCategoryEvent(List<MyEvent> myEvents) {

        // Map each MyEvent to EventResponse
        List<EventResponse> eventResponses = myEvents.stream()
                .map(myEvent -> EventResponse.builder()
                        .eventId(myEvent.getId())
                        .eventName(myEvent.getEventName())
                        .startDate(myEvent.getStartDate())
                        .endDate(myEvent.getEndDate())
                        .categoryId(myEvent.getCategory().getId())
                        .location(myEvent.getLocation())
                        .status(myEvent.getStatus())
                        .startTime(myEvent.getStartTime())
                        .endTime(myEvent.getEndTime())
                        .description(myEvent.getDescription())
                        .build()
                )
                .toList();

        // Build and return a CategoryEventResponse
        return CategoryEventResponse.builder()
                .events(eventResponses)
                .build();
    }


}

package com.example.events.category;

import com.example.events.event.EventResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class CategoryEventResponse {
    private List<EventResponse> events;
}

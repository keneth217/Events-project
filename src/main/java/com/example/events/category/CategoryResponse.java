package com.example.events.category;

import com.example.events.event.EventResponse;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Data
@Builder
public class CategoryResponse {


    private UUID id;
    private String categoryName;
    private String categoryDescription;



    private List<EventResponse> events;
}

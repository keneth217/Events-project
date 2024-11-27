package com.example.events.user;

import com.example.events.event.EventResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MyRegisteredEventsResponse {

    List<EventResponse> myEvents;
}

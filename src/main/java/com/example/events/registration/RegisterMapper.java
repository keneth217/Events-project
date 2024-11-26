package com.example.events.registration;

import com.example.events.event.EventResponse;
import com.example.events.user.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class RegisterMapper {
    public RegisterResponse toRegister(EventRegistration registerEvent) {
        if (registerEvent == null) {
            throw new IllegalArgumentException("EventRegistration cannot be null");
        }

        return RegisterResponse.builder()
                .id(registerEvent.getRegistrationId())
                .users(UserResponse.builder()
                        .userId(registerEvent.getUser().getId())
                        .firstName(registerEvent.getUser().getFirstName())
                        .lastName(registerEvent.getUser().getLastName())
                        .email(registerEvent.getUser().getEmail())
                        .build())
                .events(EventResponse.builder()
                        .eventId(registerEvent.getEvent().getId())
                        .eventName(registerEvent.getEvent().getEventName())
                        .description(registerEvent.getEvent().getDescription())
                        .Location(registerEvent.getEvent().getLocation())
                        .startDate(registerEvent.getEvent().getStartDate())
                        .endDate(registerEvent.getEvent().getEndDate())
                        .startTime(registerEvent.getEvent().getStartTime())
                        .endTime(registerEvent.getEvent().getEndTime())
                        .build())
                .regDate(registerEvent.getRegDate())
                .regTime(registerEvent.getRegTime())
                .status(registerEvent.getStatus())
                .build();
    }
    // Mapping method
    public UserResponse mapToUserResponse(EventRegistration registerEvent) {
        return UserResponse.builder()
                .userId(registerEvent.getUser().getId())
                .firstName(registerEvent.getUser().getFirstName())
                .lastName(registerEvent.getUser().getLastName())
                .email(registerEvent.getUser().getEmail())
                .build();
    }


}

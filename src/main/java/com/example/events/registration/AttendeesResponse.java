package com.example.events.registration;

import com.example.events.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class AttendeesResponse {

    private List<UserResponse> attendees;
}

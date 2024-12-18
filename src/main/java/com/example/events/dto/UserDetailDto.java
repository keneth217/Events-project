package com.example.events.dto;

import com.example.events.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDetailDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private Role role;
}

package com.example.events.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JWTAuthenticationResponse {
    private String token;
    private String refreshToken;
    private UserDetailDto user;


}

package com.example.events.dto;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

}

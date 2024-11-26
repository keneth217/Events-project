package com.example.events.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {

    @NotNull(message = "Name should not be null")
    @NotBlank(message = "Name should not be null")
    private String firstName;
    @NotNull(message = "Name should not be null")
    @NotBlank(message = "Name should not be null")
    private String lastName;
    @NotBlank(message = "Name should not be null")
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "Password should not be blank")
    private String password;
    @NotBlank(message = "Name should not be null")
    @NotNull(message = "Include phone Number")
    private String phone;

}

package com.example.events.service.Auth;



import com.example.events.dto.AuthenticationRequestDto;
import com.example.events.dto.JWTAuthenticationResponse;
import com.example.events.dto.SignUpRequestDto;
import com.example.events.user.User;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {
    User createUser(SignUpRequestDto signUpRequest);
//    Boolean hasCustomerWithPhone(String phone);
   JWTAuthenticationResponse createAuthToken(@RequestBody AuthenticationRequestDto authenticationRequest);
}

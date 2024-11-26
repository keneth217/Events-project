package com.example.events.controller;




import com.example.events.configs.JwtAuthenticationFilter;
import com.example.events.constants.AuthConstants;
import com.example.events.dto.AuthenticationRequestDto;
import com.example.events.dto.JWTAuthenticationResponse;
import com.example.events.dto.ResponseDto;
import com.example.events.dto.SignUpRequestDto;
import com.example.events.service.Auth.AuthService;
import com.example.events.service.Jwt.UserService;
import com.example.events.user.User;
import com.example.events.user.UserRepository;
import com.example.events.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthService authService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/sign")
    public ResponseEntity<ResponseDto> createUser(@Valid @RequestBody SignUpRequestDto signUpRequest){
        authService.createUser(signUpRequest);
        String userName= signUpRequest.getFirstName()+" "+signUpRequest.getLastName();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(AuthConstants.ACCOUNT_CREATION_CODE,userName+" "+ AuthConstants.ACCOUNT_CREATION));
//        return  new ResponseEntity<>("HELLO"+" "+userName+" "+"ACCOUNT CREATION SUCCESS,WELCOME TO OUR E-COMMERCE SERVICE",HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<JWTAuthenticationResponse> login(@RequestBody AuthenticationRequestDto authenticationRequest){
        return ResponseEntity.ok(authService.createAuthToken(authenticationRequest));
    }
}

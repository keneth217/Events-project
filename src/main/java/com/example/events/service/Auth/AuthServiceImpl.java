package com.example.events.service.Auth;


import com.example.events.dto.AuthenticationRequestDto;
import com.example.events.dto.JWTAuthenticationResponse;
import com.example.events.dto.SignUpRequestDto;
import com.example.events.user.User;
import com.example.events.enums.Role;
import com.example.events.exceptions.CustomerAlreadyExistException;
import com.example.events.service.Jwt.UserService;
import com.example.events.user.UserRepository;
import com.example.events.util.JwtUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements  AuthService{
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private  final UserService userService;
    private final JwtUtils jwtUtils;
    @PostConstruct
    public void createAdmin(){
        User adminAccount=userRepository.findByRole(Role.ADMIN);
        if (adminAccount==null){
            User newAdmin= new User();
            newAdmin.setFirstName("keneth ");
            newAdmin.setLastName(" admin");
            newAdmin.setPhone("0711766223");
            newAdmin.setEmail("admin@test.com");
            newAdmin.setPassword(new BCryptPasswordEncoder().encode("admin"));
            newAdmin.setRole(Role.ADMIN);
            userRepository.save(newAdmin);
            System.out.println(" add user with role admin when starting the application for the first time");
        }
    }

    @Override
    public User createUser(SignUpRequestDto signUpRequest) {
        Optional<User> optionalUser=userRepository.findByEmail(signUpRequest.getEmail());
        if (optionalUser.isPresent()){
            throw new CustomerAlreadyExistException("Customer already registered with the given phone number"+signUpRequest.getEmail());
        }
        System.out.println("adding user------------");
        User user=new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
//        user.setPhone("+254"+signUpRequest.getPhone());
        user.setPhone(signUpRequest.getPhone());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        user.setRole(Role.ATTENDEE);
        User createdUser=userRepository.save(user);
        System.out.println("user added"+createdUser);
        System.out.println("user added"+user);
       return createdUser;
    }



    public JWTAuthenticationResponse createAuthToken(@RequestBody AuthenticationRequestDto authenticationRequest)

    {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(),
                    authenticationRequest.getPassword()));
        } catch (RuntimeException e) {
            throw new RuntimeException("incorrect login credentials");
        }
        final UserDetails userDetails=userService.userDetailsService()
                .loadUserByUsername(authenticationRequest.getEmail());
        Optional<User> optionalUser=userRepository.findByEmail(authenticationRequest.getEmail());
        final String jwt= jwtUtils.generateToken(userDetails.getUsername());

        JWTAuthenticationResponse authenticationResponse= new JWTAuthenticationResponse();
        if (optionalUser.isPresent()){

            authenticationResponse.setToken(jwt);
        }
        return authenticationResponse;
    }



}

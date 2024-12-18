package com.example.events.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AuthenticationExceptions extends RuntimeException {
    public AuthenticationExceptions(String message) {
        super(message);
    }
}

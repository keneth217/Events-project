package com.example.events.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EventAlreadyScannedException extends RuntimeException{
    public EventAlreadyScannedException(String message) {
        super(message);
    }
}

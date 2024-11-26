package com.example.events.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AlreadyRegisteredToEventException extends RuntimeException {
    public AlreadyRegisteredToEventException(String message) {
        super(message);
    }
}

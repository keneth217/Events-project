package com.example.events.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EventNotFoundException extends RuntimeException{
    public EventNotFoundException(String message){
        super(message);
    }
}

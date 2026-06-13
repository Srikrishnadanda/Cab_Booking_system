package com.cabbooking.authservice.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AuthenticationAPIException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public AuthenticationAPIException(HttpStatus status,String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}

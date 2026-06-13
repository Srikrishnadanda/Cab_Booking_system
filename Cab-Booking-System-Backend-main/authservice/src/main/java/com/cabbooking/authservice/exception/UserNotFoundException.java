package com.cabbooking.authservice.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
        this.status = HttpStatus.NOT_FOUND;
        this.message = "User not found with email: " + email;
    }
}
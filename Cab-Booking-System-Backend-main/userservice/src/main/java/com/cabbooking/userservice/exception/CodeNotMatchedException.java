package com.cabbooking.userservice.exception;

public class CodeNotMatchedException extends RuntimeException {
    public CodeNotMatchedException(String message) {
        super(message);
    }

}

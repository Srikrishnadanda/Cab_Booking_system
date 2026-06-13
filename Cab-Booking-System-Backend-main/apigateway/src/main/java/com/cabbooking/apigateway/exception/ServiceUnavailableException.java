package com.cabbooking.apigateway.exception;

import lombok.Getter;

@Getter
public class ServiceUnavailableException extends RuntimeException {
    private final String message;
     public ServiceUnavailableException(String serviceName) {
         super(serviceName + " service is currently unavailable. Please try again later.");
         this.message = serviceName + " service is currently unavailable. Please try again later.";
    }
}

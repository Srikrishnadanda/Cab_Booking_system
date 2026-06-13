package com.cabbooking.apigateway.service;

public interface AuthValidationService {
    boolean validateToken(String token);
}

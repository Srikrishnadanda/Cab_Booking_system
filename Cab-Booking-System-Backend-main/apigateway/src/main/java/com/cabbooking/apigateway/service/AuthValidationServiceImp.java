package com.cabbooking.apigateway.service;

import com.cabbooking.apigateway.client.AuthServiceClient;
import com.cabbooking.apigateway.exception.AuthenticationException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthValidationServiceImp implements AuthValidationService{

    private final AuthServiceClient authServiceClient;

    @Override
    @CircuitBreaker(name = "authService", fallbackMethod = "validateTokenFallback")
    @Retry(name = "authService")
    public boolean validateToken(String token) {
        String bearerToken = "Bearer " + token;
        Boolean isValid = authServiceClient.validateToken(bearerToken);
        if (Boolean.TRUE.equals(isValid)) {
            return true;
        }
        throw new AuthenticationException("Invalid token");
    }

    private boolean validateTokenFallback(Throwable ex) {
        log.error("Auth service unavailable during token validation. cause={} message={}", ex.getClass().getSimpleName(), ex.getMessage());
        return false;
    }
}


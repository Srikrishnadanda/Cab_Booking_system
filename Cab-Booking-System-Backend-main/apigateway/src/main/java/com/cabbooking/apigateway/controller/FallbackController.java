package com.cabbooking.apigateway.controller;

import com.cabbooking.apigateway.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    // Each fallback now simply throws an exception so the GlobalExceptionHandler can format the response

    @RequestMapping("/user-service")
    public void userServiceFallback() {
        log.error("[CIRCUIT BREAKER] USER-SERVICE unavailable - circuit open");
        throw new ServiceUnavailableException("USER-SERVICE");
    }

    @RequestMapping("/driver-service")
    public void driverServiceFallback() {
        log.error("[CIRCUIT BREAKER] DRIVER-SERVICE unavailable - circuit open");
        throw new ServiceUnavailableException("DRIVER-SERVICE");
    }

    @RequestMapping("/ride-service")
    public void rideServiceFallback() {
        log.error("[CIRCUIT BREAKER] RIDE-SERVICE unavailable - circuit open");
        throw new ServiceUnavailableException("RIDE-SERVICE");
    }

    @RequestMapping("/payment-service")
    public void paymentServiceFallback() {
        log.error("[CIRCUIT BREAKER] PAYMENT-SERVICE unavailable - circuit open");
        throw new ServiceUnavailableException("PAYMENT-SERVICE");
    }

    @RequestMapping("/rating-service")
    public void ratingServiceFallback() {
        log.error("[CIRCUIT BREAKER] RATING-SERVICE unavailable - circuit open");
        throw new ServiceUnavailableException("RATING-SERVICE");
    }

    @RequestMapping("/location-service")
    public void locationServiceFallback() {
        log.error("[CIRCUIT BREAKER] LOCATION-SERVICE unavailable - circuit open");
        throw new ServiceUnavailableException("LOCATION-SERVICE");
    }

    @RequestMapping("/auth-service")
    public void authServiceFallback() {
        log.error("[CIRCUIT BREAKER] AUTH-SERVICE unavailable - circuit open");
        throw new ServiceUnavailableException("AUTH-SERVICE");
    }
}

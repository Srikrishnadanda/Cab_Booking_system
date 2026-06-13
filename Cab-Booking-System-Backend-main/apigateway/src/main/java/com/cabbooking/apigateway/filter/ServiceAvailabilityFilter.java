package com.cabbooking.apigateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceAvailabilityFilter implements Filter {

    private final DiscoveryClient discoveryClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    // Map path prefix to Eureka service name
    private static final Map<String, String> PREFIX_TO_SERVICE = Map.of(
            "/api/auth/", "AUTH-SERVICE",
            "/api/users/", "USER-SERVICE",
            "/api/drivers/", "DRIVER-SERVICE",
            "/api/rides/", "RIDE-SERVICE",
            "/api/payments/", "PAYMENT-SERVICE",
            "/api/ratings/", "RATING-SERVICE",
            "/api/locations/", "LOCATION-SERVICE"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest httpReq)) {
            chain.doFilter(request, response);
            return;
        }

        String uri = httpReq.getRequestURI();

        if (uri.startsWith("/fallback")) { // avoid recursion
            chain.doFilter(request, response);
            return;
        }

        String serviceName = resolveServiceName(uri);
        if (serviceName == null) {
            chain.doFilter(request, response);
            return;
        }

       String availabilityCbName = serviceName + "-availability";
        CircuitBreaker availabilityCB = circuitBreakerRegistry.circuitBreaker(availabilityCbName);

       if (!availabilityCB.tryAcquirePermission()) {
            log.warn("[AVAILABILITY-CB:OPEN] Fast-fail {} -> fallback", serviceName);
            forwardFallback(request, response, serviceName);
            return;
        }

        boolean available = !discoveryClient.getInstances(serviceName).isEmpty();
        if (!available) {
            log.warn("[SERVICE-DOWN] {} no Eureka instances. Recorded failure in availability CB.", serviceName);
            forwardFallback(request, response, serviceName);
            return;
        }

        chain.doFilter(request, response);
    }

    private void forwardFallback(ServletRequest request, ServletResponse response, String serviceName) throws ServletException, IOException {
        String fallbackPath = "/fallback/" + serviceName.toLowerCase(Locale.ROOT);
        request.getRequestDispatcher(fallbackPath).forward(request, response);
    }

    private String resolveServiceName(String uri) {
        String normalized = uri.endsWith("/") ? uri : uri + "/"; // allow both
        for (Map.Entry<String, String> entry : PREFIX_TO_SERVICE.entrySet()) {
            if (normalized.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}

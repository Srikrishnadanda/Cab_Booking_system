package com.cabbooking.apigateway.filter;

import com.cabbooking.apigateway.exception.AuthenticationException;
import com.cabbooking.apigateway.service.AuthValidationService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthValidationFilter implements Filter {

    private final AuthValidationService authValidationService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    private static final List<String> PUBLIC_PATTERNS = List.of(
            "/api/auth/**",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/fallback/**"
    );

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String requestPath = httpRequest.getRequestURI();
            String method = httpRequest.getMethod();

            if (log.isDebugEnabled()) {
                log.debug("Incoming request method={} path={}", method, requestPath);
            }

            if ("OPTIONS".equalsIgnoreCase(method) || isPublicUrl(requestPath)) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping auth for public/OPTIONS path={}", requestPath);
                }
                chain.doFilter(request, response);
                return;
            }

            String authHeader = httpRequest.getHeader("Authorization");
            if (!StringUtils.hasText(authHeader)) {
                log.warn("Missing Authorization header for protected path={}", requestPath);
                throw new AuthenticationException("Missing Authorization header");
            }

            authHeader = authHeader.trim();
            if (!authHeader.startsWith("Bearer ")) {
                log.warn("Invalid Authorization format for path={} headerPreview={}...", requestPath,
                        authHeader.substring(0, Math.min(15, authHeader.length())));
                throw new AuthenticationException("Invalid Authorization header format. Expected: Bearer <token>");
            }

            String token = authHeader.substring(7).trim();
            if (token.isEmpty()) {
                log.warn("Empty token for path={}", requestPath);
                throw new AuthenticationException("Empty token in Authorization header");
            }

            boolean valid = authValidationService.validateToken(token);
            if (valid) {
                log.info("Token valid path={}", requestPath);
                chain.doFilter(request, response);
            } else {
                throw new AuthenticationException("Invalid token");
            }

        } catch (Exception ex) {
            handlerExceptionResolver.resolveException(httpRequest, httpResponse, null, ex);
        }
    }


    private boolean isPublicUrl(String requestPath) {
        for (String pattern : PUBLIC_PATTERNS) {
            if (PATH_MATCHER.match(pattern, requestPath)) {
                log.info("Public match pattern={} path={}", pattern, requestPath);
                return true;
            }
        }
        return false;
    }
}
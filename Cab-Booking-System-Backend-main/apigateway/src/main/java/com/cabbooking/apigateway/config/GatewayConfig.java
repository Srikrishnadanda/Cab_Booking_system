package com.cabbooking.apigateway.config;

import com.cabbooking.apigateway.filter.AuthValidationFilter;
import com.cabbooking.apigateway.filter.ServiceAvailabilityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final AuthValidationFilter authValidationFilter;
    private final ServiceAvailabilityFilter serviceAvailabilityFilter;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));

        config.setAllowCredentials(true);
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type","Accept","Origin","X-Requested-With","Access-Control-Request-Method","Access-Control-Request-Headers"));
        config.setExposedHeaders(List.of("Authorization","Content-Type"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<AuthValidationFilter> authFilterRegistration() {
        FilterRegistrationBean<AuthValidationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authValidationFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<ServiceAvailabilityFilter> serviceAvailabilityFilterRegistration() {
        FilterRegistrationBean<ServiceAvailabilityFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(serviceAvailabilityFilter);
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        return registrationBean;
    }
}

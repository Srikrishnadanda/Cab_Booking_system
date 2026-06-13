package com.cabbooking.authservice.controller;

import com.cabbooking.authservice.dto.*;
import com.cabbooking.authservice.service.AuthenticationService;
import com.cabbooking.authservice.security.JwtAuthenticationEntryPoint;
import com.cabbooking.authservice.security.JwtAuthenticationFilter;
import com.cabbooking.authservice.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AuthenticationController.class, properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import=",
        "eureka.client.enabled=false"
})
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("POST /api/auth/login returns JWT response")
    void login_success() throws Exception {
        JwtResponse jwtResponse = new JwtResponse("token123", "USER", "Login successful", "user-1");
        Mockito.when(authenticationService.login(any(LoginDto.class))).thenReturn(jwtResponse);

        LoginDto loginDto = new LoginDto("test@example.com", "pass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("token123"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.id").value("user-1"));
    }

    @Test
    @DisplayName("POST /api/auth/register-user returns created user response")
    void registerUser_success() throws Exception {
        UserDto userDto = new UserDto("user-1", "John Doe", "john@example.com", "1234567890", "MALE", 1);
        UserServiceResponse response = new UserServiceResponse(userDto, "SUCCESS", "User created");
        Mockito.when(authenticationService.registerUser(any(UserRequest.class))).thenReturn(response);

        UserRequest req = new UserRequest("John Doe", "john@example.com", "1234567890", "pwd", "MALE", 1, "USER");

        mockMvc.perform(post("/api/auth/register-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User created"))
                .andExpect(jsonPath("$.body.fullName").value("John Doe"))
                .andExpect(jsonPath("$.body.email").value("john@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/register-driver returns created driver response")
    void registerDriver_success() throws Exception {
        DriverDto driverDto = new DriverDto();
        driverDto.setDriverId("driver-1");
        driverDto.setFullName("Jane Driver");
        driverDto.setEmail("jane@example.com");
        driverDto.setPhone("9876543210");
        driverDto.setLicenceNumber("LIC123");
        driverDto.setGender("FEMALE");
        driverDto.setVehicleNumber("AB12CD3456");
        driverDto.setVehicleName("Toyota");
        driverDto.setCarSeater("4");
        driverDto.setRating(4.8);
        driverDto.setAvailable(true);

        DriverServiceResponse response = new DriverServiceResponse(driverDto, "SUCCESS", "Driver created");
        Mockito.when(authenticationService.registerDriver(any(DriverRequest.class))).thenReturn(response);

        DriverRequest req = new DriverRequest();
        req.setFullName("Jane Driver");
        req.setEmail("jane@example.com");
        req.setPhone("9876543210");
        req.setPassword("pwd");
        req.setLicenceNumber("LIC123");
        req.setGender("FEMALE");
        req.setVehicleNumber("AB12CD3456");
        req.setVehicleName("Toyota");
        req.setCarSeater(4);
        req.setRole("DRIVER");

        mockMvc.perform(post("/api/auth/register-driver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Driver created"))
                .andExpect(jsonPath("$.body.fullName").value("Jane Driver"))
                .andExpect(jsonPath("$.body.email").value("jane@example.com"))
                .andExpect(jsonPath("$.body.licenceNumber").value("LIC123"));
    }

    @Test
    @DisplayName("PUT /api/auth/forgot-password returns reset response")
    void forgotPassword_success() throws Exception {
        PasswordResetResponse passwordResetResponse = new PasswordResetResponse("SUCCESS", "Password reset", LocalDateTime.now());
        Mockito.when(authenticationService.resetPassword(any(ForgotPassword.class))).thenReturn(passwordResetResponse);

        ForgotPassword req = new ForgotPassword("john@example.com", "newPwd");

        mockMvc.perform(put("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Password reset"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("DELETE /api/auth/delete/{email} returns success message")
    void deleteUser_success() throws Exception {
        Mockito.when(authenticationService.deleteUserByEmail("delete@example.com")).thenReturn("Deleted successfully");

        mockMvc.perform(delete("/api/auth/delete/{email}", "delete@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted successfully"));
    }

    @Test
    @DisplayName("GET /api/auth/validate returns true when token valid")
    void validateToken_success() throws Exception {
        Mockito.when(authenticationService.validateToken("Bearer token123")).thenReturn(true);

        mockMvc.perform(get("/api/auth/validate").header("Authorization", "Bearer token123"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}

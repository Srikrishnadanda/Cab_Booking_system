package com.cabbooking.userservice.controller;

import com.cabbooking.userservice.dto.ForgotPassword;
import com.cabbooking.userservice.dto.UserDto;
import com.cabbooking.userservice.dto.UserRequest;
import com.cabbooking.userservice.dto.UserServiceResponse;
import com.cabbooking.userservice.exception.SuccessResponse;
import com.cabbooking.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String EMAIL = "saikumar@gmail.com";
    private static final String FULL_NAME = "Sai Kumar";
    private static final String PHONE = "9876543210";
    private static final String GENDER = "Male";

    @Test
    @DisplayName("Register user successfully")
    void registerUser_success() throws Exception {
        UserRequest request = new UserRequest();
        request.setFullName(FULL_NAME);
        request.setEmail(EMAIL);
        request.setPhone(PHONE);
        request.setGender(GENDER);

        UserDto userDto = new UserDto();
        userDto.setEmail(EMAIL);
        userDto.setFullName(FULL_NAME);

        UserServiceResponse response = new UserServiceResponse(userDto, "success","User Registered Successfully");

        Mockito.when(userService.registerUser(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("User Registered Successfully"))
                .andExpect(jsonPath("$.body.email").value(EMAIL));
    }

    @Test
    @DisplayName("Get user by ID successfully")
    void getUserById_success() throws Exception {
        UserDto dto = new UserDto();
        dto.setEmail(EMAIL);
        dto.setFullName(FULL_NAME);

        Mockito.when(userService.getUserById(USER_ID)).thenReturn(dto);

        mockMvc.perform(get("/api/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.fullName").value(FULL_NAME));
    }

    @Test
    @DisplayName("Get user by email successfully")
    void getUserByEmail_success() throws Exception {
        UserDto dto = new UserDto();
        dto.setEmail(EMAIL);
        dto.setFullName(FULL_NAME);

        Mockito.when(userService.getUserByEmail(EMAIL)).thenReturn(dto);

        mockMvc.perform(get("/api/users/email/{email}", EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.fullName").value(FULL_NAME));
    }

    @Test
    @DisplayName("Update user successfully")
    void updateUser_success() throws Exception {
        UserRequest request = new UserRequest();
        request.setFullName(FULL_NAME);
        request.setEmail(EMAIL);
        request.setPhone(PHONE);
        request.setGender(GENDER);

        UserDto userDto = new UserDto();
        userDto.setEmail(EMAIL);
        userDto.setFullName(FULL_NAME);

        UserServiceResponse response = new UserServiceResponse(userDto,"success", "User Updated Successfully");

        Mockito.when(userService.updateUser(eq(USER_ID), any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/update/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("User Updated Successfully"))
                .andExpect(jsonPath("$.body.email").value(EMAIL));
    }

    @Test
    @DisplayName("Delete user successfully")
    void deleteUser_success() throws Exception {
        Mockito.when(userService.deleteUser(EMAIL)).thenReturn("User Deleted Successfully");

        mockMvc.perform(delete("/api/users/delete/{email}", EMAIL))
                .andExpect(status().isOk())
                .andExpect(content().string("User Deleted Successfully"));
    }

    @Test
    @DisplayName("Verify OTP successfully")
    void verifyOtp_success() throws Exception {
        SuccessResponse response = new SuccessResponse("success", "Code verified successfully.");

        Mockito.when(userService.verifyOtp(USER_ID, "5678")).thenReturn(response);

        mockMvc.perform(get("/api/users/verify-otp/{userId}/{code}", USER_ID, "5678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Code verified successfully."));
    }

    @Test
    @DisplayName("Forgot password successfully")
    void forgotPassword_success() throws Exception {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail(EMAIL);
        forgotPassword.setNewPassword("newSecurePassword");

        SuccessResponse response = new SuccessResponse("success", "Password reset");

        Mockito.when(userService.forgotPassword(any(ForgotPassword.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Password reset"));
    }


    @Test
    @DisplayName("Get user by ID not found should return 404")
    void getUserById_notFound_shouldReturn404() throws Exception {
        Mockito.when(userService.getUserById(USER_ID))
                .thenThrow(new com.cabbooking.userservice.exception.UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/{id}", USER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get user by email not found should return 404")
    void getUserByEmail_notFound_shouldReturn404() throws Exception {
        Mockito.when(userService.getUserByEmail(EMAIL))
                .thenThrow(new com.cabbooking.userservice.exception.UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/email/{email}", EMAIL))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete user not found should return 404")
    void deleteUser_notFound_shouldReturn404() throws Exception {
        Mockito.when(userService.deleteUser(EMAIL))
                .thenThrow(new com.cabbooking.userservice.exception.UserNotFoundException("User not found"));

        mockMvc.perform(delete("/api/users/delete/{email}", EMAIL))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Verify OTP invalid code should return 400")
    void verifyOtp_invalidCode_shouldReturn400() throws Exception {
        Mockito.when(userService.verifyOtp(USER_ID, "9999"))
                .thenThrow(new com.cabbooking.userservice.exception.CodeNotMatchedException("Invalid OTP"));

        mockMvc.perform(get("/api/users/verify-otp/{userId}/{code}", USER_ID, "9999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register user email exists should return 409")
    void registerUser_emailExists_shouldReturn409() throws Exception {
        UserRequest request = new UserRequest();
        request.setEmail(EMAIL);
        request.setPhone(PHONE);

        Mockito.when(userService.registerUser(any(UserRequest.class)))
                .thenThrow(new com.cabbooking.userservice.exception.EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Forgot password user not found should return 404")
    void forgotPassword_userNotFound_shouldReturn404() throws Exception {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("unknown@example.com");
        forgotPassword.setNewPassword("newPassword");

        Mockito.when(userService.forgotPassword(any(ForgotPassword.class)))
                .thenThrow(new com.cabbooking.userservice.exception.UserNotFoundException("User not found"));

        mockMvc.perform(put("/api/users/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotPassword)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Register user phone exists should return 409")
    void registerUser_phoneExists_shouldReturn409() throws Exception {
        UserRequest request = new UserRequest();
        request.setEmail("surya@example.com");
        request.setPhone(PHONE);

        Mockito.when(userService.registerUser(any(UserRequest.class)))
                .thenThrow(new com.cabbooking.userservice.exception.PhoneAlreadyExistsException("Phone already exists"));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Generic exception should return 500")
    void genericException_shouldReturn500() throws Exception {
        Mockito.when(userService.getUserById(USER_ID))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/users/{id}", USER_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }


}

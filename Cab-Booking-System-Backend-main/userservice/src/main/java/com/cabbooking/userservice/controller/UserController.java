package com.cabbooking.userservice.controller;

import com.cabbooking.userservice.dto.ForgotPassword;
import com.cabbooking.userservice.dto.UserDto;
import com.cabbooking.userservice.dto.UserRequest;
import com.cabbooking.userservice.dto.UserServiceResponse;
import com.cabbooking.userservice.exception.SuccessResponse;
import com.cabbooking.userservice.exception.UserNotFoundException;
import com.cabbooking.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User APIs", description = "Operations for user registration and retrieval")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<UserServiceResponse> registerUser(@RequestBody UserRequest userRequest) {
        log.info("User request {}", userRequest);
        UserServiceResponse savedUser = userService.registerUser(userRequest);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") String id) throws UserNotFoundException {
        UserDto userById = userService.getUserById(id);
        return new ResponseEntity<>(userById, HttpStatus.OK);
    }

    @Operation(summary = "Get user by Email")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email) throws UserNotFoundException {
        UserDto userByEmail = userService.getUserByEmail(email);
        return new ResponseEntity<>(userByEmail, HttpStatus.OK);
    }


    @Operation(summary = "Update user by ID")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserServiceResponse> updateUser(
            @PathVariable("id") String id,
            @RequestBody UserRequest userRequest) throws UserNotFoundException {
        log.info("Update request for user {}: {}", id, userRequest);
        UserServiceResponse updatedUser = userService.updateUser(id, userRequest);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @Operation(summary = "Delete user by ID")
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable("email") String email) throws UserNotFoundException{
        String response=userService.deleteUser(email);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/verify-otp/{userId}/{code}")
    @Operation(summary = "Verify OTP for user")
    public ResponseEntity<SuccessResponse> verifyOtp(
            @PathVariable("userId") String userId,
            @PathVariable("code") String code) throws UserNotFoundException{
        return ResponseEntity.ok(userService.verifyOtp(userId, code));
    }

    @PutMapping("/forgot-password")
    @Operation(summary = "Reset password for user")
    public ResponseEntity<SuccessResponse> forgotPassword(@RequestBody ForgotPassword forgotPassword) throws UserNotFoundException {
         return ResponseEntity.ok(userService.forgotPassword(forgotPassword));
    }



}

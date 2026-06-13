package com.cabbooking.authservice.controller;

import com.cabbooking.authservice.dto.*;
import com.cabbooking.authservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginDto loginDto){
        JwtResponse jwtResponse = authenticationService.login(loginDto);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register-user")
    public ResponseEntity<UserServiceResponse> register(@RequestBody UserRequest userRequest){

        UserServiceResponse userServiceResponse = authenticationService.registerUser(userRequest);

        return new ResponseEntity<>(userServiceResponse, HttpStatus.CREATED);
    }

    @PostMapping("/register-driver")
    public ResponseEntity<DriverServiceResponse> registerDriver(@RequestBody DriverRequest driverRequest){
        DriverServiceResponse driverServiceResponse = authenticationService.registerDriver(driverRequest);
        return new ResponseEntity<>(driverServiceResponse, HttpStatus.CREATED);
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<PasswordResetResponse> forgotPassword(@RequestBody ForgotPassword forgotPassword){

        return ResponseEntity.ok(authenticationService.resetPassword(forgotPassword));
    }

    @DeleteMapping("/delete/{email}")
    @Operation(summary = "Delete user by ID")
    public ResponseEntity<String> deleteUserByEmail(@PathVariable("email") String email){
        String s = authenticationService.deleteUserByEmail(email);
        return ResponseEntity.ok(s);
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestHeader("Authorization") String token) {
        return authenticationService.validateToken(token);
    }


}

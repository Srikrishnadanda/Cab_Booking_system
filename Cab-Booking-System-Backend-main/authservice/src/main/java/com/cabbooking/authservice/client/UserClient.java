package com.cabbooking.authservice.client;

import com.cabbooking.authservice.dto.*;
import com.cabbooking.authservice.dto.UserDto;
import com.cabbooking.authservice.dto.UserRequest;
import com.cabbooking.authservice.dto.UserServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "USER-SERVICE"
)
public interface UserClient {

    @PostMapping("/api/users/register")
    public ResponseEntity<UserServiceResponse> registerUser(@RequestBody UserRequest userRequest);

    @GetMapping("/api/users/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email);

    @DeleteMapping("/api/users/delete/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable("email") String email);

    @PutMapping("/api/users/forgot-password")
    public ResponseEntity<SuccessResponse> forgotPassword(@RequestBody ForgotPassword forgotPassword);


}

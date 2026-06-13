package com.cabbooking.authservice.client;

import com.cabbooking.authservice.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "DRIVER-SERVICE"
)
public interface DriverClient {

    @PostMapping("/api/drivers/register")
    public ResponseEntity<DriverServiceResponse> registerDriver(@RequestBody DriverRequest driver);

    @GetMapping("/api/drivers/email/{email}")
    public ResponseEntity<DriverDto> getDriverByEmail(@PathVariable("email") String email);

    @PutMapping("/api/drivers/forgot-password")
    public ResponseEntity<SuccessResponse> forgotPassword(@RequestBody ForgotPassword forgotPassword);

    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteDriver(@PathVariable("email") String email);
}

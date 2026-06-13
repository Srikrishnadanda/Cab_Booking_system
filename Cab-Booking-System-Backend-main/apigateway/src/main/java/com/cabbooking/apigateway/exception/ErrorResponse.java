package com.cabbooking.apigateway.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String status;
    private String message;
    private LocalDateTime timestamp;

    public static ErrorResponse fail(String message){
        return new ErrorResponse("error", message, LocalDateTime.now());
    }
}


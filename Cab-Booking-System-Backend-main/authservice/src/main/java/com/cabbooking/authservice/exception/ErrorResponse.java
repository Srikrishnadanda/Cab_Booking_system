package com.cabbooking.authservice.exception;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ErrorResponse {
    private String status = "error";
    private String message;
    private LocalDateTime timestamp;
}

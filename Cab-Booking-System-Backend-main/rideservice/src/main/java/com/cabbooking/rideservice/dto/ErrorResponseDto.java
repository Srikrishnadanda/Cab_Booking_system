package com.cabbooking.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private Date timestamp;
    private String message;
    private String status;

}

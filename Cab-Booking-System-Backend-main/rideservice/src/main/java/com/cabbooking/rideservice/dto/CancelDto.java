package com.cabbooking.rideservice.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelDto {

    private String status;
    private String reason;
    private String cancelledBy;
}

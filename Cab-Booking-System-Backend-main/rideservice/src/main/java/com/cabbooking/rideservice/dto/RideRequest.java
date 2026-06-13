package com.cabbooking.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideRequest {

    private String userId;

    private String driverId;

    private String carSeater;

    private String pickupLocation;

    private String dropLocation;

    private String bookingDate;

    private String bookingTime;

    private Boolean immediateBooking;

    private BigDecimal fare;

    private BigDecimal distance;

    private String status;
}

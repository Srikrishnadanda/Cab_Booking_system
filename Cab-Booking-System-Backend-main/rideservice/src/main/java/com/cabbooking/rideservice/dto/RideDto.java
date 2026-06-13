package com.cabbooking.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideDto {
    private String rideId;

    private String userId;

    private String driverId;

    private String carSeater;

    private String pickupLocation;

    private String dropLocation;

    private String bookingDate;

    private String bookingTime;

    private LocalDateTime requestedAt;

    private LocalDateTime assignedAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private boolean immediateBooking;

    private BigDecimal fare;

    private BigDecimal distance;

    private String status;

}

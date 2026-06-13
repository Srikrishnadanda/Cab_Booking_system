package com.cabbooking.rideservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Ride {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private String rideId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String driverId;

    @Column(length = 255, nullable = false)
    private String pickupLocation;

    @Column(length = 255, nullable = false)
    private String dropLocation;

    @Column(nullable = false)
    private String bookingDate;

    @Column(nullable = false)
    private String bookingTime;

    @Column(nullable = false)
    private boolean immediateBooking;

    @Column(nullable = false)
    private String carSeater;

    @Column(nullable = false)
    private BigDecimal fare;

    @Column(nullable = false)
    private BigDecimal distance;


    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime assignedAt;

    private LocalDateTime arrivedAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private LocalDateTime cancelledAt;
    private String cancelReason;


    @Column(length = 10)
    private String cancelledBy;


    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

}

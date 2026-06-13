package com.cabbooking.rideservice.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideNotFoundException extends RuntimeException {

    private final String fieldValue;

    public RideNotFoundException(String fieldValue) {
        super(String.format("Ride not found with ID: '%s'", fieldValue));

        this.fieldValue = fieldValue;
    }
}

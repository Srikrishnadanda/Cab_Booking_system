package com.cabbooking.rideservice.exception;

public class NoCurrentRideRequest extends RuntimeException {
    public NoCurrentRideRequest(String id) {
        super("No current ride request found for driver with ID: " + id);
    }
}

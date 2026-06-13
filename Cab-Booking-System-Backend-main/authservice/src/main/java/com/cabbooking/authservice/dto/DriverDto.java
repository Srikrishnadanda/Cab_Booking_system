package com.cabbooking.authservice.dto;

import lombok.Data;

@Data
public class DriverDto {
    private String driverId;
    private String fullName;
    private String email;
    private String phone;
    private String licenceNumber;
    private String gender;
    private String vehicleNumber;
    private String vehicleName;
    private String carSeater;
    private double rating;
    private boolean isAvailable;
}

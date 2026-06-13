package com.cabbooking.authservice.dto;

import lombok.Data;

@Data
public class DriverRequest {
    
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String licenceNumber;
    private String gender;
    private String vehicleNumber;
    private String vehicleName;
    private int carSeater;
    private String role;
}

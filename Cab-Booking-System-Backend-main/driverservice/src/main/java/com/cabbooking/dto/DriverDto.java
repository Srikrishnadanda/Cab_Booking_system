package com.cabbooking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @JsonProperty("isAvailable")
    private boolean isAvailable;
}

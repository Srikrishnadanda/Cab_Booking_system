package com.cabbooking.rideservice.service;

import com.cabbooking.rideservice.dto.CancelDto;
import com.cabbooking.rideservice.dto.RideDto;
import com.cabbooking.rideservice.dto.SuccessResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public interface RideService {
      RideDto bookARide(RideDto rideDto);
      RideDto getRideById(String rideId);
      ArrayList<RideDto> getDriverRidesByStatus(String driverId,String status);
      ArrayList<RideDto> getUserRidesByStatus(String userId, String status);
      ArrayList<RideDto> getAllUserRides(String userId);
      ArrayList<RideDto> getAllDriverRides(String driverId);
      SuccessResponseDto updateRideStatus(String rideId, String status);
      RideDto getNewestImmediateRideForDriver(String driverId);
      SuccessResponseDto cancelRideStatus(String rideId, CancelDto cancelDto);
}

package com.cabbooking.rideservice.controller;

import com.cabbooking.rideservice.dto.CancelDto;
import com.cabbooking.rideservice.dto.RideDto;
import com.cabbooking.rideservice.dto.SuccessResponseDto;
import com.cabbooking.rideservice.service.RideService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rides")
public class RideController {
    private final RideService rideService;

    @PostMapping("/book")
    public ResponseEntity<RideDto> bookARide(@RequestBody RideDto rideDto){
        return ResponseEntity.ok(rideService.bookARide(rideDto));
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideDto> getRideById(@PathVariable String rideId){
        RideDto rideDto = rideService.getRideById(rideId);
        return new ResponseEntity<>(rideDto, HttpStatus.OK);
    }

    @GetMapping(value = "/driver/{driverId}", params = "status")
    public ResponseEntity<ArrayList<RideDto>> getDriverRidesByStatus(@PathVariable String driverId, @RequestParam String status){
        ArrayList<RideDto> rides = rideService.getDriverRidesByStatus(driverId, status);
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{userId}", params = "status")
    public ResponseEntity<ArrayList<RideDto>> getUserRidesByStatus(@PathVariable String userId, @RequestParam String status){
        ArrayList<RideDto> rides = rideService.getUserRidesByStatus(userId, status);
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ArrayList<RideDto>> getUserRides(@PathVariable String id){
        ArrayList<RideDto> rides = rideService.getAllUserRides(id);
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @GetMapping("/driver/{id}")
    public ResponseEntity<ArrayList<RideDto>> getDriverRides(@PathVariable String id){
        ArrayList<RideDto> rides = rideService.getAllDriverRides(id);
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @PatchMapping("/status/{rideId}")
    public ResponseEntity<SuccessResponseDto> updateRideStatus(
            @PathVariable String rideId,
            @RequestBody Map<String, String> statusUpdate) {

        String status = statusUpdate.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(rideService.updateRideStatus(rideId, status));
    }

    @PatchMapping("/cancel/{rideId}")
    public ResponseEntity<SuccessResponseDto> cancelRideStatus(
            @PathVariable String rideId,
            @RequestBody CancelDto statusUpdate) {

        return ResponseEntity.ok(rideService.cancelRideStatus(rideId, statusUpdate));
    }


    @GetMapping("/driver/pending/{driverId}")
    public ResponseEntity<RideDto> getNewestImmediateRideForDriver(@PathVariable String driverId) {

        RideDto rideDto = rideService.getNewestImmediateRideForDriver(driverId);

        return ResponseEntity.ok(rideDto);
    }
}

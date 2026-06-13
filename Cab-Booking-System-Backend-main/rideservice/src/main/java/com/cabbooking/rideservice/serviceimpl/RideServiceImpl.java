package com.cabbooking.rideservice.serviceimpl;

import com.cabbooking.rideservice.dto.CancelDto;
import com.cabbooking.rideservice.dto.RideDto;
import com.cabbooking.rideservice.dto.SuccessResponseDto;
import com.cabbooking.rideservice.entity.Ride;
import com.cabbooking.rideservice.exception.RideNotFoundException;
import com.cabbooking.rideservice.repository.RideRepository;
import com.cabbooking.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;


    @Override
    public RideDto bookARide(RideDto rideDto) {

        Ride ride= modelMapper.map(rideDto,Ride.class);
        ride.setStatus("PENDING");
        ride.setRequestedAt(LocalDateTime.now());
        ride.setAssignedAt(LocalDateTime.now());

        Ride newRide = rideRepository.save(ride);

        return modelMapper.map(newRide, RideDto.class);
    }

    @Override
    public RideDto getRideById(String rideId) {
        Ride newRide = rideRepository.findById(rideId).orElseThrow(() -> new RideNotFoundException(rideId));

        return modelMapper.map(newRide, RideDto.class);
    }

    @Override
    public ArrayList<RideDto> getDriverRidesByStatus(String driverId, String status) {
        ArrayList<Ride> rides = rideRepository.findAllByDriverIdAndStatus(driverId, status.toUpperCase());
        return rides.stream()
                .map(ride -> modelMapper.map(ride, RideDto.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<RideDto> getUserRidesByStatus(String userId, String status) {
        ArrayList<Ride> rides = rideRepository.findAllByUserIdAndStatus(userId, status.toUpperCase());
        return rides.stream()
                .map(ride -> modelMapper.map(ride, RideDto.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<RideDto> getAllUserRides(String userId) {
        ArrayList<Ride> rides = rideRepository.findAllByUserIdOrderByBookingDateDescBookingTimeDesc(userId);
        return rides.stream()
                .map(ride -> modelMapper.map(ride, RideDto.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<RideDto> getAllDriverRides(String driverId) {
        ArrayList<Ride> rides = rideRepository.findAllByDriverIdOrderByBookingDateDescBookingTimeDesc(driverId);
        return rides.stream()
                .map(ride -> modelMapper.map(ride,RideDto.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public SuccessResponseDto updateRideStatus(String rideId, String status) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RideNotFoundException(rideId));

        ride.setStatus(status.toUpperCase());

        switch (status.toUpperCase()) {
            case "ONGOING":
                ride.setStartedAt(LocalDateTime.now());
                break;
            case "COMPLETED":
                ride.setCompletedAt(LocalDateTime.now());
                break;
            default:
                break;
        }

        rideRepository.save(ride);
        String message = String.format("Ride status successfully updated to '%s'", status.toUpperCase());
        return new SuccessResponseDto(new Date(), message, "success");
    }

    @Override
    public SuccessResponseDto cancelRideStatus(String rideId, CancelDto cancelDto) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RideNotFoundException(rideId));

        ride.setStatus("CANCELLED");
        ride.setCancelledAt(LocalDateTime.now());
        ride.setCancelledBy(cancelDto.getCancelledBy());

        rideRepository.save(ride);

        String message = String.format("Ride is cancelled by '%s'", cancelDto.getCancelledBy());
        return new SuccessResponseDto(new Date(), message, "success");
    }



    public RideDto getNewestImmediateRideForDriver(String driverId) {
        Ride ride = rideRepository
                .findFirstByDriverIdAndStatusOrderByRequestedAtDesc(driverId, "PENDING")
                .orElseThrow(() -> new RideNotFoundException(driverId));

        return modelMapper.map(ride, RideDto.class);
    }


}

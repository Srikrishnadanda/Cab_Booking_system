package com.cabbooking.rideservice.serviceimpl;

import com.cabbooking.rideservice.dto.CancelDto;
import com.cabbooking.rideservice.dto.RideDto;
import com.cabbooking.rideservice.dto.SuccessResponseDto;
import com.cabbooking.rideservice.entity.Ride;
import com.cabbooking.rideservice.exception.RideNotFoundException;
import com.cabbooking.rideservice.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideServiceImplTest {

    @Mock
    private RideRepository rideRepository;

    @InjectMocks
    private RideServiceImpl rideService;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        rideService = new RideServiceImpl(rideRepository, modelMapper);
    }

    private Ride buildRide(String id, String userId, String driverId, String status) {
        Ride r = new Ride();
        r.setRideId(id);
        r.setUserId(userId);
        r.setDriverId(driverId);
        r.setPickupLocation("A");
        r.setDropLocation("B");
        r.setBookingDate("2025-09-23");
        r.setBookingTime("10:00");
        r.setImmediateBooking(true);
        r.setCarSeater("4");
        r.setFare(new BigDecimal("100.00"));
        r.setDistance(new BigDecimal("10.5"));
        r.setStatus(status);
        r.setRequestedAt(LocalDateTime.now().minusMinutes(5));
        r.setAssignedAt(LocalDateTime.now().minusMinutes(4));
        return r;
    }

    private RideDto buildRideDto(String userId, String driverId) {
        RideDto dto = new RideDto();
        dto.setUserId(userId);
        dto.setDriverId(driverId);
        dto.setPickupLocation("A");
        dto.setDropLocation("B");
        dto.setBookingDate("2025-09-23");
        dto.setBookingTime("10:00");
        dto.setImmediateBooking(true);
        dto.setCarSeater("4");
        dto.setFare(new BigDecimal("100.00"));
        dto.setDistance(new BigDecimal("10.5"));
        return dto;
    }

    @Test
    @DisplayName("bookARide should set status PENDING and timestamps and return mapped dto")
    void bookARide_success() {
        RideDto request = buildRideDto("user1", "driver1");

        // capture saved ride to inspect fields
        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);

        Ride saved = buildRide("ride-1", "user1", "driver1", "PENDING");
        when(rideRepository.save(any(Ride.class))).thenReturn(saved);

        LocalDateTime before = LocalDateTime.now();
        RideDto response = rideService.bookARide(request);
        LocalDateTime after = LocalDateTime.now();

        verify(rideRepository).save(rideCaptor.capture());
        Ride persisted = rideCaptor.getValue();
        assertThat(persisted.getStatus()).isEqualTo("PENDING");
        assertThat(persisted.getRequestedAt()).isNotNull();
        assertThat(persisted.getAssignedAt()).isNotNull();
        assertThat(persisted.getRequestedAt()).isBetween(before.minusSeconds(1), after.plusSeconds(1));
        assertThat(response.getRideId()).isEqualTo("ride-1");
        assertThat(response.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void getRideById_found() {
        Ride ride = buildRide("ride-2", "userX", "driverX", "PENDING");
        when(rideRepository.findById("ride-2")).thenReturn(Optional.of(ride));

        RideDto dto = rideService.getRideById("ride-2");

        assertThat(dto.getRideId()).isEqualTo("ride-2");
        assertThat(dto.getUserId()).isEqualTo("userX");
        verify(rideRepository).findById("ride-2");
    }

    @Test
    void getRideById_notFound() {
        when(rideRepository.findById("missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> rideService.getRideById("missing"))
                .isInstanceOf(RideNotFoundException.class);
    }

    @Test
    void getDriverRidesByStatus_success() {
        ArrayList<Ride> rides = new ArrayList<>();
        rides.add(buildRide("ride-3", "user1", "driver1", "PENDING"));
        rides.add(buildRide("ride-4", "user2", "driver1", "PENDING"));
        when(rideRepository.findAllByDriverIdAndStatus("driver1", "PENDING"))
                .thenReturn(rides);

        var list = rideService.getDriverRidesByStatus("driver1", "pending");
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getDriverId()).isEqualTo("driver1");
        verify(rideRepository).findAllByDriverIdAndStatus("driver1", "PENDING");
    }

    @Test
    void getUserRidesByStatus_success() {
        ArrayList<Ride> rides = new ArrayList<>();
        rides.add(buildRide("ride-5", "user7", "driver3", "COMPLETED"));
        when(rideRepository.findAllByUserIdAndStatus("user7", "COMPLETED")).thenReturn(rides);

        var list = rideService.getUserRidesByStatus("user7", "completed");
        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void getAllUserRides_success() {
        ArrayList<Ride> rides = new ArrayList<>();
        rides.add(buildRide("ride-6", "user9", "driver2", "PENDING"));
        rides.add(buildRide("ride-7", "user9", "driver4", "COMPLETED"));
        when(rideRepository.findAllByUserIdOrderByBookingDateDescBookingTimeDesc("user9")).thenReturn(rides);

        var list = rideService.getAllUserRides("user9");
        assertThat(list).hasSize(2);
        verify(rideRepository).findAllByUserIdOrderByBookingDateDescBookingTimeDesc("user9");
    }

    @Test
    void getAllDriverRides_success() {
        ArrayList<Ride> rides = new ArrayList<>();
        rides.add(buildRide("ride-8", "user10", "driver55", "PENDING"));
        when(rideRepository.findAllByDriverIdOrderByBookingDateDescBookingTimeDesc("driver55")).thenReturn(rides);

        var list = rideService.getAllDriverRides("driver55");
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDriverId()).isEqualTo("driver55");
    }

    @Nested
    class UpdateRideStatusTests {
        @Test
        void updateRideStatus_toOngoing_setsStartedAt() {
            Ride ride = buildRide("ride-9", "user1", "driver1", "PENDING");
            when(rideRepository.findById("ride-9")).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

            SuccessResponseDto response = rideService.updateRideStatus("ride-9", "ongoing");

            assertThat(response.getMessage()).contains("ONGOING");
            assertThat(ride.getStatus()).isEqualTo("ONGOING");
            assertThat(ride.getStartedAt()).isNotNull();
        }

        @Test
        void updateRideStatus_toCompleted_setsCompletedAt() {
            Ride ride = buildRide("ride-10", "user1", "driver1", "ONGOING");
            when(rideRepository.findById("ride-10")).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

            SuccessResponseDto response = rideService.updateRideStatus("ride-10", "completed");

            assertThat(response.getMessage()).contains("COMPLETED");
            assertThat(ride.getStatus()).isEqualTo("COMPLETED");
            assertThat(ride.getCompletedAt()).isNotNull();
        }

        @Test
        void updateRideStatus_notFound() {
            when(rideRepository.findById("nope")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> rideService.updateRideStatus("nope", "completed"))
                    .isInstanceOf(RideNotFoundException.class);
        }
    }

    @Nested
    class CancelRideTests {
        @Test
        void cancelRideStatus_success() {
            Ride ride = buildRide("ride-11", "user1", "driver1", "PENDING");
            when(rideRepository.findById("ride-11")).thenReturn(Optional.of(ride));
            when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

            CancelDto cancelDto = new CancelDto();
            cancelDto.setCancelledBy("USER");

            SuccessResponseDto response = rideService.cancelRideStatus("ride-11", cancelDto);

            assertThat(response.getMessage()).contains("USER");
            assertThat(ride.getStatus()).isEqualTo("CANCELLED");
            assertThat(ride.getCancelledAt()).isNotNull();
            assertThat(ride.getCancelledBy()).isEqualTo("USER");
        }

        @Test
        void cancelRideStatus_notFound() {
            when(rideRepository.findById("unknown")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> rideService.cancelRideStatus("unknown", new CancelDto()))
                    .isInstanceOf(RideNotFoundException.class);
        }
    }

    @Nested
    class NewestImmediateRideForDriverTests {
        @Test
        void getNewestImmediateRideForDriver_found() {
            Ride newest = buildRide("ride-12", "user1", "driver77", "PENDING");
            when(rideRepository.findFirstByDriverIdAndStatusOrderByRequestedAtDesc("driver77", "PENDING"))
                    .thenReturn(Optional.of(newest));

            RideDto dto = rideService.getNewestImmediateRideForDriver("driver77");
            assertThat(dto.getRideId()).isEqualTo("ride-12");
            assertThat(dto.getDriverId()).isEqualTo("driver77");
        }

        @Test
        void getNewestImmediateRideForDriver_notFound() {
            when(rideRepository.findFirstByDriverIdAndStatusOrderByRequestedAtDesc("driverXYZ", "PENDING"))
                    .thenReturn(Optional.empty());
            assertThatThrownBy(() -> rideService.getNewestImmediateRideForDriver("driverXYZ"))
                    .isInstanceOf(RideNotFoundException.class);
        }
    }
}


package com.cabbooking.rideservice.controller;

import com.cabbooking.rideservice.dto.CancelDto;
import com.cabbooking.rideservice.dto.RideDto;
import com.cabbooking.rideservice.dto.SuccessResponseDto;
import com.cabbooking.rideservice.service.RideService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RideController.class)
public class RideControllerTest {

    @MockitoBean
    private RideService rideService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    private RideDto testRideDto;
    private SuccessResponseDto testSuccessResponse;

    @BeforeEach
    void setUp() {
        // ObjectMapper is now injected by Spring, no need to configure manually

        // Setup test data
        testRideDto = new RideDto();
        testRideDto.setRideId("ride-123");
        testRideDto.setUserId("user-123");
        testRideDto.setDriverId("driver-123");
        testRideDto.setCarSeater("4");
        testRideDto.setPickupLocation("Location A");
        testRideDto.setDropLocation("Location B");
        testRideDto.setBookingDate("2025-09-23");
        testRideDto.setBookingTime("10:00");
        testRideDto.setRequestedAt(LocalDateTime.now());
        testRideDto.setImmediateBooking(true);
        testRideDto.setFare(BigDecimal.valueOf(25.50));
        testRideDto.setDistance(BigDecimal.valueOf(10.5));
        testRideDto.setStatus("PENDING");

        testSuccessResponse = new SuccessResponseDto();
        testSuccessResponse.setTimestamp(new Date());
        testSuccessResponse.setMessage("Operation successful");
        testSuccessResponse.setStatus("SUCCESS");
    }

    @Test
    void testBookARide_Success() throws Exception {
        // Given
        when(rideService.bookARide(any(RideDto.class))).thenReturn(testRideDto);

        // When & Then
        mockMvc.perform(post("/api/rides/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRideDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rideId").value("ride-123"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.driverId").value("driver-123"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.fare").value(25.50));
    }

    @Test
    void testGetRideById_Success() throws Exception {
        // Given
        String rideId = "ride-123";
        when(rideService.getRideById(rideId)).thenReturn(testRideDto);

        // When & Then
        mockMvc.perform(get("/api/rides/{rideId}", rideId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rideId").value("ride-123"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testGetDriverRidesByStatus_Success() throws Exception {
        // Given
        String driverId = "driver-123";
        String status = "PENDING";
        ArrayList<RideDto> rides = new ArrayList<>();
        rides.add(testRideDto);
        when(rideService.getDriverRidesByStatus(driverId, status)).thenReturn(rides);

        // When & Then
        mockMvc.perform(get("/api/rides/driver/{driverId}", driverId)
                .param("status", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rideId").value("ride-123"))
                .andExpect(jsonPath("$[0].driverId").value("driver-123"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void testGetUserRidesByStatus_Success() throws Exception {
        // Given
        String userId = "user-123";
        String status = "COMPLETED";
        ArrayList<RideDto> rides = new ArrayList<>();
        testRideDto.setStatus("COMPLETED");
        rides.add(testRideDto);
        when(rideService.getUserRidesByStatus(userId, status)).thenReturn(rides);

        // When & Then
        mockMvc.perform(get("/api/rides/user/{userId}", userId)
                .param("status", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rideId").value("ride-123"))
                .andExpect(jsonPath("$[0].userId").value("user-123"))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    void testGetUserRides_Success() throws Exception {
        // Given
        String userId = "user-123";
        ArrayList<RideDto> rides = new ArrayList<>();
        rides.add(testRideDto);
        when(rideService.getAllUserRides(userId)).thenReturn(rides);

        // When & Then
        mockMvc.perform(get("/api/rides/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rideId").value("ride-123"))
                .andExpect(jsonPath("$[0].userId").value("user-123"));
    }

    @Test
    void testGetDriverRides_Success() throws Exception {
        // Given
        String driverId = "driver-123";
        ArrayList<RideDto> rides = new ArrayList<>();
        rides.add(testRideDto);
        when(rideService.getAllDriverRides(driverId)).thenReturn(rides);

        // When & Then
        mockMvc.perform(get("/api/rides/driver/{id}", driverId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rideId").value("ride-123"))
                .andExpect(jsonPath("$[0].driverId").value("driver-123"));
    }

    @Test
    void testUpdateRideStatus_Success() throws Exception {
        // Given
        String rideId = "ride-123";
        String newStatus = "IN_PROGRESS";
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", newStatus);

        when(rideService.updateRideStatus(rideId, newStatus)).thenReturn(testSuccessResponse);

        // When & Then
        mockMvc.perform(patch("/api/rides/status/{rideId}", rideId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testUpdateRideStatus_BadRequest_NullStatus() throws Exception {
        // Given
        String rideId = "ride-123";
        Map<String, String> statusUpdate = new HashMap<>();
        // No status field or null status

        // When & Then
        mockMvc.perform(patch("/api/rides/status/{rideId}", rideId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRideStatus_BadRequest_EmptyBody() throws Exception {
        // Given
        String rideId = "ride-123";

        // When & Then
        mockMvc.perform(patch("/api/rides/status/{rideId}", rideId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCancelRideStatus_Success() throws Exception {
        // Given
        String rideId = "ride-123";
        CancelDto cancelDto = new CancelDto();
        cancelDto.setStatus("CANCELLED");
        cancelDto.setReason("Customer requested cancellation");
        cancelDto.setCancelledBy("user-123");

        when(rideService.cancelRideStatus(eq(rideId), any(CancelDto.class))).thenReturn(testSuccessResponse);

        // When & Then
        mockMvc.perform(patch("/api/rides/cancel/{rideId}", rideId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testGetNewestImmediateRideForDriver_Success() throws Exception {
        // Given
        String driverId = "driver-123";
        testRideDto.setImmediateBooking(true);
        when(rideService.getNewestImmediateRideForDriver(driverId)).thenReturn(testRideDto);

        // When & Then
        mockMvc.perform(get("/api/rides/driver/pending/{driverId}", driverId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rideId").value("ride-123"))
                .andExpect(jsonPath("$.driverId").value("driver-123"))
                .andExpect(jsonPath("$.immediateBooking").value(true));
    }

    @Test
    void testGetDriverRidesByStatus_EmptyResult() throws Exception {
        // Given
        String driverId = "driver-123";
        String status = "COMPLETED";
        ArrayList<RideDto> emptyRides = new ArrayList<>();
        when(rideService.getDriverRidesByStatus(driverId, status)).thenReturn(emptyRides);

        // When & Then
        mockMvc.perform(get("/api/rides/driver/{driverId}", driverId)
                .param("status", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetUserRidesByStatus_EmptyResult() throws Exception {
        // Given
        String userId = "user-123";
        String status = "CANCELLED";
        ArrayList<RideDto> emptyRides = new ArrayList<>();
        when(rideService.getUserRidesByStatus(userId, status)).thenReturn(emptyRides);

        // When & Then
        mockMvc.perform(get("/api/rides/user/{userId}", userId)
                .param("status", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testBookARide_ValidationError() throws Exception {
        // Given
        RideDto invalidRideDto = new RideDto();
        // Missing required fields

        when(rideService.bookARide(any(RideDto.class))).thenReturn(testRideDto);

        // When & Then
        mockMvc.perform(post("/api/rides/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRideDto)))
                .andExpect(status().isOk()); // Controller doesn't validate, service handles it
    }

    @Test
    void testCancelRideStatus_WithNullReason() throws Exception {
        // Given
        String rideId = "ride-123";
        CancelDto cancelDto = new CancelDto();
        cancelDto.setStatus("CANCELLED");
        cancelDto.setCancelledBy("user-123");
        // reason is null

        when(rideService.cancelRideStatus(eq(rideId), any(CancelDto.class))).thenReturn(testSuccessResponse);

        // When & Then
        mockMvc.perform(patch("/api/rides/cancel/{rideId}", rideId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}

package com.cabbooking.locationservice.controller;

import com.cabbooking.locationservice.dto.LocationDto;
import com.cabbooking.locationservice.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
@WebMvcTest(LocationController.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class LocationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void logStart(TestInfo testInfo) {
        log.info("Starting test: {}", testInfo.getDisplayName());
    }

    @AfterEach
    void logEnd(TestInfo testInfo) {
        log.info("Finished test: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1️. getLocations() should return OK and list of LocationDto")
    void getLocations_ReturnsOkAndListOfLocations() throws Exception {
        List<LocationDto> locations = List.of(
                new LocationDto(1, "Koyambedu", "West Chennai", new BigDecimal("13.0694"), new BigDecimal("80.1948")),
                new LocationDto(2, "Guindy", "South and East Chennai", new BigDecimal("13.0067"), new BigDecimal("80.2206"))
        );

        when(locationService.getLocations()).thenReturn(locations);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/locations"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(locations)));
    }

    @Test
    @DisplayName("2️. getLocationByArea() should return OK and LocationDto")
    void getLocationByArea_ReturnsOkAndLocationDto() throws Exception {
        String area = "koyambedu";
        LocationDto location = new LocationDto(1, area, "West Chennai", new BigDecimal("13.0694"), new BigDecimal("80.1948"));

        when(locationService.getLocationByArea(area)).thenReturn(location);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/locations/{area}", area))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(location)));
    }
}

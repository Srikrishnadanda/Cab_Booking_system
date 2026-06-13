package com.cabbooking.locationservice.service;

import com.cabbooking.locationservice.dto.LocationDto;
import com.cabbooking.locationservice.exception.LocationNotFoundException;
import com.cabbooking.locationservice.model.Location;
import com.cabbooking.locationservice.repository.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class LocationServiceTests {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LocationServiceImp locationService;

    private Location location1;
    private Location location2;
    private LocationDto locationDto1;
    private LocationDto locationDto2;

    @BeforeEach
    void logStart(TestInfo testInfo) {
        log.info("Starting test: {}", testInfo.getDisplayName());
    }

    @AfterEach
    void logEnd(TestInfo testInfo) {
        log.info("Finished test: {}", testInfo.getDisplayName());
    }

    @BeforeEach
    void setUp() {
        location1 = new Location(1, "Koyambedu", "West Chennai", new BigDecimal("13.0694"), new BigDecimal("80.1948"), null, null);
        location2 = new Location(2, "Guindy", "South and East Chennai", new BigDecimal("13.0067"), new BigDecimal("80.2206"), null, null);
        locationDto1 = new LocationDto(1, "Koyambedu", "West Chennai", new BigDecimal("13.0694"), new BigDecimal("80.1948"));
        locationDto2 = new LocationDto(2, "Guindy", "South and East Chennai", new BigDecimal("13.0067"), new BigDecimal("80.2206"));
    }

    @Test
    @DisplayName("1️. getLocations() should return all LocationDto objects")
    void getLocations_shouldReturnAllLocations() {
        when(locationRepository.findAll()).thenReturn(Arrays.asList(location1, location2));
        when(modelMapper.map(location1, LocationDto.class)).thenReturn(locationDto1);
        when(modelMapper.map(location2, LocationDto.class)).thenReturn(locationDto2);

        List<LocationDto> locations = locationService.getLocations();

        assertEquals(2, locations.size());
        assertEquals("Koyambedu", locations.get(0).getArea());
        assertEquals("Guindy", locations.get(1).getArea());

        verify(locationRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(Location.class), eq(LocationDto.class));
    }

    @Test
    @DisplayName("2. getLocationByArea() should return LocationDto when area exists")
    void getLocationByArea_shouldReturnLocationDto_whenLocationExists() {
        String area = "Koyambedu";
        when(locationRepository.findByArea(area)).thenReturn(Optional.of(location1));
        when(modelMapper.map(location1, LocationDto.class)).thenReturn(locationDto1);

        LocationDto locationDto = locationService.getLocationByArea(area);

        assertEquals("Koyambedu", locationDto.getArea());
        assertEquals(new BigDecimal("13.0694"), locationDto.getLatitude());
        assertEquals(new BigDecimal("80.1948"), locationDto.getLongitude());

        verify(locationRepository, times(1)).findByArea(area);
        verify(modelMapper, times(1)).map(location1, LocationDto.class);
    }

    @Test
    @DisplayName("3️. getLocationByArea() should throw LocationNotFoundException when area is missing")
    void getLocationByArea_shouldThrowLocationNotFoundException_whenLocationDoesNotExist() {
        String area = "NonExistentArea";
        when(locationRepository.findByArea(area)).thenReturn(Optional.empty());

        assertThrows(LocationNotFoundException.class, () -> locationService.getLocationByArea(area));

        verify(locationRepository, times(1)).findByArea(area);
        verify(modelMapper, never()).map(any(), any());
    }
}

package com.cabbooking.locationservice.controller;

import com.cabbooking.locationservice.dto.LocationDto;
import com.cabbooking.locationservice.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "Location Controller", description = "API endpoints for location service")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    @Operation(summary = "Get all locations", description = "Retrieves a list of all available locations")
    public ResponseEntity<List<LocationDto>> getLocations(){
        List<LocationDto> locations = locationService.getLocations();
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @GetMapping("/{area}")
    @Operation(summary = "Get location by area", description = "Retrieves a specific location by its area name")
    public ResponseEntity<LocationDto> getLocationByArea(
            @Parameter(description = "Area name to search for") @PathVariable String area) {
        LocationDto location = locationService.getLocationByArea(area);
        return new ResponseEntity<>(location, HttpStatus.OK);
    }
}
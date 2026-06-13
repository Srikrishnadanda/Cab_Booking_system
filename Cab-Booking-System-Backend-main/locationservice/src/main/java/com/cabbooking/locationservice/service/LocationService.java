package com.cabbooking.locationservice.service;

import com.cabbooking.locationservice.dto.LocationDto;

import java.util.List;

public interface LocationService {
    List<LocationDto> getLocations();
    LocationDto getLocationByArea(String area);
}

package com.cabbooking.locationservice.service;

import com.cabbooking.locationservice.dto.LocationDto;
import com.cabbooking.locationservice.exception.LocationNotFoundException;
import com.cabbooking.locationservice.exception.LocationServiceException;
import com.cabbooking.locationservice.model.Location;
import com.cabbooking.locationservice.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImp implements LocationService {

    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<LocationDto> getLocations() {
        log.info("Fetching all locations from the repository");
        try {
            List<LocationDto> locationList = locationRepository.findAll()
                    .stream()
                    .map(location -> modelMapper.map(location, LocationDto.class))
                    .collect(Collectors.toList());
            log.debug("Successfully mapped {} locations to DTOs", locationList.size());
            return locationList;
        } catch (Exception e) {
            log.error("Error occurred while retrieving locations", e);
            throw new LocationServiceException("Error retrieving locations data");
        }
    }

    @Override
    public LocationDto getLocationByArea(String area) {
        log.info("Fetching location for area: {}", area);
        Location location = locationRepository.findByArea(area)
                .orElseThrow(() -> new LocationNotFoundException("Location not found for area: " + area));
        LocationDto locationDto = modelMapper.map(location, LocationDto.class);
        log.debug("Successfully mapped location '{}' to DTO", area);
        return locationDto;
    }


}

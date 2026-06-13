package com.cabbooking.serviceimpl;

import com.cabbooking.dto.DriverDto;
import com.cabbooking.dto.DriverRequest;
import com.cabbooking.dto.DriverServiceResponse;
import com.cabbooking.exception.*;
import com.cabbooking.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.cabbooking.entity.Driver;
import com.cabbooking.service.DriverService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DriverServiceImpl implements DriverService {

    private static final String NOT_FOUND = " not found";
    private static final String DRIVER_WITH_ID = "Driver with ID ";

    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;

    public DriverServiceResponse registerDriver(DriverRequest driverRequest) {
        log.info("DriverService class invoked for registration");
        if (driverRepository.existsByEmail(driverRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Given Email Already Registered: " + driverRequest.getEmail());
        }
        if (driverRepository.existsByPhone(driverRequest.getPhone())) {
            throw new PhoneAlreadyExistsException("Given Phone number already registered: " + driverRequest.getPhone());
        }

        Driver driver = modelMapper.map(driverRequest, Driver.class);
        Driver savedDriver = driverRepository.save(driver);
        log.info("Driver Saved Successfully with ID: {}", savedDriver.getDriverId());

        DriverDto driverDto = modelMapper.map(savedDriver, DriverDto.class);
        DriverServiceResponse driverResponse = new DriverServiceResponse();
        driverResponse.setBody(driverDto);
        driverResponse.setStatus("success");
        driverResponse.setMessage("Driver Registered Successfully");
        return driverResponse;
    }

    public DriverDto getDriverById(String id) throws DriverNotFoundException {
        log.info("Getting driver by ID: {}", id);
            Driver driver = driverRepository.findByDriverId(id)
                    .orElseThrow(() -> new DriverNotFoundException(DRIVER_WITH_ID + id + NOT_FOUND));
            log.info("Driver found: {}", driver);
            return modelMapper.map(driver, DriverDto.class);
    }

    @Override
    public DriverDto updateDriverRating(String driverId, double rating) throws DriverNotFoundException {
        log.info("Updating rating for driver ID: {} to {}", driverId, rating);
        Driver driver = driverRepository.findByDriverId(driverId)
                .orElseThrow(() -> new DriverNotFoundException(DRIVER_WITH_ID + driverId + NOT_FOUND));
        driver.setRating(rating);
        log.info("New rating set: {}", driver.getRating());
        Driver updatedDriver = driverRepository.save(driver);
        return modelMapper.map(updatedDriver, DriverDto.class);
    }

    public String deleteDriver(String email) throws DriverNotFoundException {
        log.info("Deleting driver with Email: {}", email);
        Driver driver = driverRepository.findByEmail(email)
                    .orElseThrow(() -> new DriverNotFoundException("Driver with Email " + email + NOT_FOUND));
        driverRepository.delete(driver);
        log.info("Driver deleted successfully");
        return "Driver Deleted Successfully";
    }

    public DriverServiceResponse forgotPassword(String email,String newPassword) {
        log.info("Processing forgot password request for email: {}", email);
        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new DriverNotFoundException("Driver with email " + email + NOT_FOUND));
        driver.setPassword(newPassword);
        driverRepository.save(driver);
        DriverServiceResponse response = new DriverServiceResponse();
        response.setStatus("success");
        response.setMessage("Password Changed Successfully");
        return response;
    }

    public DriverDto updateDriverStatus(String id) throws DriverNotFoundException {
        log.info("Updating status for driver ID: {}", id);
        Driver driver = driverRepository.findByDriverId(id)
                .orElseThrow(() -> new DriverNotFoundException(DRIVER_WITH_ID + id + NOT_FOUND));
        driver.setAvailable(!driver.isAvailable());
        Driver updatedDriver = driverRepository.save(driver);
        log.info("Driver status updated successfully to: {}", updatedDriver.isAvailable());
        return modelMapper.map(updatedDriver, DriverDto.class);
    }

    public DriverDto getAvailableDrivers(String carSr) {
            log.info("Getting first available driver with car seater: {}", carSr);
            List<Driver> availableDriver = driverRepository.findAllByIsAvailableAndCarSeater(carSr);
            if(availableDriver.isEmpty()){
                    log.warn("No available drivers found for car seater: {}", carSr);
                    throw new DriverNotFoundException("No available drivers found for car seater: " + carSr);
            }
            return modelMapper.map(availableDriver.stream().findFirst(), DriverDto.class);
    }

    @Override
    public DriverDto getDriverByEmail(String email) {
        Driver driver = driverRepository.findByEmail(email).orElseThrow(() -> new DriverNotFoundException("Driver with Email " + email + NOT_FOUND));
        return modelMapper.map(driver,DriverDto.class);
    }

    public DriverDto updateDriverProfile(String id, DriverRequest driverRequest) throws DriverNotFoundException {
        log.info("Updating driver profile for ID: {}", id);

        Driver driver = driverRepository.findByDriverId(id)
                .orElseThrow(() -> new DriverNotFoundException(DRIVER_WITH_ID + id + NOT_FOUND));

        if (driverRequest.getEmail() != null && !driverRequest.getEmail().isEmpty()
                && !driver.getEmail().equals(driverRequest.getEmail())
                && driverRepository.existsByEmail(driverRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + driverRequest.getEmail());
        }

        if (driverRequest.getPhone() != null && !driverRequest.getPhone().isEmpty()
                && !driver.getPhone().equals(driverRequest.getPhone())
                && driverRepository.existsByPhone(driverRequest.getPhone())) {
            throw new PhoneAlreadyExistsException("Phone number already registered: " + driverRequest.getPhone());
        }

        modelMapper.map(driverRequest, driver);
        driver.setDriverId(id);

        Driver updatedDriver = driverRepository.save(driver);
        log.info("Driver profile updated successfully");
        return modelMapper.map(updatedDriver, DriverDto.class);
    }
}
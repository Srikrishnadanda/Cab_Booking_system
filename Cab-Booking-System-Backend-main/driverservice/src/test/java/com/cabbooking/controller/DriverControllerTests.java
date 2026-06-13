package com.cabbooking.controller;

import com.cabbooking.controller.DriverController;
import com.cabbooking.dto.DriverDto;
import com.cabbooking.dto.DriverRequest;
import com.cabbooking.dto.DriverServiceResponse;
import com.cabbooking.dto.PasswordResetRequest;
import com.cabbooking.exception.DriverNotFoundException;
import com.cabbooking.exception.EmailAlreadyExistsException;
import com.cabbooking.exception.PhoneAlreadyExistsException;
import com.cabbooking.service.DriverService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Driver Controller Tests - Testing all endpoints and exception handling")
public class DriverControllerTests {

    @Mock
    private DriverService driverService;

    @InjectMocks
    private DriverController driverController;

    private DriverDto testDriverDto;
    private DriverRequest testDriverRequest;
    private DriverServiceResponse testDriverServiceResponse;
    private PasswordResetRequest passwordResetRequest;

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
        testDriverDto = new DriverDto();
        testDriverDto.setDriverId("test-driver-id");
        testDriverDto.setFullName("John Doe");
        testDriverDto.setEmail("john.doe@example.com");
        testDriverDto.setPhone("1234567890");
        testDriverDto.setVehicleNumber("ABC123");
        testDriverDto.setVehicleName("Toyota Camry");
        testDriverDto.setLicenceNumber("DL123456789");
        testDriverDto.setCarSeater("4");
        testDriverDto.setGender("Male");
        testDriverDto.setRating(4.5);
        testDriverDto.setAvailable(false);

        testDriverRequest = new DriverRequest();
        testDriverRequest.setFullName("John Doe");
        testDriverRequest.setEmail("john.doe@example.com");
        testDriverRequest.setPhone("1234567890");
        testDriverRequest.setVehicleNumber("ABC123");
        testDriverRequest.setVehicleName("Toyota Camry");
        testDriverRequest.setLicenceNumber("DL123456789");
        testDriverRequest.setCarSeater("4");
        testDriverRequest.setGender("Male");
        testDriverRequest.setPassword("password123");

        testDriverServiceResponse = new DriverServiceResponse();
        testDriverServiceResponse.setStatus("success");
        testDriverServiceResponse.setMessage("Driver Registered Successfully");
        testDriverServiceResponse.setBody(testDriverDto);

        passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setEmail("john.doe@example.com");
        passwordResetRequest.setNewPassword("newPassword123");
    }

    @Test
    @DisplayName("1. getUserById() should return OK and DriverDto when driver exists")
    void getUserById_shouldReturnOkAndDriverDto_whenDriverExists() throws DriverNotFoundException {
        when(driverService.getDriverById(anyString())).thenReturn(testDriverDto);

        ResponseEntity<DriverDto> response = driverController.getUserById("test-driver-id");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDriverDto, response.getBody());
        verify(driverService).getDriverById("test-driver-id");
    }

    @Test
    @DisplayName("2. getUserById() should throw DriverNotFoundException when driver not found")
    void getUserById_shouldThrowDriverNotFoundException_whenDriverNotFound() throws DriverNotFoundException {
        when(driverService.getDriverById(anyString())).thenThrow(new DriverNotFoundException("Driver not found"));

        assertThrows(DriverNotFoundException.class,
            () -> driverController.getUserById("non-existent-id"));
        verify(driverService).getDriverById("non-existent-id");
    }

    @Test
    @DisplayName("3. registerDriver() should return CREATED and DriverServiceResponse on successful registration")
    void registerDriver_shouldReturnCreatedAndResponse_whenRegistrationSuccessful() {
        when(driverService.registerDriver(any(DriverRequest.class))).thenReturn(testDriverServiceResponse);

        ResponseEntity<DriverServiceResponse> response = driverController.registerDriver(testDriverRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testDriverServiceResponse, response.getBody());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Driver Registered Successfully", response.getBody().getMessage());
        verify(driverService).registerDriver(testDriverRequest);
    }

    @Test
    @DisplayName("4. registerDriver() should handle EmailAlreadyExistsException")
    void registerDriver_shouldHandleEmailAlreadyExistsException_whenEmailExists() {
        when(driverService.registerDriver(any(DriverRequest.class)))
            .thenThrow(new EmailAlreadyExistsException("Email already registered"));

        assertThrows(EmailAlreadyExistsException.class,
            () -> driverController.registerDriver(testDriverRequest));
        verify(driverService).registerDriver(testDriverRequest);
    }

    @Test
    @DisplayName("5. registerDriver() should handle PhoneAlreadyExistsException")
    void registerDriver_shouldHandlePhoneAlreadyExistsException_whenPhoneExists() {
        when(driverService.registerDriver(any(DriverRequest.class)))
            .thenThrow(new PhoneAlreadyExistsException("Phone already registered"));

        assertThrows(PhoneAlreadyExistsException.class,
            () -> driverController.registerDriver(testDriverRequest));
        verify(driverService).registerDriver(testDriverRequest);
    }

    @Test
    @DisplayName("6. updateDriverProfile() should return OK and updated DriverDto")
    void updateDriverProfile_shouldReturnOkAndUpdatedDriverDto_whenUpdateSuccessful() throws DriverNotFoundException {
        DriverDto updatedDriverDto = new DriverDto();
        updatedDriverDto.setDriverId("test-driver-id");
        updatedDriverDto.setFullName("Jane Doe Updated");

        when(driverService.updateDriverProfile(anyString(), any(DriverRequest.class)))
            .thenReturn(updatedDriverDto);

        ResponseEntity<DriverDto> response = driverController.updateDriverProfile("test-driver-id", testDriverRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDriverDto, response.getBody());
        verify(driverService).updateDriverProfile("test-driver-id", testDriverRequest);
    }

    @Test
    @DisplayName("7. updateDriverProfile() should throw DriverNotFoundException when driver not found")
    void updateDriverProfile_shouldThrowDriverNotFoundException_whenDriverNotFound() throws DriverNotFoundException {
        when(driverService.updateDriverProfile(anyString(), any(DriverRequest.class)))
            .thenThrow(new DriverNotFoundException("Driver not found"));

        assertThrows(DriverNotFoundException.class,
            () -> driverController.updateDriverProfile("non-existent-id", testDriverRequest));
        verify(driverService).updateDriverProfile("non-existent-id", testDriverRequest);
    }

    @Test
    @DisplayName("8. deleteDriver() should return NO_CONTENT when deletion successful")
    String deleteDriver_shouldReturnOk_whenDeletionSuccessful() throws DriverNotFoundException {
        doNothing().when(driverService).deleteDriver(anyString());

        ResponseEntity<String> response = driverController.deleteDriver("test-driver-id");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(driverService).deleteDriver("test-driver-id");
        return "Driver Deleted Successfully";
    }

    @Test
    @DisplayName("9. deleteDriver() should throw DriverNotFoundException when driver not found")
    void deleteDriver_shouldThrowDriverNotFoundException_whenDriverNotFound() throws DriverNotFoundException {
        doThrow(new DriverNotFoundException("Driver not found")).when(driverService).deleteDriver(anyString());

        assertThrows(DriverNotFoundException.class,
            () -> driverController.deleteDriver("non-existent-id"));
        verify(driverService).deleteDriver("non-existent-id");
    }

    @Test
    @DisplayName("10. forgotPassword() should return OK and success response")
    void forgotPassword_shouldReturnOkAndSuccessResponse_whenPasswordResetSuccessful() {
        DriverServiceResponse passwordResetResponse = new DriverServiceResponse();
        passwordResetResponse.setStatus("success");
        passwordResetResponse.setMessage("Password Changed Successfully");

        when(driverService.forgotPassword(anyString(), anyString())).thenReturn(passwordResetResponse);

        ResponseEntity<DriverServiceResponse> response = driverController.forgotPassword(passwordResetRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(passwordResetResponse, response.getBody());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Password Changed Successfully", response.getBody().getMessage());
        verify(driverService).forgotPassword(passwordResetRequest.getEmail(), passwordResetRequest.getNewPassword());
    }

    @Test
    @DisplayName("11. forgotPassword() should handle DriverNotFoundException")
    void forgotPassword_shouldHandleDriverNotFoundException_whenDriverNotFound() {
        when(driverService.forgotPassword(anyString(), anyString()))
            .thenThrow(new DriverNotFoundException("Driver not found"));

        assertThrows(DriverNotFoundException.class,
            () -> driverController.forgotPassword(passwordResetRequest));
        verify(driverService).forgotPassword(passwordResetRequest.getEmail(), passwordResetRequest.getNewPassword());
    }

    @Test
    @DisplayName("12. getAvailableDrivers() should return OK and available DriverDto")
    void getAvailableDrivers_shouldReturnOkAndAvailableDriver_whenDriverAvailable() {
        DriverDto availableDriver = new DriverDto();
        availableDriver.setDriverId("available-driver-id");
        availableDriver.setAvailable(true);
        availableDriver.setCarSeater("4");

        when(driverService.getAvailableDrivers(anyString())).thenReturn(availableDriver);

        ResponseEntity<DriverDto> response = driverController.getAvailableDrivers("4");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(availableDriver, response.getBody());
        assertTrue(response.getBody().isAvailable());
        assertEquals("4", response.getBody().getCarSeater());
        verify(driverService).getAvailableDrivers("4");
    }

    @Test
    @DisplayName("13. getAvailableDrivers() should handle DriverNotFoundException when no drivers available")
    void getAvailableDrivers_shouldHandleDriverNotFoundException_whenNoDriversAvailable() {
        when(driverService.getAvailableDrivers(anyString()))
            .thenThrow(new DriverNotFoundException("No available drivers found"));

        assertThrows(DriverNotFoundException.class,
            () -> driverController.getAvailableDrivers("4"));
        verify(driverService).getAvailableDrivers("4");
    }

    @Test
    @DisplayName("14. updateAvailability() should return OK and updated DriverDto with toggled status")
    void updateAvailability_shouldReturnOkAndUpdatedDriverDto_whenStatusUpdateSuccessful() throws DriverNotFoundException {
        DriverDto updatedDriver = new DriverDto();
        updatedDriver.setDriverId("test-driver-id");
        updatedDriver.setAvailable(true);

        when(driverService.updateDriverStatus(anyString())).thenReturn(updatedDriver);

        ResponseEntity<DriverDto> response = driverController.updateAvailability("test-driver-id");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDriver, response.getBody());
        assertTrue(response.getBody().isAvailable());
        verify(driverService).updateDriverStatus("test-driver-id");
    }

    @Test
    @DisplayName("15. updateAvailability() should throw DriverNotFoundException when driver not found")
    void updateAvailability_shouldThrowDriverNotFoundException_whenDriverNotFound() throws DriverNotFoundException {
        when(driverService.updateDriverStatus(anyString()))
            .thenThrow(new DriverNotFoundException("Driver not found"));

        assertThrows(DriverNotFoundException.class,
            () -> driverController.updateAvailability("non-existent-id"));
        verify(driverService).updateDriverStatus("non-existent-id");
    }

    @Test
    @DisplayName("16. getDriverByEmail() should return OK and DriverDto when driver exists")
    void getDriverByEmail_shouldReturnOkAndDriverDto_whenDriverExists() throws DriverNotFoundException {
        when(driverService.getDriverByEmail(anyString())).thenReturn(testDriverDto);

        ResponseEntity<DriverDto> response = driverController.getDriverByEmail("john.doe@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDriverDto, response.getBody());
        assertEquals("john.doe@example.com", response.getBody().getEmail());
        verify(driverService).getDriverByEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("17. getDriverByEmail() should throw DriverNotFoundException when driver not found")
    void getDriverByEmail_shouldThrowDriverNotFoundException_whenDriverNotFound() throws DriverNotFoundException {
        when(driverService.getDriverByEmail(anyString()))
            .thenThrow(new DriverNotFoundException("Driver not found"));

        assertThrows(DriverNotFoundException.class,
            () -> driverController.getDriverByEmail("nonexistent@example.com"));
        verify(driverService).getDriverByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("18. updateDriverRating() should return OK and updated DriverDto with new rating")
    void updateDriverRating_shouldReturnOkAndUpdatedDriverDto_whenRatingUpdateSuccessful() throws DriverNotFoundException {
        double newRating = 4.8;
        DriverDto updatedDriver = new DriverDto();
        updatedDriver.setDriverId("test-driver-id");
        updatedDriver.setRating(newRating);

        when(driverService.updateDriverRating(anyString(), anyDouble())).thenReturn(updatedDriver);

        ResponseEntity<DriverDto> response = driverController.updateDriverRating("test-driver-id", newRating);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDriver, response.getBody());
        assertEquals(newRating, response.getBody().getRating());
        verify(driverService).updateDriverRating("test-driver-id", newRating);
    }

    @Test
    @DisplayName("19. updateDriverRating() should throw DriverNotFoundException when driver not found")
    void updateDriverRating_shouldThrowDriverNotFoundException_whenDriverNotFound() throws DriverNotFoundException {
        when(driverService.updateDriverRating(anyString(), anyDouble()))
            .thenThrow(new DriverNotFoundException("Driver not found"));

        assertThrows(DriverNotFoundException.class,
            () -> driverController.updateDriverRating("non-existent-id", 4.8));
        verify(driverService).updateDriverRating("non-existent-id", 4.8);
    }
}

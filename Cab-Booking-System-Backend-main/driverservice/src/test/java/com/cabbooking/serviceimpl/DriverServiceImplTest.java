package com.cabbooking.serviceimpl;

import com.cabbooking.dto.DriverDto;
import com.cabbooking.dto.DriverRequest;
import com.cabbooking.dto.DriverServiceResponse;
import com.cabbooking.entity.Driver;
import com.cabbooking.exception.DriverNotFoundException;
import com.cabbooking.exception.EmailAlreadyExistsException;
import com.cabbooking.exception.PhoneAlreadyExistsException;
import com.cabbooking.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DriverServiceImpl driverService;

    private Driver testDriver;
    private DriverRequest driverRequest;
    private DriverDto driverDto;

    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setDriverId("test-driver-id");
        testDriver.setFullName("John Doe");
        testDriver.setEmail("john.doe@example.com");
        testDriver.setPhone("1234567890");
        testDriver.setVehicleNumber("ABC123");
        testDriver.setVehicleName("Toyota Camry");
        testDriver.setLicenceNumber("DL123456789");
        testDriver.setCarSeater("4");
        testDriver.setGender("Male");
        testDriver.setRating(4.5);
        testDriver.setAvailable(false);

        driverRequest = new DriverRequest();
        driverRequest.setFullName("John Doe");
        driverRequest.setEmail("john.doe@example.com");
        driverRequest.setPhone("1234567890");
        driverRequest.setVehicleNumber("ABC123");
        driverRequest.setVehicleName("Toyota Camry");
        driverRequest.setLicenceNumber("DL123456789");
        driverRequest.setCarSeater("4");
        driverRequest.setGender("Male");

        driverDto = new DriverDto();
        driverDto.setDriverId("test-driver-id");
        driverDto.setFullName("John Doe");
        driverDto.setEmail("john.doe@example.com");
        driverDto.setPhone("1234567890");
        driverDto.setVehicleNumber("ABC123");
        driverDto.setVehicleName("Toyota Camry");
        driverDto.setLicenceNumber("DL123456789");
        driverDto.setCarSeater("4");
        driverDto.setGender("Male");
        driverDto.setRating(4.5);
        driverDto.setAvailable(false);
    }

    @Test
    @DisplayName("1. Should successfully register a new driver when email and phone are unique")
    void testRegisterDriver_Success() {
        when(driverRepository.existsByEmail(anyString())).thenReturn(false);
        when(driverRepository.existsByPhone(anyString())).thenReturn(false);
        when(modelMapper.map(driverRequest, Driver.class)).thenReturn(testDriver);
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);
        when(modelMapper.map(testDriver, DriverDto.class)).thenReturn(driverDto);

        DriverServiceResponse response = driverService.registerDriver(driverRequest);

        assertAll(
            () -> assertNotNull(response),
            () -> assertEquals("success", response.getStatus()),
            () -> assertEquals("Driver Registered Successfully", response.getMessage()),
            () -> assertNotNull(response.getBody())
        );

        verify(driverRepository).existsByEmail(driverRequest.getEmail());
        verify(driverRepository).existsByPhone(driverRequest.getPhone());
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    @DisplayName("2. Should throw EmailAlreadyExistsException when email already exists during registration")
    void testRegisterDriver_EmailAlreadyExists() {
        when(driverRepository.existsByEmail(anyString())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
            EmailAlreadyExistsException.class,
            () -> driverService.registerDriver(driverRequest)
        );

        assertTrue(exception.getMessage().contains("Given Email Already Registered"));
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    @DisplayName("3. Should throw PhoneAlreadyExistsException when phone already exists during registration")
    void testRegisterDriver_PhoneAlreadyExists() {
        when(driverRepository.existsByEmail(anyString())).thenReturn(false);
        when(driverRepository.existsByPhone(anyString())).thenReturn(true);

        PhoneAlreadyExistsException exception = assertThrows(
            PhoneAlreadyExistsException.class,
            () -> driverService.registerDriver(driverRequest)
        );

        assertTrue(exception.getMessage().contains("Given Phone number already registered"));
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    @DisplayName("4. Should successfully retrieve driver by ID when driver exists")
    void testGetDriverById_Success() {
        when(driverRepository.findByDriverId(anyString())).thenReturn(Optional.of(testDriver));
        when(modelMapper.map(testDriver, DriverDto.class)).thenReturn(driverDto);

        DriverDto result = driverService.getDriverById("test-driver-id");

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(driverDto.getDriverId(), result.getDriverId()),
            () -> assertEquals(driverDto.getFullName(), result.getFullName())
        );
    }

    @Test
    @DisplayName("5. Should throw DriverNotFoundException when driver ID does not exist")
    void testGetDriverById_NotFound() {
        when(driverRepository.findByDriverId(anyString())).thenReturn(Optional.empty());

        DriverNotFoundException exception = assertThrows(
            DriverNotFoundException.class,
            () -> driverService.getDriverById("non-existent-id")
        );

        assertTrue(exception.getMessage().contains("Driver with ID non-existent-id not found"));
    }

    @Test
    @DisplayName("6. Should successfully update driver rating when driver exists")
    void testUpdateDriverRating_Success() {
        double newRating = 4.8;
        when(driverRepository.findByDriverId(anyString())).thenReturn(Optional.of(testDriver));
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);
        when(modelMapper.map(testDriver, DriverDto.class)).thenReturn(driverDto);

        DriverDto result = driverService.updateDriverRating("test-driver-id", newRating);

        assertNotNull(result);
        assertEquals(newRating, testDriver.getRating());
    }

    @Test
    @DisplayName("7. Should throw DriverNotFoundException when updating rating for non-existent driver")
    void testUpdateDriverRating_DriverNotFound() {
        when(driverRepository.findByDriverId(anyString())).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class,
            () -> driverService.updateDriverRating("non-existent-id", 4.8));
    }

    @Test
    @DisplayName("8. Should successfully update driver profile when using same email and phone")
    void testUpdateDriverProfile_Success() {
        when(driverRepository.findByDriverId("test-driver-id")).thenReturn(Optional.of(testDriver));
        doNothing().when(modelMapper).map(eq(driverRequest), eq(testDriver));
        when(driverRepository.save(testDriver)).thenReturn(testDriver);
        when(modelMapper.map(eq(testDriver), eq(DriverDto.class))).thenReturn(driverDto);

        DriverDto result = driverService.updateDriverProfile("test-driver-id", driverRequest);

        assertEquals(driverDto, result);
    }

    @Test
    @DisplayName("9. Should throw EmailAlreadyExistsException when updating profile with existing email")
    void testUpdateDriverProfile_EmailAlreadyExists() {
        DriverRequest updateRequest = new DriverRequest();
        updateRequest.setEmail("different@example.com");
        updateRequest.setPhone("1234567890");

        when(driverRepository.findByDriverId(anyString())).thenReturn(Optional.of(testDriver));
        when(driverRepository.existsByEmail("different@example.com")).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
            EmailAlreadyExistsException.class,
            () -> driverService.updateDriverProfile("test-driver-id", updateRequest)
        );

        assertTrue(exception.getMessage().contains("Email already registered"));
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    @DisplayName("10. Should throw PhoneAlreadyExistsException when updating profile with existing phone")
    void testUpdateDriverProfile_PhoneAlreadyExists() {
        DriverRequest updateRequest = new DriverRequest();
        updateRequest.setEmail("john.doe@example.com");
        updateRequest.setPhone("9999999999");

        when(driverRepository.findByDriverId(anyString())).thenReturn(Optional.of(testDriver));
        when(driverRepository.existsByPhone("9999999999")).thenReturn(true);

        assertThrows(PhoneAlreadyExistsException.class,
            () -> driverService.updateDriverProfile("test-driver-id", updateRequest));
        verify(driverRepository, never()).save(any());
    }

    @Test
    @DisplayName("11. Should throw DriverNotFoundException when updating profile for non-existent driver")
    void testUpdateDriverProfile_DriverNotFound() {
        when(driverRepository.findByDriverId(anyString())).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class,
            () -> driverService.updateDriverProfile("missing-id", driverRequest));
    }

    @Test
    @DisplayName("12. Should successfully delete driver when driver exists")
    void testDeleteDriver_Success() {
        when(driverRepository.findByEmail(anyString())).thenReturn(Optional.of(testDriver));

        assertDoesNotThrow(() -> driverService.deleteDriver("john.doe@example.com"));
        verify(driverRepository).delete(testDriver);
    }

    @Test
    @DisplayName("13. Should throw DriverNotFoundException when deleting non-existent driver")
    void testDeleteDriver_NotFound() {
        when(driverRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class,
            () -> driverService.deleteDriver("absent@example.com"));
        verify(driverRepository, never()).delete(any(Driver.class));
    }

    @Test
    @DisplayName("14. Should successfully reset password when driver email exists")
    void testForgotPassword_Success() {
        String newPassword = "newPassword123";
        when(driverRepository.findByEmail(anyString())).thenReturn(Optional.of(testDriver));
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);

        DriverServiceResponse result = driverService.forgotPassword("john.doe@example.com", newPassword);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals("success", result.getStatus()),
            () -> assertEquals("Password Changed Successfully", result.getMessage()),
            () -> assertEquals(newPassword, testDriver.getPassword())
        );
    }

    @Test
    @DisplayName("15. Should throw DriverNotFoundException when resetting password for non-existent email")
    void testForgotPassword_DriverNotFound() {
        when(driverRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class,
            () -> driverService.forgotPassword("nonexistent@example.com", "newPassword"));
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    @DisplayName("16. Should successfully toggle driver availability status when driver exists")
    void testUpdateDriverStatus_Success() {
        when(driverRepository.findByDriverId(anyString())).thenReturn(Optional.of(testDriver));
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);
        when(modelMapper.map(testDriver, DriverDto.class)).thenReturn(driverDto);

        DriverDto result = driverService.updateDriverStatus("test-driver-id");

        assertNotNull(result);
        assertTrue(testDriver.isAvailable());
    }

    @Test
    @DisplayName("17. Should throw DriverNotFoundException when updating status for non-existent driver")
    void testUpdateDriverStatus_DriverNotFound() {
        when(driverRepository.findByDriverId(anyString())).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class,
            () -> driverService.updateDriverStatus("absent-id"));
    }

    @Test
    @DisplayName("18. Should successfully return available driver with matching car seater")
    void testGetAvailableDrivers_Success() {
        testDriver.setAvailable(true);
        when(driverRepository.findAllByIsAvailableAndCarSeater("4")).thenReturn(List.of(testDriver));
        // ModelMapper is invoked with an Optional<Driver> per service implementation
        when(modelMapper.map(any(Optional.class), eq(DriverDto.class))).thenReturn(driverDto);

        DriverDto result = driverService.getAvailableDrivers("4");

        assertNotNull(result);
        assertEquals(driverDto.getDriverId(), result.getDriverId());
    }

    @Test
    @DisplayName("19. Should throw DriverNotFoundException when no available drivers found for car seater")
    void testGetAvailableDrivers_NoDriversFound() {
        when(driverRepository.findAllByIsAvailableAndCarSeater("4")).thenReturn(List.of());

        assertThrows(DriverNotFoundException.class,
            () -> driverService.getAvailableDrivers("4"));
    }

    @Test
    @DisplayName("20. Should successfully retrieve driver by email when driver exists")
    void testGetDriverByEmail_Success() {
        when(driverRepository.findByEmail(anyString())).thenReturn(Optional.of(testDriver));
        when(modelMapper.map(testDriver, DriverDto.class)).thenReturn(driverDto);

        DriverDto result = driverService.getDriverByEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals(driverDto.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("21. Should throw DriverNotFoundException when driver email does not exist")
    void testGetDriverByEmail_NotFound() {
        when(driverRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class,
            () -> driverService.getDriverByEmail("nonexistent@example.com"));
    }



}

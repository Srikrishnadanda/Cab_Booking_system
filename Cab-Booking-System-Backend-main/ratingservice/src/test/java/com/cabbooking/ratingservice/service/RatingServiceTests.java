package com.cabbooking.ratingservice.service;

import com.cabbooking.ratingservice.client.DriverClient;
import com.cabbooking.ratingservice.dto.DriverDto;
import com.cabbooking.ratingservice.dto.RatingDTO;
import com.cabbooking.ratingservice.entity.Rating;
import com.cabbooking.ratingservice.exception.InvalidRatingException;
import com.cabbooking.ratingservice.repository.RatingRepository;
import com.cabbooking.ratingservice.serviceimpl.RatingServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class RatingServiceTests {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DriverClient driverClient;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private RatingDTO testRatingDTO;
    private Rating testRatingEntity;

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
        testRatingDTO = new RatingDTO();
        testRatingDTO.setRatingId(1);
        testRatingDTO.setRideId("100");
        testRatingDTO.setDriverId("200");
        testRatingDTO.setUserId("300");
        testRatingDTO.setScore(5);
        testRatingDTO.setComments("Excellent service!");

        testRatingEntity = new Rating();
        testRatingEntity.setRatingId(1);
        testRatingEntity.setRideId("100");
        testRatingEntity.setDriverId("200");
        testRatingEntity.setUserId("300");
        testRatingEntity.setScore(5);
        testRatingEntity.setFeedback("Excellent service!");
    }

    @Test
    @DisplayName("createRating() should return RatingDTO when valid data provided")
    void createRating_shouldReturnRatingDTO_whenValidDataProvided() {
        when(modelMapper.map(testRatingDTO, Rating.class)).thenReturn(testRatingEntity);
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRatingEntity);
        when(modelMapper.map(testRatingEntity, RatingDTO.class)).thenReturn(testRatingDTO);
        when(ratingRepository.findAverageRatingByDriverId("200")).thenReturn(4.5);
        when(driverClient.updateDriverRating(anyString(), anyDouble()))
                .thenReturn(ResponseEntity.ok(new DriverDto()));

        RatingDTO result = ratingService.createRating(testRatingDTO);

        assertNotNull(result);
        assertEquals(testRatingDTO.getRatingId(), result.getRatingId());
        assertEquals(testRatingDTO.getScore(), result.getScore());
        assertEquals(testRatingDTO.getComments(), result.getComments());

        verify(modelMapper, times(1)).map(testRatingDTO, Rating.class);
        verify(ratingRepository, times(1)).save(any(Rating.class));
        verify(modelMapper, times(1)).map(testRatingEntity, RatingDTO.class);
        verify(ratingRepository, times(1)).findAverageRatingByDriverId("200");
        verify(driverClient, times(1)).updateDriverRating("200", 4.5);
    }

    @Test
    @DisplayName("createRating() should throw InvalidRatingException when score is below minimum")
    void createRating_shouldThrowInvalidRatingException_whenScoreBelowMinimum() {
        testRatingDTO.setScore(0);

        InvalidRatingException exception = assertThrows(
                InvalidRatingException.class,
                () -> ratingService.createRating(testRatingDTO)
        );

        assertEquals("Score must be between 1 and 5.", exception.getMessage());
        verify(modelMapper, never()).map(any(RatingDTO.class), eq(Rating.class));
        verify(ratingRepository, never()).save(any(Rating.class));
        verify(ratingRepository, never()).findAverageRatingByDriverId(anyString());
        verify(driverClient, never()).updateDriverRating(anyString(), anyDouble());
        verify(modelMapper, never()).map(any(Rating.class), eq(RatingDTO.class));
    }

    @Test
    @DisplayName("createRating() should throw InvalidRatingException when score is above maximum")
    void createRating_shouldThrowInvalidRatingException_whenScoreAboveMaximum() {
        testRatingDTO.setScore(6);

        InvalidRatingException exception = assertThrows(
                InvalidRatingException.class,
                () -> ratingService.createRating(testRatingDTO)
        );

        assertEquals("Score must be between 1 and 5.", exception.getMessage());
        verify(modelMapper, never()).map(any(RatingDTO.class), eq(Rating.class));
        verify(ratingRepository, never()).save(any(Rating.class));
        verify(ratingRepository, never()).findAverageRatingByDriverId(anyString());
        verify(driverClient, never()).updateDriverRating(anyString(), anyDouble());
        verify(modelMapper, never()).map(any(Rating.class), eq(RatingDTO.class));
    }

    @Test
    @DisplayName("createRating() should throw InvalidRatingException when score is null")
    void createRating_shouldThrowInvalidRatingException_whenScoreIsNull() {
        testRatingDTO.setScore(null);

        InvalidRatingException exception = assertThrows(
                InvalidRatingException.class,
                () -> ratingService.createRating(testRatingDTO)
        );

        assertEquals("Score must be between 1 and 5.", exception.getMessage());
        verify(modelMapper, never()).map(any(RatingDTO.class), eq(Rating.class));
        verify(ratingRepository, never()).save(any(Rating.class));
        verify(ratingRepository, never()).findAverageRatingByDriverId(anyString());
        verify(driverClient, never()).updateDriverRating(anyString(), anyDouble());
        verify(modelMapper, never()).map(any(Rating.class), eq(RatingDTO.class));
    }

    @Test
    @DisplayName("createRating() should accept all valid scores from 1 to 5")
    void createRating_shouldAcceptAllValidScores_fromOneToFive() {
        when(modelMapper.map(any(RatingDTO.class), eq(Rating.class))).thenReturn(testRatingEntity);
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRatingEntity);
        when(modelMapper.map(any(Rating.class), eq(RatingDTO.class))).thenReturn(testRatingDTO);
        when(ratingRepository.findAverageRatingByDriverId(anyString())).thenReturn(4.0);
        when(driverClient.updateDriverRating(anyString(), anyDouble()))
                .thenReturn(ResponseEntity.ok(new DriverDto()));

        for (int score = 1; score <= 5; score++) {
            testRatingDTO.setScore(score);

            RatingDTO result = ratingService.createRating(testRatingDTO);

            assertNotNull(result);
        }

        verify(ratingRepository, times(5)).save(any(Rating.class));
    }

    @Test
    @DisplayName("createRating() should handle empty comments successfully")
    void createRating_shouldHandleEmptyComments_successfully() {
        testRatingDTO.setComments("");
        testRatingEntity.setFeedback("");

        when(modelMapper.map(testRatingDTO, Rating.class)).thenReturn(testRatingEntity);
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRatingEntity);
        when(modelMapper.map(testRatingEntity, RatingDTO.class)).thenReturn(testRatingDTO);
        when(ratingRepository.findAverageRatingByDriverId("200")).thenReturn(4.5);
        when(driverClient.updateDriverRating(anyString(), anyDouble()))
                .thenReturn(ResponseEntity.ok(new DriverDto()));

        RatingDTO result = ratingService.createRating(testRatingDTO);

        assertNotNull(result);
        assertEquals("", result.getComments());
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    @DisplayName("createRating() should handle driver service failure gracefully")
    void createRating_shouldHandleDriverServiceFailure_gracefully() {
        when(modelMapper.map(testRatingDTO, Rating.class)).thenReturn(testRatingEntity);
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRatingEntity);
        when(modelMapper.map(testRatingEntity, RatingDTO.class)).thenReturn(testRatingDTO);
        when(ratingRepository.findAverageRatingByDriverId("200")).thenReturn(4.5);
        when(driverClient.updateDriverRating(anyString(), anyDouble()))
                .thenThrow(new RuntimeException("Driver service unavailable"));

        RatingDTO result = ratingService.createRating(testRatingDTO);

        assertNotNull(result);
        assertEquals(testRatingDTO.getRatingId(), result.getRatingId());

        verify(ratingRepository, times(1)).save(any(Rating.class));
        verify(driverClient, times(1)).updateDriverRating("200", 4.5);
    }

    @Test
    @DisplayName("createRating() should throw RuntimeException when repository save fails")
    void createRating_shouldThrowRuntimeException_whenRepositorySaveFails() {
        when(modelMapper.map(testRatingDTO, Rating.class)).thenReturn(testRatingEntity);
        when(ratingRepository.save(any(Rating.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> ratingService.createRating(testRatingDTO)
        );

        assertEquals("An error occurred while saving the rating.", exception.getMessage());
        verify(ratingRepository, times(1)).save(any(Rating.class));
        verify(driverClient, never()).updateDriverRating(anyString(), anyDouble());
    }

    @Test
    @DisplayName("getAverageRatingForDriver() should return average rating when ratings exist")
    void getAverageRatingForDriver_shouldReturnAverageRating_whenRatingsExist() {
        String driverId = "200";
        Double expectedAverage = 4.5;
        when(ratingRepository.findAverageRatingByDriverId(driverId)).thenReturn(expectedAverage);

        Double result = ratingService.getAverageRatingForDriver(driverId);

        assertEquals(expectedAverage, result);
        verify(ratingRepository, times(1)).findAverageRatingByDriverId(driverId);
    }

    @Test
    @DisplayName("getAverageRatingForDriver() should return 0.0 when no ratings exist")
    void getAverageRatingForDriver_shouldReturnZero_whenNoRatingsExist() {
        String driverId = "200";
        when(ratingRepository.findAverageRatingByDriverId(driverId)).thenReturn(null);

        Double result = ratingService.getAverageRatingForDriver(driverId);

        assertEquals(0.0, result);
        verify(ratingRepository, times(1)).findAverageRatingByDriverId(driverId);
    }

    @Test
    @DisplayName("getAverageRatingForDriver() should handle different driver IDs correctly")
    void getAverageRatingForDriver_shouldHandleDifferentDriverIds_correctly() {
        String[] driverIds = {"100", "200", "300", "999"};
        Double[] expectedAverages = {4.0, 4.5, 3.8, null};

        for (int i = 0; i < driverIds.length; i++) {
            when(ratingRepository.findAverageRatingByDriverId(driverIds[i]))
                    .thenReturn(expectedAverages[i]);
        }

        for (int i = 0; i < driverIds.length; i++) {
            Double result = ratingService.getAverageRatingForDriver(driverIds[i]);

            if (expectedAverages[i] != null) {
                assertEquals(expectedAverages[i], result);
            } else {
                assertEquals(0.0, result);
            }

            verify(ratingRepository, times(1)).findAverageRatingByDriverId(driverIds[i]);
        }
    }

    @Test
    @DisplayName("getRatingById() should return RatingDTO when rating exists")
    void getRatingById_shouldReturnRatingDTO_whenRatingExists() {
        String rideId = "100";
        when(ratingRepository.findByRideId(rideId)).thenReturn(Optional.of(testRatingEntity));
        when(modelMapper.map(testRatingEntity, RatingDTO.class)).thenReturn(testRatingDTO);

        Optional<RatingDTO> result = ratingService.getRatingById(rideId);

        assertTrue(result.isPresent());
        assertEquals(testRatingDTO.getRatingId(), result.get().getRatingId());
        assertEquals(testRatingDTO.getScore(), result.get().getScore());

        verify(ratingRepository, times(1)).findByRideId(rideId);
        verify(modelMapper, times(1)).map(testRatingEntity, RatingDTO.class);
    }

    @Test
    @DisplayName("getRatingById() should return empty Optional when rating not found")
    void getRatingById_shouldReturnEmptyOptional_whenRatingNotFound() {
        String rideId = "999";
        when(ratingRepository.findByRideId(rideId)).thenReturn(Optional.empty());

        Optional<RatingDTO> result = ratingService.getRatingById(rideId);

        assertFalse(result.isPresent());
        verify(ratingRepository, times(1)).findByRideId(rideId);
        verify(modelMapper, never()).map(any(), eq(RatingDTO.class));
    }
}

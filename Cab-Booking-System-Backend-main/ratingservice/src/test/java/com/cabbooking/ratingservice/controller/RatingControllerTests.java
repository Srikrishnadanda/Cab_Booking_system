package com.cabbooking.ratingservice.controller;

import com.cabbooking.ratingservice.dto.RatingDTO;
import com.cabbooking.ratingservice.exception.InvalidRatingException;
import com.cabbooking.ratingservice.service.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(RatingController.class)
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class RatingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RatingService ratingService;

    @Autowired
    private ObjectMapper objectMapper;

    private RatingDTO testRatingDTO;

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
    }

    @Test
    @DisplayName("POST /api/ratings should create rating and return 200 OK")
    void createRating_shouldReturnOkAndRatingDto() throws Exception {
        when(ratingService.createRating(any(RatingDTO.class))).thenReturn(testRatingDTO);

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRatingDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ratingId").value(1))
                .andExpect(jsonPath("$.rideId").value("100"))
                .andExpect(jsonPath("$.driverId").value("200"))
                .andExpect(jsonPath("$.userId").value("300"))
                .andExpect(jsonPath("$.score").value(5))
                .andExpect(jsonPath("$.comments").value("Excellent service!"));
    }

    @Test
    @DisplayName("POST /api/ratings should handle InvalidRatingException and return 400")
    void createRating_shouldReturnBadRequest_whenInvalidRatingException() throws Exception {
        RatingDTO invalidRatingDTO = new RatingDTO();
        invalidRatingDTO.setRideId("101");
        invalidRatingDTO.setDriverId("201");
        invalidRatingDTO.setUserId("301");
        invalidRatingDTO.setScore(0);
        invalidRatingDTO.setComments("Invalid rating");

        when(ratingService.createRating(any(RatingDTO.class)))
                .thenThrow(new InvalidRatingException("Score must be between 1 and 5."));

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRatingDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/ratings/ride/{rideId} should return 200 OK and rating when found")
    void getRatingByRideId_shouldReturnOkAndRating_whenFound() throws Exception {
        String rideId = "100";
        when(ratingService.getRatingById(rideId)).thenReturn(Optional.of(testRatingDTO));

        mockMvc.perform(get("/api/ratings/ride/{rideId}", rideId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ratingId").value(1))
                .andExpect(jsonPath("$.rideId").value("100"))
                .andExpect(jsonPath("$.driverId").value("200"))
                .andExpect(jsonPath("$.score").value(5))
                .andExpect(jsonPath("$.comments").value("Excellent service!"));
    }

    @Test
    @DisplayName("GET /api/ratings/ride/{rideId} should return 404 Not Found when rating not found")
    void getRatingByRideId_shouldReturnNotFound_whenNotFound() throws Exception {
        String rideId = "999";
        when(ratingService.getRatingById(rideId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ratings/ride/{rideId}", rideId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/ratings/driver/{driverId} should return 200 OK and average rating")
    void getAverageRatingForDriver_shouldReturnOkAndAverageRating() throws Exception {
        String driverId = "200";
        Double expectedAverage = 4.5;
        when(ratingService.getAverageRatingForDriver(driverId)).thenReturn(expectedAverage);

        mockMvc.perform(get("/api/ratings/driver/{driverId}", driverId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(4.5));
    }

    @Test
    @DisplayName("GET /api/ratings/driver/{driverId} should return 200 OK and 0.0 when no ratings exist")
    void getAverageRatingForDriver_shouldReturnOkAndZero_whenNoRatingsExist() throws Exception {
        String driverId = "300";
        when(ratingService.getAverageRatingForDriver(driverId)).thenReturn(0.0);

        mockMvc.perform(get("/api/ratings/driver/{driverId}", driverId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(0.0));
    }

    @Test
    @DisplayName("POST /api/ratings should handle malformed JSON gracefully")
    void createRating_shouldHandleMalformedJson_gracefully() throws Exception {
        String malformedJson = "{ \"ratingId\": 1, \"rideId\": \"100\", \"score\": }";

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/ratings/ride/{rideId} should handle various rideId formats")
    void getRatingByRideId_shouldHandleVariousRideIdFormats() throws Exception {
        String[] rideIds = {"123", "ride-456", "R789"};

        for (String rideId : rideIds) {
            when(ratingService.getRatingById(rideId)).thenReturn(Optional.of(testRatingDTO));

            mockMvc.perform(get("/api/ratings/ride/{rideId}", rideId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Test
    @DisplayName("GET /api/ratings/driver/{driverId} should handle various driverId formats")
    void getAverageRatingForDriver_shouldHandleVariousDriverIdFormats() throws Exception {
        String[] driverIds = {"123", "driver-456", "D789"};

        for (String driverId : driverIds) {
            when(ratingService.getAverageRatingForDriver(driverId)).thenReturn(4.2);

            mockMvc.perform(get("/api/ratings/driver/{driverId}", driverId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").value(4.2));
        }
    }

    @Test
    @DisplayName("POST /api/ratings should handle different score values")
    void createRating_shouldHandleDifferentScoreValues() throws Exception {
        // Use a more flexible mocking approach - mock based on any RatingDTO
        // and return the appropriate response based on the score
        when(ratingService.createRating(any(RatingDTO.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // Test each score value
        for (int score = 1; score <= 5; score++) {
            RatingDTO ratingWithScore = createRatingDTOWithScore(score);

            mockMvc.perform(post("/api/ratings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ratingWithScore)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.score").value(score))
                    .andExpect(jsonPath("$.ratingId").value(score))
                    .andExpect(jsonPath("$.rideId").value("ride-" + score));
        }
    }

    private RatingDTO createRatingDTOWithScore(int score) {
        RatingDTO rating = new RatingDTO();
        rating.setRatingId(score);
        rating.setRideId("ride-" + score);
        rating.setDriverId("driver-" + score);
        rating.setUserId("user-" + score);
        rating.setScore(score);
        rating.setComments("Rating with score " + score);
        return rating;
    }

    @Test
    @DisplayName("POST /api/ratings should handle empty request body")
    void createRating_shouldHandleEmptyRequestBody() throws Exception {
        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk()); // Controller doesn't validate, service layer handles
    }

    @Test
    @DisplayName("GET endpoints should handle URL encoding")
    void endpoints_shouldHandleUrlEncoding() throws Exception {


        // Mock with the decoded values that Spring will pass to the service
        when(ratingService.getRatingById("ride 123")).thenReturn(Optional.of(testRatingDTO));
        when(ratingService.getAverageRatingForDriver("driver 456")).thenReturn(4.0);

        // Test the actual endpoints
        mockMvc.perform(get("/api/ratings/ride/{rideId}", "ride 123"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/ratings/driver/{driverId}", "driver 456"))
                .andExpect(status().isOk());
    }
}

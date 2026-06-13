package com.cabbooking.ratingservice.serviceimpl;

import com.cabbooking.ratingservice.client.DriverClient;
import com.cabbooking.ratingservice.dto.RatingDTO;
import com.cabbooking.ratingservice.entity.Rating;
import com.cabbooking.ratingservice.repository.RatingRepository;
import com.cabbooking.ratingservice.service.RatingService;
import com.cabbooking.ratingservice.exception.InvalidRatingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final ModelMapper modelMapper;
    private final DriverClient driverClient;

    @Override
    public RatingDTO createRating(RatingDTO ratingDTO) {
        log.info("Attempting to create a new rating for rideId: {}", ratingDTO.getRideId());

        if (ratingDTO.getScore() == null || ratingDTO.getScore() < 1 || ratingDTO.getScore() > 5) {
            log.error("Invalid score provided. Score must be between 1 and 5.");
            throw new InvalidRatingException("Score must be between 1 and 5.");
        }

        Rating rating = modelMapper.map(ratingDTO, Rating.class);
        log.debug("Mapped DTO to entity: {}", rating);

        Rating savedRating;
        try {
            savedRating = ratingRepository.save(rating);
            log.info("Rating successfully saved with ID: {}", savedRating.getRatingId());


            Double r = getAverageRatingForDriver(ratingDTO.getDriverId());

            updateDriverAverageRating(r, ratingDTO.getDriverId());

            log.info("Successfully updated average rating for driver: {}", savedRating.getDriverId());



        } catch (Exception e) {
            log.error("Failed to save rating to the database.", e);
            throw new InvalidRatingException("An error occurred while saving the rating.");
        }

        return modelMapper.map(savedRating, RatingDTO.class);
    }

    private void updateDriverAverageRating(Double rating, String driverId) {
        try {
            log.info("Updating average rating for driver: {}", driverId);


            driverClient.updateDriverRating(driverId, rating);

            log.info("Successfully updated driver {} with new average rating: {}", driverId, rating);

        } catch (Exception e) {
            log.error("Failed to update driver average rating for driver {}: {}", driverId, e.getMessage());
        }
    }

    @Override
    public Double getAverageRatingForDriver(String driverId) {
        Double averageRating = ratingRepository.findAverageRatingByDriverId(driverId);
        log.info("Calculated average rating for driverId {}: {}", driverId, averageRating);
        return averageRating != null ? averageRating : 0.0;
    }

    @Override
    public Optional<RatingDTO> getRatingById(String rideId) {
        log.info("Attempting to retrieve rating for rideId: {}", rideId);
        Optional<Rating> ratingOptional = ratingRepository.findByRideId(rideId);

        if (ratingOptional.isPresent()) {
            Rating rating = ratingOptional.get();
            log.info("Retrieved rating for rideId {}: {}", rideId, rating);
            return Optional.of(modelMapper.map(rating, RatingDTO.class));
        } else {
            log.warn("No rating found for rideId: {}", rideId);
            return Optional.empty();
        }
    }

}
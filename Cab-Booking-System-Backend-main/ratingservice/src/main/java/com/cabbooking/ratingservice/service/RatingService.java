package com.cabbooking.ratingservice.service;

import com.cabbooking.ratingservice.dto.RatingDTO;

import java.util.Optional;


public interface RatingService {
     RatingDTO createRating(RatingDTO ratingDTO);
     Double getAverageRatingForDriver(String driverId);
     Optional<RatingDTO> getRatingById(String rideId);
}

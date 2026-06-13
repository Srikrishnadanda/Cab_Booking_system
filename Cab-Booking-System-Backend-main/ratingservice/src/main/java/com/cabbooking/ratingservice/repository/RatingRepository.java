package com.cabbooking.ratingservice.repository;

import com.cabbooking.ratingservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.driverId = :driverId")
    Double findAverageRatingByDriverId(@Param("driverId") String driverId);

    Optional<Rating> findByRideId(String rideId);
}

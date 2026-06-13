package com.cabbooking.ratingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    private Integer ratingId;
    private String rideId;
    private String userId;
    private String driverId;
    private Integer score;
    private String comments;
}

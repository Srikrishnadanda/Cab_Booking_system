package com.cabbooking.ratingservice.client;

import com.cabbooking.ratingservice.dto.DriverDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "DRIVER-SERVICE"
)
public interface DriverClient {

    @PutMapping(value="/api/drivers/{id}/rating",params = "rating")
    public ResponseEntity<DriverDto> updateDriverRating(
            @PathVariable("id") String id,
            @RequestParam("rating") double rating);
}

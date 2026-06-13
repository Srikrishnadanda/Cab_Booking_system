package com.cabbooking.locationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private Integer locationId;

    private String area;

    private String location;

    private BigDecimal latitude;

    private BigDecimal longitude;

}

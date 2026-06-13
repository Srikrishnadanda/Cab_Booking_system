package com.cabbooking.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    private String paymentId;
    private String rideId;
    private String userId;
    private BigDecimal amount;
    private String method;
    private String status;
}

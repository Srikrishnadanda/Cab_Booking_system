package com.cabbooking.paymentservice.exception;

public class PaymentFailedException extends RuntimeException{

    public PaymentFailedException(String message) {
        super(message);
    }
}

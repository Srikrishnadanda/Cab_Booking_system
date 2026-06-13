package com.cabbooking.paymentservice.service;

import com.cabbooking.paymentservice.dto.PaymentDto;

public interface PaymentService {

    PaymentDto createPayment(PaymentDto paymentDto);

    PaymentDto getPaymentById(String paymentId);

    PaymentDto updatePaymentStatus(String paymentId, String status);

    byte[] generateReceiptPdf(PaymentDto paymentDto);
}

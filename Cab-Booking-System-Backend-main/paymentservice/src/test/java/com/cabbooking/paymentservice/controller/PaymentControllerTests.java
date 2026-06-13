package com.cabbooking.paymentservice.controller;

import com.cabbooking.paymentservice.dto.PaymentDto;
import com.cabbooking.paymentservice.exception.PaymentFailedException;
import com.cabbooking.paymentservice.exception.PaymentNotFoundException;
import com.cabbooking.paymentservice.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(PaymentController.class)
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class PaymentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentDto testPaymentDto;

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
        testPaymentDto = new PaymentDto();
        testPaymentDto.setPaymentId("pay-123");
        testPaymentDto.setRideId("ride-456");
        testPaymentDto.setUserId("user-789");
        testPaymentDto.setAmount(new BigDecimal("25.50"));
        testPaymentDto.setMethod("CREDIT_CARD");
        testPaymentDto.setStatus("COMPLETED");
    }

    @Test
    @DisplayName("POST /api/payments should create payment and return CREATED")
    void createPayment_ReturnsCreatedAndPaymentDto() throws Exception {

        when(paymentService.createPayment(any(PaymentDto.class))).thenReturn(testPaymentDto);


        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPaymentDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Payment created successfully"))
                .andExpect(jsonPath("$.data.paymentId").value("pay-123"))
                .andExpect(jsonPath("$.data.amount").value(25.50))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        verify(paymentService, times(1)).createPayment(any(PaymentDto.class));
    }

    @Test
    @DisplayName("POST /api/payments should handle payment creation failure")
    void createPayment_HandlesBadRequestWhenCreationFails() throws Exception {

        when(paymentService.createPayment(any(PaymentDto.class)))
                .thenThrow(new PaymentFailedException("Payment with status 'failed' cannot be processed."));


        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPaymentDto)))
                .andExpect(status().isBadRequest());

        verify(paymentService, times(1)).createPayment(any(PaymentDto.class));
    }

    @Test
    @DisplayName("GET /api/payments/{paymentId} should return payment and OK")
    void getPaymentById_ReturnsOkAndPaymentDto() throws Exception {

        String paymentId = "pay-123";
        when(paymentService.getPaymentById(paymentId)).thenReturn(testPaymentDto);


        mockMvc.perform(get("/api/payments/{paymentId}", paymentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Payment retrieved successfully"))
                .andExpect(jsonPath("$.data.paymentId").value("pay-123"))
                .andExpect(jsonPath("$.data.amount").value(25.50))
                .andExpect(jsonPath("$.data.method").value("CREDIT_CARD"));

        verify(paymentService, times(1)).getPaymentById(paymentId);
    }

    @Test
    @DisplayName("GET /api/payments/{paymentId} should handle payment not found")
    void getPaymentById_ReturnsNotFoundWhenPaymentDoesNotExist() throws Exception {

        String paymentId = "nonexistent-payment";
        when(paymentService.getPaymentById(paymentId))
                .thenThrow(new PaymentNotFoundException("Payment with ID " + paymentId + " not found."));


        mockMvc.perform(get("/api/payments/{paymentId}", paymentId))
                .andExpect(status().isNotFound());

        verify(paymentService, times(1)).getPaymentById(paymentId);
    }

    @Test
    @DisplayName("GET /api/payments/receipt/{paymentId} should generate PDF receipt")
    void generateReceipt_ReturnsOkAndPdfReceipt() throws Exception {

        String paymentId = "pay-123";
        byte[] pdfBytes = "Sample PDF content".getBytes();

        when(paymentService.getPaymentById(paymentId)).thenReturn(testPaymentDto);
        when(paymentService.generateReceiptPdf(testPaymentDto)).thenReturn(pdfBytes);


        mockMvc.perform(get("/api/payments/receipt/{paymentId}", paymentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=receipt_" + paymentId + ".pdf"))
                .andExpect(content().bytes(pdfBytes));

        verify(paymentService, times(1)).getPaymentById(paymentId);
        verify(paymentService, times(1)).generateReceiptPdf(testPaymentDto);
    }

    @Test
    @DisplayName("GET /api/payments/receipt/{paymentId} should handle PDF generation failure")
    void generateReceipt_ReturnsInternalServerErrorWhenPdfGenerationFails() throws Exception {

        String paymentId = "pay-123";
        when(paymentService.getPaymentById(paymentId)).thenReturn(testPaymentDto);
        when(paymentService.generateReceiptPdf(testPaymentDto))
                .thenThrow(new RuntimeException("PDF generation failed"));


        mockMvc.perform(get("/api/payments/receipt/{paymentId}", paymentId))
                .andExpect(status().isInternalServerError());

        verify(paymentService, times(1)).getPaymentById(paymentId);
        verify(paymentService, times(1)).generateReceiptPdf(testPaymentDto);
    }

    @Test
    @DisplayName("PATCH /api/payments/{rideId}/status should update payment status")
    void updatePaymentStatus_ReturnsOkAndUpdatedPayment() throws Exception {

        String rideId = "ride-456";
        String newStatus = "REFUNDED";
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", newStatus);

        PaymentDto updatedPayment = new PaymentDto();
        updatedPayment.setPaymentId("pay-123");
        updatedPayment.setRideId(rideId);
        updatedPayment.setStatus(newStatus);
        updatedPayment.setAmount(testPaymentDto.getAmount());

        when(paymentService.updatePaymentStatus(rideId, newStatus)).thenReturn(updatedPayment);


        mockMvc.perform(patch("/api/payments/{rideId}/status", rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Payment status updated successfully to: " + newStatus))
                .andExpect(jsonPath("$.data.paymentId").value("pay-123"))
                .andExpect(jsonPath("$.data.status").value(newStatus));

        verify(paymentService, times(1)).updatePaymentStatus(rideId, newStatus);
    }

    @Test
    @DisplayName("PATCH /api/payments/{rideId}/status should reject empty status")
    void updatePaymentStatus_ReturnsBadRequestWhenStatusIsEmpty() throws Exception {

        String rideId = "ride-456";
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "");


        mockMvc.perform(patch("/api/payments/{rideId}/status", rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Status is required"));

        verify(paymentService, never()).updatePaymentStatus(anyString(), anyString());
    }

    @Test
    @DisplayName("PATCH /api/payments/{rideId}/status should handle invalid status")
    void updatePaymentStatus_ReturnsBadRequestWhenStatusIsInvalid() throws Exception {
        String rideId = "ride-456";
        String invalidStatus = "INVALID_STATUS";
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", invalidStatus);

        when(paymentService.updatePaymentStatus(rideId, invalidStatus))
                .thenThrow(new IllegalArgumentException("Invalid payment status: " + invalidStatus));


        mockMvc.perform(patch("/api/payments/{rideId}/status", rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid payment status: " + invalidStatus));

        verify(paymentService, times(1)).updatePaymentStatus(rideId, invalidStatus);
    }
}

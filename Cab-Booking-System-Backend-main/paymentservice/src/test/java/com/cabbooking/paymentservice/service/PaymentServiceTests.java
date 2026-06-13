package com.cabbooking.paymentservice.service;

import com.cabbooking.paymentservice.dto.PaymentDto;
import com.cabbooking.paymentservice.entity.Payment;
import com.cabbooking.paymentservice.exception.PaymentFailedException;
import com.cabbooking.paymentservice.exception.PaymentNotFoundException;
import com.cabbooking.paymentservice.repository.PaymentRepository;
import com.cabbooking.paymentservice.serviceimpl.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class PaymentServiceTests {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentDto testPaymentDto;
    private Payment testPaymentEntity;

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

        testPaymentEntity = new Payment();
        testPaymentEntity.setPaymentId("pay-123");
        testPaymentEntity.setRideId("ride-456");
        testPaymentEntity.setUserId("user-789");
        testPaymentEntity.setAmount(new BigDecimal("25.50"));
        testPaymentEntity.setMethod("CREDIT_CARD");
        testPaymentEntity.setStatus("COMPLETED");
    }

    @Test
    @DisplayName("createPayment() should return PaymentDto when valid payment provided")
    void createPayment_shouldReturnPaymentDto_whenValidPaymentProvided() {

        when(modelMapper.map(testPaymentDto, Payment.class)).thenReturn(testPaymentEntity);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPaymentEntity);
        when(modelMapper.map(testPaymentEntity, PaymentDto.class)).thenReturn(testPaymentDto);


        PaymentDto result = paymentService.createPayment(testPaymentDto);


        assertNotNull(result);
        assertEquals(testPaymentDto.getPaymentId(), result.getPaymentId());
        assertEquals(testPaymentDto.getAmount(), result.getAmount());
        assertEquals(testPaymentDto.getStatus(), result.getStatus());

        verify(modelMapper, times(1)).map(testPaymentDto, Payment.class);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(modelMapper, times(1)).map(testPaymentEntity, PaymentDto.class);
    }

    @Test
    @DisplayName("createPayment() should throw PaymentFailedException when status is failed")
    void createPayment_shouldThrowPaymentFailedException_whenStatusIsFailed() {

        testPaymentDto.setStatus("failed");


        PaymentFailedException exception = assertThrows(
                PaymentFailedException.class,
                () -> paymentService.createPayment(testPaymentDto)
        );

        assertEquals("Payment with status 'failed' cannot be processed.", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("createPayment() should handle case insensitive failed status")
    void createPayment_shouldHandleCaseInsensitiveFailedStatus() {

        testPaymentDto.setStatus("FAILED");


        PaymentFailedException exception = assertThrows(
                PaymentFailedException.class,
                () -> paymentService.createPayment(testPaymentDto)
        );

        assertEquals("Payment with status 'failed' cannot be processed.", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("createPayment() should accept all valid payment statuses")
    void createPayment_shouldAcceptAllValidPaymentStatuses() {

        String[] validStatuses = {"PENDING", "COMPLETED", "PROCESSING", "SUCCESS"};

        when(modelMapper.map(any(PaymentDto.class), eq(Payment.class))).thenReturn(testPaymentEntity);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPaymentEntity);
        when(modelMapper.map(any(Payment.class), eq(PaymentDto.class))).thenReturn(testPaymentDto);


        for (String status : validStatuses) {
            testPaymentDto.setStatus(status);

            PaymentDto result = paymentService.createPayment(testPaymentDto);

            assertNotNull(result);
            assertEquals(status, testPaymentDto.getStatus());
        }

        verify(paymentRepository, times(validStatuses.length)).save(any(Payment.class));
    }

    @Test
    @DisplayName("createPayment() should handle different payment methods")
    void createPayment_shouldHandleDifferentPaymentMethods() {

        String[] paymentMethods = {"CREDIT_CARD", "DEBIT_CARD", "UPI", "WALLET", "CASH"};

        when(modelMapper.map(any(PaymentDto.class), eq(Payment.class))).thenReturn(testPaymentEntity);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPaymentEntity);
        when(modelMapper.map(any(Payment.class), eq(PaymentDto.class))).thenReturn(testPaymentDto);


        for (String method : paymentMethods) {
            testPaymentDto.setMethod(method);

            PaymentDto result = paymentService.createPayment(testPaymentDto);

            assertNotNull(result);
            assertEquals(method, testPaymentDto.getMethod());
        }

        verify(paymentRepository, times(paymentMethods.length)).save(any(Payment.class));
    }

    @Test
    @DisplayName("getPaymentById() should return PaymentDto when payment exists")
    void getPaymentById_shouldReturnPaymentDto_whenPaymentExists() {

        String paymentId = "pay-123";
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(testPaymentEntity));
        when(modelMapper.map(testPaymentEntity, PaymentDto.class)).thenReturn(testPaymentDto);


        PaymentDto result = paymentService.getPaymentById(paymentId);


        assertNotNull(result);
        assertEquals(testPaymentDto.getPaymentId(), result.getPaymentId());
        assertEquals(testPaymentDto.getAmount(), result.getAmount());
        assertEquals(testPaymentDto.getStatus(), result.getStatus());

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(modelMapper, times(1)).map(testPaymentEntity, PaymentDto.class);
    }

    @Test
    @DisplayName("getPaymentById() should throw PaymentNotFoundException when payment not found")
    void getPaymentById_shouldThrowPaymentNotFoundException_whenPaymentNotFound() {

        String paymentId = "nonexistent-payment";
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());


        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getPaymentById(paymentId)
        );

        assertEquals("Payment with ID " + paymentId + " not found.", exception.getMessage());
        verify(paymentRepository, times(1)).findById(paymentId);
        verify(modelMapper, never()).map(any(), eq(PaymentDto.class));
    }

    @Test
    @DisplayName("generateReceiptPdf() should generate PDF bytes for valid payment")
    void generateReceiptPdf_shouldGeneratePdfBytes_forValidPayment() {

        byte[] result = paymentService.generateReceiptPdf(testPaymentDto);


        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("generateReceiptPdf() should handle null payment gracefully")
    void generateReceiptPdf_shouldHandleNullPayment_gracefully() {

        PaymentDto nullPayment = null;


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.generateReceiptPdf(nullPayment)
        );

        assertEquals("Payment details cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("updatePaymentStatus() should update status when payment exists")
    void updatePaymentStatus_shouldUpdateStatus_whenPaymentExists() {

        String rideId = "ride-456"; // Use rideId instead of paymentId
        String newStatus = "REFUNDED";
        Payment updatedPayment = new Payment();
        updatedPayment.setPaymentId("pay-123");
        updatedPayment.setRideId(rideId);
        updatedPayment.setStatus(newStatus);

        PaymentDto updatedPaymentDto = new PaymentDto();
        updatedPaymentDto.setPaymentId("pay-123");
        updatedPaymentDto.setRideId(rideId);
        updatedPaymentDto.setStatus(newStatus);

        when(paymentRepository.findByRideId(rideId)).thenReturn(Optional.of(testPaymentEntity)); // Mock findByRideId instead of findById
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);
        when(modelMapper.map(updatedPayment, PaymentDto.class)).thenReturn(updatedPaymentDto);


        PaymentDto result = paymentService.updatePaymentStatus(rideId, newStatus); // Pass rideId instead of paymentId


        assertNotNull(result);
        assertEquals("pay-123", result.getPaymentId());
        assertEquals(rideId, result.getRideId());
        assertEquals(newStatus, result.getStatus());

        verify(paymentRepository, times(1)).findByRideId(rideId); // Verify findByRideId was called
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(modelMapper, times(1)).map(updatedPayment, PaymentDto.class);
    }
}

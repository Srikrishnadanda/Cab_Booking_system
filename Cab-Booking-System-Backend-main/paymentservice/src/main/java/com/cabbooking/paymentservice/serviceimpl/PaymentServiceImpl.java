package com.cabbooking.paymentservice.serviceimpl;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

import com.cabbooking.paymentservice.exception.PaymentFailedException;
import com.cabbooking.paymentservice.exception.PaymentNotFoundException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cabbooking.paymentservice.dto.PaymentDto;
import com.cabbooking.paymentservice.entity.Payment;
import com.cabbooking.paymentservice.repository.PaymentRepository;
import com.cabbooking.paymentservice.service.PaymentService;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;

	private final ModelMapper modelMapper;

	public PaymentServiceImpl(PaymentRepository paymentRepository, ModelMapper modelMapper) {
		this.paymentRepository = paymentRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public PaymentDto createPayment(PaymentDto paymentDto) {
		log.info("Attempting to create payment for user: {}", paymentDto.getUserId());

		if ("failed".equalsIgnoreCase(paymentDto.getStatus())) {
			log.error("Payment creation failed: status is 'failed'. User: {}", paymentDto.getUserId());
			throw new PaymentFailedException("Payment with status 'failed' cannot be processed.");
		}

		Payment payment = modelMapper.map(paymentDto, Payment.class);
		log.debug("Mapped PaymentDto to Payment entity for processing.");

		try {
			Payment savedPayment = paymentRepository.save(payment);
			log.info("Successfully created payment with ID: {}", savedPayment.getPaymentId());
			return modelMapper.map(savedPayment, PaymentDto.class);
		} catch (Exception e) {
			log.error("An error occurred while saving the payment to the repository.", e);
			throw new RuntimeException("Payment saving failed unexpectedly.", e);
		}
	}

	@Override
	public PaymentDto getPaymentById(String paymentId) {
		log.info("Fetching payment with ID: {}", paymentId);
		Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

		if (paymentOptional.isPresent()) {
			log.info("Found payment with ID: {}", paymentId);
			return modelMapper.map(paymentOptional.get(), PaymentDto.class);
		} else {
			log.warn("Payment with ID {} not found.", paymentId);
			throw new PaymentNotFoundException("Payment with ID " + paymentId + " not found.");
		}
	}


	@Override
	public byte[] generateReceiptPdf(PaymentDto paymentDto) {
		if (paymentDto == null) {
			throw new IllegalArgumentException("Payment details cannot be null");
		}

		log.info("Generating invoice for payment Id: {}", paymentDto.getPaymentId());
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			Document document = new Document();
			PdfWriter.getInstance(document, baos);
			document.open();

			// Invoice Title
			Paragraph title = new Paragraph("Payment Invoice");
			title.setAlignment(Paragraph.ALIGN_CENTER);
			document.add(title);
			document.add(new Paragraph(" "));

			// Invoice Table
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(80);
			table.setSpacingBefore(10f);
			table.setSpacingAfter(10f);

			table.addCell("Payment Id");
			table.addCell(String.valueOf(paymentDto.getPaymentId()));

			table.addCell("Ride Id");
			table.addCell(String.valueOf(paymentDto.getRideId()));

			table.addCell("Amount");
			table.addCell(String.valueOf(paymentDto.getAmount()));

			table.addCell("Status");
			table.addCell(paymentDto.getStatus());

			document.add(table);
			document.add(new Paragraph("Thank you for your payment!",
					new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD)));

			document.close();
			log.info("Invoice for payment Id {} generated successfully.", paymentDto.getPaymentId());
			return baos.toByteArray();
		} catch (Exception e) {
			log.error("An error occurred during PDF generation for payment Id {}.", paymentDto.getPaymentId());
			throw new RuntimeException("Failed to generate PDF INVOICE.", e);
		}
	}



	@Override
	public PaymentDto updatePaymentStatus(String rideId, String status) {
		log.info("Attempting to update payment status for ID: {} to status: {}", rideId, status);

		if (status == null || status.trim().isEmpty()) {
			log.error("Status cannot be null or empty for payment ID: {}", rideId);
			throw new IllegalArgumentException("Payment status cannot be null or empty.");
		}

		Optional<Payment> paymentOptional = paymentRepository.findByRideId(rideId);

		if (paymentOptional.isPresent()) {
			Payment payment = paymentOptional.get();
			String oldStatus = payment.getStatus();

			payment.setStatus(status);

			try {
				Payment updatedPayment = paymentRepository.save(payment);
				log.info("Successfully updated payment ID: {} from status '{}' to '{}'", rideId, oldStatus, status);
				return modelMapper.map(updatedPayment, PaymentDto.class);
			} catch (Exception e) {
				log.error("Failed to save updated payment status for ID: {}", rideId, e);
				throw new RuntimeException("Failed to update payment status.", e);
			}
		} else {
			log.warn("Payment with Ride ID {} not found for status update.", rideId);
			throw new PaymentNotFoundException("Payment with Ride ID " + rideId + " not found.");
		}
	}

}


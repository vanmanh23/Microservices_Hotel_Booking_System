package com.manhnguyen.payment.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.payment.client.BookingServiceClient;
import com.manhnguyen.payment.dto.PaymentDTO;
import com.manhnguyen.payment.dto.PaymentRequest;
import com.manhnguyen.payment.dto.RefundRequest;
import com.manhnguyen.payment.event.PaymentEvent;
import com.manhnguyen.payment.model.Payment;
import com.manhnguyen.payment.model.PaymentStatus;
import com.manhnguyen.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final String TOPIC = "payment-events";

    private final PaymentRepository paymentRepository;
    private final BookingServiceClient bookingServiceClient;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Transactional
    public PaymentDTO processPayment(PaymentRequest request) {
        if (paymentRepository.findByBookingId(request.bookingId()).isPresent()) {
            throw new ApiException("Payment already exists for this booking", HttpStatus.CONFLICT);
        }

        Payment payment = Payment.builder()
                .bookingId(request.bookingId())
                .userId(request.userId())
                .amount(request.amount())
                .paymentMethod(request.paymentMethod() != null ? request.paymentMethod() : "CARD")
                .status(PaymentStatus.PENDING)
                .build();

        payment = paymentRepository.save(payment);

        // Simulate payment gateway processing
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment = paymentRepository.save(payment);

        bookingServiceClient.confirmBooking(request.bookingId());
        publishEvent(payment, "PAYMENT_COMPLETED");
        log.info("Payment completed for booking {}: {}", request.bookingId(), payment.getTransactionId());

        return PaymentDTO.from(payment);
    }

    @Transactional
    public PaymentDTO refund(RefundRequest request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new ApiException("Payment not found", HttpStatus.NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.COMPLETED
                && payment.getStatus() != PaymentStatus.PARTIALLY_REFUNDED) {
            throw new ApiException("Payment cannot be refunded", HttpStatus.BAD_REQUEST);
        }

        BigDecimal remaining = payment.getAmount().subtract(payment.getRefundAmount());
        if (request.amount().compareTo(remaining) > 0) {
            throw new ApiException("Refund amount exceeds remaining balance", HttpStatus.BAD_REQUEST);
        }

        BigDecimal newRefundTotal = payment.getRefundAmount().add(request.amount());
        payment.setRefundAmount(newRefundTotal);
        payment.setStatus(newRefundTotal.compareTo(payment.getAmount()) >= 0
                ? PaymentStatus.REFUNDED
                : PaymentStatus.PARTIALLY_REFUNDED);
        payment = paymentRepository.save(payment);

        publishEvent(payment, "PAYMENT_REFUNDED");
        return PaymentDTO.from(payment);
    }

    public List<PaymentDTO> getPaymentHistory(Long userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(PaymentDTO::from)
                .toList();
    }

    private void publishEvent(Payment payment, String eventType) {
        PaymentEvent event = new PaymentEvent(
                payment.getId(),
                payment.getBookingId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                eventType
        );
        try {
            kafkaTemplate.send(TOPIC, String.valueOf(payment.getId()), event);
        } catch (Exception e) {
            log.warn("Failed to publish payment event: {}", e.getMessage());
        }
    }
}

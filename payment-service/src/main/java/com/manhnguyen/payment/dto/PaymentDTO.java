package com.manhnguyen.payment.dto;

import com.manhnguyen.payment.model.Payment;
import com.manhnguyen.payment.model.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentDTO(
        Long id,
        Long bookingId,
        Long userId,
        BigDecimal amount,
        PaymentStatus status,
        String paymentMethod,
        String transactionId,
        BigDecimal refundAmount,
        LocalDateTime createdAt
) {
    public static PaymentDTO from(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .refundAmount(payment.getRefundAmount())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}

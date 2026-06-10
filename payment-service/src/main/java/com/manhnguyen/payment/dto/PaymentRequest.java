package com.manhnguyen.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull Long bookingId,
        @NotNull Long userId,
        @NotNull @Positive BigDecimal amount,
        String paymentMethod
) {}

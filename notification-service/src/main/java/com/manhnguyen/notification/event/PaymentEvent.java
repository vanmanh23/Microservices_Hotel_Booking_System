package com.manhnguyen.notification.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentEvent {
    private Long paymentId;
    private Long bookingId;
    private Long userId;
    private BigDecimal amount;
    private String status;
    private String eventType;
}

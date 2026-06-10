package com.manhnguyen.notification.consumer;

import com.manhnguyen.notification.event.PaymentEvent;
import com.manhnguyen.notification.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentEventConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PaymentEventConsumer consumer;

    @Test
    void shouldSendPaymentConfirmation_whenPaymentCompletedEvent() {
        PaymentEvent event = new PaymentEvent();
        event.setUserId(7L);
        event.setBookingId(201L);
        event.setAmount(new BigDecimal("120.00"));
        event.setEventType("PAYMENT_COMPLETED");

        consumer.handlePaymentEvent(event);

        verify(emailService).sendPaymentConfirmation("user-7@booking.local", 201L, "120.00");
    }

    @Test
    void shouldSendRefundNotification_whenPaymentRefundedEvent() {
        PaymentEvent event = new PaymentEvent();
        event.setUserId(8L);
        event.setBookingId(202L);
        event.setAmount(new BigDecimal("55.00"));
        event.setEventType("PAYMENT_REFUNDED");

        consumer.handlePaymentEvent(event);

        verify(emailService).sendRefundNotification("user-8@booking.local", 202L, "55.00");
    }

    @Test
    void shouldIgnoreUnknownPaymentEvents() {
        PaymentEvent event = new PaymentEvent();
        event.setUserId(9L);
        event.setBookingId(203L);
        event.setAmount(new BigDecimal("70.00"));
        event.setEventType("PAYMENT_PENDING");

        consumer.handlePaymentEvent(event);

        verify(emailService, never()).sendPaymentConfirmation("user-9@booking.local", 203L, "70.00");
        verify(emailService, never()).sendRefundNotification("user-9@booking.local", 203L, "70.00");
    }
}

package com.manhnguyen.notification.consumer;

import com.manhnguyen.notification.event.PaymentEvent;
import com.manhnguyen.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "payment-events", groupId = "notification-service")
    public void handlePaymentEvent(PaymentEvent event) {
        String email = "user-" + event.getUserId() + "@booking.local";
        switch (event.getEventType()) {
            case "PAYMENT_COMPLETED" ->
                    emailService.sendPaymentConfirmation(email, event.getBookingId(), event.getAmount().toString());
            case "PAYMENT_REFUNDED" ->
                    emailService.sendRefundNotification(email, event.getBookingId(), event.getAmount().toString());
            default -> { }
        }
    }
}

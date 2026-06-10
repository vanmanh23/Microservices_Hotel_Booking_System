package com.manhnguyen.notification.consumer;

import com.manhnguyen.notification.event.BookingEvent;
import com.manhnguyen.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "booking-events", groupId = "notification-service")
    public void handleBookingEvent(BookingEvent event) {
        if (event.getUserEmail() == null) {
            return;
        }
        switch (event.getEventType()) {
            case "BOOKING_CONFIRMED" ->
                    emailService.sendBookingConfirmation(event.getUserEmail(), event.getBookingId());
            case "BOOKING_CANCELLED" ->
                    emailService.sendBookingCancellation(event.getUserEmail(), event.getBookingId());
            default -> { /* BOOKING_CREATED handled after payment */ }
        }
    }
}

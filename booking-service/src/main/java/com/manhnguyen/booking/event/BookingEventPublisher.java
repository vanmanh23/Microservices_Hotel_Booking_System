package com.manhnguyen.booking.event;

import com.manhnguyen.booking.model.Booking;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(BookingEventPublisher.class);
    private static final String TOPIC = "booking-events";

    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    public void publish(Booking booking, String eventType) {
        BookingEvent event = new BookingEvent(
                booking.getId(),
                booking.getUserId(),
                booking.getUserEmail(),
                booking.getHotelId(),
                booking.getRoomId(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getTotalPrice(),
                booking.getStatus(),
                eventType
        );
        try {
            kafkaTemplate.send(TOPIC, String.valueOf(booking.getId()), event);
            log.info("Published booking event: {} for booking {}", eventType, booking.getId());
        } catch (Exception e) {
            log.warn("Failed to publish booking event (Kafka may be unavailable): {}", e.getMessage());
        }
    }
}

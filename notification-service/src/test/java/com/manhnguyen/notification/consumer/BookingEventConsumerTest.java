package com.manhnguyen.notification.consumer;

import com.manhnguyen.notification.event.BookingEvent;
import com.manhnguyen.notification.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingEventConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BookingEventConsumer consumer;

    @Test
    void shouldSendBookingConfirmation_whenBookingConfirmedEvent() {
        BookingEvent event = new BookingEvent();
        event.setUserEmail("user@example.com");
        event.setBookingId(101L);
        event.setEventType("BOOKING_CONFIRMED");

        consumer.handleBookingEvent(event);

        verify(emailService).sendBookingConfirmation("user@example.com", 101L);
    }

    @Test
    void shouldSendBookingCancellation_whenBookingCancelledEvent() {
        BookingEvent event = new BookingEvent();
        event.setUserEmail("cancel@example.com");
        event.setBookingId(102L);
        event.setEventType("BOOKING_CANCELLED");

        consumer.handleBookingEvent(event);

        verify(emailService).sendBookingCancellation("cancel@example.com", 102L);
    }

    @Test
    void shouldIgnoreEvent_whenUserEmailIsNull() {
        BookingEvent event = new BookingEvent();
        event.setUserEmail(null);
        event.setBookingId(103L);
        event.setEventType("BOOKING_CONFIRMED");

        consumer.handleBookingEvent(event);

        verify(emailService, never()).sendBookingConfirmation(null, 103L);
        verify(emailService, never()).sendBookingCancellation(null, 103L);
    }

    @Test
    void shouldIgnoreUnknownEventTypes() {
        BookingEvent event = new BookingEvent();
        event.setUserEmail("user@example.com");
        event.setBookingId(104L);
        event.setEventType("BOOKING_CREATED");

        consumer.handleBookingEvent(event);

        verify(emailService, never()).sendBookingConfirmation("user@example.com", 104L);
        verify(emailService, never()).sendBookingCancellation("user@example.com", 104L);
    }
}

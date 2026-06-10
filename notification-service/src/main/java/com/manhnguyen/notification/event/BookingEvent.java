package com.manhnguyen.notification.event;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingEvent {
    private Long bookingId;
    private Long userId;
    private String userEmail;
    private Long hotelId;
    private Long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BigDecimal totalPrice;
    private String status;
    private String eventType;
}

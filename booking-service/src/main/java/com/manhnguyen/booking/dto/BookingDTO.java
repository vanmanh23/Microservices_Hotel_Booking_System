package com.manhnguyen.booking.dto;

import com.manhnguyen.booking.model.Booking;
import com.manhnguyen.booking.model.BookingStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record BookingDTO(
        Long id,
        Long userId,
        String userEmail,
        Long roomId,
        Long hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        BigDecimal totalPrice,
        BookingStatus status,
        LocalDateTime createdAt
) {
    public static BookingDTO from(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .userEmail(booking.getUserEmail())
                .roomId(booking.getRoomId())
                .hotelId(booking.getHotelId())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}

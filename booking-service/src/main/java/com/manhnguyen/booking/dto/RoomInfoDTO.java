package com.manhnguyen.booking.dto;

import java.math.BigDecimal;

public record RoomInfoDTO(
        Long id,
        Long hotelId,
        String roomNumber,
        String roomType,
        BigDecimal pricePerNight,
        Integer capacity,
        Boolean isActive
) {}

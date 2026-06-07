package com.manhnguyen.hotel.dto;

import com.manhnguyen.hotel.model.Room;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Builder
public record RoomDTO(
        Long id,
        Long hotelId,
        String roomNumber,
        String roomType,
        BigDecimal pricePerNight,
        Integer capacity,
        List<String> amenities,
        Boolean isActive
) {
    public static RoomDTO from(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .pricePerNight(room.getPricePerNight())
                .capacity(room.getCapacity())
                .amenities(parseAmenities(room.getAmenities()))
                .isActive(room.getIsActive())
                .build();
    }

    private static List<String> parseAmenities(String amenities) {
        if (amenities == null || amenities.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.asList(amenities.split(","));
    }
}

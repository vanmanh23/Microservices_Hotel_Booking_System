package com.manhnguyen.hotel.dto;

import com.manhnguyen.hotel.model.Hotel;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Builder
public record HotelDTO(
        Long id,
        String name,
        String description,
        String address,
        String city,
        String country,
        BigDecimal rating,
        List<String> amenities
) {
    public static HotelDTO from(Hotel hotel) {
        return HotelDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .address(hotel.getAddress())
                .city(hotel.getCity())
                .country(hotel.getCountry())
                .rating(hotel.getRating())
                .amenities(parseAmenities(hotel.getAmenities()))
                .build();
    }

    private static List<String> parseAmenities(String amenities) {
        if (amenities == null || amenities.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.asList(amenities.split(","));
    }
}

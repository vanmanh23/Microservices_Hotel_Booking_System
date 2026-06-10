package com.manhnguyen.review.dto;

import com.manhnguyen.review.model.Review;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewDTO(
        Long id,
        Long hotelId,
        Long userId,
        Long bookingId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
    public static ReviewDTO from(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .hotelId(review.getHotelId())
                .userId(review.getUserId())
                .bookingId(review.getBookingId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

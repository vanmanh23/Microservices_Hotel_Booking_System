package com.manhnguyen.review.repository;

import com.manhnguyen.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByHotelIdOrderByCreatedAtDesc(Long hotelId);
    boolean existsByUserIdAndBookingId(Long userId, Long bookingId);
}

package com.manhnguyen.review.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.review.client.BookingServiceClient;
import com.manhnguyen.review.dto.CreateReviewRequest;
import com.manhnguyen.review.dto.ReviewDTO;
import com.manhnguyen.review.model.Review;
import com.manhnguyen.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingServiceClient bookingServiceClient;

    @Transactional
    public ReviewDTO createReview(CreateReviewRequest request) {
        if (reviewRepository.existsByUserIdAndBookingId(request.userId(), request.bookingId())) {
            throw new ApiException("You have already reviewed this booking", HttpStatus.CONFLICT);
        }

        Map<String, Boolean> canReview = bookingServiceClient.canReview(
                request.bookingId(), request.userId(), request.roomId());
        if (canReview == null || !Boolean.TRUE.equals(canReview.get("canReview"))) {
            throw new ApiException("You can only review after completing your stay", HttpStatus.FORBIDDEN);
        }

        Review review = Review.builder()
                .hotelId(request.hotelId())
                .userId(request.userId())
                .bookingId(request.bookingId())
                .rating(request.rating())
                .comment(request.comment())
                .build();

        return ReviewDTO.from(reviewRepository.save(review));
    }

    public List<ReviewDTO> getReviewsByHotel(Long hotelId) {
        return reviewRepository.findByHotelIdOrderByCreatedAtDesc(hotelId)
                .stream()
                .map(ReviewDTO::from)
                .toList();
    }
}

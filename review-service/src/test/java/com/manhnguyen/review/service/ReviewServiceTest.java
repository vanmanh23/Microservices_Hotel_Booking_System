package com.manhnguyen.review.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.review.client.BookingServiceClient;
import com.manhnguyen.review.dto.CreateReviewRequest;
import com.manhnguyen.review.model.Review;
import com.manhnguyen.review.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingServiceClient bookingServiceClient;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void shouldCreateReview_whenRequestValidAndEligible() {
        CreateReviewRequest request = new CreateReviewRequest(10L, 1L, 100L, 20L, 5, "Excellent stay");
        when(reviewRepository.existsByUserIdAndBookingId(1L, 100L)).thenReturn(false);
        when(bookingServiceClient.canReview(100L, 1L, 20L)).thenReturn(Map.of("canReview", true));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var actual = reviewService.createReview(request);

        assertThat(actual.hotelId()).isEqualTo(10L);
        assertThat(actual.userId()).isEqualTo(1L);
        assertThat(actual.rating()).isEqualTo(5);
    }

    @Test
    void shouldThrowApiException_whenAlreadyReviewed() {
        CreateReviewRequest request = new CreateReviewRequest(10L, 2L, 101L, 21L, 4, "Nice");
        when(reviewRepository.existsByUserIdAndBookingId(2L, 101L)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("You have already reviewed this booking");
    }

    @Test
    void shouldThrowApiException_whenCannotReviewYet() {
        CreateReviewRequest request = new CreateReviewRequest(10L, 3L, 102L, 22L, 4, "Good");
        when(reviewRepository.existsByUserIdAndBookingId(3L, 102L)).thenReturn(false);
        when(bookingServiceClient.canReview(102L, 3L, 22L)).thenReturn(Map.of("canReview", false));

        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("You can only review after completing your stay");
    }

    @Test
    void shouldReturnReviewsByHotel_whenReviewsExist() {
        Review review = Review.builder().id(20L).hotelId(10L).userId(4L).bookingId(102L).rating(4).comment("Nice").build();
        when(reviewRepository.findByHotelIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(review));

        var actual = reviewService.getReviewsByHotel(10L);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).bookingId()).isEqualTo(102L);
    }
}

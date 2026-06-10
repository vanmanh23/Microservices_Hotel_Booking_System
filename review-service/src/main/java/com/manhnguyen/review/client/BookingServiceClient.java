package com.manhnguyen.review.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "booking-service", url = "${services.booking.url}")
public interface BookingServiceClient {

    @GetMapping("/api/bookings/{id}/can-review")
    Map<String, Boolean> canReview(
            @PathVariable("id") Long bookingId,
            @RequestParam("userId") Long userId,
            @RequestParam("roomId") Long roomId
    );
}

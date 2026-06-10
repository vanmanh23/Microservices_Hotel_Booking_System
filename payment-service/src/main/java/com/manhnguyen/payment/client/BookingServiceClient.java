package com.manhnguyen.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "booking-service", url = "${services.booking.url}")
public interface BookingServiceClient {

    @PutMapping("/api/bookings/{id}/confirm")
    void confirmBooking(@PathVariable("id") Long id);
}

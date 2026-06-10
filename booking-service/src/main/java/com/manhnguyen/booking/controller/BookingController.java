package com.manhnguyen.booking.controller;

import com.manhnguyen.booking.dto.BookingDTO;
import com.manhnguyen.booking.dto.CreateBookingRequest;
import com.manhnguyen.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/user")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@RequestParam Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<BookingDTO> confirmBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.confirmBooking(id));
    }

    @PutMapping("/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(
            @RequestParam Long bookingId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId, userId));
    }

    @GetMapping("/{id}/can-review")
    public ResponseEntity<Map<String, Boolean>> canReview(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam Long roomId
    ) {
        return ResponseEntity.ok(Map.of("canReview", bookingService.hasCompletedStay(userId, roomId)));
    }
}

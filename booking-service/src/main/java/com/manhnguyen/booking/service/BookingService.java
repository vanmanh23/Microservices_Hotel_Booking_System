package com.manhnguyen.booking.service;

import com.manhnguyen.booking.client.HotelServiceClient;
import com.manhnguyen.booking.dto.BookingDTO;
import com.manhnguyen.booking.dto.CreateBookingRequest;
import com.manhnguyen.booking.dto.RoomInfoDTO;
import com.manhnguyen.booking.event.BookingEventPublisher;
import com.manhnguyen.booking.model.Booking;
import com.manhnguyen.booking.model.BookingStatus;
import com.manhnguyen.booking.repository.BookingRepository;
import com.manhnguyen.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final List<BookingStatus> ACTIVE_STATUSES = List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final BookingRepository bookingRepository;
    private final HotelServiceClient hotelServiceClient;
    private final BookingEventPublisher eventPublisher;

    @Transactional
    public BookingDTO createBooking(CreateBookingRequest request) {
        validateDates(request.checkIn(), request.checkOut());

        RoomInfoDTO room = hotelServiceClient.getRoomById(request.roomId());
        if (room == null || !Boolean.TRUE.equals(room.isActive())) {
            throw new ApiException("Room is not available", HttpStatus.BAD_REQUEST);
        }

        if (bookingRepository.existsOverlappingBooking(
                request.roomId(), request.checkIn(), request.checkOut(), ACTIVE_STATUSES)) {
            throw new ApiException("Room is already booked for the selected dates", HttpStatus.CONFLICT);
        }

        long nights = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());
        BigDecimal totalPrice = room.pricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .userId(request.userId())
                .userEmail(request.userEmail())
                .roomId(request.roomId())
                .hotelId(room.hotelId())
                .checkIn(request.checkIn())
                .checkOut(request.checkOut())
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING)
                .build();

        Booking saved = bookingRepository.save(booking);
        eventPublisher.publish(saved, "BOOKING_CREATED");
        return BookingDTO.from(saved);
    }

    @Transactional
    public BookingDTO confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new ApiException("Booking not found", HttpStatus.NOT_FOUND));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ApiException("Only pending bookings can be confirmed", HttpStatus.BAD_REQUEST);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);
        eventPublisher.publish(saved, "BOOKING_CONFIRMED");
        return BookingDTO.from(saved);
    }

    @Transactional
    public BookingDTO cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new ApiException("Booking not found", HttpStatus.NOT_FOUND));

        if (!booking.getUserId().equals(userId)) {
            throw new ApiException("Not authorized to cancel this booking", HttpStatus.FORBIDDEN);
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ApiException("Booking is already cancelled", HttpStatus.BAD_REQUEST);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);
        eventPublisher.publish(saved, "BOOKING_CANCELLED");
        return BookingDTO.from(saved);
    }

    public BookingDTO getBookingById(Long id) {
        return BookingDTO.from(findBooking(id));
    }

    public List<BookingDTO> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(BookingDTO::from)
                .toList();
    }

    public boolean hasCompletedStay(Long userId, Long roomId) {
        return bookingRepository.findCompletedStay(userId, roomId, LocalDate.now()).isPresent();
    }

    private Booking findBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ApiException("Booking not found", HttpStatus.NOT_FOUND));
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new ApiException("Check-out must be after check-in", HttpStatus.BAD_REQUEST);
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new ApiException("Check-in cannot be in the past", HttpStatus.BAD_REQUEST);
        }
    }
}

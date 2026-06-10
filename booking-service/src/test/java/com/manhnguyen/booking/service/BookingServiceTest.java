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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private HotelServiceClient hotelServiceClient;

    @Mock
    private BookingEventPublisher eventPublisher;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void shouldCreateBooking_whenRequestValid() {
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(5);
        CreateBookingRequest request = new CreateBookingRequest(1L, "user@test.com", 11L, checkIn, checkOut);
        RoomInfoDTO room = new RoomInfoDTO(11L, 21L, "501", "DELUXE", new BigDecimal("100.00"), 2, true);
        when(hotelServiceClient.getRoomById(11L)).thenReturn(room);
        when(bookingRepository.existsOverlappingBooking(11L, checkIn, checkOut, List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED))).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(101L);
            return booking;
        });
        doNothing().when(eventPublisher).publish(any(Booking.class), any(String.class));

        BookingDTO actual = bookingService.createBooking(request);

        assertThat(actual.id()).isEqualTo(101L);
        assertThat(actual.status()).isEqualTo(BookingStatus.PENDING);
        assertThat(actual.totalPrice()).isEqualTo(new BigDecimal("300.00"));
    }

    @Test
    void shouldThrowApiException_whenRoomIsNotAvailable() {
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(3);
        CreateBookingRequest request = new CreateBookingRequest(1L, "user@test.com", 12L, checkIn, checkOut);
        RoomInfoDTO room = new RoomInfoDTO(12L, 22L, "502", "STANDARD", new BigDecimal("80.00"), 2, false);
        when(hotelServiceClient.getRoomById(12L)).thenReturn(room);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Room is not available");
    }

    @Test
    void shouldThrowApiException_whenOverlappingBookingExists() {
        LocalDate checkIn = LocalDate.now().plusDays(4);
        LocalDate checkOut = LocalDate.now().plusDays(7);
        CreateBookingRequest request = new CreateBookingRequest(1L, "user@test.com", 13L, checkIn, checkOut);
        RoomInfoDTO room = new RoomInfoDTO(13L, 23L, "503", "STANDARD", new BigDecimal("70.00"), 2, true);
        when(hotelServiceClient.getRoomById(13L)).thenReturn(room);
        when(bookingRepository.existsOverlappingBooking(13L, checkIn, checkOut, List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED))).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Room is already booked");
    }

    @Test
    void shouldThrowApiException_whenCheckOutNotAfterCheckIn() {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(5);
        CreateBookingRequest request = new CreateBookingRequest(1L, "user@test.com", 14L, checkIn, checkOut);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Check-out must be after check-in");
    }

    @Test
    void shouldThrowApiException_whenCheckInInPast() {
        LocalDate checkIn = LocalDate.now().minusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(1);
        CreateBookingRequest request = new CreateBookingRequest(1L, "user@test.com", 15L, checkIn, checkOut);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Check-in cannot be in the past");
    }

    @Test
    void shouldConfirmBooking_whenPendingBookingExists() {
        Booking existing = Booking.builder().id(200L).status(BookingStatus.PENDING).userId(2L).build();
        when(bookingRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(existing));
        when(bookingRepository.save(existing)).thenReturn(existing);
        doNothing().when(eventPublisher).publish(existing, "BOOKING_CONFIRMED");

        BookingDTO actual = bookingService.confirmBooking(200L);

        assertThat(actual.status()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void shouldThrowApiException_whenConfirmingNonPendingBooking() {
        Booking existing = Booking.builder().id(201L).status(BookingStatus.CONFIRMED).build();
        when(bookingRepository.findByIdForUpdate(201L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> bookingService.confirmBooking(201L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Only pending bookings can be confirmed");
    }

    @Test
    void shouldCancelBooking_whenUserOwnsPendingBooking() {
        Booking booking = Booking.builder().id(300L).status(BookingStatus.PENDING).userId(4L).build();
        when(bookingRepository.findByIdForUpdate(300L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        doNothing().when(eventPublisher).publish(booking, "BOOKING_CANCELLED");

        BookingDTO actual = bookingService.cancelBooking(300L, 4L);

        assertThat(actual.status()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void shouldThrowApiException_whenCancelingNotOwnedBooking() {
        Booking booking = Booking.builder().id(301L).status(BookingStatus.PENDING).userId(5L).build();
        when(bookingRepository.findByIdForUpdate(301L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking(301L, 6L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Not authorized to cancel this booking");
    }

    @Test
    void shouldThrowApiException_whenCancelingAlreadyCanceledBooking() {
        Booking booking = Booking.builder().id(302L).status(BookingStatus.CANCELLED).userId(6L).build();
        when(bookingRepository.findByIdForUpdate(302L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking(302L, 6L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Booking is already cancelled");
    }

    @Test
    void shouldReturnUserBookings_whenBookingsExist() {
        Booking booking = Booking.builder().id(400L).userId(7L).build();
        when(bookingRepository.findByUserIdOrderByCreatedAtDesc(7L)).thenReturn(List.of(booking));

        var actual = bookingService.getUserBookings(7L);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).id()).isEqualTo(400L);
    }

    @Test
    void shouldReturnFalse_whenNoCompletedStayExists() {
        when(bookingRepository.findCompletedStay(8L, 21L, LocalDate.now())).thenReturn(Optional.empty());

        assertThat(bookingService.hasCompletedStay(8L, 21L)).isFalse();
    }
}

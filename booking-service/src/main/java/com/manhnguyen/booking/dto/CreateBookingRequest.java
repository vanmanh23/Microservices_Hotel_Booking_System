package com.manhnguyen.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBookingRequest(
        @NotNull Long userId,
        @NotNull String userEmail,
        @NotNull Long roomId,
        @NotNull @Future LocalDate checkIn,
        @NotNull @Future LocalDate checkOut
) {}

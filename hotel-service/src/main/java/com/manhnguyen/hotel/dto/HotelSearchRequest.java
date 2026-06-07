package com.manhnguyen.hotel.dto;

import java.math.BigDecimal;

public record HotelSearchRequest(
        String city,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal minRating,
        String sortBy
) {}

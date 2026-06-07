package com.manhnguyen.hotel.repository;

import com.manhnguyen.hotel.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByCityIgnoreCase(String city);

    @Query("""
            SELECT DISTINCT h FROM Hotel h
            JOIN h.rooms r
            WHERE (:city IS NULL OR LOWER(h.city) = LOWER(:city))
            AND (:minRating IS NULL OR h.rating >= :minRating)
            AND (:minPrice IS NULL OR r.pricePerNight >= :minPrice)
            AND (:maxPrice IS NULL OR r.pricePerNight <= :maxPrice)
            AND r.isActive = true
            """)
    List<Hotel> searchHotels(
            @Param("city") String city,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRating") BigDecimal minRating
    );
}

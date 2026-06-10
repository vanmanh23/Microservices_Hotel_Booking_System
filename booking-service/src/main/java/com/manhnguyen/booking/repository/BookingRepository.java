package com.manhnguyen.booking.repository;

import com.manhnguyen.booking.model.Booking;
import com.manhnguyen.booking.model.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.roomId = :roomId
            AND b.status IN :statuses
            AND b.checkIn < :checkOut
            AND b.checkOut > :checkIn
            """)
    boolean existsOverlappingBooking(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("statuses") List<BookingStatus> statuses
    );

    @Query("""
            SELECT b FROM Booking b
            WHERE b.userId = :userId
            AND b.roomId = :roomId
            AND b.status = 'CONFIRMED'
            AND b.checkOut <= :today
            """)
    Optional<Booking> findCompletedStay(
            @Param("userId") Long userId,
            @Param("roomId") Long roomId,
            @Param("today") LocalDate today
    );
}

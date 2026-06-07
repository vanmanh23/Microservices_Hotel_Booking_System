package com.manhnguyen.hotel.repository;

import com.manhnguyen.hotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelIdAndIsActiveTrue(Long hotelId);
}

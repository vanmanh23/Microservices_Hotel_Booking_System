package com.manhnguyen.hotel.controller;

import com.manhnguyen.hotel.dto.RoomDTO;
import com.manhnguyen.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(@RequestParam Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotelId(hotelId));
    }
}

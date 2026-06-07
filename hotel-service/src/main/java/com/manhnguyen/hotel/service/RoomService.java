package com.manhnguyen.hotel.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.hotel.dto.RoomDTO;
import com.manhnguyen.hotel.model.Room;
import com.manhnguyen.hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomDTO getRoomById(Long id) {
        return RoomDTO.from(findRoom(id));
    }

    public List<RoomDTO> getRoomsByHotelId(Long hotelId) {
        return roomRepository.findByHotelIdAndIsActiveTrue(hotelId)
                .stream()
                .map(RoomDTO::from)
                .toList();
    }

    public RoomDTO getRoomInternal(Long id) {
        return getRoomById(id);
    }

    private Room findRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ApiException("Room not found", HttpStatus.NOT_FOUND));
    }
}

package com.manhnguyen.hotel.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.hotel.model.Room;
import com.manhnguyen.hotel.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void shouldReturnRoom_whenIdExists() {
        Room room = Room.builder()
                .id(1L)
                .hotel(new com.manhnguyen.hotel.model.Hotel())
                .roomNumber("100")
                .roomType(" DELUXE")
                .pricePerNight(new BigDecimal("120.00"))
                .isActive(true)
                .build();
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        var actual = roomService.getRoomById(1L);

        assertThat(actual.id()).isEqualTo(1L);
        assertThat(actual.roomType()).isEqualTo(" DELUXE");
    }

    @Test
    void shouldThrowApiException_whenRoomNotFound() {
        when(roomRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoomById(2L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Room not found");
    }

    @Test
    void shouldReturnActiveRooms_whenHotelHasActiveRooms() {
        Room activeRoom = Room.builder()
                .id(10L)
                .hotel(new com.manhnguyen.hotel.model.Hotel())
                .roomNumber("101")
                .isActive(true)
                .build();
        when(roomRepository.findByHotelIdAndIsActiveTrue(5L)).thenReturn(List.of(activeRoom));

        var actual = roomService.getRoomsByHotelId(5L);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).id()).isEqualTo(10L);
    }

    @Test
    void shouldDelegateToGetRoomById_whenGetRoomInternalCalled() {
        Room room = Room.builder()
                .id(3L)
                .hotel(new com.manhnguyen.hotel.model.Hotel())
                .isActive(true)
                .build();
        when(roomRepository.findById(3L)).thenReturn(Optional.of(room));

        var actual = roomService.getRoomInternal(3L);

        assertThat(actual.id()).isEqualTo(3L);
    }
}

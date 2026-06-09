package com.manhnguyen.hotel.controller;

import com.manhnguyen.common.exception.GlobalExceptionHandler;
import com.manhnguyen.hotel.dto.RoomDTO;
import com.manhnguyen.hotel.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RoomController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
public class RoomControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @Test
    void getRoomById_returns200() throws Exception {
        RoomDTO room = RoomDTO.builder()
                .id(1L)
                .hotelId(100L)
                .roomNumber("101")
                .roomType("STANDARD")
                .pricePerNight(BigDecimal.valueOf(80.00))
                .capacity(2)
                .amenities(List.of("WiFi",
                        "TV",
                        "AC"))
                .isActive(true)
                .build();
        when(roomService.getRoomById(1L)).thenReturn(room);
        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("hotelId").value(100L));
    }
    @Test
    void getAvailableRooms_returns200() throws Exception {
        RoomDTO room = RoomDTO.builder()
                .id(1L)
                .hotelId(100L)
                .roomNumber("101")
                .roomType("STANDARD")
                .pricePerNight(BigDecimal.valueOf(80.00))
                .capacity(2)
                .amenities(List.of("WiFi",
                        "TV",
                        "AC"))
                .isActive(true)
                .build();
        when(roomService.getRoomsByHotelId(100L)).thenReturn(List.of(room));
        mockMvc.perform(get("/api/rooms/available")
                        .param("hotelId", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}

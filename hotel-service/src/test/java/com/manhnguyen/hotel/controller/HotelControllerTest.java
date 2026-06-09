package com.manhnguyen.hotel.controller;

import com.manhnguyen.common.exception.GlobalExceptionHandler;
import com.manhnguyen.hotel.dto.HotelDTO;
import com.manhnguyen.hotel.service.HotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HotelController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
public class HotelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HotelService hotelService;

    @Test
    void getAllHotels_returns200() throws Exception {
        HotelDTO hotel = HotelDTO.builder()
                .id(1L)
                .name("InterContinental Danang Sun Peninsula Resort")
                .description("Luxury beachfront resort located on Son Tra Peninsula.")
                .address("Bai Bac, Son Tra Peninsula")
                .city("Da Nang")
                .country("Vietnam")
                .rating(BigDecimal.valueOf(4.8))
                .amenities(List.of(
                        "Free WiFi",
                        "Swimming Pool",
                        "Spa",
                        "Fitness Center",
                        "Airport Shuttle",
                        "Restaurant",
                        "Private Beach",
                        "Parking"
                ))
                .build();
        when(hotelService.getAllHotels()).thenReturn(List.of(hotel));

        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("InterContinental Danang Sun Peninsula Resort"));
    }

    @Test
    void getHotelById_returns200() throws Exception {
        HotelDTO hotel = HotelDTO.builder()
                .id(1L)
                .name("InterContinental Danang Sun Peninsula Resort")
                .description("Luxury beachfront resort located on Son Tra Peninsula.")
                .address("Bai Bac, Son Tra Peninsula")
                .city("Da Nang")
                .country("Vietnam")
                .rating(BigDecimal.valueOf(4.8))
                .amenities(List.of(
                        "Free WiFi",
                        "Swimming Pool",
                        "Spa",
                        "Fitness Center",
                        "Airport Shuttle",
                        "Restaurant",
                        "Private Beach",
                        "Parking"
                ))
                .build();
        when(hotelService.getHotelById(1L)).thenReturn(hotel);

        mockMvc.perform(get("/api/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("InterContinental Danang Sun Peninsula Resort"));
    }

    @Test
    void searchHotels_returns200() throws Exception {
        HotelDTO hotel = HotelDTO.builder()
                .id(1L)
                .name("InterContinental Danang Sun Peninsula Resort")
                .description("Luxury beachfront resort located on Son Tra Peninsula.")
                .address("Bai Bac, Son Tra Peninsula")
                .city("Da Nang")
                .country("Vietnam")
                .rating(BigDecimal.valueOf(4.8))
                .amenities(List.of(
                        "Free WiFi",
                        "Swimming Pool",
                        "Spa",
                        "Fitness Center",
                        "Airport Shuttle",
                        "Restaurant",
                        "Private Beach",
                        "Parking"
                ))
                .build();
        when(hotelService.searchHotels(any())).thenReturn(List.of(hotel));

        mockMvc.perform(get("/api/hotels/search")
                        .param("city", "Da Nang")
                        .param("minPrice", "100")
                        .param("maxPrice", "500")
                        .param("minRating", "4.5")
                        .param("sortBy", "rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("InterContinental Danang Sun Peninsula Resort"));
    }
}

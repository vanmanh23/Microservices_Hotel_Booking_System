package com.manhnguyen.hotel.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.hotel.dto.HotelSearchRequest;
import com.manhnguyen.hotel.model.Hotel;
import com.manhnguyen.hotel.repository.HotelRepository;
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
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    @Test
    void shouldReturnAllHotels_whenRepositoryReturnsHotels() {
        Hotel hotelA = Hotel.builder().id(1L).name("Alpha Hotel").city("Hanoi").country("VN").rating(new BigDecimal("4.1")).build();
        Hotel hotelB = Hotel.builder().id(2L).name("Beta Lodge").city("Hanoi").country("VN").rating(new BigDecimal("3.9")).build();
        when(hotelRepository.findAll()).thenReturn(List.of(hotelA, hotelB));

        var actual = hotelService.getAllHotels();

        assertThat(actual).hasSize(2);
        assertThat(actual).extracting("id").containsExactly(1L, 2L);
    }

    @Test
    void shouldReturnHotel_whenIdExists() {
        Hotel hotel = Hotel.builder().id(5L).name("Grand").city("Hue").country("VN").build();
        when(hotelRepository.findById(5L)).thenReturn(Optional.of(hotel));

        var actual = hotelService.getHotelById(5L);

        assertThat(actual.id()).isEqualTo(5L);
        assertThat(actual.name()).isEqualTo("Grand");
    }

    @Test
    void shouldThrowApiException_whenHotelNotFound() {
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.getHotelById(999L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Hotel not found");
    }

    @Test
    void shouldSortByName_whenRequestSortByName() {
        Hotel hotelA = Hotel.builder().id(1L).name("Delta").rating(new BigDecimal("4.2")).build();
        Hotel hotelB = Hotel.builder().id(2L).name("Alpha").rating(new BigDecimal("5.0")).build();
        when(hotelRepository.searchHotels(null, null, null, null)).thenReturn(List.of(hotelA, hotelB));

        var actual = hotelService.searchHotels(new HotelSearchRequest(null, null, null, null, "name"));

        assertThat(actual).extracting("name").containsExactly("Alpha", "Delta");
    }

    @Test
    void shouldSortByRatingDescending_whenRequestSortByRating() {
        Hotel hotelA = Hotel.builder().id(1L).name("Delta").rating(new BigDecimal("4.2")).build();
        Hotel hotelB = Hotel.builder().id(2L).name("Alpha").rating(new BigDecimal("5.0")).build();
        when(hotelRepository.searchHotels(null, null, null, null)).thenReturn(List.of(hotelA, hotelB));

        var actual = hotelService.searchHotels(new HotelSearchRequest(null, null, null, null, "rating"));

        assertThat(actual).extracting("rating").containsExactly(new BigDecimal("5.0"), new BigDecimal("4.2"));
    }
}

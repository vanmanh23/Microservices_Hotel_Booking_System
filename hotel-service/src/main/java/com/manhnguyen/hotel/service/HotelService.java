package com.manhnguyen.hotel.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.hotel.dto.HotelDTO;
import com.manhnguyen.hotel.dto.HotelSearchRequest;
import com.manhnguyen.hotel.model.Hotel;
import com.manhnguyen.hotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    @Cacheable(value = "hotels", key = "'all'")
    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream().map(HotelDTO::from).toList();
    }

    @Cacheable(value = "hotels", key = "#id")
    public HotelDTO getHotelById(Long id) {
        return HotelDTO.from(findHotel(id));
    }

    public List<HotelDTO> searchHotels(HotelSearchRequest request) {
        List<Hotel> hotels = hotelRepository.searchHotels(
                request.city(),
                request.minPrice(),
                request.maxPrice(),
                request.minRating()
        );

        Comparator<Hotel> comparator = "name".equals(request.sortBy())
                ? Comparator.comparing(Hotel::getName)
                : Comparator.comparing(Hotel::getRating).reversed();

        return hotels.stream().sorted(comparator).map(HotelDTO::from).toList();
    }

    private Hotel findHotel(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new ApiException("Hotel not found", HttpStatus.NOT_FOUND));
    }
}

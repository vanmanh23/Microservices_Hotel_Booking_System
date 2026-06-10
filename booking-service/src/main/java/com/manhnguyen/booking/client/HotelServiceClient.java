package com.manhnguyen.booking.client;

import com.manhnguyen.booking.dto.RoomInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hotel-service", url = "${services.hotel.url}")
public interface HotelServiceClient {

    @GetMapping("/api/rooms/{id}")
    RoomInfoDTO getRoomById(@PathVariable("id") Long id);
}

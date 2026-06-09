package com.manhnguyen.hotel.repository;

import com.manhnguyen.hotel.model.Hotel;
import com.manhnguyen.hotel.model.Room;
import com.manhnguyen.hotel.support.AbstractPostgresIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class RoomRepositoryTest extends AbstractPostgresIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private RoomRepository roomRepository;

    private Hotel persistHotel(String name) {
        Hotel hotel = Hotel.builder()
                .name(name)
                .address("123 Test Street")
                .city("Da Nang")
                .country("Vietnam")
                .build();
        em.persist(hotel);
        return hotel;
    }

    private Room persistRoom(Hotel hotel, String roomNumber, String roomType,
                             BigDecimal price, int capacity, boolean active) {
        Room room = Room.builder()
                .hotel(hotel)
                .roomNumber(roomNumber)
                .roomType(roomType)
                .pricePerNight(price)
                .capacity(capacity)
                .isActive(active)
                .amenities("WiFi, TV, AC")
                .build();
        em.persist(room);
        return room;
    }

    private Hotel hotelA;
    private Hotel hotelB;

    @BeforeEach
    void setUp() {
        hotelA = persistHotel("Grand Palace");
        hotelB = persistHotel("Ocean View");
        em.flush();
    }
    @Test
    @DisplayName("save() – persists room and auto-generates id")
    void save_persistsRoom_andGeneratesId() {
        Room room = Room.builder()
                .hotel(hotelA)
                .roomNumber("101")
                .roomType("STANDARD")
                .pricePerNight(new BigDecimal("99.99"))
                .build();

        Room saved = roomRepository.save(room);

        assertThat(saved.getId()).isNotNull().isPositive();
    }
}

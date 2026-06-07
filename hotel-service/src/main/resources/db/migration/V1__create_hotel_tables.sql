CREATE TABLE IF NOT EXISTS hotels (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    address     VARCHAR(500) NOT NULL,
    city        VARCHAR(100) NOT NULL,
    country     VARCHAR(100) NOT NULL,
    rating      DECIMAL(3,2) DEFAULT 0,
    amenities   TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_hotel_city ON hotels (city);
CREATE INDEX IF NOT EXISTS idx_hotel_rating ON hotels (rating);

CREATE TABLE IF NOT EXISTS rooms (
    id           BIGSERIAL PRIMARY KEY,
    hotel_id     BIGINT NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
    room_number  VARCHAR(20) NOT NULL,
    room_type    VARCHAR(50) NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    capacity     INT NOT NULL DEFAULT 2,
    amenities    TEXT,
    is_active    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (hotel_id, room_number)
);

CREATE INDEX IF NOT EXISTS idx_room_hotel ON rooms (hotel_id);

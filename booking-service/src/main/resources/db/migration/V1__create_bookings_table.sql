CREATE TABLE IF NOT EXISTS bookings (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    user_email   VARCHAR(255) NOT NULL,
    room_id      BIGINT NOT NULL,
    hotel_id     BIGINT NOT NULL,
    check_in     DATE NOT NULL,
    check_out    DATE NOT NULL,
    total_price  DECIMAL(10,2) NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_dates CHECK (check_out > check_in)
);

CREATE INDEX IF NOT EXISTS idx_booking_user ON bookings (user_id);
CREATE INDEX IF NOT EXISTS idx_booking_room_dates ON bookings (room_id, check_in, check_out);
CREATE INDEX IF NOT EXISTS idx_booking_status ON bookings (status);

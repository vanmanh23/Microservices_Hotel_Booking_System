CREATE TABLE IF NOT EXISTS reviews (
    id          BIGSERIAL PRIMARY KEY,
    hotel_id    BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    booking_id  BIGINT NOT NULL,
    rating      INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, booking_id)
);

CREATE INDEX IF NOT EXISTS idx_review_hotel ON reviews (hotel_id);

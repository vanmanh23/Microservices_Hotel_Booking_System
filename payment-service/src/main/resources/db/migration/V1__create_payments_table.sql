CREATE TABLE IF NOT EXISTS payments (
    id              BIGSERIAL PRIMARY KEY,
    booking_id      BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method  VARCHAR(50) NOT NULL DEFAULT 'CARD',
    transaction_id  VARCHAR(100),
    refund_amount   DECIMAL(10,2) DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_payment_booking ON payments (booking_id);
CREATE INDEX IF NOT EXISTS idx_payment_user ON payments (user_id);

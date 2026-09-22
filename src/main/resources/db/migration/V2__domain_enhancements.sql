ALTER TABLE orders ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(12,2) NOT NULL DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS promotion_id BIGINT;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS idempotency_key VARCHAR(120);
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_orders_promotion'
    ) THEN
        ALTER TABLE orders
            ADD CONSTRAINT fk_orders_promotion FOREIGN KEY (promotion_id) REFERENCES promotions(id);
    END IF;
END $$;

ALTER TABLE promotions ADD COLUMN IF NOT EXISTS max_discount_amount NUMERIC(12,2);

ALTER TABLE notifications ADD COLUMN IF NOT EXISTS notification_status VARCHAR(30) NOT NULL DEFAULT 'QUEUED';

ALTER TABLE inventory ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

CREATE UNIQUE INDEX IF NOT EXISTS idx_orders_idempotency_customer ON orders(idempotency_key, customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_promotion_id ON orders(promotion_id);
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(notification_status);

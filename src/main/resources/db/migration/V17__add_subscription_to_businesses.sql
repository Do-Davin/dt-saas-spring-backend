-- V17: Add subscription fields to businesses table.
-- All new columns default to safe values; existing rows are backfilled.
-- Backfill: start = created_at, expires = created_at + 1 year, status = ACTIVE.

ALTER TABLE businesses
    ADD COLUMN subscription_status   VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN subscription_start_date TIMESTAMPTZ,
    ADD COLUMN subscription_expires_at TIMESTAMPTZ,
    ADD COLUMN subscription_plan     VARCHAR(20),
    ADD COLUMN user_limit            INTEGER,
    ADD COLUMN menu_item_limit       INTEGER,
    ADD COLUMN monthly_price         NUMERIC(12, 2);

UPDATE businesses
SET subscription_start_date = created_at,
    subscription_expires_at = created_at + INTERVAL '1 year';

ALTER TABLE businesses
    ADD CONSTRAINT ck_businesses_subscription_status
        CHECK (subscription_status IN ('ACTIVE', 'TRIAL', 'EXPIRED', 'CANCELLED'));

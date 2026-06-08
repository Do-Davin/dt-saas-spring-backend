-- V9: Database hardening — enum CHECK constraints, numeric CHECK constraints,
--     money precision alignment, one-primary-image index, and useful query indexes.

-- ─── A. Money precision alignment ────────────────────────────────────────────

ALTER TABLE customer_request_items
    ALTER COLUMN sales_price_snapshot TYPE numeric(12, 2);

-- ─── B. Enum CHECK constraints ────────────────────────────────────────────────

ALTER TABLE businesses
    ADD CONSTRAINT ck_businesses_type
        CHECK (type IN (
            'COFFEE_SHOP', 'RESTAURANT', 'BAKERY', 'BUFFET', 'CAR_WASH',
            'GARAGE', 'ONLINE_SELLER', 'RETAIL_STORE', 'SERVICE_BUSINESS'
        )),
    ADD CONSTRAINT ck_businesses_catalog_mode
        CHECK (catalog_mode IN ('MENU', 'PRODUCT_CATALOG', 'SERVICE_CATALOG'));

ALTER TABLE products
    ADD CONSTRAINT ck_products_pricing_type
        CHECK (pricing_type IN ('FIXED', 'STARTING_FROM', 'CONTACT_FOR_PRICE', 'NO_PRICE')),
    ADD CONSTRAINT ck_products_uom
        CHECK (uom IN ('UNIT', 'CUP', 'BOTTLE', 'BOX', 'KG', 'G', 'LITER', 'ML', 'HOUR', 'SERVICE'));

ALTER TABLE customer_requests
    ADD CONSTRAINT ck_customer_requests_type
        CHECK (type IN ('ORDER', 'INQUIRY', 'BOOKING', 'SERVICE_REQUEST')),
    ADD CONSTRAINT ck_customer_requests_status
        CHECK (status IN ('NEW', 'SEEN', 'ACCEPTED', 'REJECTED', 'COMPLETED', 'CANCELLED'));

ALTER TABLE customer_request_items
    ADD CONSTRAINT ck_customer_request_items_pricing_type_snapshot
        CHECK (pricing_type_snapshot IS NULL OR pricing_type_snapshot IN (
            'FIXED', 'STARTING_FROM', 'CONTACT_FOR_PRICE', 'NO_PRICE'
        ));

-- ─── C. Numeric CHECK constraints ────────────────────────────────────────────

ALTER TABLE customer_request_items
    ADD CONSTRAINT ck_customer_request_items_quantity
        CHECK (quantity >= 1);

ALTER TABLE categories
    ADD CONSTRAINT ck_categories_position_nonneg
        CHECK (position >= 0);

ALTER TABLE product_images
    ADD CONSTRAINT ck_product_images_position_nonneg
        CHECK (position >= 0);

ALTER TABLE products
    ADD CONSTRAINT ck_products_discount_nonneg
        CHECK (discount IS NULL OR discount >= 0);

-- ─── D. One primary image per product ────────────────────────────────────────

DO $$
BEGIN
    IF EXISTS (
        SELECT product_id
        FROM product_images
        WHERE is_primary = true
        GROUP BY product_id
        HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'Duplicate primary images found. Resolve duplicate product_images.is_primary rows before applying V9 hardening migration.';
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS ux_product_images_one_primary_per_product
    ON product_images (product_id)
    WHERE is_primary = true;

-- ─── E. Useful indexes ────────────────────────────────────────────────────────

-- Supports: findAllByBusinessIdAndDeletedAtIsNull (owner product list)
CREATE INDEX IF NOT EXISTS ix_products_business_deleted_at
    ON products (business_id, deleted_at);

-- Supports: public catalog Specification (business_id + deleted_at IS NULL + is_visible = true)
CREATE INDEX IF NOT EXISTS ix_products_public_catalog
    ON products (business_id, is_visible, deleted_at);

-- Supports: potential queries joining items to a product (no existing index on this FK)
CREATE INDEX IF NOT EXISTS ix_customer_request_items_product_id
    ON customer_request_items (product_id);

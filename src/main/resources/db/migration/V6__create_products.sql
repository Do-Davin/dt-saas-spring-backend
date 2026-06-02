CREATE TABLE products (
    id             uuid                     PRIMARY KEY,
    business_id    uuid                     NOT NULL,
    branch_id      uuid,
    category_id    uuid,
    name           text                     NOT NULL,
    name_km        text,
    description    text,
    description_km text,
    purchase_price numeric(12, 2),
    sales_price    numeric(12, 2),
    discount       numeric(12, 2),
    pricing_type   text                     NOT NULL,
    label          text,
    uom            text                     NOT NULL,
    toppings       jsonb,
    ingredients    jsonb,
    is_available   boolean                  NOT NULL,
    is_visible     boolean                  NOT NULL,
    deleted_at     timestamp with time zone,
    created_at     timestamp with time zone NOT NULL,
    updated_at     timestamp with time zone NOT NULL,
    CONSTRAINT fk_products_business  FOREIGN KEY (business_id)  REFERENCES businesses (id),
    CONSTRAINT fk_products_branch    FOREIGN KEY (branch_id)    REFERENCES branches   (id),
    CONSTRAINT fk_products_category  FOREIGN KEY (category_id)  REFERENCES categories (id)
);

CREATE INDEX ix_products_business_id  ON products (business_id);
CREATE INDEX ix_products_branch_id    ON products (branch_id);
CREATE INDEX ix_products_category_id  ON products (category_id);
CREATE INDEX ix_products_deleted_at   ON products (deleted_at);
CREATE INDEX ix_products_is_available ON products (is_available);
CREATE INDEX ix_products_is_visible   ON products (is_visible);

CREATE TABLE sales (
    id            uuid                     PRIMARY KEY,
    business_id   uuid                     NOT NULL,
    branch_id     uuid,
    sale_date     timestamp with time zone NOT NULL,
    total_amount  numeric(12, 2)           NOT NULL,
    total_cost    numeric(12, 2)           NOT NULL,
    profit        numeric(12, 2)           NOT NULL,
    note          text,
    created_at    timestamp with time zone NOT NULL,
    updated_at    timestamp with time zone NOT NULL,
    CONSTRAINT fk_sales_business FOREIGN KEY (business_id) REFERENCES businesses (id),
    CONSTRAINT fk_sales_branch   FOREIGN KEY (branch_id)   REFERENCES branches (id)
);

CREATE INDEX ix_sales_business_id_sale_date ON sales (business_id, sale_date DESC);
CREATE INDEX ix_sales_created_at            ON sales (created_at DESC);

CREATE TABLE sale_items (
    id                     uuid                     PRIMARY KEY,
    sale_id                uuid                     NOT NULL,
    product_id             uuid,
    product_name_snapshot  text                     NOT NULL,
    category_id_snapshot   uuid,
    category_name_snapshot text,
    quantity               integer                  NOT NULL,
    unit_sales_price       numeric(12, 2)           NOT NULL,
    unit_cost_price        numeric(12, 2)           NOT NULL,
    discount_amount        numeric(12, 2)           NOT NULL DEFAULT 0,
    line_total             numeric(12, 2)           NOT NULL,
    line_cost              numeric(12, 2)           NOT NULL,
    created_at             timestamp with time zone NOT NULL,
    CONSTRAINT fk_sale_items_sale    FOREIGN KEY (sale_id)    REFERENCES sales (id) ON DELETE CASCADE,
    CONSTRAINT fk_sale_items_product FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE INDEX ix_sale_items_sale_id ON sale_items (sale_id);

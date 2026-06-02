CREATE TABLE customer_requests (
    id            uuid                     PRIMARY KEY,
    business_id   uuid                     NOT NULL,
    branch_id     uuid,
    type          text                     NOT NULL,
    status        text                     NOT NULL DEFAULT 'NEW',
    customer_name text,
    customer_phone text,
    customer_note text,
    created_at    timestamp with time zone NOT NULL,
    updated_at    timestamp with time zone NOT NULL,
    CONSTRAINT fk_customer_requests_business FOREIGN KEY (business_id) REFERENCES businesses (id),
    CONSTRAINT fk_customer_requests_branch   FOREIGN KEY (branch_id)   REFERENCES branches (id)
);

CREATE INDEX ix_customer_requests_business_id     ON customer_requests (business_id);
CREATE INDEX ix_customer_requests_business_status ON customer_requests (business_id, status);
CREATE INDEX ix_customer_requests_business_type   ON customer_requests (business_id, type);
CREATE INDEX ix_customer_requests_created_at      ON customer_requests (created_at DESC);

CREATE TABLE customer_request_items (
    id                    uuid                     PRIMARY KEY,
    request_id            uuid                     NOT NULL,
    product_id            uuid,
    product_name_snapshot text                     NOT NULL,
    sales_price_snapshot  numeric(10, 2),
    pricing_type_snapshot text,
    quantity              integer                  NOT NULL DEFAULT 1,
    note                  text,
    created_at            timestamp with time zone NOT NULL,
    CONSTRAINT fk_customer_request_items_request FOREIGN KEY (request_id) REFERENCES customer_requests (id) ON DELETE CASCADE,
    CONSTRAINT fk_customer_request_items_product FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE INDEX ix_customer_request_items_request_id ON customer_request_items (request_id);

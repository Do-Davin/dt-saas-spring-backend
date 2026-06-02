CREATE TABLE product_images (
    id         uuid                     PRIMARY KEY,
    product_id uuid                     NOT NULL,
    key        text                     NOT NULL,
    url        text,
    alt        text,
    position   integer                  NOT NULL,
    is_primary boolean                  NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT ux_product_images_key     UNIQUE (key)
);

CREATE INDEX ix_product_images_product_id       ON product_images (product_id);
CREATE INDEX ix_product_images_product_position ON product_images (product_id, position);

CREATE TABLE categories (
    id          uuid                     PRIMARY KEY,
    business_id uuid                     NOT NULL,
    branch_id   uuid,
    name        text                     NOT NULL,
    name_km     text,
    position    integer                  NOT NULL,
    is_active   boolean                  NOT NULL,
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone NOT NULL,
    CONSTRAINT fk_categories_business FOREIGN KEY (business_id) REFERENCES businesses (id),
    CONSTRAINT fk_categories_branch FOREIGN KEY (branch_id) REFERENCES branches (id)
);

CREATE INDEX ix_categories_business_id ON categories (business_id);
CREATE INDEX ix_categories_branch_id ON categories (branch_id);

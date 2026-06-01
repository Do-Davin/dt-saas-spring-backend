CREATE TABLE branches (
    id          uuid                     PRIMARY KEY,
    business_id uuid                     NOT NULL,
    name        text                     NOT NULL,
    name_km     text,
    slug        text                     NOT NULL,
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone NOT NULL,
    CONSTRAINT fk_branches_business FOREIGN KEY (business_id) REFERENCES businesses (id),
    CONSTRAINT ux_branches_business_slug UNIQUE (business_id, slug)
);

CREATE INDEX ix_branches_business_id ON branches (business_id);

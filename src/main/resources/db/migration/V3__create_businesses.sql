CREATE TABLE businesses (
    id           uuid                     PRIMARY KEY,
    owner_id     uuid                     NOT NULL,
    name         text                     NOT NULL,
    name_km      text,
    slug         text                     NOT NULL,
    type         text                     NOT NULL,
    catalog_mode text                     NOT NULL,
    created_at   timestamp with time zone NOT NULL,
    updated_at   timestamp with time zone NOT NULL,
    CONSTRAINT fk_businesses_owner FOREIGN KEY (owner_id) REFERENCES owners (id)
);

CREATE UNIQUE INDEX ux_businesses_slug ON businesses (slug);
CREATE INDEX ix_businesses_owner_id ON businesses (owner_id);

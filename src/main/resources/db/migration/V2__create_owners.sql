CREATE TABLE owners (
    id              uuid                     PRIMARY KEY,
    email           text                     NOT NULL,
    password_hash   text                     NOT NULL,
    name            text,
    created_at      timestamp with time zone NOT NULL,
    updated_at      timestamp with time zone NOT NULL
);

CREATE UNIQUE INDEX ux_owners_email ON owners (email);

-- V11: Add role column to owners table.
-- Existing rows default to 'OWNER'. New registrations also default to 'OWNER'.
-- SUPER_ADMIN must be assigned explicitly via a subsequent migration or manual update.

ALTER TABLE owners
    ADD COLUMN role TEXT NOT NULL DEFAULT 'OWNER';

ALTER TABLE owners
    ADD CONSTRAINT ck_owners_role
        CHECK (role IN ('SUPER_ADMIN', 'OWNER'));

-- V16: Add username column to owners for username-based authentication.
-- Step 1: nullable first so backfill can populate it.
-- Step 2: derive username from email prefix (e.g. dev@dt-kitchen.local → dev).
-- Step 3: enforce NOT NULL and unique after backfill succeeds.
-- If any two existing owners share an email prefix, the UNIQUE constraint will
-- fail here rather than silently creating duplicate usernames.

ALTER TABLE owners ADD COLUMN username VARCHAR(100);

UPDATE owners SET username = split_part(email, '@', 1);

ALTER TABLE owners ALTER COLUMN username SET NOT NULL;

ALTER TABLE owners ADD CONSTRAINT uq_owners_username UNIQUE (username);

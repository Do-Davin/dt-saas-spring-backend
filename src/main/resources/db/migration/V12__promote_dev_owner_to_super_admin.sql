-- V12: Promote the dev seed owner to SUPER_ADMIN.
-- Targets only the fixed dev seed UUID from V10. Safe to re-run on a fresh DB
-- because V10 inserts ON CONFLICT DO NOTHING, so the row will always exist
-- before this migration runs.

UPDATE owners
SET role = 'SUPER_ADMIN'
WHERE id = 'a0000000-0000-0000-0000-000000000001';

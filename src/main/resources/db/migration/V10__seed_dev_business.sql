-- V10: Dev seed — owner + dt-kitchen sample business for local development.
-- Owner email: dev@dt-kitchen.local  password: dev-password
-- This migration is idempotent (ON CONFLICT DO NOTHING).

INSERT INTO owners (id, email, password_hash, name, created_at, updated_at)
VALUES (
    'a0000000-0000-0000-0000-000000000001',
    'dev@dt-kitchen.local',
    '$2y$10$DNdi3XQm7hzKNLbRrE70He7Vb5zO82fm4Ryz6PijucNmT.JDwUnZG',
    'DT Kitchen Dev',
    now(),
    now()
)
ON CONFLICT DO NOTHING;

INSERT INTO businesses (id, owner_id, name, name_km, slug, type, catalog_mode, created_at, updated_at)
VALUES (
    'b0000000-0000-0000-0000-000000000001',
    'a0000000-0000-0000-0000-000000000001',
    'DT Kitchen',
    'ផ្ទះបាយ DT',
    'dt-kitchen',
    'RESTAURANT',
    'MENU',
    now(),
    now()
)
ON CONFLICT DO NOTHING;

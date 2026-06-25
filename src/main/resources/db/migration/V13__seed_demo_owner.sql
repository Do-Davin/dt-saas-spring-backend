-- V13: Demo seed — OWNER role account + business for MVP demo.
-- Owner  email: demo-owner@dt-saas.local  password: demo-password
-- Conflict handling is explicit ON CONFLICT (id) to avoid silent skips when
-- a different row with the same slug already exists from prior manual testing.

INSERT INTO owners (id, email, password_hash, name, role, created_at, updated_at)
VALUES (
    'd0000000-0000-0000-0000-000000000001',
    'demo-owner@dt-saas.local',
    '$2a$12$hL1/bWzu5OyHRYiXWSLIa.UGkKc9O7VrGndQaz9uJ0ExloOt465vK',
    'Demo Owner',
    'OWNER',
    now(),
    now()
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO businesses (id, owner_id, name, name_km, slug, type, catalog_mode, created_at, updated_at)
VALUES (
    'e0000000-0000-0000-0000-000000000001',
    'd0000000-0000-0000-0000-000000000001',
    'Demo OWNER Biz',
    NULL,
    'demo-owner-biz-2',
    'RESTAURANT',
    'MENU',
    now(),
    now()
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO categories (id, business_id, branch_id, name, name_km, position, is_active, created_at, updated_at)
VALUES (
    'f0000000-0000-0000-0000-000000000001',
    'e0000000-0000-0000-0000-000000000001',
    NULL,
    'Drinks',
    NULL,
    1,
    true,
    now(),
    now()
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, business_id, branch_id, category_id, name, name_km, pricing_type, uom, sales_price, is_available, is_visible, created_at, updated_at)
VALUES (
    'a1000000-0000-0000-0000-000000000001',
    'e0000000-0000-0000-0000-000000000001',
    NULL,
    'f0000000-0000-0000-0000-000000000001',
    'Demo Drink',
    NULL,
    'FIXED',
    'UNIT',
    5.00,
    true,
    true,
    now(),
    now()
)
ON CONFLICT (id) DO NOTHING;

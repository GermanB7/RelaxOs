-- Sprint 4 — Home Setup Roadmap Migration.
-- Creates purchase_catalog_item and user_purchase_item tables with seeds.

CREATE TABLE purchase_catalog_item (
  id BIGSERIAL PRIMARY KEY,
  code VARCHAR(80) NOT NULL UNIQUE,
  name VARCHAR(160) NOT NULL,
  category VARCHAR(80) NOT NULL,
  tier VARCHAR(20) NOT NULL,
  estimated_min_price NUMERIC(14,2),
  estimated_max_price NUMERIC(14,2),
  impact_level VARCHAR(20) NOT NULL,
  urgency_level VARCHAR(20) NOT NULL,
  recommended_moment VARCHAR(160),
  early_purchase_risk TEXT,
  dependencies TEXT,
  rationale TEXT,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  sort_order INTEGER NOT NULL DEFAULT 100,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_purchase_item (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  scenario_id BIGINT REFERENCES scenario(id),
  catalog_item_id BIGINT REFERENCES purchase_catalog_item(id),
  name VARCHAR(160) NOT NULL,
  category VARCHAR(80) NOT NULL,
  tier VARCHAR(20) NOT NULL,
  estimated_price NUMERIC(14,2),
  actual_price NUMERIC(14,2),
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  priority INTEGER NOT NULL DEFAULT 100,
  link TEXT,
  notes TEXT,
  purchased_at TIMESTAMP,
  postponed_until DATE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_purchase_catalog_active_tier
ON purchase_catalog_item(is_active, tier, sort_order);

CREATE INDEX idx_user_purchase_user_status
ON user_purchase_item(user_id, status, priority);

CREATE INDEX idx_user_purchase_scenario_status
ON user_purchase_item(scenario_id, status, priority);

CREATE UNIQUE INDEX idx_user_purchase_unique_catalog_per_scope
ON user_purchase_item(user_id, COALESCE(scenario_id, 0), catalog_item_id)
WHERE catalog_item_id IS NOT NULL;

-- Seed purchase_catalog_item: Top 20 essentials for home setup tiers
INSERT INTO purchase_catalog_item (code, name, category, tier, estimated_min_price, estimated_max_price, impact_level, urgency_level, sort_order, is_active)
VALUES
-- TIER_1: Essential sleep, bathroom, kitchen, cleaning, emergency
('mattress', 'Colchón', 'Dormir', 'TIER_1', 600000, 1200000, 'HIGH', 'HIGH', 10, TRUE),
('pillow', 'Almohada', 'Dormir', 'TIER_1', 80000, 200000, 'MEDIUM', 'HIGH', 20, TRUE),
('sheets_blanket', 'Sábanas/cobija', 'Dormir', 'TIER_1', 100000, 300000, 'MEDIUM', 'HIGH', 30, TRUE),
('towels', 'Toallas', 'Baño', 'TIER_1', 100000, 200000, 'MEDIUM', 'HIGH', 40, TRUE),
('fridge', 'Nevera', 'Cocina', 'TIER_1', 1500000, 3000000, 'HIGH', 'HIGH', 50, TRUE),
('pan', 'Sartén', 'Cocina', 'TIER_1', 50000, 150000, 'MEDIUM', 'HIGH', 60, TRUE),
('pot', 'Olla', 'Cocina', 'TIER_1', 50000, 150000, 'MEDIUM', 'HIGH', 70, TRUE),
('knife_board', 'Cuchillo/tabla', 'Cocina', 'TIER_1', 60000, 150000, 'MEDIUM', 'HIGH', 80, TRUE),
('plates_cutlery', 'Platos/cubiertos', 'Cocina', 'TIER_1', 100000, 250000, 'MEDIUM', 'HIGH', 90, TRUE),
('rice_cooker', 'Arrocera', 'Cocina', 'TIER_1', 100000, 300000, 'MEDIUM', 'MEDIUM', 100, TRUE),
('broom_dustpan', 'Escoba/recogedor', 'Limpieza', 'TIER_1', 30000, 80000, 'MEDIUM', 'HIGH', 110, TRUE),
('mop_bucket', 'Trapero/balde', 'Limpieza', 'TIER_1', 50000, 150000, 'MEDIUM', 'HIGH', 120, TRUE),
('multipurpose_cloths', 'Multiusos/paños', 'Limpieza', 'TIER_1', 20000, 60000, 'MEDIUM', 'HIGH', 130, TRUE),
('trash_bin_bags', 'Caneca/bolsas', 'Limpieza', 'TIER_1', 30000, 80000, 'MEDIUM', 'HIGH', 140, TRUE),
('first_aid_kit', 'Botiquín', 'Emergencias', 'TIER_1', 50000, 150000, 'MEDIUM', 'HIGH', 150, TRUE),
('internet_plan', 'Plan internet', 'Internet/Energía', 'TIER_1', 0, 0, 'HIGH', 'HIGH', 160, TRUE),
('power_strip', 'Regleta/protector', 'Internet/Energía', 'TIER_1', 30000, 100000, 'MEDIUM', 'HIGH', 170, TRUE),
('chair_desk', 'Silla/escritorio', 'Trabajo/estudio', 'TIER_1', 200000, 600000, 'MEDIUM', 'MEDIUM', 180, TRUE),
-- TIER_2: Comfort improvements
('air_fryer', 'Air fryer', 'Cocina', 'TIER_2', 300000, 800000, 'MEDIUM', 'LOW', 200, TRUE),
('containers', 'Recipientes', 'Cocina', 'TIER_2', 80000, 200000, 'LOW', 'LOW', 210, TRUE)
ON CONFLICT (code) DO NOTHING;

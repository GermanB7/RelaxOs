-- Sprint 5 - Adaptive Modes.
-- Adds a small mode catalog and user mode activations.

CREATE TABLE adaptive_mode (
  id BIGSERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(120) NOT NULL,
  description TEXT,
  objective TEXT,
  recommended_min_days INTEGER,
  recommended_max_days INTEGER,
  intensity_level VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
  spending_policy VARCHAR(40) NOT NULL DEFAULT 'NORMAL',
  alert_policy VARCHAR(40) NOT NULL DEFAULT 'NORMAL',
  purchase_policy VARCHAR(40) NOT NULL DEFAULT 'NORMAL',
  routine_policy VARCHAR(40) NOT NULL DEFAULT 'NORMAL',
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  sort_order INTEGER NOT NULL DEFAULT 100,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE mode_activation (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  mode_id BIGINT NOT NULL REFERENCES adaptive_mode(id),
  scenario_id BIGINT REFERENCES scenario(id),
  objective VARCHAR(180),
  intensity_level VARCHAR(20),
  activated_at TIMESTAMP NOT NULL DEFAULT NOW(),
  expires_at TIMESTAMP,
  ended_at TIMESTAMP,
  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  notes TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_adaptive_mode_active_sort
ON adaptive_mode(is_active, sort_order);

CREATE INDEX idx_mode_activation_user_status
ON mode_activation(user_id, status);

CREATE INDEX idx_mode_activation_scenario_status
ON mode_activation(scenario_id, status);

CREATE UNIQUE INDEX idx_mode_activation_one_active_per_user
ON mode_activation(user_id)
WHERE status = 'ACTIVE';

INSERT INTO adaptive_mode (
  code,
  name,
  description,
  objective,
  recommended_min_days,
  recommended_max_days,
  intensity_level,
  spending_policy,
  alert_policy,
  purchase_policy,
  routine_policy,
  sort_order
) VALUES
('WAR_MODE', 'Modo Guerra', 'Short intense focus period.', 'Maximize progress, savings, and focus for a short period.', 14, 30, 'HIGH', 'STRICT', 'STRICT', 'FREEZE_NON_ESSENTIAL', 'STRICT', 10),
('STABLE_MODE', 'Modo Estable', 'Balanced normal operating mode.', 'Operate normal life with balance, steady savings, and planned purchases.', 14, 60, 'MEDIUM', 'NORMAL', 'NORMAL', 'NORMAL', 'NORMAL', 20),
('LIVE_LIFE_MODE', 'Modo Vivir la Vida', 'Conscious flexibility mode.', 'Allow conscious flexibility without touching financial minimums.', 7, 21, 'LOW', 'FLEXIBLE', 'SOFT', 'FLEXIBLE', 'MINIMUM_VIABLE', 30),
('RECOVERY_MODE', 'Modo Recuperación', 'Low energy protection mode.', 'Protect minimum viable routines during low energy or burnout.', 3, 7, 'LOW', 'MINIMAL', 'MINIMAL', 'PLAN_ONLY', 'MINIMUM_VIABLE', 40),
('AGGRESSIVE_SAVING_MODE', 'Modo Ahorro Agresivo', 'Temporary aggressive saving mode.', 'Build emergency fund or save for a major independence goal.', 14, 45, 'HIGH', 'STRICT', 'STRICT', 'FREEZE_NON_ESSENTIAL', 'NORMAL', 50),
('RESET_MODE', 'Modo Reset', 'Short cleanup and reorganization mode.', 'Reorganize budget, home setup, wishlist, and weekly plan after accumulated chaos.', 1, 7, 'MEDIUM', 'NORMAL', 'NORMAL', 'PLAN_ONLY', 'RESET', 60)
ON CONFLICT (code) DO NOTHING;

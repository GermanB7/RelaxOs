CREATE TABLE admin_audit_log (
  id BIGSERIAL PRIMARY KEY,
  admin_user_id BIGINT REFERENCES app_user(id),
  action_type VARCHAR(60) NOT NULL,
  entity_type VARCHAR(80) NOT NULL,
  entity_id VARCHAR(120),
  summary TEXT NOT NULL,
  before_json JSONB,
  after_json JSONB,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE system_setting (
  setting_key VARCHAR(120) PRIMARY KEY,
  setting_value VARCHAR(500) NOT NULL,
  value_type VARCHAR(30) NOT NULL DEFAULT 'STRING',
  description TEXT,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE recommendation_copy (
  rule_key VARCHAR(80) PRIMARY KEY,
  title VARCHAR(160) NOT NULL,
  message TEXT NOT NULL,
  action_label VARCHAR(120),
  severity VARCHAR(20),
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE expense_category
ADD COLUMN IF NOT EXISTS sort_order INTEGER NOT NULL DEFAULT 100,
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW(),
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();

CREATE INDEX idx_admin_audit_created
ON admin_audit_log(created_at DESC);

CREATE INDEX idx_system_setting_active
ON system_setting(is_active);

INSERT INTO system_setting (setting_key, setting_value, value_type, description)
VALUES
  ('rent.safe.max', '0.30', 'DECIMAL', 'Maximum rent burden considered safe.'),
  ('rent.fragile.max', '0.40', 'DECIMAL', 'Maximum rent burden before the scenario becomes fragile.'),
  ('emergency.min.months', '2.00', 'DECIMAL', 'Minimum emergency coverage in months for major commitments.'),
  ('emergency.target.months', '6.00', 'DECIMAL', 'Target emergency coverage in months.'),
  ('transport.burden.warning', '0.15', 'DECIMAL', 'Transport burden threshold that should trigger attention.'),
  ('delivery.food.warning', '0.08', 'DECIMAL', 'Food delivery burden threshold that should trigger attention.')
ON CONFLICT (setting_key) DO NOTHING;

INSERT INTO recommendation_copy (rule_key, title, message, action_label, severity)
SELECT DISTINCT source_rule_key, title, message, action_label, severity
FROM recommendation
WHERE source_rule_key IS NOT NULL
ON CONFLICT (rule_key) DO NOTHING;

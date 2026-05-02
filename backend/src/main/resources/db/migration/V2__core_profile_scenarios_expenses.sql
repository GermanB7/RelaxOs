CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_profile (
    user_id BIGINT PRIMARY KEY REFERENCES app_user(id),
    display_name VARCHAR(120),
    city VARCHAR(120),
    currency VARCHAR(10) NOT NULL DEFAULT 'COP',
    monthly_income NUMERIC(14,2),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE scenario (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    name VARCHAR(160) NOT NULL,
    monthly_income NUMERIC(14,2) NOT NULL,
    emergency_fund_current NUMERIC(14,2) NOT NULL DEFAULT 0,
    emergency_fund_target NUMERIC(14,2),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE scenario_version (
    id BIGSERIAL PRIMARY KEY,
    scenario_id BIGINT NOT NULL REFERENCES scenario(id),
    version_number INTEGER NOT NULL,
    snapshot_json JSONB NOT NULL,
    change_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (scenario_id, version_number)
);

CREATE TABLE expense_category (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    parent_id BIGINT REFERENCES expense_category(id),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE scenario_expense (
    id BIGSERIAL PRIMARY KEY,
    scenario_id BIGINT NOT NULL REFERENCES scenario(id),
    category_id BIGINT NOT NULL REFERENCES expense_category(id),
    name VARCHAR(160) NOT NULL,
    amount NUMERIC(14,2) NOT NULL CHECK (amount >= 0),
    frequency VARCHAR(30) NOT NULL DEFAULT 'MONTHLY',
    is_essential BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_scenario_user_status ON scenario(user_id, status);
CREATE INDEX idx_scenario_expense_scenario ON scenario_expense(scenario_id);
CREATE INDEX idx_expense_category_active ON expense_category(is_active);

INSERT INTO app_user (email)
VALUES ('local@tranquiloos.dev')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_profile (user_id, display_name, city, currency)
SELECT id, 'Local User', 'Bogota', 'COP'
FROM app_user
WHERE email = 'local@tranquiloos.dev'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO expense_category (code, name)
VALUES
    ('rent', 'Rent'),
    ('building_admin', 'Building Admin'),
    ('utilities', 'Utilities'),
    ('internet', 'Internet'),
    ('groceries', 'Groceries'),
    ('food_delivery', 'Food Delivery'),
    ('transport', 'Transport'),
    ('pets', 'Pets'),
    ('health', 'Health'),
    ('gym', 'Gym'),
    ('subscriptions', 'Subscriptions'),
    ('leisure', 'Leisure'),
    ('debt', 'Debt'),
    ('emergency_fund', 'Emergency Fund'),
    ('education', 'Education'),
    ('household_maintenance', 'Household Maintenance')
ON CONFLICT (code) DO NOTHING;

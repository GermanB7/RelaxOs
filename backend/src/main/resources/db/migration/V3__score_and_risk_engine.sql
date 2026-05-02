CREATE TABLE score_snapshot (
    id BIGSERIAL PRIMARY KEY,
    scenario_id BIGINT NOT NULL REFERENCES scenario(id),
    score INTEGER NOT NULL CHECK (score BETWEEN 0 AND 100),
    status VARCHAR(40) NOT NULL,
    confidence_level VARCHAR(20),
    summary TEXT,
    input_snapshot_json JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE score_factor (
    id BIGSERIAL PRIMARY KEY,
    score_snapshot_id BIGINT NOT NULL REFERENCES score_snapshot(id) ON DELETE CASCADE,
    factor_key VARCHAR(80) NOT NULL,
    label VARCHAR(120) NOT NULL,
    value_text VARCHAR(120),
    impact INTEGER NOT NULL,
    weight NUMERIC(8,3),
    explanation TEXT
);

CREATE TABLE risk_factor (
    id BIGSERIAL PRIMARY KEY,
    score_snapshot_id BIGINT NOT NULL REFERENCES score_snapshot(id) ON DELETE CASCADE,
    risk_key VARCHAR(80) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    title VARCHAR(160) NOT NULL,
    explanation TEXT
);

CREATE INDEX idx_score_snapshot_scenario_created ON score_snapshot(scenario_id, created_at DESC);
CREATE INDEX idx_score_factor_snapshot ON score_factor(score_snapshot_id);
CREATE INDEX idx_risk_factor_snapshot ON risk_factor(score_snapshot_id);

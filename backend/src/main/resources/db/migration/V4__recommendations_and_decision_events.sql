CREATE TABLE recommendation (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  scenario_id BIGINT REFERENCES scenario(id),
  score_snapshot_id BIGINT REFERENCES score_snapshot(id),
  type VARCHAR(50) NOT NULL,
  severity VARCHAR(20) NOT NULL,
  priority INTEGER NOT NULL,
  title VARCHAR(160) NOT NULL,
  message TEXT NOT NULL,
  action_label VARCHAR(120),
  action_type VARCHAR(50),
  source_rule_key VARCHAR(80),
  status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
  context_json JSONB,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE decision_event (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  scenario_id BIGINT REFERENCES scenario(id),
  recommendation_id BIGINT REFERENCES recommendation(id),
  decision_type VARCHAR(50) NOT NULL,
  question TEXT NOT NULL,
  chosen_option VARCHAR(120),
  score_before INTEGER,
  score_after INTEGER,
  reason TEXT,
  context_json JSONB,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_recommendation_user_status_priority
ON recommendation(user_id, status, priority);

CREATE INDEX idx_recommendation_scenario_status
ON recommendation(scenario_id, status);

CREATE INDEX idx_recommendation_snapshot
ON recommendation(score_snapshot_id);

CREATE INDEX idx_decision_event_user_created
ON decision_event(user_id, created_at DESC);

CREATE INDEX idx_decision_event_scenario_created
ON decision_event(scenario_id, created_at DESC);

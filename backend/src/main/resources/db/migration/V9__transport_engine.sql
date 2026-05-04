CREATE TABLE transport_option (
  id BIGSERIAL PRIMARY KEY,
  scenario_id BIGINT NOT NULL REFERENCES scenario(id),
  option_type VARCHAR(40) NOT NULL,
  monthly_cost NUMERIC(14,2) NOT NULL CHECK (monthly_cost >= 0),
  trips_per_week INTEGER NOT NULL CHECK (trips_per_week >= 0),
  average_time_minutes INTEGER NOT NULL CHECK (average_time_minutes >= 0),
  comfort_score INTEGER NOT NULL CHECK (comfort_score BETWEEN 1 AND 5),
  safety_score INTEGER NOT NULL CHECK (safety_score BETWEEN 1 AND 5),
  flexibility_score INTEGER NOT NULL CHECK (flexibility_score BETWEEN 1 AND 5),
  parking_cost NUMERIC(14,2),
  maintenance_cost NUMERIC(14,2),
  insurance_cost NUMERIC(14,2),
  fuel_cost NUMERIC(14,2),
  upfront_cost NUMERIC(14,2),
  has_parking BOOLEAN,
  has_license BOOLEAN,
  notes TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE transport_evaluation (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  scenario_id BIGINT NOT NULL REFERENCES scenario(id),
  recommended_current_option VARCHAR(40),
  future_viable_option VARCHAR(40),
  transport_burden NUMERIC(8,6),
  fit_score INTEGER NOT NULL,
  risk_level VARCHAR(20) NOT NULL,
  explanation TEXT,
  conditions_to_switch TEXT,
  evaluated_options_json JSONB,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_transport_option_scenario
ON transport_option(scenario_id);

CREATE INDEX idx_transport_evaluation_scenario_created
ON transport_evaluation(scenario_id, created_at DESC);

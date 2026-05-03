-- Sprint 6 - Basic Meal Planner.

CREATE TABLE meal_catalog_item (
  id BIGSERIAL PRIMARY KEY,
  code VARCHAR(80) UNIQUE NOT NULL,
  name VARCHAR(160) NOT NULL,
  category VARCHAR(80) NOT NULL,
  estimated_cost_min NUMERIC(14,2),
  estimated_cost_max NUMERIC(14,2),
  prep_time_minutes INTEGER NOT NULL,
  effort_level VARCHAR(20) NOT NULL,
  craving_level VARCHAR(20) NOT NULL,
  budget_level VARCHAR(20) NOT NULL,
  required_equipment TEXT,
  suggested_mode VARCHAR(50),
  description TEXT,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  sort_order INTEGER NOT NULL DEFAULT 100,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_meal_catalog_active_sort
ON meal_catalog_item(is_active, sort_order);

CREATE INDEX idx_meal_catalog_effort_budget
ON meal_catalog_item(effort_level, budget_level);

INSERT INTO meal_catalog_item (
  code,
  name,
  category,
  estimated_cost_min,
  estimated_cost_max,
  prep_time_minutes,
  effort_level,
  craving_level,
  budget_level,
  required_equipment,
  suggested_mode,
  description,
  sort_order
) VALUES
('chicken_tacos', 'Tacos de pollo', 'Fast comfort', 9000, 16000, 20, 'LOW', 'RICH', 'MEDIUM', 'Sartén o air fryer', 'WAR_MODE', 'Fast, tasty, low effort, and easy to repeat.', 10),
('teriyaki_bowl', 'Bowl teriyaki', 'Bowl', 12000, 22000, 25, 'MEDIUM', 'RICH', 'MEDIUM', 'Sartén y rice cooker', 'STABLE_MODE', 'Rice bowl with a sweet-salty sauce profile.', 20),
('homemade_burger', 'Hamburguesa casera', 'Comfort', 14000, 26000, 25, 'MEDIUM', 'RICH', 'HIGH', 'Sartén', 'LIVE_LIFE_MODE', 'Comfort meal without ordering delivery.', 30),
('loaded_potatoes', 'Papas cargadas', 'Comfort', 8000, 16000, 25, 'LOW', 'COMFORT', 'MEDIUM', 'Air fryer u horno', 'LIVE_LIFE_MODE', 'Flexible comfort meal using potatoes and toppings.', 40),
('rice_egg_avocado', 'Arroz con huevo y aguacate', 'Simple', 6000, 12000, 15, 'LOW', 'SIMPLE', 'LOW', 'Rice cooker y sartén', 'AGGRESSIVE_SAVING_MODE', 'Cheap low-effort fallback meal.', 50),
('simple_creamy_pasta', 'Pasta cremosa simple', 'Pasta', 8000, 15000, 20, 'LOW', 'COMFORT', 'LOW', 'Olla', 'RECOVERY_MODE', 'Simple comfort pasta without many steps.', 60),
('stuffed_arepa', 'Arepa rellena', 'Fast', 7000, 14000, 15, 'LOW', 'COMFORT', 'LOW', 'Sartén', 'RECOVERY_MODE', 'Fast filling arepa with simple ingredients.', 70),
('hot_sandwich', 'Sándwich caliente', 'Fast', 6000, 13000, 10, 'LOW', 'SIMPLE', 'LOW', 'Sartén o sandwichera', 'RECOVERY_MODE', 'Very quick low-energy option.', 80),
('air_fryer_bbq_chicken', 'Pollo BBQ air fryer', 'Protein', 12000, 22000, 25, 'LOW', 'RICH', 'MEDIUM', 'Air fryer', 'WAR_MODE', 'Low-effort protein with strong flavor.', 90),
('chicken_rice', 'Arroz con pollo', 'Classic', 10000, 20000, 35, 'MEDIUM', 'COMFORT', 'MEDIUM', 'Olla o rice cooker', 'STABLE_MODE', 'Classic filling meal for normal weeks.', 100),
('eggs_rice', 'Huevos con arroz', 'Simple', 5000, 9000, 10, 'LOW', 'SIMPLE', 'LOW', 'Sartén y rice cooker', 'AGGRESSIVE_SAVING_MODE', 'Ultra cheap fallback meal.', 110),
('tuna_bowl', 'Bowl de atún', 'Bowl', 8000, 15000, 10, 'LOW', 'SIMPLE', 'LOW', 'Ninguno o rice cooker', 'RESET_MODE', 'Quick bowl for cleanup days.', 120),
('air_fryer_potatoes', 'Papas air fryer', 'Side', 5000, 10000, 20, 'LOW', 'COMFORT', 'LOW', 'Air fryer', 'RECOVERY_MODE', 'Easy comfort side or light meal.', 130),
('quesadillas', 'Quesadillas', 'Fast comfort', 7000, 14000, 15, 'LOW', 'COMFORT', 'LOW', 'Sartén', 'LIVE_LIFE_MODE', 'Fast cheesy meal that beats delivery.', 140),
('chicken_wrap', 'Wrap de pollo', 'Fast', 9000, 17000, 15, 'LOW', 'RICH', 'MEDIUM', 'Sartén o air fryer', 'WAR_MODE', 'Portable quick meal with good flavor.', 150)
ON CONFLICT (code) DO NOTHING;

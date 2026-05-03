# Sprint 6 - Meal Planner

## Scope

Sprint 6 adds a basic deterministic Meal Planner for practical meal suggestions based on craving, available time, effort, budget, equipment, and active mode.

Included:

- `meal_catalog_item` table.
- 15 seeded meals.
- Meal catalog API.
- Meal suggestion API with backend fitScore.
- Recommendations that can point to the meal planner.
- Frontend `/meals` page with a simple suggestion form.

Not included:

- AI.
- Nutrition, calories, macros, or fitness diets.
- Inventory.
- Shopping lists.
- Weekly meal prep.
- Long recipes.

## Database

Migration:

```text
backend/src/main/resources/db/migration/V7__meal_planner.sql
```

Seeded meals:

- `chicken_tacos`
- `teriyaki_bowl`
- `homemade_burger`
- `loaded_potatoes`
- `rice_egg_avocado`
- `simple_creamy_pasta`
- `stuffed_arepa`
- `hot_sandwich`
- `air_fryer_bbq_chicken`
- `chicken_rice`
- `eggs_rice`
- `tuna_bowl`
- `air_fryer_potatoes`
- `quesadillas`
- `chicken_wrap`

## Endpoints

```text
GET /api/v1/meals/catalog
POST /api/v1/meals/suggest
```

## Manual QA

```bash
docker compose down -v
docker compose up --build
```

Verify:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/v1/meals/catalog
curl -X POST http://localhost:8080/api/v1/meals/suggest \
  -H "Content-Type: application/json" \
  -d '{"cravingLevel":"RICH","maxPrepTimeMinutes":25,"effortLevel":"LOW","budgetLevel":"MEDIUM","availableEquipment":["AIR_FRYER"]}'
```

UI:

```text
1. Open http://localhost:5173/meals.
2. Submit the default form.
3. Confirm suggestions appear with fitScore and reason.
4. Recalculate recommendations for a scenario with food delivery pressure.
5. Confirm OPEN_MEAL_PLANNER recommendation appears.
```

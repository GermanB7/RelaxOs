# Sprint 3 - Rule-Based Recommendations

## Scope

Sprint 3 implements deterministic recommendations generated from the latest scenario score snapshot, risk factors, scenario, and expenses.

Included:

- `recommendation` and `decision_event` tables.
- Rule-based recommendation engine.
- Deduplication of open recommendations by user, scenario, and source rule key.
- Accept, postpone, and dismiss actions.
- Minimal decision event audit trail.
- Frontend recommendations page, scenario-detail controls, and dashboard summary.

Not included:

- AI.
- Recommendation admin.
- Home Setup Roadmap.
- Modes.
- Meal Planner.
- Transport engine.
- Smart home.
- Dashboard analytics or charts.

## Database

Migration:

```text
backend/src/main/resources/db/migration/V4__recommendations_and_decision_events.sql
```

Tables:

- `recommendation`
- `decision_event`

Indexes:

- `idx_recommendation_user_status_priority`
- `idx_recommendation_scenario_status`
- `idx_recommendation_snapshot`
- `idx_decision_event_user_created`
- `idx_decision_event_scenario_created`

## Endpoints

```text
GET /api/v1/recommendations?status=OPEN&scenarioId=1
POST /api/v1/recommendations/recalculate
POST /api/v1/recommendations/{id}/accept
POST /api/v1/recommendations/{id}/postpone
POST /api/v1/recommendations/{id}/dismiss
GET /api/v1/decisions?scenarioId=1
```

`POST /api/v1/recommendations/recalculate` requires a latest score snapshot. If the scenario has no score, the backend returns `409` with a clear message.

## Rules

- `NEGATIVE_MARGIN_RULE`
- `LOW_EMERGENCY_FUND_RULE`
- `HIGH_RENT_BURDEN_RULE`
- `HIGH_FIXED_BURDEN_RULE`
- `HIGH_DEBT_BURDEN_RULE`
- `FOOD_DELIVERY_PRESSURE_RULE`
- `DATA_QUALITY_RULE`

Reserved placeholders exist for future recalculation/critical-risk behavior, but Sprint 3 only emits the MVP rules above.

## Manual QA

```bash
docker compose down -v
docker compose up --build
```

Verify:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/v1/system/status
curl http://localhost:8080/api/v1/expense-categories
```

API flow:

```bash
curl -X POST http://localhost:8080/api/v1/scenarios \
  -H "Content-Type: application/json" \
  -d '{"name":"Sprint 3 smoke","monthlyIncome":3000000,"emergencyFundCurrent":0,"emergencyFundTarget":6000000}'

curl -X POST http://localhost:8080/api/v1/scenarios/1/expenses \
  -H "Content-Type: application/json" \
  -d '{"categoryId":1,"name":"Rent","amount":1400000,"frequency":"MONTHLY","isEssential":true}'

curl -X POST http://localhost:8080/api/v1/scenarios/1/score/calculate

curl -X POST http://localhost:8080/api/v1/recommendations/recalculate \
  -H "Content-Type: application/json" \
  -d '{"scenarioId":1}'

curl "http://localhost:8080/api/v1/recommendations?scenarioId=1&status=OPEN"

curl -X POST http://localhost:8080/api/v1/recommendations/1/accept \
  -H "Content-Type: application/json" \
  -d '{"reason":"Makes sense for current scenario"}'

curl "http://localhost:8080/api/v1/decisions?scenarioId=1"
```

UI flow:

```text
1. Open http://localhost:5173.
2. Create a scenario.
3. Add expenses.
4. Calculate Score.
5. Recalculate Recommendations.
6. Open /recommendations.
7. Accept, postpone, or dismiss one recommendation.
8. Confirm status filtering reflects the action.
```

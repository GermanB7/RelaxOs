# Sprint 5 - Adaptive Modes

## Scope

Sprint 5 adds a basic Adaptive Modes catalog and one active temporary mode per user. Modes affect backend recommendations through deterministic rules.

Included:

- `adaptive_mode` catalog.
- `mode_activation` history.
- Activate/end active mode.
- `decision_event` entries for mode activation and ending.
- Active mode context inside the recommendation engine.
- Frontend `/modes`, dashboard banner, recommendations badge, and scenario-detail banner.

Not included:

- AI.
- Calendar.
- Task manager.
- Daily routine engine.
- Meal planner.
- Smart home.
- Transport engine.
- Dashboard analytics or charts.

## Database

Migration:

```text
backend/src/main/resources/db/migration/V6__adaptive_modes.sql
```

Tables:

- `adaptive_mode`
- `mode_activation`

Seeded modes:

- `WAR_MODE`
- `STABLE_MODE`
- `LIVE_LIFE_MODE`
- `RECOVERY_MODE`
- `AGGRESSIVE_SAVING_MODE`
- `RESET_MODE`

## Endpoints

```text
GET /api/v1/modes
GET /api/v1/modes/active
POST /api/v1/modes/activate
POST /api/v1/modes/active/end
GET /api/v1/modes/history
```

## Manual QA

```bash
docker compose down -v
docker compose up --build
```

Verify:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/v1/modes
curl http://localhost:8080/api/v1/modes/active
```

Activate:

```bash
curl -X POST http://localhost:8080/api/v1/modes/activate \
  -H "Content-Type: application/json" \
  -d '{"modeCode":"WAR_MODE","scenarioId":1,"objective":"Save aggressively for independence","durationDays":30,"intensityLevel":"HIGH","notes":"Sprint 5 manual test"}'
```

End:

```bash
curl -X POST http://localhost:8080/api/v1/modes/active/end \
  -H "Content-Type: application/json" \
  -d '{"reason":"Manual test completed"}'
```

UI:

```text
1. Open http://localhost:5173/modes.
2. Activate Modo Guerra.
3. Confirm the banner appears on Dashboard.
4. Recalculate recommendations.
5. Confirm mode-based recommendations appear when conditions match.
6. End mode and check history.
```

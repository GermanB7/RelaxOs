# Sprint 7 - Dashboard Integration & MVP Polish

## Scope

Sprint 7 turns the Dashboard into a usable MVP command center. It aggregates existing backend data from profile, scenarios, score, risks, recommendations, adaptive modes, home setup, and meal planner.

Not included: charts, analytics, AI, notifications, calendar, production deploy, native mobile app, admin tooling, task manager.

## Backend

Endpoint:

```text
GET /api/v1/dashboard
```

The Dashboard API returns:

- Minimal profile.
- Primary scenario summary.
- Latest score, if one exists.
- Top risks from the latest score.
- Top 3 open recommendations.
- Active mode summary.
- Home setup summary.
- Meal planner CTA.

Rules:

- Dashboard does not calculate score automatically.
- Dashboard does not recalculate recommendations automatically.
- Dashboard is empty-state friendly when no scenario, score, recommendations, or home roadmap exists.

## Frontend

Route:

```text
/
```

Dashboard cards:

- Scenario summary.
- Score and risks.
- Top open recommendations.
- Active mode.
- Home setup.
- Meal planner CTA.
- Quick actions.

Quick actions:

- Create scenario.
- Calculate score.
- Recalculate recommendations.
- Open home setup.
- Activate mode.
- Open meal planner.

## Manual QA Checklist

1. Reset and start Docker:
   ```bash
   docker compose down -v
   docker compose up --build
   ```

2. Verify platform basics:
   ```bash
   curl http://localhost:8080/actuator/health
   curl http://localhost:8080/api/v1/system/status
   curl http://localhost:8080/api/v1/dashboard
   ```

3. Open:
   ```text
   http://localhost:5173
   http://localhost:8080/swagger-ui/index.html
   ```

4. Create a scenario.

5. Add expenses.

6. Calculate score.

7. Recalculate recommendations.

8. Initialize home setup.

9. Activate a mode.

10. Open `/meals` and request suggestions.

11. Return to Dashboard and confirm every card reflects real backend data.

## Expected Empty States

- No scenario: Dashboard shows create scenario CTA.
- No score: score card shows not calculated yet.
- No recommendations: recommendations card points user to scenarios.
- No home roadmap: home setup card asks to initialize roadmap.
- No active mode: mode card asks to activate one.

## Not Now

- No charting.
- No analytics engine.
- No AI.
- No calendar.
- No notifications.
- No deploy production.
- No admin tooling.

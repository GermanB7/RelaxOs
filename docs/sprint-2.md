# Sprint 2 Docker Dev + Score and Risk

## Sprint 2A Docker Dev Environment

Run the full local stack:

```bash
docker compose up --build
```

Services:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- Actuator: `http://localhost:8080/actuator/health`

Reset the local Docker database:

```bash
docker compose down -v
docker compose up --build
```

## Sprint 2B Score and Risk Engine

Endpoints:

```text
POST /api/v1/scenarios/{scenarioId}/score/calculate
GET /api/v1/scenarios/{scenarioId}/score/latest
GET /api/v1/scenarios/{scenarioId}/score/history
```

The backend calculates and persists:

- score 0-100
- textual status
- confidence level
- score factors
- risk factors
- score snapshot

The frontend only displays backend results.

## Manual Smoke

1. Run `docker compose up --build`.
2. Open `http://localhost:5173`.
3. Create a scenario.
4. Add expenses for rent, utilities, internet, groceries, and food delivery.
5. Open the scenario detail page.
6. Click `Calculate Score`.
7. Confirm the score card, factors list, and risks list render.
8. Open `http://localhost:8080/swagger-ui/index.html` and test the score endpoints.

## Non-Goals

- No recommendation engine.
- No AI.
- No home setup roadmap.
- No adaptive modes.
- No meal planner.
- No smart home.
- No transport engine.
- No fancy dashboard.

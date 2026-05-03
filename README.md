# TranquiloOS / IndependenceOS

Sprint 4 adds Home Setup Roadmap: a personal purchase tracker for home essentials organized by priority tiers, with status management, custom items, and recommendation guidance.

Previous sprints: Score snapshots, risks, deterministic recommendation engine, basic infrastructure.

## Requirements

- Java 21
- Node.js LTS
- npm
- Docker

## Environment

Copy `.env.example` to `.env` if you want Docker Compose and local apps to share the same values.

```bash
POSTGRES_DB=tranquiloos
POSTGRES_USER=tranquiloos
POSTGRES_PASSWORD=tranquiloos_dev
DATABASE_URL=jdbc:postgresql://localhost:5432/tranquiloos
FRONTEND_URL=http://localhost:5173
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

## Run PostgreSQL

```bash
docker compose up -d postgres
docker compose ps
docker compose logs postgres
```

Expected result: `postgres` is `healthy` and listening on `localhost:5432`.

## Run Full Stack With Docker

```bash
docker compose up --build
```

Expected result:

- PostgreSQL is healthy.
- Backend runs at `http://localhost:8080`.
- Frontend runs at `http://localhost:5173`.
- Flyway applies all pending migrations.

Reset the Docker database:

```bash
docker compose down -v
docker compose up --build
```

Main URLs:

```text
http://localhost:5173
http://localhost:8080/api/v1/system/status
http://localhost:8080/actuator/health
http://localhost:8080/swagger-ui/index.html
```

## Run Backend

Linux/macOS:

```bash
cd backend
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Expected result: Spring Boot starts on `http://localhost:8080`, Flyway applies pending migrations, and the Sprint 1 local user plus expense categories are available.

## Backend Tests

Linux/macOS:

```bash
cd backend
./mvnw test
```

Windows PowerShell:

```powershell
cd backend
.\mvnw.cmd test
```

Expected result: Maven reports `BUILD SUCCESS`.

## Verify Backend

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/v1/system/status
```

Expected health response:

```json
{"status":"UP"}
```

Expected system status response:

```json
{"app":"TranquiloOS","status":"UP","version":"0.0.1"}
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Core Endpoints

Profile:

```text
GET /api/v1/me/profile
PUT /api/v1/me/profile
```

Scenarios:

```text
GET /api/v1/scenarios
POST /api/v1/scenarios
GET /api/v1/scenarios/{id}
PUT /api/v1/scenarios/{id}
POST /api/v1/scenarios/{id}/duplicate
GET /api/v1/scenarios/{id}/summary
```

Expenses:

```text
GET /api/v1/expense-categories
GET /api/v1/scenarios/{scenarioId}/expenses
POST /api/v1/scenarios/{scenarioId}/expenses
PUT /api/v1/scenarios/{scenarioId}/expenses/{expenseId}
DELETE /api/v1/scenarios/{scenarioId}/expenses/{expenseId}
```

Score:

```text
POST /api/v1/scenarios/{scenarioId}/score/calculate
GET /api/v1/scenarios/{scenarioId}/score/latest
GET /api/v1/scenarios/{scenarioId}/score/history
```

Recommendations:

```text
GET /api/v1/recommendations
POST /api/v1/recommendations/recalculate
POST /api/v1/recommendations/{id}/accept
POST /api/v1/recommendations/{id}/postpone
POST /api/v1/recommendations/{id}/dismiss
GET /api/v1/decisions
```

Adaptive Modes:

```text
GET /api/v1/modes
GET /api/v1/modes/active
POST /api/v1/modes/activate
POST /api/v1/modes/active/end
GET /api/v1/modes/history
```

Meal Planner:

```text
GET /api/v1/meals/catalog
POST /api/v1/meals/suggest
```

## Manual API Smoke

```bash
curl http://localhost:8080/api/v1/expense-categories
curl http://localhost:8080/api/v1/me/profile
curl -X POST http://localhost:8080/api/v1/scenarios \
  -H "Content-Type: application/json" \
  -d '{"name":"Bogota solo","monthlyIncome":3000000,"emergencyFundCurrent":500000,"emergencyFundTarget":6000000}'
curl http://localhost:8080/api/v1/scenarios
curl -X POST http://localhost:8080/api/v1/scenarios/1/expenses \
  -H "Content-Type: application/json" \
  -d '{"categoryId":1,"name":"Rent","amount":1200000,"frequency":"MONTHLY","isEssential":true}'
curl http://localhost:8080/api/v1/scenarios/1/summary
curl -X POST http://localhost:8080/api/v1/scenarios/1/score/calculate
curl -X POST http://localhost:8080/api/v1/recommendations/recalculate \
  -H "Content-Type: application/json" \
  -d '{"scenarioId":1}'
curl "http://localhost:8080/api/v1/recommendations?scenarioId=1&status=OPEN"
```

Expected summary shape:

```json
{
  "scenarioId": 1,
  "monthlyIncome": 3000000,
  "totalMonthlyExpenses": 1200000,
  "estimatedMonthlyAvailable": 1800000,
  "expenseCount": 1
}
```

## Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Expected result: Vite starts on `http://localhost:5173`.

Manual UI flow:

```text
http://localhost:5173/scenarios
http://localhost:5173/settings
```

Expected result: create a scenario, open its detail page, add an expense, and see the backend summary update.

Score UI flow:

```text
1. Create a scenario.
2. Add rent, utilities, internet, groceries, and food delivery expenses.
3. Open the scenario detail page.
4. Click Calculate Score.
5. Confirm score, status, summary, factors, and risks appear.
```

Recommendations UI flow:

```text
1. Open a scenario detail page.
2. Click Calculate Score if no score exists.
3. Click Recalculate Recommendations.
4. Open http://localhost:5173/recommendations.
5. Filter by OPEN, ACCEPTED, POSTPONED, or DISMISSED.
6. Accept, postpone, or dismiss one recommendation.
7. Confirm it disappears from OPEN and appears under the selected status.
```

Adaptive Modes UI flow:

```text
1. Open http://localhost:5173/modes.
2. Activate WAR_MODE for 30 days.
3. Confirm the active mode banner appears on Dashboard.
4. Recalculate recommendations for a scenario.
5. Confirm mode-based recommendations can appear.
6. End the active mode.
7. Confirm mode history and decision events update.
```

Adaptive Modes API smoke:

```bash
curl http://localhost:8080/api/v1/modes
curl -X POST http://localhost:8080/api/v1/modes/activate \
  -H "Content-Type: application/json" \
  -d '{"modeCode":"WAR_MODE","scenarioId":1,"objective":"Save aggressively for independence","durationDays":30,"intensityLevel":"HIGH","notes":"Manual test"}'
curl http://localhost:8080/api/v1/modes/active
curl -X POST http://localhost:8080/api/v1/modes/active/end \
  -H "Content-Type: application/json" \
  -d '{"reason":"Manual test completed"}'
curl http://localhost:8080/api/v1/modes/history
curl http://localhost:8080/api/v1/decisions
```

## Sprint 6 - Meal Planner

Manual UI flow:

```text
1. Open http://localhost:5173/meals.
2. Select craving RICH.
3. Use max time 25.
4. Select effort LOW.
5. Select budget MEDIUM.
6. Keep AIR_FRYER checked.
7. Click Suggest meals.
8. Confirm 3-5 suggestions, fitScore, and reason appear.
```

Manual API smoke:

```bash
curl http://localhost:8080/api/v1/meals/catalog
curl -X POST http://localhost:8080/api/v1/meals/suggest \
  -H "Content-Type: application/json" \
  -d '{"cravingLevel":"RICH","maxPrepTimeMinutes":25,"effortLevel":"LOW","budgetLevel":"MEDIUM","availableEquipment":["AIR_FRYER","RICE_COOKER"]}'
```

Accept a recommendation by API after listing recommendations:

```bash
curl -X POST http://localhost:8080/api/v1/recommendations/1/accept \
  -H "Content-Type: application/json" \
  -d '{"reason":"Makes sense for current scenario"}'
curl "http://localhost:8080/api/v1/decisions?scenarioId=1"
```

## Sprint 4 — Home Setup Roadmap

Home Setup Roadmap allows users to plan their home purchases in tiers of necessity, track status, and receive guidance.

### Endpoints

```text
GET /api/v1/home/catalog
POST /api/v1/home/roadmap/initialize
GET /api/v1/home/roadmap
GET /api/v1/home/roadmap/summary
POST /api/v1/home/roadmap/items
PUT /api/v1/home/roadmap/items/{id}
PATCH /api/v1/home/roadmap/items/{id}/status
DELETE /api/v1/home/roadmap/items/{id}
```

### Manual QA Flow

1. **Ensure full stack is running:**
   ```bash
   docker compose down -v
   docker compose up --build
   ```

2. **Create a scenario:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/scenarios \
     -H "Content-Type: application/json" \
     -d '{"name":"Home Setup Test","monthlyIncome":3000000,"emergencyFundCurrent":500000}'
   ```

3. **Calculate score:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/scenarios/1/score/calculate
   ```

4. **Initialize home roadmap:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/home/roadmap/initialize \
     -H "Content-Type: application/json" \
     -d '{"scenarioId":1}'
   ```

5. **View roadmap:**
   ```bash
   curl http://localhost:8080/api/v1/home/roadmap?scenarioId=1
   ```

6. **Mark item as bought:**
   ```bash
   curl -X PATCH http://localhost:8080/api/v1/home/roadmap/items/1/status \
     -H "Content-Type: application/json" \
     -d '{"status":"BOUGHT","actualPrice":850000,"reason":"Purchased at discount"}'
   ```

7. **Add custom item:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/home/roadmap/items \
     -H "Content-Type: application/json" \
     -d '{"scenarioId":1,"name":"Cortina blackout","category":"Dormir","tier":"TIER_2","estimatedPrice":220000}'
   ```

8. **View summary:**
   ```bash
   curl http://localhost:8080/api/v1/home/roadmap/summary?scenarioId=1
   ```

9. **Recalculate recommendations:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/recommendations/recalculate \
     -H "Content-Type: application/json" \
     -d '{"scenarioId":1}'
   ```

10. **Frontend workflow:**
    - Open http://localhost:5173/home-setup
    - Click "Inicializar desde catálogo"
    - View summary and items by tier
    - Mark 2–3 items as BOUGHT
    - Add a custom item
    - Filter by status/tier/category
    - Go to /recommendations and confirm home-setup recommendations

For complete Sprint 4 documentation, see [docs/sprint-4.md](docs/sprint-4.md).

## Sprint 7 - Dashboard Integration

The Dashboard is now the MVP command center. It aggregates existing backend data and does not calculate score or recommendations automatically.

### Endpoint

```text
GET /api/v1/dashboard
```

### Manual MVP Flow

1. Start the full stack:
   ```bash
   docker compose down -v
   docker compose up --build
   ```

2. Open the frontend:
   ```text
   http://localhost:5173
   ```

3. Create a scenario and add expenses from `/scenarios`.

4. Calculate score from the scenario detail page or Dashboard quick action.

5. Recalculate recommendations from the scenario detail page, `/recommendations`, or Dashboard quick action.

6. Initialize Home Setup from `/home-setup`.

7. Activate an adaptive mode from `/modes`.

8. Request meals from `/meals`.

9. Return to `/` and confirm Dashboard shows:
   - Profile
   - Primary scenario
   - Latest score
   - Top risks
   - Top 3 open recommendations
   - Active mode
   - Home setup summary
   - Meal planner CTA

For complete Sprint 7 documentation, see [docs/sprint-7.md](docs/sprint-7.md).

## Frontend Build

```bash
cd frontend
npm run build
```

Expected result: TypeScript and Vite finish successfully and create `frontend/dist`.

## Private Prod-Like Deploy

Sprint 8 adds a private production-like stack for real personal use outside local dev.

```bash
cp .env.prod.example .env.prod
```

Edit `.env.prod` and replace `change_me_strong_password` with a strong password. Then run:

```bash
docker compose -f docker-compose.prod.yml --env-file .env.prod up --build -d
```

Open:

```text
http://localhost
http://localhost/swagger-ui/index.html
```

Health:

```bash
./scripts/check-health.sh
```

Backup:

```bash
./scripts/backup-db.sh
```

Restore:

```bash
./scripts/restore-db.sh backups/file.sql
```

Recovery summary:

1. Stop backend/frontend.
2. Keep or start Postgres.
3. Restore the selected backup.
4. Start backend/frontend.
5. Run health checks.
6. Open Dashboard.

Detailed docs:

- [Private deploy](docs/deploy-private.md)
- [Backup and restore](docs/backup-restore.md)

## Sprint 9 - Auth

Auth is now required for private MVP data endpoints. Register the first private user from the UI or API.

### Endpoints

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
GET /api/v1/auth/me
```

### Environment

```text
JWT_SECRET=change_me_dev_secret_at_least_32_chars
JWT_EXPIRATION_MINUTES=1440
```

For production-like deploy, change `JWT_SECRET` in `.env.prod` before starting the stack.

### Manual Flow

1. Open `http://localhost:5173/register`.
2. Create a user.
3. Create a scenario.
4. Add expenses.
5. Calculate score.
6. Logout.
7. Login again.
8. Confirm data is still visible.
9. Confirm protected endpoints without token return `401`.

API example:

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"strong-password","displayName":"Juan","city":"Bogota","currency":"COP"}'
```

Use the returned token:

```bash
curl http://localhost:8080/api/v1/dashboard \
  -H "Authorization: Bearer TOKEN_HERE"
```

For full Sprint 9 notes, see [docs/sprint-9.md](docs/sprint-9.md).

## Reset Local Database

Use this when you want Flyway to reapply all local migrations from a clean volume.

```bash
docker compose down -v
docker compose up -d postgres
```

Then restart the backend with `./mvnw spring-boot:run` or `.\mvnw.cmd spring-boot:run`.

## Troubleshooting

- Port `5432` occupied: stop the local PostgreSQL service or change the compose port mapping.
- Backend cannot connect to DB: confirm `docker compose ps` shows Postgres as healthy and `DATABASE_URL` points to `jdbc:postgresql://localhost:5432/tranquiloos`.
- CORS errors: confirm `FRONTEND_URL=http://localhost:5173` when running Vite locally.
- Missing variables: compare your local environment with `.env.example`.
- Maven unavailable globally: use the Maven Wrapper included in `backend` with `./mvnw` or `.\mvnw.cmd`.
- Existing local data looks stale: reset the Docker volume with `docker compose down -v`.

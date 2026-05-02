# TranquiloOS / IndependenceOS

Sprint 1 builds on the MVP foundation with local profile, independence scenarios, scenario expenses, backend monthly summaries, and real frontend API integration.

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

## Sprint 1 Endpoints

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

## Manual Sprint 1 Smoke

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

## Frontend Build

```bash
cd frontend
npm run build
```

Expected result: TypeScript and Vite finish successfully and create `frontend/dist`.

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

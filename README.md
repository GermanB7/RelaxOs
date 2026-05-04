# TranquiloOS — Personal Independence System

TranquiloOS is a personal decision system for independent living.
It helps a user model move-out scenarios, understand financial risk, choose safer next actions, and avoid impulse decisions under pressure.
It is not a generic budgeting app: the product is built around scenario thinking, independence readiness, and behavior-aware recommendations.

## Core Features

- Scenario modeling: compare possible living situations with income, expenses, emergency fund, and status.
- Independence score: calculate a readiness score from rent burden, monthly margin, emergency coverage, fixed costs, debt, and food delivery pressure.
- Risk detection: surface the main risks that make a scenario fragile or unsafe.
- Recommendation engine: generate deterministic next actions from score, risks, modes, home setup, and transport context.
- Home setup roadmap: organize purchases by necessity tier and track bought, postponed, wishlist, or skipped items.
- Adaptive modes: activate short-term operating modes such as aggressive saving, recovery, reset, or stable mode.
- Meal planner: get low-friction meal suggestions based on craving, effort, budget, equipment, and active mode.
- Decision timeline: keep product-level memory of meaningful decisions.
- Scenario comparison: compare 2-4 scenarios and recommend the strongest option without blindly choosing the highest score.
- Transport engine MVP: evaluate manual transport options such as public transport, Uber/Didi, motorcycle, car, bike, walking, and mixed plans.
- Command Center dashboard: see current status, main risk, next best action, transport summary, home priority, recommendations, and recent decisions.
- Internal admin MVP: manage catalogs, settings, recommendation copy, imports/exports, and audit log without direct database editing.

## Architecture

- Spring Boot backend as a modular monolith.
- React + Vite frontend.
- PostgreSQL database.
- Docker Compose for dev and private prod-like deployment.
- Flyway migrations for schema evolution and seed data.
- JWT authentication for private user data.
- Deterministic domain rules before AI or external integrations.

The backend owns score, recommendation, comparison, transport, and dashboard decision logic. The frontend presents data and submits user actions.

## Why This Project Matters

Most budgeting tools record what happened. TranquiloOS helps decide what to do next.

The project demonstrates system thinking: the user is not only entering expenses, they are evaluating life options under constraints. A scenario can look affordable but still be risky because emergency coverage is weak, transport costs are too high, or home setup purchases are poorly timed.

The codebase is intentionally a modular monolith. For an MVP with tightly related domains, that keeps transactions, ownership checks, and deployment simple while still separating users, scenarios, expenses, scoring, recommendations, modes, home setup, meals, decisions, comparison, transport, dashboard, and admin.

## Screenshots

Screenshots are intentionally left as portfolio placeholders until final capture.

- Dashboard / Command Center: `docs/screenshots/dashboard.png`
- Scenario detail with score and transport: `docs/screenshots/scenario-detail.png`
- Recommendations center: `docs/screenshots/recommendations.png`
- Decision timeline: `docs/screenshots/decisions.png`
- Internal admin panel: `docs/screenshots/admin.png`

## Demo Flow

- Register or log in.
- Create a move-out scenario.
- Add rent, utilities, groceries, transport, and discretionary expenses.
- Calculate the independence score.
- Review risks and recommendations.
- Compare scenarios and select the stronger option.
- Initialize the home setup roadmap.
- Mark one item as bought and postpone another.
- Activate an adaptive mode.
- Evaluate transport options.
- Request meal suggestions.
- Return to the dashboard and show the system-level next action.

## Development

Requirements:

- Docker Desktop
- Java 21
- Node.js and npm, only needed when running backend/frontend outside Docker

Run the full local stack:

```bash
docker compose up --build
```

Open:

```text
http://localhost:5173
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/api/v1/system/status
http://localhost:8080/actuator/health
```

Run backend tests:

```powershell
cd backend
.\mvnw.cmd test
```

Run frontend checks:

```powershell
cd frontend
npm run lint
npm run build
```

## Production-Like Local

Production-like local mode uses the same three-service shape expected on a future private server: PostgreSQL, Spring Boot, and Nginx serving the Vite build.

Create a local prod env file:

```bash
cp .env.prod.example .env.prod
```

Edit `.env.prod` before running it:

- Replace `POSTGRES_PASSWORD` with a strong password.
- Make `SPRING_DATASOURCE_PASSWORD` match `POSTGRES_PASSWORD`.
- Replace `JWT_SECRET` with a long random value of at least 32 characters.
- Set `FRONTEND_URL` to the browser origin allowed by CORS.
- Set `VITE_API_BASE_URL` to the public API path used by the frontend.

Start the stack:

```bash
docker compose -f docker-compose.prod.yml --env-file .env.prod up --build -d
```

For prod-like local behind Nginx:

```bash
FRONTEND_HEALTH_URL=http://localhost \
BACKEND_HEALTH_URL=http://localhost/actuator/health \
SYSTEM_STATUS_URL=http://localhost/api/v1/system/status \
  ./scripts/check-health.sh
```

On Windows without a Unix shell, use the equivalent HTTP checks:

```powershell
Invoke-RestMethod http://localhost/actuator/health
Invoke-RestMethod http://localhost/api/v1/system/status
Invoke-WebRequest http://localhost/
```

## CI

GitHub Actions is prepared in `.github/workflows/ci.yml`.

It runs on pushes to `main` and pull requests targeting `main`:

- Backend: Java 21 setup plus `./mvnw test`.
- Frontend: Node LTS setup, `npm ci`, and `npm run build`.
- Docker validation: builds backend and frontend images.

The workflow intentionally does not deploy, push Docker images, or require server secrets yet.

## Private Deploy

Future VPS deployment is documented in [docs/deploy-private.md](docs/deploy-private.md).

The prepared script is:

```bash
DEPLOY_HOST=your-host \
DEPLOY_USER=your-user \
DEPLOY_PATH=/path/to/tranquiloos \
  ./scripts/deploy.sh
```

Do not run it until a VPS exists, SSH access is configured, and `.env.prod` has been created manually on the server.

## Backup & Restore

Create a database backup:

```bash
./scripts/backup-db.sh
```

Restore a backup:

```bash
./scripts/restore-db.sh backups/<file>.sql
```

Backup and restore are documented in [docs/backup-restore.md](docs/backup-restore.md).

The backup script keeps the latest 7 SQL dumps by default. Override with `KEEP_BACKUPS=<N>` if needed.

## Tech Decisions

- Modular monolith over microservices: the product is still one cohesive decision system, so distributed services would add operational cost without product value.
- Rules before AI: early correctness and explainability matter more than probabilistic output.
- BigDecimal for money: financial calculations must avoid floating-point drift.
- Flyway for migrations: schema changes are reviewable and reproducible from a clean database.
- JSONB only for snapshots/context: core entities stay relational; JSONB is used for audit, decision context, and historical snapshots.
- JWT auth: enough for a private MVP while keeping the API stateless.

## Future Work

- Capture final screenshots and short product video.
- Add role-based admin authorization.
- Add focused integration tests for the browser flow.
- Add richer seed import validation and admin previews.
- Add optional analytics around decision outcomes.

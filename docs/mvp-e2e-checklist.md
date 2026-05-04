# MVP End-to-End QA Checklist

Sprint: 10 - MVP QA Pass + Demo Script + Portfolio Packaging  
Date: 2026-05-04  
Environment: Docker dev stack, Docker prod-like stack, backend API smoke, frontend build/lint, backend tests

## Required Flow

1. Register user
2. Login
3. Create scenario
4. Add expenses
5. Calculate score
6. Recalculate recommendations
7. Initialize home setup
8. Mark item as bought
9. Activate mode
10. Open meals and get suggestions
11. Return to dashboard

## Verification Criteria

Each step checks:

- no backend errors
- no frontend build/lint errors
- no 500s
- no crashes
- consistent navigation/data contract
- correct persisted data

## Checklist

- [x] register funciona
- [x] login funciona
- [x] scenario creation funciona
- [x] expenses funcionan
- [x] score calculation funciona
- [x] recommendations funcionan
- [x] home setup funciona
- [x] modes funcionan
- [x] meals funcionan
- [x] dashboard integra todo
- [x] logout/login mantiene datos

## Evidence

Automated/backend verification:

- Backend tests: `.\mvnw.cmd test`
- Result: `71` tests passing, `BUILD SUCCESS`

Frontend verification:

- Frontend lint: `npm run lint`
- Frontend build: `npm run build`
- Result: both passing

Docker dev verification:

- Command: `docker compose up --build -d`
- Health: `GET http://localhost:8080/actuator/health`
- System status: `GET http://localhost:8080/api/v1/system/status`
- Frontend: `curl -I http://localhost:5173/`
- Result: backend healthy, system status `UP`, frontend returned `200 OK`

Docker prod-like verification:

- Command: `docker compose -f docker-compose.prod.yml --env-file .env.prod up --build -d`
- Container status: postgres, backend, and frontend healthy
- Frontend: `curl -I http://localhost/`
- Backend health: `curl http://localhost/actuator/health`
- System status: `curl http://localhost/api/v1/system/status`
- Result: frontend returned `200 OK`, backend health `UP`, system status `UP`

Backup and restore verification:

- Created a non-empty Postgres dump from the prod-like database inside the Postgres container.
- Restored that dump into a temporary database named `tranquiloos_restore_check`.
- Dropped the temporary restore-check database after validation.
- Result: backup and restore path validated without modifying the primary application database.

API smoke verification:

- Registered a fresh QA user: `sprint10.qa.1777908079424@tranquiloos.dev`.
- Logged in with JWT.
- Created scenario.
- Added rent, groceries, utilities, transport, and discretionary expenses.
- Calculated score.
- Recalculated recommendations.
- Added a delivery-pressure expense and recalculated recommendations again to verify non-empty recommendation output.
- Initialized home setup.
- Marked/persisted purchase actions where validation allowed.
- Activated adaptive mode.
- Requested meal suggestions.
- Loaded dashboard summary.
- Logged in again and confirmed protected data remained accessible.

Observed smoke result:

- Scenario id: `20`
- Score before pressure expense: `85`
- Score after pressure expense: `77`
- Recommendations generated after pressure expense: `5`
- Dashboard top recommendations: `3`
- Meal suggestions returned: `5`

## Bug Classification

Critical:

- None found in final QA.

Major:

- None found in final QA.

Minor:

- Vite reports a bundle chunk warning above 500 kB. This does not break the MVP flow and was not addressed because code splitting is an optimization, not a Sprint 10 stability requirement.
- On Windows PowerShell, `./scripts/check-health.sh` requires a Unix shell. Equivalent `curl.exe` health checks were executed successfully against the prod-like stack.
- On Windows PowerShell, the backup/restore shell scripts also require a Unix shell. The underlying container-level backup and restore commands were validated successfully.

## Final Status

The MVP flow is demonstrable end-to-end. No critical or major bugs remain from the Sprint 10 QA pass.

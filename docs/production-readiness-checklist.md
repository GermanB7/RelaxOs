# Production Readiness Checklist

Sprint: 11 - Production Readiness & CI/CD Preparation

## Checklist

- [x] docker-compose.prod.yml probado
- [x] .env.prod.example completo
- [x] CI workflow creado
- [x] backend tests pasan
- [x] frontend build pasa
- [x] Docker builds pasan
- [x] backup probado
- [x] restore probado en base temporal
- [x] healthcheck probado
- [x] CORS prod revisado
- [x] JWT_SECRET por env
- [x] no secretos reales en repo
- [x] Swagger decisión documentada
- [x] dominio pendiente
- [x] HTTPS pendiente
- [x] VPS pendiente

## Current Local Evidence

- Backend tests: `cd backend && ./mvnw test`
- Frontend build: `cd frontend && npm run build`
- Prod-like stack: `docker compose -f docker-compose.prod.yml --env-file .env.prod up -d --build`
- Health check through Nginx:
  ```bash
  FRONTEND_HEALTH_URL=http://localhost BACKEND_HEALTH_URL=http://localhost/actuator/health SYSTEM_STATUS_URL=http://localhost/api/v1/system/status ./scripts/check-health.sh
  ```
- Backup: `./scripts/backup-db.sh`
- Restore: validated in a temporary database, not against the primary app database.

## Explicitly Deferred

- Real VPS.
- Real domain.
- Real HTTPS.
- Automatic deployment.
- Container registry publishing.
- Cloud backups.
- Kubernetes or autoscaling.

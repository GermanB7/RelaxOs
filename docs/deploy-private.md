# Private Deploy Guide

This guide prepares TranquiloOS for a future private VPS deployment. It does not assume a real VPS, domain, or HTTPS certificate exists yet.

## Future Server Requirements

- Ubuntu 22.04 or newer.
- Docker Engine.
- Docker Compose plugin.
- Git.
- SSH access restricted to trusted operators.
- Port `80` open for the initial HTTP deployment.
- Port `443` reserved for the later HTTPS sprint.
- PostgreSQL port not exposed publicly.

## First Server Setup

Clone the repository on the server:

```bash
git clone <repo-url> tranquiloos
cd tranquiloos
```

Create the production env file manually:

```bash
cp .env.prod.example .env.prod
```

Edit `.env.prod`:

- `POSTGRES_PASSWORD`: strong random value.
- `SPRING_DATASOURCE_PASSWORD`: same value as `POSTGRES_PASSWORD`.
- `JWT_SECRET`: strong random value with at least 32 characters.
- `FRONTEND_URL`: final public origin, for example `http://your-host` before HTTPS exists.
- `VITE_API_BASE_URL`: final public API path, for example `http://your-host/api/v1`.

Never commit `.env.prod`.

## Start Production-Like Stack

```bash
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d --build
```

Services:

- `postgres`: private Docker network only, persistent named volume.
- `backend`: Spring Boot `prod` profile, env-driven datasource, Flyway enabled, healthcheck enabled.
- `frontend`: Nginx serving static Vite output, SPA fallback enabled, API proxied to backend.

## Health Check

On a server where Nginx proxies the backend:

```bash
FRONTEND_HEALTH_URL=http://localhost \
BACKEND_HEALTH_URL=http://localhost/actuator/health \
SYSTEM_STATUS_URL=http://localhost/api/v1/system/status \
  ./scripts/check-health.sh
```

Expected result:

```text
All health checks passed.
```

## Prepared Deploy Script

When the VPS exists and SSH is configured:

```bash
DEPLOY_HOST=your-host \
DEPLOY_USER=your-user \
DEPLOY_PATH=/path/to/tranquiloos \
  ./scripts/deploy.sh
```

The script validates required variables, runs `git pull`, rebuilds the prod compose stack, and runs health checks. It contains no real IP, user, path, or secret.

## Domain And HTTPS Later

Not included in Sprint 11:

- Real domain setup.
- Real HTTPS certificates.
- Reverse proxy certificate automation.

When those are added, update:

- `FRONTEND_URL=https://your-domain`
- `VITE_API_BASE_URL=https://your-domain/api/v1`
- Nginx or external reverse proxy TLS configuration.

## Swagger Decision

Swagger is still reachable in prod-like mode for the private MVP because it is useful during portfolio/demo QA. Before exposing the system beyond a trusted private environment, disable or protect:

- `/swagger-ui/**`
- `/v3/api-docs/**`

## Pre-Deploy Checklist

- `JWT_SECRET` is strong and not a placeholder.
- `POSTGRES_PASSWORD` is strong and not a placeholder.
- `SPRING_DATASOURCE_PASSWORD` matches `POSTGRES_PASSWORD`.
- `FRONTEND_URL` matches the real public origin.
- CORS does not use `*` in prod.
- PostgreSQL is not publicly exposed.
- Backups run successfully.
- Restore has been tested in a disposable database.
- `./scripts/check-health.sh` passes.
- `.env.prod` is ignored by git.

## Stop Stack

```bash
docker compose -f docker-compose.prod.yml --env-file .env.prod down
```

Do not run `down -v` on a real deployment unless you intentionally want to delete the database volume.

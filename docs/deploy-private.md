# Private Deploy Guide

This guide runs TranquiloOS as a private production-like Docker Compose stack.

## Requirements

- A small VPS or private machine.
- Docker and Docker Compose plugin installed.
- Ports open:
  - `80` for the frontend and proxied API.
  - SSH only from trusted IPs.
- The repository copied to the server.

## Environment

Create a private env file:

```bash
cp .env.prod.example .env.prod
```

Edit `.env.prod`:

- Set a strong `POSTGRES_PASSWORD`.
- Make `SPRING_DATASOURCE_PASSWORD` match it.
- Set a strong `JWT_SECRET` with at least 32 characters.
- Set `FRONTEND_URL` to your private URL.
- Set `VITE_API_BASE_URL` to `http://your-host/api/v1`.

Do not commit `.env.prod`.

## Start Stack

```bash
docker compose -f docker-compose.prod.yml --env-file .env.prod up --build -d
```

Services:

- `postgres`: private Docker network only, persistent volume.
- `backend`: Spring Boot profile `prod`, Flyway enabled.
- `frontend`: Nginx serving the Vite build and proxying `/api`, `/actuator`, and Swagger paths.

## Verify

```bash
./scripts/check-health.sh
```

Or manually:

```bash
curl http://localhost/actuator/health
curl http://localhost/api/v1/system/status
curl http://localhost/api/v1/dashboard
```

Open:

```text
http://localhost
http://localhost/swagger-ui/index.html
```

Swagger is intentionally available for this private MVP. Disable it later when auth and deployment policy mature.

## Update App

```bash
git pull
docker compose -f docker-compose.prod.yml --env-file .env.prod up --build -d
```

## Stop Stack

```bash
docker compose -f docker-compose.prod.yml --env-file .env.prod down
```

Do not use `down -v` on a real private deployment unless you intentionally want to delete the database volume.

## Hardening Notes

- Keep `.env.prod` outside git.
- Do not expose Postgres publicly.
- Keep SSH locked down.
- Run backups before updates.
- Use HTTPS later with a simple reverse proxy or hosted private tunnel.
- Keep registration private; add stronger auth controls before sharing outside a trusted private context.

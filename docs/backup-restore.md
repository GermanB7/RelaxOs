# Backup And Restore

Sprint 8 provides simple local filesystem backups with `pg_dump`.

## Backup

Run:

```bash
./scripts/backup-db.sh
```

Expected output:

```text
backups/tranquiloos_YYYYMMDD_HHMMSS.sql
```

The script fails with a non-zero exit code if `pg_dump` fails or the generated file is empty.

## Restore

Run:

```bash
./scripts/restore-db.sh backups/tranquiloos_YYYYMMDD_HHMMSS.sql
```

The script asks you to type:

```text
RESTORE
```

It then drops and recreates the database and restores the SQL backup.

## Suggested Cron

On a private server:

```cron
0 3 * * * /path/to/tranquiloos/scripts/backup-db.sh >> /path/to/tranquiloos/backups/backup.log 2>&1
```

## Recovery Checklist

1. Stop app services:
   ```bash
   docker compose -f docker-compose.prod.yml --env-file .env.prod stop backend frontend
   ```

2. Ensure Postgres is running:
   ```bash
   docker compose -f docker-compose.prod.yml --env-file .env.prod up -d postgres
   ```

3. Restore backup:
   ```bash
   ./scripts/restore-db.sh backups/file.sql
   ```

4. Start app services:
   ```bash
   docker compose -f docker-compose.prod.yml --env-file .env.prod up -d backend frontend
   ```

5. Verify health:
   ```bash
   ./scripts/check-health.sh
   ```

6. Open Dashboard and confirm key data is present.

## Monthly Restore Test

At least monthly, restore the latest backup into a disposable environment and verify:

- `GET /api/v1/dashboard` returns data.
- Scenarios are present.
- Recommendations are present.
- Home setup and modes are present if used.

Not included yet: cloud backup upload, encryption at rest, backup rotation, monitoring alerts.

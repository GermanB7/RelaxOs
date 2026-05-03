#!/usr/bin/env sh
set -eu

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
ENV_FILE="${ENV_FILE:-$ROOT_DIR/.env.prod}"
COMPOSE_FILE="${COMPOSE_FILE:-$ROOT_DIR/docker-compose.prod.yml}"
BACKUP_FILE="${1:-}"

if [ -z "$BACKUP_FILE" ]; then
  echo "Usage: ./scripts/restore-db.sh backups/tranquiloos_YYYYMMDD_HHMMSS.sql"
  exit 1
fi

if [ ! -f "$BACKUP_FILE" ]; then
  echo "Backup file not found: $BACKUP_FILE"
  exit 1
fi

if [ ! -f "$ENV_FILE" ]; then
  echo "Missing env file: $ENV_FILE"
  exit 1
fi

# shellcheck disable=SC1090
. "$ENV_FILE"

echo "WARNING: this will drop and recreate database '$POSTGRES_DB'."
echo "Backup to restore: $BACKUP_FILE"
printf "Type RESTORE to continue: "
read CONFIRM

if [ "$CONFIRM" != "RESTORE" ]; then
  echo "Restore cancelled."
  exit 1
fi

echo "Dropping and recreating database..."
docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" exec -T postgres sh -c \
  "psql -v ON_ERROR_STOP=1 -U \"$POSTGRES_USER\" -d postgres -c \"DROP DATABASE IF EXISTS $POSTGRES_DB WITH (FORCE);\" -c \"CREATE DATABASE $POSTGRES_DB OWNER $POSTGRES_USER;\""

echo "Restoring backup..."
docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" exec -T postgres \
  psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" < "$BACKUP_FILE"

echo "Restore completed."

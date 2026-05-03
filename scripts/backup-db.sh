#!/usr/bin/env sh
set -eu

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
ENV_FILE="${ENV_FILE:-$ROOT_DIR/.env.prod}"
COMPOSE_FILE="${COMPOSE_FILE:-$ROOT_DIR/docker-compose.prod.yml}"
BACKUP_DIR="${BACKUP_DIR:-$ROOT_DIR/backups}"

if [ ! -f "$ENV_FILE" ]; then
  echo "Missing env file: $ENV_FILE"
  echo "Copy .env.prod.example to .env.prod and set strong passwords first."
  exit 1
fi

# shellcheck disable=SC1090
. "$ENV_FILE"

mkdir -p "$BACKUP_DIR"
TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
BACKUP_FILE="$BACKUP_DIR/tranquiloos_$TIMESTAMP.sql"

echo "Creating database backup: $BACKUP_FILE"
docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" exec -T postgres \
  pg_dump -U "$POSTGRES_USER" "$POSTGRES_DB" > "$BACKUP_FILE"

if [ ! -s "$BACKUP_FILE" ]; then
  echo "Backup failed or produced an empty file."
  rm -f "$BACKUP_FILE"
  exit 1
fi

echo "Backup completed: $BACKUP_FILE"

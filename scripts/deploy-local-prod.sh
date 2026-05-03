#!/usr/bin/env sh
set -eu

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
ENV_FILE="${ENV_FILE:-$ROOT_DIR/.env.prod}"
COMPOSE_FILE="${COMPOSE_FILE:-$ROOT_DIR/docker-compose.prod.yml}"

if [ ! -f "$ENV_FILE" ]; then
  cp "$ROOT_DIR/.env.prod.example" "$ENV_FILE"
  echo "Created $ENV_FILE from .env.prod.example."
  echo "Edit strong passwords before running this script again."
  exit 1
fi

echo "Starting production-like stack..."
docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" up --build -d

echo "Stack started. Run ./scripts/check-health.sh after services finish booting."

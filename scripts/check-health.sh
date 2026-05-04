#!/usr/bin/env sh
set -eu

FRONTEND_HEALTH_URL="${FRONTEND_HEALTH_URL:-http://localhost}"
BACKEND_HEALTH_URL="${BACKEND_HEALTH_URL:-http://localhost:8080/actuator/health}"
SYSTEM_STATUS_URL="${SYSTEM_STATUS_URL:-http://localhost:8080/api/v1/system/status}"

check_url() {
  NAME="$1"
  URL="$2"
  printf 'Checking %s: %s ... ' "$NAME" "$URL"
  curl -fsS "$URL" >/dev/null
  echo "OK"
}

check_url "frontend" "$FRONTEND_HEALTH_URL"
check_url "backend health" "$BACKEND_HEALTH_URL"
check_url "system status" "$SYSTEM_STATUS_URL"

echo "All health checks passed."

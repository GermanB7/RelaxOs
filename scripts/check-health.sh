#!/usr/bin/env sh
set -eu

APP_ORIGIN="${APP_ORIGIN:-http://localhost}"
API_ORIGIN="${API_ORIGIN:-$APP_ORIGIN}"

check_url() {
  NAME="$1"
  URL="$2"
  echo "Checking $NAME: $URL"
  curl -fsS "$URL" >/dev/null
}

check_url "frontend" "$APP_ORIGIN/"
check_url "backend health" "$API_ORIGIN/actuator/health"
check_url "system status" "$API_ORIGIN/api/v1/system/status"

echo "All health checks passed."

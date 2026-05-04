#!/usr/bin/env sh
set -eu

require_var() {
  NAME="$1"
  VALUE="$(eval "printf '%s' \"\${$NAME:-}\"")"
  if [ -z "$VALUE" ]; then
    echo "Missing required environment variable: $NAME"
    exit 1
  fi
}

require_var DEPLOY_HOST
require_var DEPLOY_USER
require_var DEPLOY_PATH

echo "Preparing deploy to ${DEPLOY_USER}@${DEPLOY_HOST}:${DEPLOY_PATH}"
echo "This script expects the future server to already contain the repository and a private .env.prod file."

ssh "${DEPLOY_USER}@${DEPLOY_HOST}" "
  set -eu
  cd '${DEPLOY_PATH}'
  git pull
  docker compose -f docker-compose.prod.yml --env-file .env.prod down
  docker compose -f docker-compose.prod.yml --env-file .env.prod up -d --build
  FRONTEND_HEALTH_URL=http://localhost \
  BACKEND_HEALTH_URL=http://localhost/actuator/health \
  SYSTEM_STATUS_URL=http://localhost/api/v1/system/status \
    ./scripts/check-health.sh
"

#!/bin/bash
# ~/deploy/deploy.sh
set -e

APP_NAME="${1:?APP_NAME required}"
DOCKERHUB_USERNAME="${2:?DOCKERHUB_USERNAME required}"

BLUE_PORT=8081
GREEN_PORT=8082

# Detect current running slot
if docker ps --format '{{.Names}}' | grep -q app-blue; then
  CURRENT="blue"
  NEW="green"
  NEW_PORT=$GREEN_PORT
elif docker ps --format '{{.Names}}' | grep -q app-green; then
  CURRENT="green"
  NEW="blue"
  NEW_PORT=$BLUE_PORT
else
  CURRENT="none"
  NEW="blue"
  NEW_PORT=$BLUE_PORT
fi

echo "Current: $CURRENT → New: $NEW (port: $NEW_PORT)"

# Pull new image
docker pull ${DOCKERHUB_USERNAME}/${APP_NAME}:latest

# Remove if new slot container already exists
docker rm -f app-${NEW} 2>/dev/null || true

# Start a new container
docker run -d \
  --name app-${NEW} \
  -p ${NEW_PORT}:8080 \
  -v ~/deploy/secret.yml:/app/config/secret.yml:ro \
  -v ~/deploy/firebase-service-account-key.json:/app/firebase/firebase-service-account-key.json:ro \
  -e SPRING_PROFILES_ACTIVE=stag \
  -e SPRING_CONFIG_IMPORT=optional:file:/app/config/secret.yml \
  -e TZ=Asia/Seoul \
  ${DOCKERHUB_USERNAME}/${APP_NAME}:latest

# Waiting health check
echo "Waiting for health check..."
for i in $(seq 1 30); do
  if curl -sf http://localhost:${NEW_PORT}/actuator/health > /dev/null 2>&1; then
    echo "✓ app-${NEW} successfully started"
    echo "$NEW" > ~/deploy/active-slot

    # Terminate previous container
    if [ "$CURRENT" != "none" ]; then
      echo "Terminating previous container app-${CURRENT}"
      docker rm -f app-${CURRENT} || true
    fi
    exit 0
  fi
  sleep 2
done

echo "✗ Health check failed, rolling back"
docker rm -f app-${NEW}
exit 1

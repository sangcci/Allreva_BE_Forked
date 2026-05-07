#!/bin/bash
set -e

SLOT="${1:?Usage: switch.sh <blue|green>}"
APP_VM_IP="192.168.122.20"
CONF=~/nginx/conf.d/default.conf

if [ "$SLOT" = "blue" ]; then
  NEW_PORT=8081
else
  NEW_PORT=8082
fi

# Replace with upstream server port
sed -i "s|server ${APP_VM_IP}:[0-9]*;|server ${APP_VM_IP}:${NEW_PORT};|" "$CONF"

# Nginx reload
docker exec nginx nginx -s reload

echo "Nginx upstream switched to ${SLOT} (${APP_VM_IP}:${NEW_PORT})"

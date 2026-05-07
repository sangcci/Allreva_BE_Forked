#!/bin/bash
# ~/deploy/deploy.sh
set -e

APP_NAME="${1:?APP_NAME required}"
DOCKERHUB_USERNAME="${2:?DOCKERHUB_USERNAME required}"

BLUE_PORT=8081
GREEN_PORT=8082

# Checkout current running container
if docker ps --format '{{.Names}}' | grep -q app-blue; then
  CURRENT="blue"
  NEW="green"
  NEW_PORT=$GREEN_PORT
else
  CURRENT="green"
  NEW="blue"
  NEW_PORT=$BLUE_PORT
fi

echo "Current: $CURRENT → New: $NEW (port: $NEW_PORT)"

# Pull new image
docker pull ${DOCKERHUB_USERNAME}/${APP_NAME}:latest                       
                                                                           
# Remove if current container exist                                        
docker rm -f app-${NEW} 2>/dev/null || true                                
                                                                           
# Start a new Container                                                    
docker run -d \                                                            
  --name app-${NEW} \                                                      
  -p ${NEW_PORT}:8080 \                                                    
  -v ~/deploy/application-stag.yml:/app/config/application-stag.yml:ro \   
  -v ~/deploy/firebase-service-account-key.json:/app/firebase/firebase-service-account-key.json:ro \
  -e SPRING_PROFILES_ACTIVE=stag \                                         
  -e SPRING_CONFIG_ADDITIONAL_LOCATION=/app/config/ \                      
  -e FCM_SERVICE_ACCOUNT_KEY_PATH=file:/app/firebase/firebase-service-account-key.json \    
  -e TZ=Asia/Seoul \                                                       
  ${DOCKERHUB_USERNAME}/${APP_NAME}:latest                                 
                                                                           
# Waiting healcheck                                                        
echo "Waiting a heal check..."                                             
for i in $(seq 1 30); do                                                   
  if curl -sf http://localhost:${NEW_PORT}/actuator/health > /dev/null 2>&1; then
    echo "✓ app-${NEW} successfully run"                                   
    echo "$NEW" > ~/deploy/active-slot                                     
                                                                           
    # Terminal previous container                                          
    if [ "$CURRENT" != "none" ]; then                                      
      echo "Previous Container app-${CURRENT} terminate"                   
      docker rm -f app-${CURRENT}                                          
    fi                                                                     
    exit 0                                                                 
  fi                                                                       
  sleep 2
done

echo "✗ Failed to healcheck, Rollback"
docker rm -f app-${NEW}
exit 1

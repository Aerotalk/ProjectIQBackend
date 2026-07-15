#!/bin/bash
# start.sh
# This script acts as the entrypoint for deployments. It starts the app, pings the health check, 
# and automatically crashes/rolls back if the app doesn't become healthy in time.

# 1. Start the Spring Boot application in the background
# We assume the jar has been built and is located in build/libs or provided via JAR_PATH.
JAR_PATH=${JAR_PATH:-"build/libs/invoiceiq-0.0.1-SNAPSHOT.jar"}
echo "🚀 Starting InvoiceIQ Backend from $JAR_PATH..."
java -jar $JAR_PATH &
APP_PID=$!

echo "⏳ Performing startup health check..."
MAX_ATTEMPTS=30
ATTEMPT=1
HEALTHY=false

# Wait for a few seconds to let Spring Boot begin initializing
sleep 5

# 2. Loop and ping the /health endpoint
while [ $ATTEMPT -le $MAX_ATTEMPTS ]; do
    # The PORT variable is usually provided by deployment platforms (Render, Railway, etc).
    # If it's not set, we default to 8080.
    TARGET_PORT=${PORT:-8080}
    
    # We use curl to fetch the HTTP status code
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${TARGET_PORT}/health || echo "000")
    
    if [ "$HTTP_STATUS" -eq 200 ]; then
        echo "✅ Health check passed! Application is fully operational."
        HEALTHY=true
        break
    fi
    
    echo "Waiting for application to become healthy... (Attempt $ATTEMPT/$MAX_ATTEMPTS, Status: $HTTP_STATUS)"
    sleep 3
    ATTEMPT=$((ATTEMPT+1))
done

# 3. If it never became healthy, we kill the process and exit with an error.
# This exit code (1) signals the deployment platform to ABORT the rollout and rollback.
if [ "$HEALTHY" = false ]; then
    echo "❌ Health check failed after $MAX_ATTEMPTS attempts. Aborting deployment."
    kill -9 $APP_PID
    exit 1
fi

# 4. If healthy, we wait on the Java process so the script doesn't exit (which would kill the container)
wait $APP_PID

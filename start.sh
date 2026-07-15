#!/bin/bash
# start.sh
# This script acts as the entrypoint for deployments. It starts the app, pings the health check, 
# and automatically crashes/rolls back if the app doesn't become healthy in time.

# We dynamically find the jar file. Nixpacks, Docker, and local builds might place it differently.
if [ -n "$JAR_PATH" ] && [ -f "$JAR_PATH" ]; then
    FOUND_JAR="$JAR_PATH"
else
    # Search for the jar, excluding plain jars which are not executable
    FOUND_JAR=$(find . -name "*.jar" ! -name "*-plain.jar" | head -n 1)
fi

if [ -z "$FOUND_JAR" ]; then
    echo "❌ Error: Could not find any executable Spring Boot .jar file!"
    exit 1
fi

# Railway sometimes injects variables with literal double quotes if they were copied from a .env file.
# This causes JDBC and JWT to crash. We strip any leading/trailing quotes here before starting Java.
strip_quotes() {
    local val="$1"
    val="${val%\"}" # Strip trailing quote
    val="${val#\"}" # Strip leading quote
    echo "$val"
}

export RENDER_DB_URL=$(strip_quotes "$RENDER_DB_URL")
export RENDER_DB_USER=$(strip_quotes "$RENDER_DB_USER")
export RENDER_DB_PASSWORD=$(strip_quotes "$RENDER_DB_PASSWORD")
export JWT_SECRET=$(strip_quotes "$JWT_SECRET")
export CORS_ALLOWED_ORIGINS=$(strip_quotes "$CORS_ALLOWED_ORIGINS")

echo "🚀 Starting InvoiceIQ Backend from $FOUND_JAR..."
java -jar "$FOUND_JAR" &
APP_PID=$!

echo "⏳ Performing startup health check..."
MAX_ATTEMPTS=100
ATTEMPT=1
HEALTHY=false

# Wait for a few seconds to let Spring Boot begin initializing
sleep 5

# 2. Loop and ping the /health endpoint
while [ $ATTEMPT -le $MAX_ATTEMPTS ]; do
    # Check if the Java process actually crashed!
    if ! kill -0 $APP_PID 2>/dev/null; then
        echo "❌ FATAL: The Spring Boot application crashed! Please check Railway App Logs."
        exit 1
    fi

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

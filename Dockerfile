# Stage 1: Build the application
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

# Copy gradle configuration files
COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY gradlew ./
COPY gradle.lockfile ./

# Ensure the gradlew script is executable (solves Windows to Linux permission issues)
RUN chmod +x gradlew

# Resolve dependencies first to cache them in a separate Docker layer
RUN ./gradlew dependencies --no-daemon

# Copy source code and build the application
COPY src src
RUN ./gradlew build -x test --no-daemon

# Stage 2: Create the minimal runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install bash and curl required for our start.sh script
RUN apk add --no-cache bash curl

# Copy the built jar from the builder stage
COPY --from=builder /app/build/libs/invoiceiq-0.0.1-SNAPSHOT.jar app.jar

# Copy the deployment script and make it executable
COPY start.sh start.sh
RUN chmod +x start.sh

# Environment variables for our start script
ENV JAR_PATH="app.jar"

# Use start.sh as the entrypoint so it enforces the health check loop
CMD ["bash", "start.sh"]

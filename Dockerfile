# ==============================================================================
# Task 3: Java Application using Gradle
# Multi-Stage Dockerfile (Build & Runtime Optimization)
# ==============================================================================

# Stage 1: Build using Gradle JDK Image
FROM gradle:8.7-jdk17-alpine AS builder

WORKDIR /home/gradle/src

# Copy project source and Gradle files
COPY --chown=gradle:gradle build.gradle settings.gradle ./
COPY --chown=gradle:gradle src ./src

# Compile, run tests, and package the JAR application
RUN gradle build --no-daemon

# ==============================================================================
# Stage 2: Minimal Lightweight JRE 17 Runtime
# ==============================================================================
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="CodeAlpha Intern"
LABEL description="Containerized Java Application built with Gradle and automated CI/CD"
LABEL version="1.0.0"

WORKDIR /app

# Install curl for container health monitoring
RUN apk add --no-cache curl

# Create non-root user for enhanced container security
RUN addgroup -S devops && adduser -S appuser -G devops

# Copy built JAR from builder stage
COPY --from=builder /home/gradle/src/build/libs/codealpha-java-app-1.0.0.jar app.jar

# Adjust ownership to non-root user
RUN chown -R appuser:devops /app

USER appuser

# Expose HTTP port
EXPOSE 8080

# Environment variables
ENV PORT=8080 \
    JAVA_OPTS="-Xms64m -Xmx256m"

# Healthcheck configuration
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# Execute the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

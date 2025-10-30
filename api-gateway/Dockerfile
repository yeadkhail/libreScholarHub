# Multi-stage build for API Gateway
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app

# Copy maven wrapper and pom.xml first for better layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src src
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring

# Copy JAR file from build stage
COPY --from=build /workspace/app/target/*.jar app.jar

# Change ownership to spring user
RUN chown spring:spring app.jar

USER spring:spring

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
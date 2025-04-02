# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew clean bootJar -x test

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the jar file from the builder image
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

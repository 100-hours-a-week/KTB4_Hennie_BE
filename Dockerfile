# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build-stage

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew \
    && ./gradlew dependencies --no-daemon

COPY src/main src/main

RUN ./gradlew clean bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS final-stage

WORKDIR /app

RUN addgroup -S spring \
    && adduser -S spring -G spring \
    && mkdir -p /app/uploads \
    && chown -R spring:spring /app

COPY --from=build-stage --chown=spring:spring /app/build/libs/*.jar app.jar

ENV IMAGE_UPLOAD_DIRECTORY=/app/uploads

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

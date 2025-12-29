# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Gradle Wrapper 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY src src

# 실행 권한 부여 및 빌드
RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

LABEL authors="hojin"

ENTRYPOINT ["java", "-jar", "app.jar"]

# BigWheel Spring Boot 애플리케이션
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8080

LABEL authors="hojin"

ENTRYPOINT ["java", "-jar", "app.jar"]
# Стадия сборки с Maven
FROM maven:3.8-openjdk-17 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# Финальный образ
FROM openjdk:17-jdk-slim

# Установка необходимых зависимостей для работы с графикой в headless-режиме
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    libfreetype6 \
    fontconfig \
    fonts-dejavu \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Параметры для оптимальной работы в контейнере
ENTRYPOINT ["java", \
    "-Djava.awt.headless=true", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
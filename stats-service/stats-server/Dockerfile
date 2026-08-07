# Этап 1: Сборка всех модулей статистики из исходного Java-кода
FROM maven:3.9.8-eclipse-temurin-21-jammy AS build
WORKDIR /app

# Копируем структуру POM-файлов проекта
COPY pom.xml /app/
COPY stats-service/pom.xml /app/stats-service/
COPY stats-service/stats-dto/pom.xml /app/stats-service/stats-dto/
COPY stats-service/stats-client/pom.xml /app/stats-service/stats-client/
COPY stats-service/stats-server/pom.xml /app/stats-service/stats-server/

# Копируем исходный код (сервер зависит от DTO, поэтому копируем оба)
COPY stats-service/stats-dto/src /app/stats-service/stats-dto/src
COPY stats-service/stats-client/src /app/stats-service/stats-client/src
COPY stats-service/stats-server/src /app/stats-service/stats-server/src

# Запускаем честную компиляцию Maven прямо внутри контейнера
RUN mvn clean package -DskipTests -f /app/stats-service/stats-server/pom.xml

# Этап 2: Запуск готового приложения в чистом JRE-образе от Amazon (как вы использовали)
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/stats-service/stats-server/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

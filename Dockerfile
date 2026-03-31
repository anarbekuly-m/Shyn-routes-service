# СТАДИЯ 1: Сборка (Build)
# Используем тяжелый образ с Maven только для компиляции
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Кэшируем зависимости (чтобы каждый раз не качать весь интернет)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем код и собираем JAR
COPY src ./src
RUN mvn clean package -DskipTests

# СТАДИЯ 2: Запуск (Runtime)
# Используем максимально легкий образ только с Javda (JRE)
FROM eclipse-temurin:17-jre
WORKDIR /app

# Копируем только готовый JAR из первой стадии
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

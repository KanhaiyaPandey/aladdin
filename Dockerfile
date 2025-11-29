# -------- Build stage --------
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline

COPY . .
RUN ./mvnw clean package -DskipTests

# -------- Run stage --------
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Spring Boot MUST listen on this port
EXPOSE 8080

# Use explicit Spring Boot binding
ENTRYPOINT ["java", "-jar", "app.jar"]

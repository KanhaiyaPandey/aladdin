# -------- Build stage --------
    FROM maven:3.9.4-eclipse-temurin-21 AS build

    WORKDIR /app
    
    # Copy Maven files first to leverage layer caching
    COPY pom.xml .
    COPY .mvn .mvn
    COPY mvnw .
    RUN ./mvnw dependency:go-offline
    
    # Now copy the full source
    COPY . .
    
    # Build the project and skip tests (faster for CI/CD)
    RUN ./mvnw clean package -DskipTests
    
    # -------- Run stage --------
    FROM eclipse-temurin:21-jdk
    
    WORKDIR /app
    
    # Copy the built jar from the previous stage
    COPY --from=build /app/target/aladdin-0.0.1-SNAPSHOT.jar app.jar
    
    # Let Render.com set the port via $PORT
    ENV PORT=8080
    
    # Expose that port (optional for local dev)
    EXPOSE 8080
    
    # Run the jar
    CMD ["java", "-jar", "app.jar"]
    
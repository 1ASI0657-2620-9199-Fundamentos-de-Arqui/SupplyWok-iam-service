# Dockerfile for iam-service
# Summary:
# This Dockerfile builds and runs the iam-service application using Maven and OpenJDK 26.

# Step 1: Build the application using Maven
FROM maven:3.9.16-eclipse-temurin-26 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B -f pom.xml -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package

# Step 2: Create a runtime image
FROM eclipse-temurin:26-jre-noble AS runtime
ENV SPRING_PROFILES_ACTIVE=prod
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Step 3: Configure and run the application
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

# Step 1: Use Maven to build the project
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies (cached separately)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source
COPY . .

# Build the jar
RUN mvn clean package -DskipTests

# Step 2: Run the app with a smaller JDK image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the jar from the builder stage
COPY --from=builder /app/target/investment-app-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

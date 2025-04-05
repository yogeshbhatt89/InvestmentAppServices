# Use a Java base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container (adjust the file name)
COPY target/investment-app-0.0.1-SNAPSHOT.jar app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Expose the port the app will run on
EXPOSE 8080

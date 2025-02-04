# Use Eclipse Temurin JDK 17
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy JAR file from the build context
COPY target/*.jar app.jar

# Expose port (should match your application port)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 1: Build the application
# Start with gradle image
FROM gradle:8.5.0-jdk17-alpine as builder

# Copy the source code into the container
COPY --chown=gradle:gradle . /home/gradle/src

# Set the working directory
WORKDIR /home/gradle/src

# Use Gradle to build the application
RUN gradle clean bootJar

# Stage 2: Run the application
# Start with Java runtime base image
FROM eclipse-temurin:17-jre-alpine as build

# Copy the built artifact from the builder stage
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/todo-list.jar

# Set the working directory
WORKDIR /app

# Run the jar file
ENTRYPOINT ["java","-jar","todo-list.jar"]

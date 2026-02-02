# syntax=docker/dockerfile:1
FROM gradle:8.8-jdk25 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :services:batch:bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/services/batch/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app/app.jar"]

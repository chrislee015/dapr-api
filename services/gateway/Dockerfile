# syntax=docker/dockerfile:1
FROM gradle:8.8-jdk25 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :services:gateway:bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/services/gateway/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]

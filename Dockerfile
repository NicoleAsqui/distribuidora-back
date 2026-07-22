# Multi-stage build for Google Cloud Run + Developer Connect
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline
COPY src src
RUN ./mvnw -q -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /workspace/target/*.jar /app/app.jar
USER app
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar /app/app.jar --server.port=${PORT}"]

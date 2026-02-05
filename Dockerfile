FROM maven:3.9.12-eclipse-temurin-25 AS builder

COPY /src /app/src
COPY pom.xml /app

WORKDIR /app

RUN mvn -e clean package -DskipTests

FROM eclipse-temurin:25-jdk-alpine

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

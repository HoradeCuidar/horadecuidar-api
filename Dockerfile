FROM maven:3.9.12-eclipse-temurin-25 AS builder
WORKDIR /build

COPY .env .env
COPY . .

RUN mvn -e clean package -DskipTests

FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

COPY --from=builder /build/.env .env
COPY --from=builder /build/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

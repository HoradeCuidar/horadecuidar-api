FROM maven:3.9.12-eclipse-temurin-25 AS builder
WORKDIR /build

COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src ./src

RUN mvn -e -B clean package -DskipTests

FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

COPY --from=builder /build/target/*.jar /app/app.jar

EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
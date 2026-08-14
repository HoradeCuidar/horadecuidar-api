FROM maven:3.9.12-eclipse-temurin-25 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn -B -q -e -DskipTests dependency:go-offline

COPY src ./src

RUN mvn -B -q package -DskipTests

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

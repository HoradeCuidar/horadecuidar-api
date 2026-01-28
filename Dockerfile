FROM eclipse-temurin:25-jdk-alpine AS builder

RUN apk update && apk add maven

COPY /src /app/src
COPY pom.xml /app

WORKDIR /app

RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jdk-alpine

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
COPY notification-service/pom.xml notification-service/
COPY common-lib/pom.xml common-lib/
COPY common-lib/src common-lib/src
COPY notification-service/src notification-service/src
COPY auth_service auth_service
COPY hotel-service hotel-service
COPY booking-service booking-service
COPY payment-service payment-service
COPY review-service review-service
COPY api-gateway api-gateway

RUN mvn -pl notification-service -am clean package -DskipTests
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/notification-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]

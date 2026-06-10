FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .

COPY common-lib/pom.xml common-lib/
COPY common-lib/src common-lib/src
COPY auth_service/pom.xml auth_service/
COPY auth_service/src auth_service/src
COPY booking-service/pom.xml booking-service/
COPY booking-service/src booking-service/src
COPY hotel-service hotel-service
COPY payment-service payment-service
COPY review-service review-service
COPY notification-service notification-service
COPY api-gateway api-gateway

RUN mvn -pl booking-service -am clean package -DskipTests
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/booking-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

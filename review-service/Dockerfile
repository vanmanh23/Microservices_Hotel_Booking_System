FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .

COPY common-lib/pom.xml common-lib/
COPY common-lib/src common-lib/src
COPY review-service/pom.xml review-service/
COPY review-service/src review-service/src
COPY auth_service auth_service
COPY hotel-service hotel-service
COPY booking-service booking-service
COPY payment-service payment-service
COPY notification-service notification-service
COPY api-gateway api-gateway
RUN mvn -pl review-service -am clean package -DskipTests
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/review-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]

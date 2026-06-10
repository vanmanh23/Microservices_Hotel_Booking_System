FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .

COPY common-lib/pom.xml common-lib/
COPY common-lib/src common-lib/src
COPY payment-service/pom.xml payment-service/
COPY payment-service/src payment-service/src
COPY auth_service auth_service
COPY hotel-service hotel-service
COPY booking-service booking-service
COPY review-service review-service
COPY notification-service notification-service
COPY api-gateway api-gateway
RUN mvn -pl payment-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/payment-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]

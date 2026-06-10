FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/
COPY common-lib/pom.xml common-lib/
COPY common-lib/src common-lib/src
COPY api-gateway/src api-gateway/src
COPY auth_service auth_service
COPY booking-service booking-service
COPY hotel-service hotel-service
COPY payment-service payment-service
COPY review-service review-service
COPY notification-service notification-service

RUN mvn -f pom.xml -pl api-gateway -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

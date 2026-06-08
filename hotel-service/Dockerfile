FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
# Root pom
COPY pom.xml .
# Module pom files và Copy source để tận dụng cache
COPY common-lib/pom.xml common-lib/
COPY common-lib/src common-lib/src
COPY auth_service/pom.xml auth_service/
COPY auth_service/src auth_service/src
COPY hotel-service/pom.xml hotel-service/
COPY hotel-service/src hotel-service/src
# Download dependencies trước
RUN mvn -f pom.xml -pl hotel-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/hotel-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

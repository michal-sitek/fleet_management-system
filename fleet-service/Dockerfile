FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -q
COPY src src
RUN ./mvnw package -DskipTests -q

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/fleet-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

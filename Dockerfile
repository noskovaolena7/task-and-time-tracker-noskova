# ---------- build ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml ./
COPY .mvn ./.mvn
COPY mvnw mvnw.cmd ./
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline
COPY src ./src
RUN ./mvnw -q -DskipTests package

# ---------- run ----------
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# Etapa 1: build do projeto com Maven
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: imagem final apenas com o jar
FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /app/target/furia-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Xmx128m", "-Xms64m", "-jar", "app.jar"]
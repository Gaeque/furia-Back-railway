FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia o arquivo .jar para o diretório de trabalho
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
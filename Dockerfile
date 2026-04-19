# Usar directamente el JAR compilado sin construir en el contenedor
FROM docker.io/eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/Senior-java-tech-challenge-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
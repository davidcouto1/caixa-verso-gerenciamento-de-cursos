# Dockerfile para aplicação Spring Boot
# Construção leve com OpenJDK 17
FROM eclipse-temurin:17-jdk-jammy

# diretório de trabalho dentro do container
WORKDIR /app

# copia o artefato gerado pelo Maven
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# expõe a porta em que o Spring Boot roda
EXPOSE 8080

# comando de inicialização
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

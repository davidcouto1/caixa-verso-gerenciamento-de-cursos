# Dockerfile multi-stage para aplicação Spring Boot

# etapa de build usando imagem Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# copia os arquivos do projeto e compila
COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests

# etapa final com JRE leve
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# copia o artefato gerado
COPY --from=build /workspace/target/sistema-gerenciamento-cursos-1.0.0.jar app.jar

# expõe a porta em que o Spring Boot roda
EXPOSE 8080

# comando de inicialização
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

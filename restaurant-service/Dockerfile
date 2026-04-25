# Stage 1: Build faza (Koristimo Maven i JDK)
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Kopiramo pom.xml i source kod
COPY pom.xml .

# Docker će ovaj layer skinuti samo ako se pom.xml promeni
RUN mvn dependency:go-offline -B && \
    mvn dependency:resolve-plugins -B && \
    mvn io.github.git-commit-id:git-commit-id-maven-plugin:revision -B || true

COPY src ./src

# Kompajliramo i pakujemo (preskačemo testove radi brzine demoa)
RUN mvn clean package -DskipTests

# Stage 2: Runtime faza (Minimalni JRE)
FROM container-registry.oracle.com/java/openjdk:24-oraclelinux9
WORKDIR /app

# Kopiramo samo gotov .jar iz prve faze
COPY --from=build /app/target/*.jar app.jar

# Eksternalizacija porta (Faktor III - Config)
ENV SERVER_PORT=8082

# Dokumentujemo port (Swagger će obično biti ovde)
EXPOSE 8082

# Komanda za pokretanje
ENTRYPOINT ["java", "-jar", "app.jar"]
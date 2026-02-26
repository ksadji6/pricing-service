# Étape 1 : Build avec Maven et Java 21
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Installation de Maven
RUN apk add --no-cache maven

# Gestion des dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Build du projet
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Image d'exécution
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Récupération du JAR (on utilise l'étoile pour éviter les erreurs de version)
COPY --from=build /app/target/pricing-service-*.jar app.jar

# Le Pricing-Service tourne sur le port 8084
EXPOSE 8084

# Optimisation de la mémoire
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
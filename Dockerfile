# Stage 1: Build
FROM amazoncorretto:17-alpine-jdk AS build

WORKDIR /app

# Copier les fichiers Maven wrapper
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Rendre le wrapper exécutable
RUN chmod +x ./mvnw

# Télécharger les dépendances (cache Docker)
RUN ./mvnw dependency:go-offline -B

# Copier le code source
COPY src src

# Construire l'application
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:17-alpine

# Installer curl pour health check
RUN apk add --no-cache curl

# Créer un utilisateur non-root
RUN addgroup -g 1001 -S spring && adduser -u 1001 -S spring -G spring

WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/*.jar app.jar

# Changer la propriété
RUN chown spring:spring app.jar

# Utiliser l'utilisateur non-root
USER spring

# Exposer le port (Render utilise PORT env variable)
EXPOSE ${PORT:-10000}

# Variables d'environnement JVM
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=prod

# Démarrer l'application avec port dynamique
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-10000} -jar app.jar"]

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-10000}/actuator/health || exit 1
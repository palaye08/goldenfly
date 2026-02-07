#!/bin/sh

# Script de démarrage pour Render
# Convertit DATABASE_URL (format postgres://) en JDBC URL

echo "🚀 Starting GoldenFly Backend..."

# Vérifier si DATABASE_URL est défini
if [ -n "$DATABASE_URL" ]; then
    echo "✅ DATABASE_URL detected"

    # Convertir postgres:// en jdbc:postgresql://
    export JDBC_DATABASE_URL=$(echo $DATABASE_URL | sed 's/^postgres:/jdbc:postgresql:/')

    echo "📊 Database URL configured"
else
    echo "⚠️  WARNING: DATABASE_URL not set, using default"
    export JDBC_DATABASE_URL="jdbc:postgresql://localhost:5432/goldenfly_db"
fi

# Options JVM optimisées pour Render
JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# Démarrer l'application Spring Boot
echo "🎯 Starting Spring Boot application..."
exec java $JAVA_OPTS \
    -Dserver.port=${PORT:-10000} \
    -Dspring.profiles.active=prod \
    -Dspring.datasource.url=$JDBC_DATABASE_URL \
    -jar app.jar
#!/bin/bash

# Transformer DATABASE_URL
if [ -n "$DATABASE_URL" ]; then
  export SPRING_DATASOURCE_URL=$(echo $DATABASE_URL | sed 's/^postgres:/jdbc:postgresql:/')
  echo "✅ Database URL: ${SPRING_DATASOURCE_URL:0:50}..."
fi

# FORCER le profil prod
export SPRING_PROFILES_ACTIVE=prod

# Vérifier les variables
echo "🔧 Configuration:"
echo "  - Profile: $SPRING_PROFILES_ACTIVE"
echo "  - JWT Secret: ${APP_JWT_SECRET:0:20}..."
echo "  - JWT Expiration: ${APP_JWT_EXPIRATION:-86400000} ms"

# Démarrer l'application
exec java \
  -Dspring.profiles.active=prod \
  -Dapp.jwt.secret=${APP_JWT_SECRET} \
  -Dapp.jwt.expiration=${APP_JWT_EXPIRATION:-86400000} \
  -Dapp.jwt.refresh-expiration=${APP_JWT_REFRESH_EXPIRATION:-604800000} \
  -jar target/*.jar

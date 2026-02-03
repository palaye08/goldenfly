package com.goldenfly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // IMPORTANT: Permettre les credentials
        config.setAllowCredentials(true);

        // IMPORTANT: Spécifier l'origine exacte quand allowCredentials = true
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));

        // Autoriser tous les headers
        config.setAllowedHeaders(Arrays.asList("*"));

        // Autoriser toutes les méthodes HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Exposer les headers nécessaires
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Durée de cache pour les requêtes preflight
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
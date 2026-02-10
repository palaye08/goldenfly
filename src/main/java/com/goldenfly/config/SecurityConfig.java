package com.goldenfly.config;

import com.goldenfly.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "https://goldenfly-frontend.onrender.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // ========== Routes publiques ==========
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Callbacks de paiement (doivent être publics pour les webhooks)
                        .requestMatchers("/api/paiements/wave/callback").permitAll()
                        .requestMatchers("/api/paiements/orange/callback").permitAll()

                        // ========== VILLES ==========
                        // GET public (recherche de villes)
                        .requestMatchers(HttpMethod.GET, "/api/villes/**").permitAll()
                        // Création, modification, suppression : ADMIN uniquement
                        .requestMatchers(HttpMethod.POST, "/api/villes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/villes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/villes/**").hasRole("ADMIN")

                        // ========== VOLS ==========
                        // Consultation publique (recherche et détails des vols)
                        .requestMatchers(HttpMethod.GET, "/api/vols/**").permitAll()
                        // Création, modification, suppression : ADMIN uniquement
                        .requestMatchers(HttpMethod.POST, "/api/vols/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/vols/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/vols/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/vols/**").hasRole("ADMIN")

                        // ========== UTILISATEURS ==========
                        // IMPORTANT: Les routes spécifiques DOIVENT être avant les routes génériques
                                // Profil personnel (accessible à tous les utilisateurs authentifiés)
                                .requestMatchers(HttpMethod.GET, "/api/utilisateurs/me").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/utilisateurs/me").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/utilisateurs/me").authenticated()

// Gestion globale des utilisateurs : ADMIN uniquement
                                .requestMatchers(HttpMethod.GET, "/api/utilisateurs").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/utilisateurs/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/utilisateurs/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/utilisateurs/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/utilisateurs/**").hasRole("ADMIN")

                        // ========== RÉSERVATIONS ==========
                        // Toutes les opérations sur réservations nécessitent une authentification
                        // Le contrôle d'accès (utilisateur ne peut voir que ses réservations) se fait dans le controller
                        .requestMatchers(HttpMethod.GET, "/api/reservations/utilisateur/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reservations").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/**").authenticated()

                        // ========== PAIEMENTS ==========
                        // Paiement manuel : accessible aux utilisateurs authentifiés (pour payer leurs réservations)
                        .requestMatchers(HttpMethod.POST, "/api/paiements/manuel").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/paiements/initier").authenticated()

                        // Consultation des paiements
                        .requestMatchers(HttpMethod.GET, "/api/paiements/reservation/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/paiements/**").authenticated()

                        // Liste complète des paiements : ADMIN uniquement
                        .requestMatchers(HttpMethod.GET, "/api/paiements").authenticated()

                        // ========== Par défaut ==========
                        // Toute autre route nécessite une authentification
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
package com.goldenfly.security;

import com.goldenfly.design.repositories.TokenBlacklistRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        log.debug("🔍 Processing request: {} {}", request.getMethod(), path);

        // Skip JWT validation for public endpoints
        if (isPublicEndpoint(path)) {
            log.debug("✅ Public endpoint, skipping JWT validation");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);

            if (!StringUtils.hasText(jwt)) {
                log.debug("⚠️ No JWT token found in request");
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("🔐 JWT token found, validating...");

            // Valider le token
            if (!tokenProvider.validateToken(jwt)) {
                log.warn("❌ Invalid JWT token");
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("✅ JWT token is valid");

            // Vérifier si le token est en blacklist
            if (tokenBlacklistRepository.existsByToken(jwt)) {
                log.warn("🚫 Token is blacklisted (user logged out)");
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("✅ Token is not blacklisted");

            // Extraire l'email et charger l'utilisateur
            String email = tokenProvider.getEmailFromToken(jwt);
            log.debug("📧 Email from token: {}", email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                log.debug("👤 User loaded: {}", userDetails.getUsername());
                log.debug("🔑 Authorities: {}", userDetails.getAuthorities());

                // Créer l'authentification
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("✅ User authenticated successfully: {}", email);
            }
        } catch (Exception ex) {
            log.error("❌ Could not set user authentication in security context: {}", ex.getMessage(), ex);
            // Ne pas bloquer la requête, laisser Spring Security gérer
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Vérifie si l'endpoint est public (ne nécessite pas d'authentification)
     */
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api/paiements/wave/callback") ||
                path.startsWith("/api/paiements/orange/callback") ||
                path.equals("/error");
    }

    /**
     * Extrait le JWT du header Authorization
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken)) {
            log.debug("📨 Authorization header: {}", bearerToken.substring(0, Math.min(20, bearerToken.length())) + "...");

            if (bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            } else {
                log.warn("⚠️ Authorization header does not start with 'Bearer '");
            }
        } else {
            log.debug("⚠️ No Authorization header found");
        }

        return null;
    }
}
package com.goldenfly.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key signingKey;
    private final long jwtExpiration;
    private final long refreshExpiration;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.expiration}") long jwtExpiration,
            @Value("${app.jwt.refresh-expiration}") long refreshExpiration) {

        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;

        // Initialiser la clé de signature
        byte[] keyBytes;
        boolean isBase64 = false;

        try {
            // Essayer de décoder comme Base64
            keyBytes = Base64.getDecoder().decode(jwtSecret);
            isBase64 = true;
            log.info("✅ JWT secret decoded from Base64");
        } catch (IllegalArgumentException e) {
            // Si ce n'est pas du Base64 valide, utiliser directement comme bytes
            log.warn("⚠️ JWT secret is not Base64 encoded, using raw bytes");
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }

        // Vérifier que la clé est assez longue (minimum 512 bits pour HS512)
        if (keyBytes.length < 64) {
            throw new IllegalArgumentException(
                    String.format("JWT secret must be at least 512 bits (64 bytes) for HS512 algorithm. Current length: %d bytes", keyBytes.length)
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);

        log.info("✅ JwtTokenProvider initialized successfully ({} secret)", isBase64 ? "Base64" : "raw");
        log.debug("📋 JWT Expiration: {} ms ({} hours)", jwtExpiration, jwtExpiration / 3600000);
        log.debug("📋 Refresh Token Expiration: {} ms ({} hours)", refreshExpiration, refreshExpiration / 3600000);
    }

    private Key getSigningKey() {
        return signingKey;
    }

    public String generateToken(Long id, String nom, String prenom, String email, String profile) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("nom", nom);
        claims.put("prenom", prenom);
        claims.put("profile", profile);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            log.error("❌ Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("❌ Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("❌ Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("❌ Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("❌ JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }
}
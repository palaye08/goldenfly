package com.goldenfly.design.services;

import com.goldenfly.design.repositories.TokenBlacklistRepository;
import com.goldenfly.design.repositories.UtilisateurRepository;
import com.goldenfly.domain.entities.TokenBlacklist;
import com.goldenfly.domain.entities.Utilisateur;
import com.goldenfly.security.JwtTokenProvider;
import com.goldenfly.web.dtos.AuthResponseDto;
import com.goldenfly.web.dtos.LoginDto;
import com.goldenfly.web.dtos.UtilisateurDto;
import com.goldenfly.web.mappers.UtilisateurMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UtilisateurMapper utilisateurMapper;

    public AuthResponseDto login(LoginDto loginDto) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(loginDto.getPassword(), utilisateur.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        if (!utilisateur.getActif()) {
            throw new RuntimeException("Compte désactivé");
        }

        String token = jwtTokenProvider.generateToken(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getProfile().name()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(utilisateur.getEmail());

        UtilisateurDto userDto = utilisateurMapper.toDto(utilisateur);

        AuthResponseDto response = new AuthResponseDto();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUtilisateur(userDto);

        return response;
    }

    public void logout(String token) {
        TokenBlacklist blacklist = new TokenBlacklist();
        blacklist.setToken(token);
        blacklist.setDateBlacklist(LocalDateTime.now());
        blacklist.setDateExpiration(
                jwtTokenProvider.getExpirationDateFromToken(token).toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()
        );
        tokenBlacklistRepository.save(blacklist);
    }

    @Transactional(readOnly = true)
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    public AuthResponseDto refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token invalide");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String newToken = jwtTokenProvider.generateToken(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getProfile().name()
        );

        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        UtilisateurDto userDto = utilisateurMapper.toDto(utilisateur);

        AuthResponseDto response = new AuthResponseDto();
        response.setToken(newToken);
        response.setRefreshToken(newRefreshToken);
        response.setUtilisateur(userDto);

        return response;
    }
}
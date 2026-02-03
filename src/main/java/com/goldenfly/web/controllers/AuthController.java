package com.goldenfly.web.controllers;

import com.goldenfly.design.services.AuthService;
import com.goldenfly.web.dtos.AuthResponseDto;
import com.goldenfly.web.dtos.CreateUtilisateurDto;
import com.goldenfly.web.dtos.LoginDto;
import com.goldenfly.web.dtos.UtilisateurDto;
import com.goldenfly.design.services.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Gestion de l'authentification")
public class AuthController {

    private final AuthService authService;
    private final UtilisateurService utilisateurService;

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    public ResponseEntity<UtilisateurDto> register(@Valid @RequestBody CreateUtilisateurDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(utilisateurService.creerUtilisateur(dto));
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7); // Remove "Bearer "
        authService.logout(jwtToken);
        return ResponseEntity.ok("Déconnexion réussie");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchir le token")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}
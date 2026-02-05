package com.goldenfly.web.controllers;

import com.goldenfly.design.services.UtilisateurService;
import com.goldenfly.domain.enums.ProfileEnum;
import com.goldenfly.web.dtos.CreateUtilisateurDto;
import com.goldenfly.web.dtos.UpdateUtilisateurDto;
import com.goldenfly.web.dtos.UtilisateurDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    /**
     * Récupérer le profil de l'utilisateur connecté
     * Accessible à tous les utilisateurs authentifiés
     */
    @GetMapping("/me")
    @Operation(summary = "Récupérer mon profil")
    public ResponseEntity<UtilisateurDto> getMonProfil(Authentication authentication) {
        log.debug("📝 Récupération du profil pour: {}", authentication.getName());

        String email = authentication.getName();
        UtilisateurDto utilisateur = utilisateurService.getUtilisateurByEmail(email);

        return ResponseEntity.ok(utilisateur);
    }

    /**
     * Mettre à jour le profil de l'utilisateur connecté
     * Accessible à tous les utilisateurs authentifiés
     */
    @PutMapping("/me")
    @Operation(summary = "Modifier mon profil")
    public ResponseEntity<UtilisateurDto> updateMonProfil(
            @Valid @RequestBody UpdateUtilisateurDto dto,
            Authentication authentication) {

        log.debug("✏️ Mise à jour du profil pour: {}", authentication.getName());

        String email = authentication.getName();
        UtilisateurDto utilisateur = utilisateurService.updateProfilUtilisateur(email, dto);

        log.info("✅ Profil mis à jour pour: {}", email);

        return ResponseEntity.ok(utilisateur);
    }

    /**
     * Créer un utilisateur - ADMIN uniquement
     */
    @PostMapping
    @Operation(summary = "Créer un utilisateur (ADMIN)")
    public ResponseEntity<UtilisateurDto> creerUtilisateur(@Valid @RequestBody CreateUtilisateurDto dto) {
        log.debug("➕ Création d'un nouvel utilisateur: {}", dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurService.creerUtilisateur(dto));
    }

    /**
     * Liste tous les utilisateurs - ADMIN uniquement
     */
    @GetMapping
    public ResponseEntity<List<UtilisateurDto>> getAllUtilisateurs() {
        log.debug("📋 Récupération de tous les utilisateurs");
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }

    /**
     * Obtenir un utilisateur par ID - ADMIN uniquement
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un utilisateur par ID (ADMIN)")
    public ResponseEntity<UtilisateurDto> getUtilisateur(@PathVariable Long id) {
        log.debug("🔍 Récupération de l'utilisateur {}", id);
        return ResponseEntity.ok(utilisateurService.getUtilisateurById(id));
    }

    /**
     * Filtrer par profil - ADMIN uniquement
     */
    @GetMapping("/profile/{profile}")
    @Operation(summary = "Filtrer par profil (ADMIN)")
    public ResponseEntity<List<UtilisateurDto>> getByProfile(@PathVariable ProfileEnum profile) {
        log.debug("🔍 Recherche utilisateurs avec profil: {}", profile);
        return ResponseEntity.ok(utilisateurService.getUtilisateursByProfile(profile));
    }

    /**
     * Modifier un utilisateur - ADMIN uniquement
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurDto> updateUtilisateur(
            @PathVariable Long id, @Valid @RequestBody CreateUtilisateurDto dto) {
        log.debug("✏️ Modification de l'utilisateur {}", id);
        return ResponseEntity.ok(utilisateurService.updateUtilisateur(id, dto));
    }

    /**
     * Supprimer un utilisateur - ADMIN uniquement
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur (ADMIN)")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        log.debug("🗑️ Suppression de l'utilisateur {}", id);
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }
}
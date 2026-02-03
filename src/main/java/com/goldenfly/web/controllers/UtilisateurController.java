package com.goldenfly.web.controllers;

import com.goldenfly.design.services.UtilisateurService;
import com.goldenfly.domain.enums.ProfileEnum;
import com.goldenfly.web.dtos.CreateUtilisateurDto;
import com.goldenfly.web.dtos.UtilisateurDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping
    @Operation(summary = "Créer un utilisateur")
    public ResponseEntity<UtilisateurDto> creerUtilisateur(@Valid @RequestBody CreateUtilisateurDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurService.creerUtilisateur(dto));
    }

    @GetMapping
    @Operation(summary = "Liste tous les utilisateurs")
    public ResponseEntity<List<UtilisateurDto>> getAllUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un utilisateur par ID")
    public ResponseEntity<UtilisateurDto> getUtilisateur(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.getUtilisateurById(id));
    }

    @GetMapping("/profile/{profile}")
    @Operation(summary = "Filtrer par profil")
    public ResponseEntity<List<UtilisateurDto>> getByProfile(@PathVariable ProfileEnum profile) {
        return ResponseEntity.ok(utilisateurService.getUtilisateursByProfile(profile));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un utilisateur")
    public ResponseEntity<UtilisateurDto> updateUtilisateur(
            @PathVariable Long id, @Valid @RequestBody CreateUtilisateurDto dto) {
        return ResponseEntity.ok(utilisateurService.updateUtilisateur(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }
}
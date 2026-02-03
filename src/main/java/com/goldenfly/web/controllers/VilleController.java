package com.goldenfly.web.controllers;

import com.goldenfly.design.services.VilleService;
import com.goldenfly.web.dtos.CreateVilleDto;
import com.goldenfly.web.dtos.VilleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villes")
@RequiredArgsConstructor
@Tag(name = "Villes", description = "Gestion des villes et aéroports")
public class VilleController {

    private final VilleService villeService;

    @PostMapping
    @Operation(summary = "Créer une ville")
    public ResponseEntity<VilleDto> creerVille(@Valid @RequestBody CreateVilleDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(villeService.creerVille(dto));
    }

    @GetMapping
    @Operation(summary = "Liste toutes les villes")
    public ResponseEntity<List<VilleDto>> getAllVilles() {
        return ResponseEntity.ok(villeService.getAllVilles());
    }

    @GetMapping("/actives")
    @Operation(summary = "Liste les villes actives")
    public ResponseEntity<List<VilleDto>> getVillesActives() {
        return ResponseEntity.ok(villeService.getVillesActives());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une ville par ID")
    public ResponseEntity<VilleDto> getVille(@PathVariable Long id) {
        return ResponseEntity.ok(villeService.getVilleById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une ville")
    public ResponseEntity<VilleDto> updateVille(
            @PathVariable Long id, @Valid @RequestBody CreateVilleDto dto) {
        return ResponseEntity.ok(villeService.updateVille(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une ville")
    public ResponseEntity<Void> deleteVille(@PathVariable Long id) {
        villeService.deleteVille(id);
        return ResponseEntity.noContent().build();
    }
}
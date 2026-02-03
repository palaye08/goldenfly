package com.goldenfly.web.controllers;

import com.goldenfly.design.services.VolService;
import com.goldenfly.web.dtos.CreateVolDto;
import com.goldenfly.web.dtos.SearchVolDto;
import com.goldenfly.web.dtos.VolDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vols")
@RequiredArgsConstructor
@Tag(name = "Vols", description = "Gestion des vols")
public class VolController {

    private final VolService volService;

    @PostMapping
    @Operation(summary = "Créer un vol")
    public ResponseEntity<VolDto> creerVol(@Valid @RequestBody CreateVolDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(volService.creerVol(dto));
    }

    @PostMapping("/rechercher")
    @Operation(summary = "Rechercher des vols disponibles")
    public ResponseEntity<List<VolDto>> rechercherVols(@RequestBody SearchVolDto searchDto) {
        return ResponseEntity.ok(volService.rechercherVols(searchDto));
    }

    @GetMapping
    @Operation(summary = "Liste tous les vols")
    public ResponseEntity<List<VolDto>> getAllVols() {
        return ResponseEntity.ok(volService.getAllVols());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un vol par ID")
    public ResponseEntity<VolDto> getVol(@PathVariable Long id) {
        return ResponseEntity.ok(volService.getVolById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un vol")
    public ResponseEntity<Void> deleteVol(@PathVariable Long id) {
        volService.deleteVol(id);
        return ResponseEntity.noContent().build();
    }
}
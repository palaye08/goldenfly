package com.goldenfly.web.controllers;

import com.goldenfly.design.services.PaiementService;
import com.goldenfly.web.dtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
@Tag(name = "Paiements", description = "Gestion des paiements")
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping("/initier")
    @Operation(summary = "Initier un paiement en ligne (Wave/Orange Money)")
    public ResponseEntity<PaiementDto> initierPaiement(@Valid @RequestBody InitierPaiementDto dto) {
        return ResponseEntity.ok(paiementService.initierPaiement(dto));
    }

    @PostMapping("/manuel")
    @Operation(summary = "Enregistrer un paiement manuel (Admin uniquement)")
    public ResponseEntity<PaiementDto> enregistrerPaiementManuel(@Valid @RequestBody PaiementManuelDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paiementService.enregistrerPaiementManuel(dto));
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Obtenir le statut du paiement d'une réservation")
    public ResponseEntity<PaiementDto> getStatutPaiement(@PathVariable Long reservationId) {
        return ResponseEntity.ok(paiementService.getStatutPaiement(reservationId));
    }

    @GetMapping
    @Operation(summary = "Lister tous les paiements (Admin)")
    public ResponseEntity<List<PaiementDto>> getAllPaiements() {
        return ResponseEntity.ok(paiementService.getAllPaiements());
    }

    // Callbacks pour les providers de paiement
    @PostMapping("/wave/callback")
    @Operation(summary = "Callback Wave (webhook)")
    public ResponseEntity<Void> waveCallback(@RequestBody WaveCallbackDto callback) {
        paiementService.traiterCallbackWave(callback);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orange/callback")
    @Operation(summary = "Callback Orange Money (webhook)")
    public ResponseEntity<Void> orangeMoneyCallback(@RequestBody Map<String, Object> callback) {
        // Traiter le callback Orange Money (structure similaire)
        return ResponseEntity.ok().build();
    }
}
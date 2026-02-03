package com.goldenfly.web.controllers;

import com.goldenfly.design.services.ReservationService;
import com.goldenfly.web.dtos.BilletDto;
import com.goldenfly.web.dtos.CreateReservationDto;
import com.goldenfly.web.dtos.ReservationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "Gestion des réservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Créer une réservation")
    public ResponseEntity<ReservationDto> creerReservation(@Valid @RequestBody CreateReservationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.creerReservation(dto));
    }

    @GetMapping
    @Operation(summary = "Liste toutes les réservations")
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    @Operation(summary = "Réservations par utilisateur")
    public ResponseEntity<List<ReservationDto>> getReservationsByUtilisateur(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(reservationService.getReservationsByUtilisateur(utilisateurId));
    }

    @GetMapping("/{id}/billet")
    @Operation(summary = "Obtenir le billet")
    public ResponseEntity<BilletDto> getBillet(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getBillet(id));
    }

    @PutMapping("/{id}/confirmer")
    @Operation(summary = "Confirmer une réservation")
    public ResponseEntity<ReservationDto> confirmerReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmerReservation(id));
    }

    @PutMapping("/{id}/annuler")
    @Operation(summary = "Annuler une réservation")
    public ResponseEntity<ReservationDto> annulerReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.annulerReservation(id));
    }
}
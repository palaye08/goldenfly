package com.goldenfly.web.dtos;

import com.goldenfly.domain.enums.ModePaiementEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitierPaiementDto {

    @NotNull(message = "L'ID de la réservation est obligatoire")
    private Long reservationId;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiementEnum modePaiement;

    // Pour Wave et Orange Money
    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "Le numéro de téléphone doit être valide")
    private String numeroTelephone;
}
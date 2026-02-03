package com.goldenfly.web.dtos;

import com.goldenfly.domain.enums.ModePaiementEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementManuelDto {

    @NotNull(message = "L'ID de la réservation est obligatoire")
    private Long reservationId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false)
    private Double montant;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiementEnum modePaiement;

    private String numeroRecu;
    private String commentaire;
}
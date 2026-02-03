package com.goldenfly.web.dtos;

import com.goldenfly.domain.enums.ModePaiementEnum;
import com.goldenfly.domain.enums.StatutPaiementEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementDto {
    private Long id;
    private String numeroPaiement;
    private Long reservationId;
    private String numeroReservation;
    private Double montant;
    private ModePaiementEnum modePaiement;
    private StatutPaiementEnum statut;
    private String transactionId;
    private String numeroTelephone;
    private String numeroRecu;
    private String referenceExterne;
    private LocalDateTime datePaiement;
    private LocalDateTime dateCreation;
    private String commentaire;
}
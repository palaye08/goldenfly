package com.goldenfly.web.dtos;

import com.goldenfly.domain.enums.ClasseVolEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BilletDto {
    private String numeroReservation;
    private String nomPassager;
    private String prenomPassager;
    private String emailPassager;
    private String telephonePassager;

    // Vol Aller
    private String numeroVolAller;
    private String villeDepartAller;
    private String codeVilleDepartAller;
    private String villeArriveeAller;
    private String codeVilleArriveeAller;
    private LocalDate dateDepartAller;
    private LocalTime heureDepartAller;
    private LocalTime heureArriveeAller;
    private Integer dureeVolAller;

    // Vol Retour (optionnel)
    private String numeroVolRetour;
    private String villeDepartRetour;
    private String codeVilleDepartRetour;
    private String villeArriveeRetour;
    private String codeVilleArriveeRetour;
    private LocalDate dateDepartRetour;
    private LocalTime heureDepartRetour;
    private LocalTime heureArriveeRetour;
    private Integer dureeVolRetour;

    private ClasseVolEnum classeVol;
    private Integer nombrePassagers;
    private Double prixTotal;
    private String qrCode;
    private String statut;
}
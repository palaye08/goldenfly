package com.goldenfly.web.dtos;

import com.goldenfly.domain.enums.ClasseVolEnum;
import com.goldenfly.domain.enums.StatutReservationEnum;
import com.goldenfly.domain.enums.TypeReservationEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Long id;
    private String numeroReservation;
    private UtilisateurDto utilisateur;
    private VolDto volAller;
    private VolDto volRetour;
    private TypeReservationEnum typeReservation;
    private ClasseVolEnum classeVol;
    private LocalDate dateDepart;
    private LocalDate dateRetour;
    private Double prixTotal;
    private StatutReservationEnum statut;
    private String qrCode;
    private LocalDateTime dateEmbarquement;
    private Integer nombrePassagers;
    private LocalDateTime dateCreation;
    private Boolean estPaye;
}
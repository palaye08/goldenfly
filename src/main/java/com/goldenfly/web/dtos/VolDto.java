package com.goldenfly.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolDto {
    private Long id;
    private String numeroVol;
    private String nom;
    private VilleDto villeDepart;
    private VilleDto villeArrivee;
    private LocalTime heureDepart;
    private LocalTime heureArrivee;
    private Integer dureeVol;
    private Integer nombreSieges;
    private Integer siegesDisponibles;
    private Double prixBase;
    private Double distance;
    private Boolean lundi;
    private Boolean mardi;
    private Boolean mercredi;
    private Boolean jeudi;
    private Boolean vendredi;
    private Boolean samedi;
    private Boolean dimanche;
    private Boolean actif;
    private LocalDateTime dateCreation;
}
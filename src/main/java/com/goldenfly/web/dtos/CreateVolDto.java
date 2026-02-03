package com.goldenfly.web.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVolDto {

    @NotBlank(message = "Le numéro de vol est obligatoire")
    private String numeroVol;

    @NotBlank(message = "Le nom du vol est obligatoire")
    private String nom;

    @NotNull(message = "La ville de départ est obligatoire")
    private Long villeDepartId;

    @NotNull(message = "La ville d'arrivée est obligatoire")
    private Long villeArriveeId;

    @NotNull(message = "L'heure de départ est obligatoire")
    private LocalTime heureDepart;

    @NotNull(message = "L'heure d'arrivée est obligatoire")
    private LocalTime heureArrivee;

    @NotNull(message = "La durée du vol est obligatoire")
    @Min(value = 1, message = "La durée doit être d'au moins 1 minute")
    private Integer dureeVol;

    @NotNull(message = "Le nombre de sièges est obligatoire")
    @Min(value = 1, message = "Le nombre de sièges doit être au moins 1")
    @Max(value = 1000, message = "Le nombre de sièges maximum est 1000")
    private Integer nombreSieges;

    @NotNull(message = "Le prix de base est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private Double prixBase;

    @NotNull(message = "La distance est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "La distance doit être supérieure à 0")
    private Double distance;

    private Boolean lundi = false;
    private Boolean mardi = false;
    private Boolean mercredi = false;
    private Boolean jeudi = false;
    private Boolean vendredi = false;
    private Boolean samedi = false;
    private Boolean dimanche = false;
}
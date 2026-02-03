package com.goldenfly.web.dtos;

import com.goldenfly.domain.enums.ClasseVolEnum;
import com.goldenfly.domain.enums.TypeReservationEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationDto {

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long utilisateurId;

    @NotNull(message = "L'ID du vol aller est obligatoire")
    private Long volAllerId;

    private Long volRetourId;

    @NotNull(message = "Le type de réservation est obligatoire")
    private TypeReservationEnum typeReservation;

    @NotNull(message = "La classe de vol est obligatoire")
    private ClasseVolEnum classeVol;

    @NotNull(message = "La date de départ est obligatoire")
    @FutureOrPresent(message = "La date de départ doit être dans le futur ou aujourd'hui")
    private LocalDate dateDepart;

    @Future(message = "La date de retour doit être dans le futur")
    private LocalDate dateRetour;

    @NotNull(message = "Le nombre de passagers est obligatoire")
    @Min(value = 1, message = "Le nombre de passagers doit être au moins 1")
    @Max(value = 10, message = "Le nombre maximum de passagers est 10")
    private Integer nombrePassagers = 1;
}
package com.goldenfly.web.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVilleDto {

    @NotBlank(message = "Le nom de la ville est obligatoire")
    private String nom;

    @NotBlank(message = "Le code de la ville est obligatoire")
    @Size(min = 3, max = 3, message = "Le code doit contenir exactement 3 caractères")
    private String code;

    @NotBlank(message = "Le nom de l'aéroport est obligatoire")
    private String nomAeroport;

    @NotBlank(message = "Le pays est obligatoire")
    private String pays;

    private Double latitude;
    private Double longitude;
}
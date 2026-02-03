package com.goldenfly.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VilleDto {
    private Long id;
    private String nom;
    private String code;
    private String nomAeroport;
    private String pays;
    private Double latitude;
    private Double longitude;
    private Boolean actif;
    private LocalDateTime dateCreation;
}
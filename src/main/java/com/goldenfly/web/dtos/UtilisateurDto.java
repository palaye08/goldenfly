package com.goldenfly.web.dtos;

import com.goldenfly.domain.enums.ProfileEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Integer age;
    private ProfileEnum profile;
    private Boolean actif;
    private LocalDateTime dateCreation;
}
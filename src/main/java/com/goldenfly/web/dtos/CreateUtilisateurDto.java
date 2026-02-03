package com.goldenfly.web.dtos;

import com.goldenfly.domain.enums.ProfileEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUtilisateurDto {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "Le téléphone doit être valide")
    private String telephone;

    @NotNull(message = "L'âge est obligatoire")
    @Min(value = 18, message = "L'âge minimum est 18 ans")
    @Max(value = 120, message = "L'âge maximum est 120 ans")
    private Integer age;

    @NotNull(message = "Le profil est obligatoire")
    private ProfileEnum profile;
}
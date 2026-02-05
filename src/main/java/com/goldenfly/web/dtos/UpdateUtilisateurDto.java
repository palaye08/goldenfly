package com.goldenfly.web.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUtilisateurDto {

    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @Email(message = "L'email doit être valide")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "Le téléphone doit être valide")
    private String telephone;

    @Min(value = 18, message = "L'âge minimum est 18 ans")
    @Max(value = 120, message = "L'âge maximum est 120 ans")
    private Integer age;

    // Pour le changement de mot de passe (optionnel)
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String nouveauMotDePasse;

    @Size(min = 6, message = "Veuillez confirmer le nouveau mot de passe")
    private String confirmationMotDePasse;
}
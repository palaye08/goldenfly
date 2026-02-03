package com.goldenfly.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// AuthResponseDto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private UtilisateurDto utilisateur;
}

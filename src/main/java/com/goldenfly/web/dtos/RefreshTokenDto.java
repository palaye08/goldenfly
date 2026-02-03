package com.goldenfly.web.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// RefreshTokenDto
@Data
@NoArgsConstructor
@AllArgsConstructor
class RefreshTokenDto {
    @NotBlank(message = "Le refresh token est obligatoire")
    private String refreshToken;
}
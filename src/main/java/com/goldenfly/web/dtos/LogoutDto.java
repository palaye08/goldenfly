package com.goldenfly.web.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// LogoutDto
@Data
@NoArgsConstructor
@AllArgsConstructor
class LogoutDto {
    @NotBlank(message = "Le token est obligatoire")
    private String token;
}
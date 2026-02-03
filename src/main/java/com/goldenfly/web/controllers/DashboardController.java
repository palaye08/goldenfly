package com.goldenfly.web.controllers;

import com.goldenfly.design.services.DashboardService;
import com.goldenfly.web.dtos.DashboardDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Statistiques de la compagnie")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/statistiques")
    @Operation(summary = "Obtenir les statistiques")
    public ResponseEntity<DashboardDto> getStatistiques() {
        return ResponseEntity.ok(dashboardService.getStatistiques());
    }
}
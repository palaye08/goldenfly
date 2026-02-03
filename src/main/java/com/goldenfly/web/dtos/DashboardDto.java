package com.goldenfly.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private Long totalReservations;
    private Long totalVols;
    private Long totalUtilisateurs;
    private Long totalVilles;
    private Double revenusTotal;
    private Long reservationsConfirmees;
    private Long reservationsEnAttente;
    private Long reservationsAnnulees;
    private Long reservationsEmbarquees;
    private Map<String, Long> reservationsParMois;
    private Map<String, Double> revenusParMois;
    private Map<String, Long> volsPopulaires;
}
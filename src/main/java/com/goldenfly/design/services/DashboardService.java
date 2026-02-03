package com.goldenfly.design.services;

import com.goldenfly.design.repositories.*;
import com.goldenfly.domain.enums.StatutReservationEnum;
import com.goldenfly.web.dtos.DashboardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ReservationRepository reservationRepository;
    private final VolRepository volRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VilleRepository villeRepository;

    public DashboardDto getStatistiques() {
        DashboardDto dashboard = new DashboardDto();

        dashboard.setTotalReservations(reservationRepository.count());
        dashboard.setTotalVols(volRepository.count());
        dashboard.setTotalUtilisateurs(utilisateurRepository.count());
        dashboard.setTotalVilles(villeRepository.count());

        dashboard.setRevenusTotal(reservationRepository.calculateTotalRevenue());

        dashboard.setReservationsConfirmees(
                reservationRepository.countByStatut(StatutReservationEnum.CONFIRMEE));
        dashboard.setReservationsEnAttente(
                reservationRepository.countByStatut(StatutReservationEnum.EN_ATTENTE));
        dashboard.setReservationsAnnulees(
                reservationRepository.countByStatut(StatutReservationEnum.ANNULEE));
        dashboard.setReservationsEmbarquees(
                reservationRepository.countByStatut(StatutReservationEnum.EMBARQUEE));

        dashboard.setReservationsParMois(new HashMap<>());
        dashboard.setRevenusParMois(new HashMap<>());
        dashboard.setVolsPopulaires(new HashMap<>());

        return dashboard;
    }
}
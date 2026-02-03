package com.goldenfly.config;

import com.goldenfly.design.services.PaiementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final PaiementService paiementService;

    /**
     * Vérifie et annule les réservations non payées 24h avant le départ
     * S'exécute toutes les heures
     */
    @Scheduled(cron = "0 0 * * * *") // Toutes les heures à la minute 0
    public void annulerReservationsNonPayees() {
        log.info("Début de la vérification des réservations non payées");

        try {
            paiementService.annulerReservationsNonPayees();
            log.info("Vérification des réservations non payées terminée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de l'annulation des réservations non payées", e);
        }
    }

    /**
     * Alternative : S'exécute toutes les 30 minutes pour une vérification plus fréquente
     * Décommentez cette méthode et commentez celle du dessus si vous préférez
     */
    // @Scheduled(cron = "0 */30 * * * *") // Toutes les 30 minutes
    // public void annulerReservationsNonPayeesFrequent() {
    //     log.info("Début de la vérification des réservations non payées (fréquente)");
    //
    //     try {
    //         paiementService.annulerReservationsNonPayees();
    //         log.info("Vérification des réservations non payées terminée avec succès");
    //     } catch (Exception e) {
    //         log.error("Erreur lors de l'annulation des réservations non payées", e);
    //     }
    // }
}
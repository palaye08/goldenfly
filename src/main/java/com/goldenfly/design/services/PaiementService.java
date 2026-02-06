package com.goldenfly.design.services;

import com.goldenfly.design.repositories.PaiementRepository;
import com.goldenfly.design.repositories.ReservationRepository;
import com.goldenfly.design.repositories.VolRepository;
import com.goldenfly.domain.entities.Paiement;
import com.goldenfly.domain.entities.Reservation;
import com.goldenfly.domain.entities.Vol;
import com.goldenfly.domain.enums.ModePaiementEnum;
import com.goldenfly.domain.enums.StatutPaiementEnum;
import com.goldenfly.domain.enums.StatutReservationEnum;
import com.goldenfly.helpers.PaiementHelper;
import com.goldenfly.web.dtos.*;
import com.goldenfly.web.mappers.PaiementMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;
    private final VolRepository volRepository;
    private final PaiementMapper paiementMapper;
    private final PaiementHelper paiementHelper;
    private final WaveService waveService;
    private final OrangeMoneyService orangeMoneyService;

    /**
     * Initier un paiement en ligne (Wave ou Orange Money)
     */
    public PaiementDto initierPaiement(InitierPaiementDto dto) {
        log.debug("🔄 Initiation du paiement pour la réservation {}", dto.getReservationId());

        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // Vérifier si la réservation est déjà payée (en vérifiant le paiement existant)
        paiementRepository.findByReservation(reservation).ifPresent(existingPaiement -> {
            if (existingPaiement.getStatut() == StatutPaiementEnum.PAYE) {
                log.warn("⚠️ Tentative de paiement d'une réservation déjà payée: {}", reservation.getNumeroReservation());
                throw new RuntimeException("Cette réservation est déjà payée");
            }
        });

        // Vérifier si la réservation est annulée
//        if (reservation.getStatut() == StatutReservationEnum.ANNULEE) {
//            log.warn("⚠️ Tentative de paiement d'une réservation annulée: {}", reservation.getNumeroReservation());
//            throw new RuntimeException("Cette réservation est annulée");
//        }

        // Créer le paiement
        Paiement paiement = new Paiement();
        paiement.setNumeroPaiement(paiementHelper.genererNumeroPaiement());
        paiement.setReservation(reservation);
        paiement.setMontant(reservation.getPrixTotal());
        paiement.setModePaiement(dto.getModePaiement());
        paiement.setStatut(StatutPaiementEnum.EN_ATTENTE);
        paiement.setNumeroTelephone(dto.getNumeroTelephone());

        // Initier le paiement selon le mode choisi
        String referenceExterne = null;

        if (dto.getModePaiement() == ModePaiementEnum.WAVE) {
            log.debug("🌊 Initiation paiement Wave");
            referenceExterne = waveService.initierPaiement(
                    paiement.getNumeroPaiement(),
                    reservation.getPrixTotal(),
                    dto.getNumeroTelephone()
            );
        } else if (dto.getModePaiement() == ModePaiementEnum.ORANGE_MONEY) {
            log.debug("🍊 Initiation paiement Orange Money");
            referenceExterne = orangeMoneyService.initierPaiement(
                    paiement.getNumeroPaiement(),
                    reservation.getPrixTotal(),
                    dto.getNumeroTelephone()
            );
        }

        paiement.setReferenceExterne(referenceExterne);
        paiement = paiementRepository.save(paiement);

        log.info("✅ Paiement initié: {} - Montant: {} FCFA", paiement.getNumeroPaiement(), paiement.getMontant());

        return paiementMapper.toDto(paiement);
    }

    /**
     * Enregistrer un paiement manuel (par l'admin ou l'utilisateur)
     */
    public PaiementDto enregistrerPaiementManuel(PaiementManuelDto dto) {
        log.debug("💳 Enregistrement paiement manuel pour la réservation {}", dto.getReservationId());

        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // Vérifier si la réservation est déjà payée
//        paiementRepository.findByReservation(reservation).ifPresent(existingPaiement -> {
//            if (existingPaiement.getStatut() == StatutPaiementEnum.PAYE) {
//                log.warn("⚠️ Tentative de paiement d'une réservation déjà payée: {}", reservation.getNumeroReservation());
//                throw new RuntimeException("Cette réservation est déjà payée");
//            }
//        });

        // Vérifier si la réservation est annulée
        if (reservation.getStatut() == StatutReservationEnum.ANNULEE) {
            log.warn("⚠️ Tentative de paiement d'une réservation annulée: {}", reservation.getNumeroReservation());
            throw new RuntimeException("Cette réservation est annulée");
        }

        log.debug("📝 Avant paiement - Réservation {} - Statut: {}",
                reservation.getNumeroReservation(), reservation.getStatut());

        // Créer le paiement
        Paiement paiement = new Paiement();
        paiement.setNumeroPaiement(paiementHelper.genererNumeroPaiement());
        paiement.setReservation(reservation);
        paiement.setMontant(dto.getMontant());
        paiement.setModePaiement(dto.getModePaiement());
        paiement.setStatut(StatutPaiementEnum.PAYE);  // Statut PAYE
        paiement.setNumeroRecu(dto.getNumeroRecu());
        paiement.setCommentaire(dto.getCommentaire());
        paiement.setDatePaiement(LocalDateTime.now());

        // Sauvegarder le paiement d'abord
        paiement = paiementRepository.save(paiement);
        log.debug("✅ Paiement sauvegardé: {} - Statut: {}", paiement.getNumeroPaiement(), paiement.getStatut());

        // Mettre à jour le statut de la réservation à CONFIRMEE
        reservation.setStatut(StatutReservationEnum.CONFIRMEE);
        reservation.setPaiement(paiement);

        // Sauvegarder explicitement avec saveAndFlush pour forcer l'écriture immédiate
        reservation = reservationRepository.saveAndFlush(reservation);

        log.debug("📝 Après paiement - Réservation {} - Statut: {}",
                reservation.getNumeroReservation(), reservation.getStatut());

        log.info("✅ Paiement manuel enregistré: {} - Réservation: {} - Montant: {} FCFA - Statut réservation: {}",
                paiement.getNumeroPaiement(), reservation.getNumeroReservation(),
                paiement.getMontant(), reservation.getStatut());

        return paiementMapper.toDto(paiement);
    }

    /**
     * Callback Wave après paiement
     */
    public void traiterCallbackWave(WaveCallbackDto callback) {
        log.debug("🌊 Traitement callback Wave pour référence: {}", callback.getReference());

        Paiement paiement = paiementRepository.findByNumeroPaiement(callback.getReference())
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

        paiement.setTransactionId(callback.getId());

        if ("success".equalsIgnoreCase(callback.getStatus())) {
            paiement.setStatut(StatutPaiementEnum.PAYE);
            paiement.setDatePaiement(LocalDateTime.now());

            // Mettre à jour le statut de la réservation
            Reservation reservation = paiement.getReservation();
            reservation.setStatut(StatutReservationEnum.CONFIRMEE);
            reservationRepository.saveAndFlush(reservation);

            log.info("✅ Paiement Wave réussi: {} - Réservation: {} - Statut: {}",
                    paiement.getNumeroPaiement(), reservation.getNumeroReservation(), reservation.getStatut());
        } else {
            paiement.setStatut(StatutPaiementEnum.ECHOUE);
            paiement.setCommentaire("Paiement échoué: " + callback.getStatus());
            log.warn("❌ Paiement Wave échoué: {} - Status: {}", paiement.getNumeroPaiement(), callback.getStatus());
        }

        paiementRepository.save(paiement);
    }

    /**
     * Callback Orange Money après paiement
     */
    public void traiterCallbackOrangeMoney(OrangeMoneyCallbackDto callback) {
        log.debug("🍊 Traitement callback Orange Money pour référence: {}", callback.getReference());

        Paiement paiement = paiementRepository.findByNumeroPaiement(callback.getReference())
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

        paiement.setTransactionId(callback.getTransactionId());

        if ("SUCCESS".equalsIgnoreCase(callback.getStatus())) {
            paiement.setStatut(StatutPaiementEnum.PAYE);
            paiement.setDatePaiement(LocalDateTime.now());

            // Mettre à jour le statut de la réservation
            Reservation reservation = paiement.getReservation();
            reservation.setStatut(StatutReservationEnum.CONFIRMEE);
            reservationRepository.saveAndFlush(reservation);

            log.info("✅ Paiement Orange Money réussi: {} - Réservation: {} - Statut: {}",
                    paiement.getNumeroPaiement(), reservation.getNumeroReservation(), reservation.getStatut());
        } else {
            paiement.setStatut(StatutPaiementEnum.ECHOUE);
            paiement.setCommentaire("Paiement échoué: " + callback.getStatus());
            log.warn("❌ Paiement Orange Money échoué: {} - Status: {}", paiement.getNumeroPaiement(), callback.getStatus());
        }

        paiementRepository.save(paiement);
    }

    /**
     * Vérifier le statut d'un paiement
     */
    @Transactional(readOnly = true)
    public PaiementDto getStatutPaiement(Long reservationId) {
        log.debug("🔍 Vérification statut paiement pour réservation {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        Paiement paiement = paiementRepository.findByReservation(reservation)
                .orElseThrow(() -> new RuntimeException("Aucun paiement trouvé pour cette réservation"));

        log.debug("📊 Paiement trouvé: {} - Statut: {} - Montant: {} FCFA",
                paiement.getNumeroPaiement(), paiement.getStatut(), paiement.getMontant());

        return paiementMapper.toDto(paiement);
    }

    /**
     * Lister tous les paiements (ADMIN)
     */
    @Transactional(readOnly = true)
    public List<PaiementDto> getAllPaiements() {
        log.debug("📋 Récupération de tous les paiements (ADMIN)");

        List<Paiement> paiements = paiementRepository.findAll();

        log.debug("📊 {} paiement(s) trouvé(s)", paiements.size());

        return paiements.stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Lister les paiements d'un utilisateur
     * OPTIMISÉ: Utilise une requête JPQL au lieu de filtrer en mémoire
     */
    @Transactional(readOnly = true)
    public List<PaiementDto> getPaiementsByUtilisateur(Long utilisateurId) {
        log.debug("📋 Récupération des paiements pour l'utilisateur {}", utilisateurId);

        // OPTIMISATION: Requête directe au lieu de findAll() + filter
        List<Paiement> paiements = paiementRepository.findByUtilisateurId(utilisateurId);

        log.debug("📊 {} paiement(s) trouvé(s) pour l'utilisateur {}", paiements.size(), utilisateurId);

        return paiements.stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Annuler les réservations non payées 24h avant le départ
     */
    @Transactional
    public void annulerReservationsNonPayees() {
        log.debug("🔍 Recherche des réservations non payées à annuler...");

        LocalDateTime maintenant = LocalDateTime.now();

        // Récupérer toutes les réservations EN_ATTENTE avec date limite dépassée
        List<Reservation> reservations = reservationRepository.findAll().stream()
                .filter(r -> r.getStatut() == StatutReservationEnum.EN_ATTENTE)
                .filter(r -> r.getDateLimitePaiement() != null && r.getDateLimitePaiement().isBefore(maintenant))
                .filter(r -> {
                    // Vérifier qu'il n'y a pas de paiement avec statut PAYE
                    return paiementRepository.findByReservation(r)
                            .map(p -> p.getStatut() != StatutPaiementEnum.PAYE)
                            .orElse(true);  // Si pas de paiement, considérer comme non payé
                })
                .collect(Collectors.toList());

        log.info("📊 {} réservation(s) à annuler pour non-paiement", reservations.size());

        for (Reservation reservation : reservations) {
            reservation.setStatut(StatutReservationEnum.ANNULEE);

            // Libérer les sièges
            Vol volAller = reservation.getVolAller();
            volAller.setSiegesDisponibles(volAller.getSiegesDisponibles() + reservation.getNombrePassagers());
            volRepository.save(volAller);

            if (reservation.getVolRetour() != null) {
                Vol volRetour = reservation.getVolRetour();
                volRetour.setSiegesDisponibles(volRetour.getSiegesDisponibles() + reservation.getNombrePassagers());
                volRepository.save(volRetour);
            }

            log.info("❌ Réservation annulée pour non-paiement: {} - Date limite dépassée: {}",
                    reservation.getNumeroReservation(), reservation.getDateLimitePaiement());
        }

        reservationRepository.saveAll(reservations);
    }
}
package com.goldenfly.design.services;

import com.goldenfly.design.repositories.PaiementRepository;
import com.goldenfly.design.repositories.ReservationRepository;
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
    private final PaiementMapper paiementMapper;
    private final PaiementHelper paiementHelper;
    private final WaveService waveService;
    private final OrangeMoneyService orangeMoneyService;

    /**
     * Initier un paiement en ligne (Wave ou Orange Money)
     */
    public PaiementDto initierPaiement(InitierPaiementDto dto) {
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // Vérifier si la réservation est déjà payée
        if (reservation.getEstPaye()) {
            throw new RuntimeException("Cette réservation est déjà payée");
        }

        // Vérifier si la réservation est annulée
        if (reservation.getStatut() == StatutReservationEnum.ANNULEE) {
            throw new RuntimeException("Cette réservation est annulée");
        }

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
            referenceExterne = waveService.initierPaiement(
                    paiement.getNumeroPaiement(),
                    reservation.getPrixTotal(),
                    dto.getNumeroTelephone()
            );
        } else if (dto.getModePaiement() == ModePaiementEnum.ORANGE_MONEY) {
            referenceExterne = orangeMoneyService.initierPaiement(
                    paiement.getNumeroPaiement(),
                    reservation.getPrixTotal(),
                    dto.getNumeroTelephone()
            );
        }

        paiement.setReferenceExterne(referenceExterne);
        paiement = paiementRepository.save(paiement);

        return paiementMapper.toDto(paiement);
    }

    /**
     * Enregistrer un paiement manuel (par l'admin)
     */
    public PaiementDto enregistrerPaiementManuel(PaiementManuelDto dto) {
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (reservation.getEstPaye()) {
            throw new RuntimeException("Cette réservation est déjà payée");
        }

        Paiement paiement = new Paiement();
        paiement.setNumeroPaiement(paiementHelper.genererNumeroPaiement());
        paiement.setReservation(reservation);
        paiement.setMontant(dto.getMontant());
        paiement.setModePaiement(dto.getModePaiement());
        paiement.setStatut(StatutPaiementEnum.PAYE);
        paiement.setNumeroRecu(dto.getNumeroRecu());
        paiement.setCommentaire(dto.getCommentaire());
        paiement.setDatePaiement(LocalDateTime.now());

        paiement = paiementRepository.save(paiement);

        // Mettre à jour la réservation
        reservation.setEstPaye(true);
        reservation.setPaiement(paiement);
        reservation.setStatut(StatutReservationEnum.CONFIRMEE);
        reservationRepository.save(reservation);

        return paiementMapper.toDto(paiement);
    }

    /**
     * Callback Wave après paiement
     */
    public void traiterCallbackWave(WaveCallbackDto callback) {
        Paiement paiement = paiementRepository.findByNumeroPaiement(callback.getReference())
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

        paiement.setTransactionId(callback.getId());

        if ("success".equalsIgnoreCase(callback.getStatus())) {
            paiement.setStatut(StatutPaiementEnum.PAYE);
            paiement.setDatePaiement(LocalDateTime.now());

            // Mettre à jour la réservation
            Reservation reservation = paiement.getReservation();
            reservation.setEstPaye(true);
            reservation.setStatut(StatutReservationEnum.CONFIRMEE);
            reservationRepository.save(reservation);

            log.info("Paiement Wave réussi: {}", paiement.getNumeroPaiement());
        } else {
            paiement.setStatut(StatutPaiementEnum.ECHOUE);
            paiement.setCommentaire("Paiement échoué: " + callback.getStatus());
            log.warn("Paiement Wave échoué: {}", paiement.getNumeroPaiement());
        }

        paiementRepository.save(paiement);
    }

    /**
     * Vérifier le statut d'un paiement
     */
    @Transactional(readOnly = true)
    public PaiementDto getStatutPaiement(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        Paiement paiement = paiementRepository.findByReservation(reservation)
                .orElse(null);

        return paiementMapper.toDto(paiement);
    }

    /**
     * Lister tous les paiements
     */
    @Transactional(readOnly = true)
    public List<PaiementDto> getAllPaiements() {
        return paiementRepository.findAll().stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Annuler les réservations non payées 24h avant le départ
     */
    @Transactional
    public void annulerReservationsNonPayees() {
        LocalDateTime maintenant = LocalDateTime.now();

        List<Reservation> reservations = reservationRepository.findAll().stream()
                .filter(r -> !r.getEstPaye() && r.getStatut() == StatutReservationEnum.EN_ATTENTE)
                .filter(r -> r.getDateLimitePaiement() != null && r.getDateLimitePaiement().isBefore(maintenant))
                .collect(Collectors.toList());

        for (Reservation reservation : reservations) {
            reservation.setStatut(StatutReservationEnum.ANNULEE);

            // Libérer les sièges
            Vol volAller = reservation.getVolAller();
            volAller.setSiegesDisponibles(volAller.getSiegesDisponibles() + reservation.getNombrePassagers());

            if (reservation.getVolRetour() != null) {
                Vol volRetour = reservation.getVolRetour();
                volRetour.setSiegesDisponibles(volRetour.getSiegesDisponibles() + reservation.getNombrePassagers());
            }

            log.info("Réservation annulée pour non-paiement: {}", reservation.getNumeroReservation());
        }

        reservationRepository.saveAll(reservations);
    }
}
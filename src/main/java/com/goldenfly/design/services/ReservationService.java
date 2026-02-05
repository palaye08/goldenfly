package com.goldenfly.design.services;

import com.goldenfly.design.repositories.*;
import com.goldenfly.domain.entities.*;
import com.goldenfly.domain.enums.*;
import com.goldenfly.helpers.ReservationHelper;
import com.goldenfly.web.dtos.*;
import com.goldenfly.web.mappers.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VolRepository volRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationHelper reservationHelper;

    public ReservationDto creerReservation(CreateReservationDto dto) {
        log.debug("📝 Création d'une réservation pour l'utilisateur {}", dto.getUtilisateurId());

        // Validation
        Utilisateur utilisateur = utilisateurRepository.findById(dto.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Vol volAller = volRepository.findById(dto.getVolAllerId())
                .orElseThrow(() -> new RuntimeException("Vol aller non trouvé"));

        // Vérifier disponibilité sièges
        Integer siegesOccupes = reservationRepository.countPassengersForVolOnDate(
                volAller.getId(), dto.getDateDepart());
        if (siegesOccupes == null) siegesOccupes = 0;

        if ((siegesOccupes + dto.getNombrePassagers()) > volAller.getNombreSieges()) {
            throw new RuntimeException("Nombre de sièges insuffisant pour ce vol");
        }

        Vol volRetour = null;
        Double prixTotal;

        if (dto.getTypeReservation() == TypeReservationEnum.ALLER_RETOUR) {
            if (dto.getVolRetourId() == null || dto.getDateRetour() == null) {
                throw new RuntimeException("Vol retour et date retour requis pour aller-retour");
            }
            volRetour = volRepository.findById(dto.getVolRetourId())
                    .orElseThrow(() -> new RuntimeException("Vol retour non trouvé"));

            // Vérifier disponibilité sièges vol retour
            Integer siegesOccupesRetour = reservationRepository.countPassengersForVolOnDate(
                    volRetour.getId(), dto.getDateRetour());
            if (siegesOccupesRetour == null) siegesOccupesRetour = 0;

            if ((siegesOccupesRetour + dto.getNombrePassagers()) > volRetour.getNombreSieges()) {
                throw new RuntimeException("Nombre de sièges insuffisant pour le vol retour");
            }

            prixTotal = reservationHelper.calculerPrixAllerRetour(
                    volAller.getPrixBase(),
                    volRetour.getPrixBase(),
                    dto.getClasseVol().getMultiplicateur(),
                    dto.getNombrePassagers()
            );
        } else {
            prixTotal = reservationHelper.calculerPrixTotal(
                    volAller.getPrixBase(),
                    dto.getClasseVol().getMultiplicateur(),
                    dto.getNombrePassagers()
            );
        }

        // Créer réservation
        Reservation reservation = new Reservation();
        reservation.setNumeroReservation(reservationHelper.genererNumeroReservation());
        reservation.setUtilisateur(utilisateur);
        reservation.setVolAller(volAller);
        reservation.setVolRetour(volRetour);
        reservation.setTypeReservation(dto.getTypeReservation());
        reservation.setClasseVol(dto.getClasseVol());
        reservation.setDateDepart(dto.getDateDepart());
        reservation.setDateRetour(dto.getDateRetour());
        reservation.setPrixTotal(prixTotal);
        reservation.setStatut(StatutReservationEnum.EN_ATTENTE);
        reservation.setNombrePassagers(dto.getNombrePassagers());

        // IMPORTANT: Initialiser explicitement estPaye à false
        reservation.setEstPaye(false);

        // Calculer la date limite de paiement (24h avant le départ)
        LocalDateTime dateLimitePaiement = dto.getDateDepart().atStartOfDay().minusHours(24);
        reservation.setDateLimitePaiement(dateLimitePaiement);

        // Générer QR Code
        String qrData = String.format("%s|%s|%s|%s",
                reservation.getNumeroReservation(),
                utilisateur.getEmail(),
                volAller.getNumeroVol(),
                dto.getDateDepart());
        reservation.setQrCode(reservationHelper.genererQRCode(qrData));

        reservation = reservationRepository.save(reservation);

        log.info("✅ Réservation créée avec succès: {} - Montant: {} FCFA - EstPaye: {}",
                reservation.getNumeroReservation(), reservation.getPrixTotal(), reservation.getEstPaye());

        // Mettre à jour sièges disponibles
        volAller.setSiegesDisponibles(volAller.getSiegesDisponibles() - dto.getNombrePassagers());
        volRepository.save(volAller);

        if (volRetour != null) {
            volRetour.setSiegesDisponibles(volRetour.getSiegesDisponibles() - dto.getNombrePassagers());
            volRepository.save(volRetour);
        }

        return reservationMapper.toDto(reservation);
    }

    public BilletDto getBillet(Long reservationId) {
        log.debug("🎫 Récupération du billet pour la réservation {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // Vérifier que la réservation est payée
        if (!Boolean.TRUE.equals(reservation.getEstPaye())) {
            throw new RuntimeException("Cette réservation n'est pas encore payée");
        }

        return reservationMapper.toBilletDto(reservation);
    }

    public ReservationDto confirmerReservation(Long id) {
        log.debug("✅ Confirmation de la réservation {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.setStatut(StatutReservationEnum.CONFIRMEE);
        reservation = reservationRepository.save(reservation);

        log.info("✅ Réservation confirmée: {}", reservation.getNumeroReservation());

        return reservationMapper.toDto(reservation);
    }

    public ReservationDto annulerReservation(Long id) {
        log.debug("❌ Annulation de la réservation {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // Vérifier qu'on peut annuler
        if (reservation.getEstPaye() && reservation.getStatut() == StatutReservationEnum.CONFIRMEE) {
            throw new RuntimeException("Impossible d'annuler une réservation déjà payée et confirmée. Veuillez contacter le service client.");
        }

        reservation.setStatut(StatutReservationEnum.ANNULEE);

        // Libérer sièges
        Vol volAller = reservation.getVolAller();
        volAller.setSiegesDisponibles(volAller.getSiegesDisponibles() + reservation.getNombrePassagers());
        volRepository.save(volAller);

        if (reservation.getVolRetour() != null) {
            Vol volRetour = reservation.getVolRetour();
            volRetour.setSiegesDisponibles(volRetour.getSiegesDisponibles() + reservation.getNombrePassagers());
            volRepository.save(volRetour);
        }

        reservation = reservationRepository.save(reservation);

        log.info("❌ Réservation annulée: {}", reservation.getNumeroReservation());

        return reservationMapper.toDto(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUtilisateur(Long utilisateurId) {
        log.debug("📋 Récupération des réservations pour l'utilisateur {}", utilisateurId);

        List<Reservation> reservations = reservationRepository.findByUtilisateurIdOrderByDateCreationDesc(utilisateurId);

        log.debug("📊 {} réservation(s) trouvée(s)", reservations.size());

        return reservations.stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getAllReservations() {
        log.debug("📋 Récupération de toutes les réservations (ADMIN)");

        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationDto getReservationById(Long id) {
        log.debug("🔍 Récupération de la réservation {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        return reservationMapper.toDto(reservation);
    }
}
package com.goldenfly.design.services;

import com.goldenfly.design.repositories.*;
import com.goldenfly.domain.entities.*;
import com.goldenfly.domain.enums.*;
import com.goldenfly.helpers.ReservationHelper;
import com.goldenfly.web.dtos.*;
import com.goldenfly.web.mappers.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

        // Générer QR Code
        String qrData = String.format("%s|%s|%s|%s",
                reservation.getNumeroReservation(),
                utilisateur.getEmail(),
                volAller.getNumeroVol(),
                dto.getDateDepart());
        reservation.setQrCode(reservationHelper.genererQRCode(qrData));

        reservation = reservationRepository.save(reservation);

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
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        return reservationMapper.toBilletDto(reservation);
    }

    public ReservationDto confirmerReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        reservation.setStatut(StatutReservationEnum.CONFIRMEE);
        reservation = reservationRepository.save(reservation);
        return reservationMapper.toDto(reservation);
    }

    public ReservationDto annulerReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
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
        return reservationMapper.toDto(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUtilisateur(Long utilisateurId) {
        return reservationRepository.findByUtilisateurIdOrderByDateCreationDesc(utilisateurId)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }
}
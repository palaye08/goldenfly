package com.goldenfly.design.services;

import com.goldenfly.design.repositories.VolRepository;
import com.goldenfly.design.repositories.VilleRepository;
import com.goldenfly.design.repositories.ReservationRepository;
import com.goldenfly.domain.entities.Vol;
import com.goldenfly.domain.entities.Ville;
import com.goldenfly.helpers.ReservationHelper;
import com.goldenfly.web.dtos.CreateVolDto;
import com.goldenfly.web.dtos.SearchVolDto;
import com.goldenfly.web.dtos.VolDto;
import com.goldenfly.web.mappers.VolMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VolService {

    private final VolRepository volRepository;
    private final VilleRepository villeRepository;
    private final VolMapper volMapper;
    private final ReservationHelper reservationHelper;
    private final ReservationRepository reservationRepository;

    public VolDto creerVol(CreateVolDto dto) {
        log.info("📝 Création d'un nouveau vol: {}", dto.getNumeroVol());

        if (volRepository.existsByNumeroVol(dto.getNumeroVol())) {
            log.error("❌ Vol {} existe déjà", dto.getNumeroVol());
            throw new RuntimeException("Un vol avec ce numéro existe déjà");
        }

        Ville villeDepart = villeRepository.findById(dto.getVilleDepartId())
                .orElseThrow(() -> new RuntimeException("Ville de départ non trouvée"));
        Ville villeArrivee = villeRepository.findById(dto.getVilleArriveeId())
                .orElseThrow(() -> new RuntimeException("Ville d'arrivée non trouvée"));

        Vol vol = new Vol();
        vol.setNumeroVol(dto.getNumeroVol());
        vol.setNom(dto.getNom());
        vol.setVilleDepart(villeDepart);
        vol.setVilleArrivee(villeArrivee);
        vol.setHeureDepart(dto.getHeureDepart());
        vol.setHeureArrivee(dto.getHeureArrivee());
        vol.setDureeVol(dto.getDureeVol());
        vol.setNombreSieges(dto.getNombreSieges());
        vol.setSiegesDisponibles(dto.getNombreSieges());
        vol.setPrixBase(dto.getPrixBase());
        vol.setDistance(dto.getDistance());
        vol.setLundi(dto.getLundi());
        vol.setMardi(dto.getMardi());
        vol.setMercredi(dto.getMercredi());
        vol.setJeudi(dto.getJeudi());
        vol.setVendredi(dto.getVendredi());
        vol.setSamedi(dto.getSamedi());
        vol.setDimanche(dto.getDimanche());
        vol.setActif(true);

        vol = volRepository.save(vol);
        log.info("✅ Vol {} créé avec succès", vol.getNumeroVol());
        return volMapper.toDto(vol);
    }

    @Transactional(readOnly = true)
    public List<VolDto> rechercherVols(SearchVolDto searchDto) {
        log.info("🔍 Recherche de vols: {} → {}", searchDto.getVilleDepartId(), searchDto.getVilleArriveeId());

        List<Vol> vols = volRepository.findVolsDisponiblesAvecSieges(
                searchDto.getVilleDepartId(),
                searchDto.getVilleArriveeId(),
                searchDto.getNombrePassagers()
        );

        List<VolDto> result = vols.stream()
                .filter(vol -> isVolDisponiblePourDate(vol, searchDto.getDateDepart()))
                .map(volMapper::toDto)
                .collect(Collectors.toList());

        log.info("✅ {} vol(s) trouvé(s)", result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public VolDto getVolById(Long id) {
        log.info("🔍 Récupération du vol ID: {}", id);
        Vol vol = volRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé"));
        return volMapper.toDto(vol);
    }

    @Transactional(readOnly = true)
    public List<VolDto> getAllVols() {
        log.info("📋 Récupération de tous les vols");
        return volRepository.findAll().stream()
                .map(volMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteVol(Long id) {
        log.info("🗑️ Tentative de suppression du vol ID: {}", id);

        // Vérifier que le vol existe
        Vol vol = volRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Vol non trouvé: {}", id);
                    return new RuntimeException("Vol non trouvé avec l'ID: " + id);
                });

        log.debug("✅ Vol trouvé: {}", vol.getNumeroVol());

        // Vérifier s'il y a des réservations pour ce vol
        long nombreReservationsAller = reservationRepository.countByVolAllerId(id);
        long nombreReservationsRetour = reservationRepository.countByVolRetourId(id);

        long totalReservations = nombreReservationsAller + nombreReservationsRetour;

        if (totalReservations > 0) {
            log.warn("⚠️ Impossible de supprimer le vol {} : {} réservation(s) associée(s)",
                    vol.getNumeroVol(), totalReservations);
            throw new RuntimeException(
                    String.format("Impossible de supprimer le vol %s car il y a %d réservation(s) associée(s). " +
                                    "Veuillez d'abord annuler ou supprimer les réservations.",
                            vol.getNumeroVol(), totalReservations)
            );
        }

        log.debug("✅ Aucune réservation trouvée pour le vol {}", vol.getNumeroVol());

        // Désactivation du vol (soft delete)
        vol.setActif(false);
        volRepository.save(vol);

        log.info("✅ Vol {} désactivé avec succès", vol.getNumeroVol());
    }

    private boolean isVolDisponiblePourDate(Vol vol, LocalDate date) {
        return reservationHelper.isVolDisponiblePourDate(
                date.getDayOfWeek(),
                vol.getLundi(), vol.getMardi(), vol.getMercredi(),
                vol.getJeudi(), vol.getVendredi(), vol.getSamedi(), vol.getDimanche()
        );
    }

    @Transactional(readOnly = true)
    public long countVols() {
        return volRepository.count();
    }
}
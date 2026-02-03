package com.goldenfly.design.services;

import com.goldenfly.design.repositories.VolRepository;
import com.goldenfly.design.repositories.VilleRepository;
import com.goldenfly.domain.entities.Vol;
import com.goldenfly.domain.entities.Ville;
import com.goldenfly.helpers.ReservationHelper;
import com.goldenfly.web.dtos.CreateVolDto;
import com.goldenfly.web.dtos.SearchVolDto;
import com.goldenfly.web.dtos.VolDto;
import com.goldenfly.web.mappers.VolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VolService {

    private final VolRepository volRepository;
    private final VilleRepository villeRepository;
    private final VolMapper volMapper;
    private final ReservationHelper reservationHelper;

    public VolDto creerVol(CreateVolDto dto) {
        if (volRepository.existsByNumeroVol(dto.getNumeroVol())) {
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
        return volMapper.toDto(vol);
    }

    @Transactional(readOnly = true)
    public List<VolDto> rechercherVols(SearchVolDto searchDto) {
        List<Vol> vols = volRepository.findVolsDisponiblesAvecSieges(
                searchDto.getVilleDepartId(),
                searchDto.getVilleArriveeId(),
                searchDto.getNombrePassagers()
        );

        return vols.stream()
                .filter(vol -> isVolDisponiblePourDate(vol, searchDto.getDateDepart()))
                .map(volMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VolDto getVolById(Long id) {
        Vol vol = volRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé"));
        return volMapper.toDto(vol);
    }

    @Transactional(readOnly = true)
    public List<VolDto> getAllVols() {
        return volRepository.findAll().stream()
                .map(volMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteVol(Long id) {
        Vol vol = volRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé"));
        vol.setActif(false);
        volRepository.save(vol);
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
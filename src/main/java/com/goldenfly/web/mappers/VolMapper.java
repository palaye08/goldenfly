package com.goldenfly.web.mappers;

import com.goldenfly.domain.entities.Vol;
import com.goldenfly.web.dtos.CreateVolDto;
import com.goldenfly.web.dtos.VolDto;
import org.springframework.stereotype.Component;

@Component
public class VolMapper {

    private final VilleMapper villeMapper;

    public VolMapper(VilleMapper villeMapper) {
        this.villeMapper = villeMapper;
    }

    public VolDto toDto(Vol entity) {
        if (entity == null) return null;

        VolDto dto = new VolDto();
        dto.setId(entity.getId());
        dto.setNumeroVol(entity.getNumeroVol());
        dto.setNom(entity.getNom());
        dto.setVilleDepart(villeMapper.toDto(entity.getVilleDepart()));
        dto.setVilleArrivee(villeMapper.toDto(entity.getVilleArrivee()));
        dto.setHeureDepart(entity.getHeureDepart());
        dto.setHeureArrivee(entity.getHeureArrivee());
        dto.setDureeVol(entity.getDureeVol());
        dto.setNombreSieges(entity.getNombreSieges());
        dto.setSiegesDisponibles(entity.getSiegesDisponibles());
        dto.setPrixBase(entity.getPrixBase());
        dto.setDistance(entity.getDistance());
        dto.setLundi(entity.getLundi());
        dto.setMardi(entity.getMardi());
        dto.setMercredi(entity.getMercredi());
        dto.setJeudi(entity.getJeudi());
        dto.setVendredi(entity.getVendredi());
        dto.setSamedi(entity.getSamedi());
        dto.setDimanche(entity.getDimanche());
        dto.setActif(entity.getActif());
        dto.setDateCreation(entity.getDateCreation());
        return dto;
    }
}
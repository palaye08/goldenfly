package com.goldenfly.web.mappers;

import com.goldenfly.domain.entities.Ville;
import com.goldenfly.web.dtos.CreateVilleDto;
import com.goldenfly.web.dtos.VilleDto;
import org.springframework.stereotype.Component;

@Component
public class VilleMapper {

    public VilleDto toDto(Ville entity) {
        if (entity == null) return null;

        VilleDto dto = new VilleDto();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setCode(entity.getCode());
        dto.setNomAeroport(entity.getNomAeroport());
        dto.setPays(entity.getPays());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setActif(entity.getActif());
        dto.setDateCreation(entity.getDateCreation());
        return dto;
    }

    public Ville toEntity(CreateVilleDto dto) {
        if (dto == null) return null;

        Ville entity = new Ville();
        entity.setNom(dto.getNom());
        entity.setCode(dto.getCode().toUpperCase());
        entity.setNomAeroport(dto.getNomAeroport());
        entity.setPays(dto.getPays());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setActif(true);
        return entity;
    }

    public void updateEntity(Ville entity, CreateVilleDto dto) {
        if (entity == null || dto == null) return;

        entity.setNom(dto.getNom());
        entity.setCode(dto.getCode().toUpperCase());
        entity.setNomAeroport(dto.getNomAeroport());
        entity.setPays(dto.getPays());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
    }
}
package com.goldenfly.web.mappers;

import com.goldenfly.domain.entities.Utilisateur;
import com.goldenfly.web.dtos.CreateUtilisateurDto;
import com.goldenfly.web.dtos.UtilisateurDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapper {

    private final PasswordEncoder passwordEncoder;

    public UtilisateurMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UtilisateurDto toDto(Utilisateur entity) {
        if (entity == null) return null;

        UtilisateurDto dto = new UtilisateurDto();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setPrenom(entity.getPrenom());
        dto.setEmail(entity.getEmail());
        dto.setTelephone(entity.getTelephone());
        dto.setAge(entity.getAge());
        dto.setProfile(entity.getProfile());
        dto.setActif(entity.getActif());
        dto.setDateCreation(entity.getDateCreation());
        return dto;
    }

    public Utilisateur toEntity(CreateUtilisateurDto dto) {
        if (dto == null) return null;

        Utilisateur entity = new Utilisateur();
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setEmail(dto.getEmail());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setTelephone(dto.getTelephone());
        entity.setAge(dto.getAge());
        entity.setProfile(dto.getProfile());
        entity.setActif(true);
        return entity;
    }

    public void updateEntity(Utilisateur entity, CreateUtilisateurDto dto) {
        if (entity == null || dto == null) return;

        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        entity.setTelephone(dto.getTelephone());
        entity.setAge(dto.getAge());
        entity.setProfile(dto.getProfile());
    }
}
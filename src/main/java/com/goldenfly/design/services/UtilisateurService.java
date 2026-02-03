package com.goldenfly.design.services;

import com.goldenfly.design.repositories.UtilisateurRepository;
import com.goldenfly.domain.entities.Utilisateur;
import com.goldenfly.domain.enums.ProfileEnum;
import com.goldenfly.web.dtos.CreateUtilisateurDto;
import com.goldenfly.web.dtos.UtilisateurDto;
import com.goldenfly.web.mappers.UtilisateurMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;

    public UtilisateurDto creerUtilisateur(CreateUtilisateurDto dto) {
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        Utilisateur utilisateur = utilisateurMapper.toEntity(dto);
        utilisateur = utilisateurRepository.save(utilisateur);
        return utilisateurMapper.toDto(utilisateur);
    }

    @Transactional(readOnly = true)
    public UtilisateurDto getUtilisateurById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return utilisateurMapper.toDto(utilisateur);
    }

    @Transactional(readOnly = true)
    public UtilisateurDto getUtilisateurByEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return utilisateurMapper.toDto(utilisateur);
    }

    @Transactional(readOnly = true)
    public List<UtilisateurDto> getAllUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(utilisateurMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UtilisateurDto> getUtilisateursByProfile(ProfileEnum profile) {
        return utilisateurRepository.findByProfile(profile).stream()
                .map(utilisateurMapper::toDto)
                .collect(Collectors.toList());
    }

    public UtilisateurDto updateUtilisateur(Long id, CreateUtilisateurDto dto) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!utilisateur.getEmail().equals(dto.getEmail()) &&
                utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        utilisateurMapper.updateEntity(utilisateur, dto);
        utilisateur = utilisateurRepository.save(utilisateur);
        return utilisateurMapper.toDto(utilisateur);
    }

    public void deleteUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
    }

    @Transactional(readOnly = true)
    public long countUtilisateurs() {
        return utilisateurRepository.count();
    }
}
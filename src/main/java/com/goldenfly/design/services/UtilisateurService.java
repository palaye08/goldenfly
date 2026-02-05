package com.goldenfly.design.services;

import com.goldenfly.design.repositories.UtilisateurRepository;
import com.goldenfly.domain.entities.Utilisateur;
import com.goldenfly.domain.enums.ProfileEnum;
import com.goldenfly.web.dtos.CreateUtilisateurDto;
import com.goldenfly.web.dtos.UpdateUtilisateurDto;
import com.goldenfly.web.dtos.UtilisateurDto;
import com.goldenfly.web.mappers.UtilisateurMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Créer un utilisateur
     */
    public UtilisateurDto creerUtilisateur(CreateUtilisateurDto dto) {
        log.debug("➕ Création d'un utilisateur: {}", dto.getEmail());

        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        Utilisateur utilisateur = utilisateurMapper.toEntity(dto);

        // Encoder le mot de passe
        utilisateur.setPassword(passwordEncoder.encode(dto.getPassword()));

        utilisateur = utilisateurRepository.save(utilisateur);

        log.info("✅ Utilisateur créé: {} - Profile: {}", utilisateur.getEmail(), utilisateur.getProfile());

        return utilisateurMapper.toDto(utilisateur);
    }

    /**
     * Récupérer un utilisateur par son ID
     */
    @Transactional(readOnly = true)
    public UtilisateurDto getUtilisateurById(Long id) {
        log.debug("🔍 Recherche utilisateur par ID: {}", id);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return utilisateurMapper.toDto(utilisateur);
    }

    /**
     * Récupérer un utilisateur par son email
     */
    @Transactional(readOnly = true)
    public UtilisateurDto getUtilisateurByEmail(String email) {
        log.debug("🔍 Recherche utilisateur par email: {}", email);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return utilisateurMapper.toDto(utilisateur);
    }

    /**
     * Récupérer tous les utilisateurs
     */
    @Transactional(readOnly = true)
    public List<UtilisateurDto> getAllUtilisateurs() {
        log.debug("📋 Récupération de tous les utilisateurs");

        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();

        log.debug("📊 {} utilisateur(s) trouvé(s)", utilisateurs.size());

        return utilisateurs.stream()
                .map(utilisateurMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les utilisateurs par profil
     */
    @Transactional(readOnly = true)
    public List<UtilisateurDto> getUtilisateursByProfile(ProfileEnum profile) {
        log.debug("🔍 Recherche utilisateurs avec profil: {}", profile);

        return utilisateurRepository.findByProfile(profile).stream()
                .map(utilisateurMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour le profil d'un utilisateur (par lui-même)
     */
    public UtilisateurDto updateProfilUtilisateur(String email, UpdateUtilisateurDto dto) {
        log.debug("✏️ Mise à jour du profil pour: {}", email);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Mettre à jour les champs si fournis
        if (dto.getNom() != null && !dto.getNom().isBlank()) {
            utilisateur.setNom(dto.getNom());
        }

        if (dto.getPrenom() != null && !dto.getPrenom().isBlank()) {
            utilisateur.setPrenom(dto.getPrenom());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(email)) {
            // Vérifier que le nouvel email n'est pas déjà utilisé
            if (utilisateurRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Cet email est déjà utilisé");
            }
            utilisateur.setEmail(dto.getEmail());
        }

        if (dto.getTelephone() != null && !dto.getTelephone().isBlank()) {
            utilisateur.setTelephone(dto.getTelephone());
        }

        if (dto.getAge() != null) {
            utilisateur.setAge(dto.getAge());
        }

        // Mise à jour du mot de passe si fourni
        if (dto.getNouveauMotDePasse() != null && !dto.getNouveauMotDePasse().isBlank()) {
            if (!dto.getNouveauMotDePasse().equals(dto.getConfirmationMotDePasse())) {
                throw new RuntimeException("Les mots de passe ne correspondent pas");
            }
            utilisateur.setPassword(passwordEncoder.encode(dto.getNouveauMotDePasse()));
            log.info("🔒 Mot de passe mis à jour pour: {}", email);
        }

        utilisateur = utilisateurRepository.save(utilisateur);

        log.info("✅ Profil mis à jour pour: {}", email);

        return utilisateurMapper.toDto(utilisateur);
    }

    /**
     * Mettre à jour un utilisateur (par admin)
     */
    public UtilisateurDto updateUtilisateur(Long id, CreateUtilisateurDto dto) {
        log.debug("✏️ Modification de l'utilisateur {}", id);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!utilisateur.getEmail().equals(dto.getEmail()) &&
                utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        utilisateurMapper.updateEntity(utilisateur, dto);

        // Encoder le mot de passe s'il a changé
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            utilisateur.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        utilisateur = utilisateurRepository.save(utilisateur);

        log.info("✅ Utilisateur {} modifié", id);

        return utilisateurMapper.toDto(utilisateur);
    }

    /**
     * Supprimer un utilisateur (soft delete)
     */
    public void deleteUtilisateur(Long id) {
        log.debug("🗑️ Suppression de l'utilisateur {}", id);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);

        log.info("✅ Utilisateur {} désactivé", id);
    }

    /**
     * Compter les utilisateurs
     */
    @Transactional(readOnly = true)
    public long countUtilisateurs() {
        return utilisateurRepository.count();
    }
}
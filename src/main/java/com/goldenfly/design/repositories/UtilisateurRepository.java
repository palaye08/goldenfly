package com.goldenfly.design.repositories;

import com.goldenfly.domain.entities.Utilisateur;
import com.goldenfly.domain.enums.ProfileEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Utilisateur> findByProfile(ProfileEnum profile);
    List<Utilisateur> findByActif(Boolean actif);
    long countByProfile(ProfileEnum profile);
}
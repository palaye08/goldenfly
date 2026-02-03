package com.goldenfly.design.repositories;

import com.goldenfly.domain.entities.Ville;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VilleRepository extends JpaRepository<Ville, Long> {
    Optional<Ville> findByCode(String code);
    boolean existsByCode(String code);
    List<Ville> findByActif(Boolean actif);
    List<Ville> findByPays(String pays);
    List<Ville> findByNomContainingIgnoreCase(String nom);
}
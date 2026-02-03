package com.goldenfly.design.repositories;

import com.goldenfly.domain.entities.Vol;
import com.goldenfly.domain.entities.Ville;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VolRepository extends JpaRepository<Vol, Long> {
    Optional<Vol> findByNumeroVol(String numeroVol);
    boolean existsByNumeroVol(String numeroVol);
    List<Vol> findByActif(Boolean actif);
    List<Vol> findByVilleDepartAndVilleArrivee(Ville villeDepart, Ville villeArrivee);

    @Query("SELECT v FROM Vol v WHERE v.villeDepart.id = :villeDepartId " +
            "AND v.villeArrivee.id = :villeArriveeId AND v.actif = true")
    List<Vol> findVolsDisponibles(@Param("villeDepartId") Long villeDepartId,
                                  @Param("villeArriveeId") Long villeArriveeId);

    @Query("SELECT v FROM Vol v WHERE v.villeDepart.id = :villeDepartId " +
            "AND v.villeArrivee.id = :villeArriveeId AND v.actif = true " +
            "AND v.siegesDisponibles >= :nombrePassagers")
    List<Vol> findVolsDisponiblesAvecSieges(@Param("villeDepartId") Long villeDepartId,
                                            @Param("villeArriveeId") Long villeArriveeId,
                                            @Param("nombrePassagers") Integer nombrePassagers);
}
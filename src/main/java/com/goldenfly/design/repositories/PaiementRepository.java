package com.goldenfly.design.repositories;

import com.goldenfly.domain.entities.Paiement;
import com.goldenfly.domain.entities.Reservation;
import com.goldenfly.domain.enums.StatutPaiementEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByNumeroPaiement(String numeroPaiement);
    Optional<Paiement> findByReservation(Reservation reservation);
    Optional<Paiement> findByTransactionId(String transactionId);
    List<Paiement> findByStatut(StatutPaiementEnum statut);

    @Query("SELECT SUM(p.montant) FROM Paiement p WHERE p.statut = 'PAYE'")
    Double calculateTotalPaiements();

    // NOUVEAU: Requête pour récupérer les paiements d'un utilisateur
    @Query("SELECT p FROM Paiement p " +
            "WHERE p.reservation.utilisateur.id = :utilisateurId " +
            "ORDER BY p.dateCreation DESC")
    List<Paiement> findByUtilisateurId(@Param("utilisateurId") Long utilisateurId);
}
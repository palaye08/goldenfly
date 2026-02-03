package com.goldenfly.design.repositories;

import com.goldenfly.domain.entities.Reservation;
import com.goldenfly.domain.entities.Utilisateur;
import com.goldenfly.domain.entities.Vol;
import com.goldenfly.domain.enums.StatutReservationEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByNumeroReservation(String numeroReservation);
    List<Reservation> findByUtilisateur(Utilisateur utilisateur);
    List<Reservation> findByStatut(StatutReservationEnum statut);
    List<Reservation> findByVolAller(Vol vol);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.volAller.id = :volId " +
            "AND r.dateDepart = :date AND r.statut IN ('CONFIRMEE', 'EN_ATTENTE', 'EMBARQUEE')")
    Integer countReservationsForVolOnDate(@Param("volId") Long volId, @Param("date") LocalDate date);

    @Query("SELECT SUM(r.nombrePassagers) FROM Reservation r WHERE r.volAller.id = :volId " +
            "AND r.dateDepart = :date AND r.statut IN ('CONFIRMEE', 'EN_ATTENTE', 'EMBARQUEE')")
    Integer countPassengersForVolOnDate(@Param("volId") Long volId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.statut = :statut")
    long countByStatut(@Param("statut") StatutReservationEnum statut);

    @Query("SELECT SUM(r.prixTotal) FROM Reservation r WHERE r.statut = 'CONFIRMEE'")
    Double calculateTotalRevenue();

    @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :utilisateurId " +
            "ORDER BY r.dateCreation DESC")
    List<Reservation> findByUtilisateurIdOrderByDateCreationDesc(@Param("utilisateurId") Long utilisateurId);
}
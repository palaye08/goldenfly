package com.goldenfly.domain.entities;

import com.goldenfly.domain.enums.ClasseVolEnum;
import com.goldenfly.domain.enums.StatutReservationEnum;
import com.goldenfly.domain.enums.TypeReservationEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroReservation;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "vol_aller_id", nullable = false)
    private Vol volAller;

    @ManyToOne
    @JoinColumn(name = "vol_retour_id")
    private Vol volRetour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeReservationEnum typeReservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClasseVolEnum classeVol;

    @Column(nullable = false)
    private LocalDate dateDepart;

    private LocalDate dateRetour;

    @Column(nullable = false)
    private Double prixTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservationEnum statut = StatutReservationEnum.EN_ATTENTE;

    @Column(columnDefinition = "TEXT")
    private String qrCode;

    private LocalDateTime dateEmbarquement;

    @Column(nullable = false)
    private Integer nombrePassagers = 1;

    // NOUVEAU: Relation avec Paiement
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Paiement paiement;

    // NOUVEAU: Champ pour indiquer si la réservation est payée
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean estPaye = false;

    // NOUVEAU: Date limite de paiement (24h avant le départ)
    private LocalDateTime dateLimitePaiement;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;
}
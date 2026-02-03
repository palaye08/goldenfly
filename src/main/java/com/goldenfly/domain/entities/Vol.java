package com.goldenfly.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "vols")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroVol; // Ex: GF001

    @Column(nullable = false)
    private String nom;

    @ManyToOne
    @JoinColumn(name = "ville_depart_id", nullable = false)
    private Ville villeDepart;

    @ManyToOne
    @JoinColumn(name = "ville_arrivee_id", nullable = false)
    private Ville villeArrivee;

    @Column(nullable = false)
    private LocalTime heureDepart;

    @Column(nullable = false)
    private LocalTime heureArrivee;

    @Column(nullable = false)
    private Integer dureeVol; // en minutes

    @Column(nullable = false)
    private Integer nombreSieges;

    @Column(nullable = false)
    private Integer siegesDisponibles;

    @Column(nullable = false)
    private Double prixBase; // Prix de base économique

    @Column(nullable = false)
    private Double distance; // en km

    // Jours de la semaine où le vol est disponible
    private Boolean lundi = false;
    private Boolean mardi = false;
    private Boolean mercredi = false;
    private Boolean jeudi = false;
    private Boolean vendredi = false;
    private Boolean samedi = false;
    private Boolean dimanche = false;

    @Column(nullable = false)
    private Boolean actif = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "volAller", cascade = CascadeType.ALL)
    private List<Reservation> reservationsAller;

    @OneToMany(mappedBy = "volRetour", cascade = CascadeType.ALL)
    private List<Reservation> reservationsRetour;
}
package com.goldenfly.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "villes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ville {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true, length = 3)
    private String code; // Ex: DSS pour Dakar

    @Column(nullable = false)
    private String nomAeroport;

    @Column(nullable = false)
    private String pays;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private Boolean actif = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;
}
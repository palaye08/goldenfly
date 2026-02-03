package com.goldenfly.domain.entities;

import com.goldenfly.domain.enums.ModePaiementEnum;
import com.goldenfly.domain.enums.StatutPaiementEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "paiements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false, unique = true)
    private String numeroPaiement; // Ex: PAY-20241215-ABC123

    @Column(nullable = false)
    private Double montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModePaiementEnum modePaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiementEnum statut = StatutPaiementEnum.EN_ATTENTE;

    // Pour Wave et Orange Money
    private String transactionId; // ID de la transaction du provider
    private String numeroTelephone; // Numéro pour Wave/Orange Money

    // Pour paiement manuel (admin)
    private String numeroRecu; // Numéro de reçu si paiement espèces

    @Column(columnDefinition = "TEXT")
    private String referenceExterne; // Référence externe (Wave checkout URL, etc.)

    private LocalDateTime datePaiement;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;

    @Column(columnDefinition = "TEXT")
    private String commentaire;
}
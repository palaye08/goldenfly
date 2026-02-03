package com.goldenfly.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;

    @Column(nullable = false)
    private LocalDateTime dateBlacklist;

    @Column(nullable = false)
    private LocalDateTime dateExpiration;
}
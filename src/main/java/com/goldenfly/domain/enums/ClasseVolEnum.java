package com.goldenfly.domain.enums;

public enum ClasseVolEnum {
    ECONOMIQUE(1.0),
    PREMIUM(1.5),
    AFFAIRES(2.5);

    private final double multiplicateur;

    ClasseVolEnum(double multiplicateur) {
        this.multiplicateur = multiplicateur;
    }

    public double getMultiplicateur() {
        return multiplicateur;
    }
}
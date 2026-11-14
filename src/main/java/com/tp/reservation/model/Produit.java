package com.tp.reservation.model;

import java.math.BigDecimal;

public class Produit {
    private final String id;
    private final String libelle;
    private final BigDecimal tarifJournalier;
    private final BigDecimal caution;

    public Produit(String id, String libelle, BigDecimal tarifJournalier, BigDecimal caution) {
        // RG01: ID is mandatory
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'identifiant du produit ne peut pas être vide");
        }
        // RG09: Rate must be strictly positive
        if (tarifJournalier == null || tarifJournalier.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le tarif journalier doit être strictement positif");
        }
        // RG09: Deposit must be positive or zero
        if (caution == null || caution.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La caution doit être positive ou nulle");
        }

        this.id = id;
        this.libelle = libelle;
        this.tarifJournalier = tarifJournalier;
        this.caution = caution;
    }

    public String getId() { return id; }
    public String getLibelle() { return libelle; }
    public BigDecimal getTarifJournalier() { return tarifJournalier; }
    public BigDecimal getCaution() { return caution; }
}
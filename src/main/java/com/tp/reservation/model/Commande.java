package com.tp.reservation.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Commande {
    private final String reference;
    private final String idEtudiant;
    private final String idProduit;
    private final LocalDate dateDebut;
    private final LocalDate dateFin;
    private final int duree;
    private final BigDecimal montantLocation;
    private final BigDecimal caution;
    private final BigDecimal montantTotal;

    public Commande(String reference, String idEtudiant, String idProduit,
                    LocalDate dateDebut, LocalDate dateFin, int duree,
                    BigDecimal montantLocation, BigDecimal caution, BigDecimal montantTotal) {
        this.reference = reference;
        this.idEtudiant = idEtudiant;
        this.idProduit = idProduit;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.duree = duree;
        this.montantLocation = montantLocation;
        this.caution = caution;
        this.montantTotal = montantTotal;
    }

    public String getReference() { return reference; }
    public String getIdEtudiant() { return idEtudiant; }
    public String getIdProduit() { return idProduit; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public int getDuree() { return duree; }
    public BigDecimal getMontantLocation() { return montantLocation; }
    public BigDecimal getCaution() { return caution; }
    public BigDecimal getMontantTotal() { return montantTotal; }
}
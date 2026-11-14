package com.tp.reservation.service;

import com.tp.reservation.exception.ReservationException;
import com.tp.reservation.model.Commande;
import com.tp.reservation.model.Etudiant;
import com.tp.reservation.model.Produit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation {
    private final List<Etudiant> etudiants = new ArrayList<>();
    private final List<Produit> produits = new ArrayList<>();
    private final List<Commande> commandes = new ArrayList<>();
    private int compteurReference = 1;

    public void ajouterEtudiant(Etudiant e) {
        etudiants.add(e);
    }

    public void ajouterProduit(Produit p) {
        produits.add(p);
    }

    public Commande reserver(String idEtudiant, String idProduit, LocalDate dateDebut, LocalDate dateFin) {
        // RG01: Mandatory fields
        if (idEtudiant == null || idEtudiant.isBlank()) {
            throw new ReservationException("L'identifiant de l'étudiant est obligatoire");
        }
        if (idProduit == null || idProduit.isBlank()) {
            throw new ReservationException("L'identifiant du produit est obligatoire");
        }
        if (dateDebut == null || dateFin == null) {
            throw new ReservationException("Les dates de début et de fin sont obligatoires");
        }

        // RG02: Start date must be today or later
        if (dateDebut.isBefore(LocalDate.now())) {
            throw new ReservationException("La date de début ne peut pas être dans le passé");
        }

        // RG03: End date must be >= start date
        if (dateFin.isBefore(dateDebut)) {
            throw new ReservationException("La date de fin doit être égale ou postérieure à la date de début");
        }

        // RG05
        boolean etudiantExiste = etudiants.stream()
                .anyMatch(e -> e.getId().equals(idEtudiant));
        if (!etudiantExiste) {
            throw new ReservationException("L'étudiant avec l'identifiant " + idEtudiant + " n'existe pas");
        }

        // RG05
        boolean produitExiste = produits.stream()
                .anyMatch(p -> p.getId().equals(idProduit));
        if (!produitExiste) {
            throw new ReservationException("Le produit avec l'identifiant " + idProduit + " n'existe pas");
        }

        // RG04
        long daysBetween = ChronoUnit.DAYS.between(dateDebut, dateFin);
        int duree = (int) daysBetween + 1;

        // RG06
        if (duree > 7) {
            throw new ReservationException("La durée maximale d'une réservation est de 7 jours");
        }

        // RG07
        long activeOrders = commandes.stream()
                .filter(c -> c.getIdEtudiant().equals(idEtudiant))
                .filter(c -> !c.getDateFin().isBefore(LocalDate.now()))
                .count();
        if (activeOrders >= 2) {
            throw new ReservationException("L'étudiant a déjà atteint le quota maximal de 2 commandes");
        }

        // RG08
        boolean disponible = commandes.stream()
                .filter(c -> c.getIdProduit().equals(idProduit))
                .noneMatch(c -> !dateDebut.isAfter(c.getDateFin()) && !dateFin.isBefore(c.getDateDebut()));
        if (!disponible) {
            throw new ReservationException("Le produit n'est pas disponible sur cette période");
        }

        // Retrieve product data
        Produit produit = produits.stream()
                .filter(p -> p.getId().equals(idProduit))
                .findFirst()
                .orElseThrow();

        // RG10 & RG12
        BigDecimal montantLocation = produit.getTarifJournalier()
                .multiply(BigDecimal.valueOf(duree))
                .setScale(2, RoundingMode.HALF_UP);

        // RG11
        BigDecimal caution = produit.getCaution().setScale(2, RoundingMode.HALF_UP);
        BigDecimal montantTotal = montantLocation.add(caution);

        // RG13
        String reference = "CMD-" + String.format("%03d", compteurReference++);

        // Create and save order
        Commande commande = new Commande(
                reference, idEtudiant, idProduit,
                dateDebut, dateFin, duree,
                montantLocation, caution, montantTotal
        );
        commandes.add(commande);

        return commande;
    }
}
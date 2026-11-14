package com.tp.reservation.model;

public class Etudiant {
    private final String id;
    private final String nom;
    private final String prenom;

    public Etudiant(String id, String nom, String prenom) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'identifiant de l'étudiant ne peut pas être vide");
        }
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
}
// src/com/monapp/Membre.java
package com.monapp;

/**
 * Représente un membre de la bibliothèque.
 */
public class Membre {
    private int id;
    private String nom;
    private String prenom;
    private String identifiant;    // ex. login ou matricule
    private String coordonnees;    // ex. email ou téléphone
    private boolean actif;         // pour désactivation

    // Constructeur pour création (sans ID, actif=true par défaut)
    public Membre(String nom, String prenom, String identifiant, String coordonnees) {
        this.nom = nom;
        this.prenom = prenom;
        this.identifiant = identifiant;
        this.coordonnees = coordonnees;
        this.actif = true;
    }

    // Constructeur pour lecture / modification (avec tous les champs)
    public Membre(int id, String nom, String prenom,
                  String identifiant, String coordonnees, boolean actif) {
        this(nom, prenom, identifiant, coordonnees);
        this.id = id;
        this.actif = actif;
    }

    // Getters & setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getIdentifiant() { return identifiant; }
    public void setIdentifiant(String identifiant) { this.identifiant = identifiant; }
    public String getCoordonnees() { return coordonnees; }
    public void setCoordonnees(String coordonnees) { this.coordonnees = coordonnees; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    @Override
    public String toString() {
        return String.format(
            "%d – %s %s [%s, %s] %s",
            id, prenom, nom, identifiant, coordonnees,
            (actif ? "" : "(désactivé)")
        );
    }
}

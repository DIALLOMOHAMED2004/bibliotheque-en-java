package com.monapp;

/**
 * Représente un livre dans la bibliothèque.
 */
public class Livre {
    private int id;               
    private String titre;
    private String auteur;
    private String isbn;
    private int anneePublication;
    private String genre;

    // Constructeur pour création (sans ID)
    public Livre(String titre, String auteur, String isbn, int anneePublication, String genre) {
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
        this.anneePublication = anneePublication;
        this.genre = genre;
    }

    // Constructeur pour lecture (avec ID)
    public Livre(int id, String titre, String auteur, String isbn, int anneePublication, String genre) {
        this(titre, auteur, isbn, anneePublication, genre);
        this.id = id;
    }

    // Getters / Setters
    public int getId() { return id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getAnneePublication() { return anneePublication; }
    public void setAnneePublication(int anneePublication) { this.anneePublication = anneePublication; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    @Override
    public String toString() {
        return String.format("%d – %s (%s) by %s [%d, %s]",
                id, titre, isbn, auteur, anneePublication, genre);
    }
}

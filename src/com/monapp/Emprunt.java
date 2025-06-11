package com.monapp;

import java.time.LocalDate;

public class Emprunt {
    private int id;
    private int membreId;
    private int livreId;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourEffective;

    public Emprunt(int membreId, int livreId, LocalDate dateEmprunt) {
        this.membreId = membreId;
        this.livreId = livreId;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourEffective = null;
    }

    public Emprunt(int id, int membreId, int livreId,
                   LocalDate dateEmprunt, LocalDate dateRetourEffective) {
        this(membreId, livreId, dateEmprunt);
        this.id = id;
        this.dateRetourEffective = dateRetourEffective;
    }

    // Getters
    public int getId() { return id; }
    public int getMembreId() { return membreId; }
    public int getLivreId() { return livreId; }
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }

    // Nouveaux getters pour corriger App.java
    public int getMembre() { return membreId; }
    public int getLivre() { return livreId; }
    public LocalDate getDateRetourPrevue() {
        return dateEmprunt.plusWeeks(2); // ou toute autre logique
    }
    public boolean isRetourne() {
        return dateRetourEffective != null;
    }

    public void setDateRetourEffective(LocalDate dateRetourEffective) {
        this.dateRetourEffective = dateRetourEffective;
    }

    @Override
    public String toString() {
        String ret = (dateRetourEffective == null) ? "EN COURS" : dateRetourEffective.toString();
        return String.format("Emprunt{id=%d, m=%d, l=%d, le=%s, retour=%s}",
            id, membreId, livreId, dateEmprunt, ret);
    }
}

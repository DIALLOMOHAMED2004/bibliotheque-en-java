package com.monapp;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour CRUD des emprunts.
 */
public class EmpruntDAO {
    private final Connection conn;

    public EmpruntDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Vérifie si un livre est disponible (pas d’emprunt non retourné).
     */
    public boolean isLivreDisponible(int livreId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM emprunts " +
                     "WHERE livre_id = ? AND date_retour_effective IS NULL";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, livreId);
            try (ResultSet rs = stm.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 0;
            }
        }
    }

    /**
     * Enregistre un nouvel emprunt, en calculant et insérant la date de retour prévue.
     * Renvoie l'ID de l'emprunt créé.
     */
    public int enregistrerEmprunt(Emprunt e) throws SQLException {
        if (!isLivreDisponible(e.getLivreId())) {
            throw new SQLException("Livre déjà emprunté");
        }
        String sql = "INSERT INTO emprunts " +
                     "(membre_id, livre_id, date_emprunt, date_retour_prevue) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stm.setInt(1, e.getMembreId());
            stm.setInt(2, e.getLivreId());
            stm.setDate(3, Date.valueOf(e.getDateEmprunt()));
            // Calculer la date de retour prévue, par exemple 14 jours après la date d'emprunt
            LocalDate retourPrevue = e.getDateEmprunt().plusDays(14);
            stm.setDate(4, Date.valueOf(retourPrevue));
            stm.executeUpdate();
            try (ResultSet rs = stm.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                else throw new SQLException("Échec récupération ID emprunt");
            }
        }
    }

    /**
     * Enregistre la date de retour effective.
     */
    public boolean enregistrerRetour(int empruntId, LocalDate dateRetour) throws SQLException {
        String sql = "UPDATE emprunts SET date_retour_effective = ? WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setDate(1, Date.valueOf(dateRetour));
            stm.setInt(2, empruntId);
            return stm.executeUpdate() > 0;
        }
    }

    /**
     * Liste tous les emprunts (retournés et en cours).
     */
    public List<Emprunt> listerTous() throws SQLException {
        List<Emprunt> lst = new ArrayList<>();
        String sql = "SELECT * FROM emprunts";
        try (Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {
            while (rs.next()) {
                lst.add(new Emprunt(
                    rs.getInt("id"),
                    rs.getInt("membre_id"),
                    rs.getInt("livre_id"),
                    rs.getDate("date_emprunt").toLocalDate(),
                    rs.getDate("date_retour_effective") != null
                        ? rs.getDate("date_retour_effective").toLocalDate()
                        : null
                ));
            }
        }
        return lst;
    }

    /**
     * Liste uniquement les emprunts non retournés.
     */
    public List<Emprunt> listerEnCours() throws SQLException {
        List<Emprunt> lst = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE date_retour_effective IS NULL";
        try (Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {
            while (rs.next()) {
                lst.add(new Emprunt(
                    rs.getInt("id"),
                    rs.getInt("membre_id"),
                    rs.getInt("livre_id"),
                    rs.getDate("date_emprunt").toLocalDate(),
                    null
                ));
            }
        }
        return lst;
    }

    /**
     * Méthode "ajouter" exposée pour l'application.
     */
    public int ajouter(Emprunt e) throws SQLException {
        return enregistrerEmprunt(e);
    }

    /**
     * Méthode "retourner" exposée pour l'application.
     */
    public boolean retourner(int empruntId) throws SQLException {
        return enregistrerRetour(empruntId, LocalDate.now());
    }

    /**
     * Supprime un emprunt.
     */
    public boolean supprimer(int id) throws SQLException {
        String sql = "DELETE FROM emprunts WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, id);
            return stm.executeUpdate() > 0;
        }
    }
}

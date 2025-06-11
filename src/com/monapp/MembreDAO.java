package com.monapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour CRUD des membres.
 */
public class MembreDAO {
    private final Connection conn;

    public MembreDAO(Connection conn) {
        this.conn = conn;
    }

    /** Ajoute un membre et renvoie son ID. */
    public int ajouter(Membre m) throws SQLException {
        String sql = "INSERT INTO membres (nom, prenom, identifiant, coordonnees, actif) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, m.getNom());
            stmt.setString(2, m.getPrenom());
            stmt.setString(3, m.getIdentifiant());
            stmt.setString(4, m.getCoordonnees());
            stmt.setBoolean(5, m.isActif());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                else throw new SQLException("Échec récupération ID membre");
            }
        }
    }

    /** Supprime physiquement un membre. */
    public boolean supprimer(int id) throws SQLException {
        String sql = "DELETE FROM membres WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /** Désactive un membre (actif = false). */
    public boolean desactiver(int id) throws SQLException {
        String sql = "UPDATE membres SET actif = false WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /** Modifie un membre existant. */
    public boolean modifier(Membre m) throws SQLException {
        String sql = "UPDATE membres SET nom = ?, prenom = ?, identifiant = ?, coordonnees = ?, actif = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getNom());
            stmt.setString(2, m.getPrenom());
            stmt.setString(3, m.getIdentifiant());
            stmt.setString(4, m.getCoordonnees());
            stmt.setBoolean(5, m.isActif());
            stmt.setInt(6, m.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /** Liste tous les membres (actifs et inactifs). */
    public List<Membre> listerTous() throws SQLException {
        List<Membre> liste = new ArrayList<>();
        String sql = "SELECT * FROM membres";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(new Membre(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("identifiant"),
                    rs.getString("coordonnees"),
                    rs.getBoolean("actif")
                ));
            }
        }
        return liste;
    }

    /** Recherche un membre par son ID. */
    public Membre findById(int id) throws SQLException {
        String sql = "SELECT * FROM membres WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Membre(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("identifiant"),
                        rs.getString("coordonnees"),
                        rs.getBoolean("actif")
                    );
                }
                return null;
            }
        }
    }
}
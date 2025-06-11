package com.monapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'accès aux données pour les livres.
 */
public class LivreDAO {
    private final Connection conn;

    public LivreDAO(Connection conn) {
        this.conn = conn;
    }

    /** Ajoute un livre et renvoie l'ID généré. */
    public int ajouter(Livre livre) throws SQLException {
        String sql = "INSERT INTO livres (titre, auteur, isbn, annee_publication, genre) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setString(3, livre.getIsbn());
            stmt.setInt(4, livre.getAnneePublication());
            stmt.setString(5, livre.getGenre());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                else throw new SQLException("ID non généré");
            }
        }
    }

    /** Supprime un livre par ID. */
    public boolean supprimer(int id) throws SQLException {
        String sql = "DELETE FROM livres WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /** Modifie un livre existant. */
    public boolean modifier(Livre livre) throws SQLException {
        String sql = "UPDATE livres SET titre=?, auteur=?, isbn=?, annee_publication=?, genre=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setString(3, livre.getIsbn());
            stmt.setInt(4, livre.getAnneePublication());
            stmt.setString(5, livre.getGenre());
            stmt.setInt(6, livre.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /** Retourne la liste de tous les livres. */
    public List<Livre> listerTous() throws SQLException {
        List<Livre> liste = new ArrayList<>();
        String sql = "SELECT * FROM livres";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(new Livre(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("auteur"),
                    rs.getString("isbn"),
                    rs.getInt("annee_publication"),
                    rs.getString("genre")
                ));
            }
        }
        return liste;
    }

    /** Recherche dans titre, auteur ou ISBN (LIKE %motCle%). */
    public List<Livre> rechercher(String motCle) throws SQLException {
        List<Livre> liste = new ArrayList<>();
        String sql = "SELECT * FROM livres WHERE titre LIKE ? OR auteur LIKE ? OR isbn LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String like = "%" + motCle + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Livre(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("isbn"),
                        rs.getInt("annee_publication"),
                        rs.getString("genre")
                    ));
                }
            }
        }
        return liste;
    }

    /** Filtre par genre exact. */
    public List<Livre> chercherParGenre(String genre) throws SQLException {
        List<Livre> liste = new ArrayList<>();
        String sql = "SELECT * FROM livres WHERE genre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, genre);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Livre(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("isbn"),
                        rs.getInt("annee_publication"),
                        rs.getString("genre")
                    ));
                }
            }
        }
        return liste;
    }

    /** Filtre par année de publication. */
    public List<Livre> chercherParAnnee(int annee) throws SQLException {
        List<Livre> liste = new ArrayList<>();
        String sql = "SELECT * FROM livres WHERE annee_publication = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, annee);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Livre(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("isbn"),
                        rs.getInt("annee_publication"),
                        rs.getString("genre")
                    ));
                }
            }
        }
        return liste;
    }

    /** Recherche un livre par son ID. */
    public Livre findById(int id) throws SQLException {
        String sql = "SELECT * FROM livres WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Livre(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("isbn"),
                        rs.getInt("annee_publication"),
                        rs.getString("genre")
                    );
                }
                return null;
            }
        }
    }
}
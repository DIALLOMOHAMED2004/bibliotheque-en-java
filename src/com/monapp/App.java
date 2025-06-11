package com.monapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class App extends JFrame {
    private final LivreDAO livreDao;
    private final MembreDAO membreDao;
    private final EmpruntDAO empruntDao;

    public App() {
        // 1) Connexion à la base
        Connection conn;
        try {
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bibliotheque?useSSL=false&serverTimezone=UTC",
                "root", ""
            );
            livreDao   = new LivreDAO(conn);
            membreDao  = new MembreDAO(conn);
            empruntDao = new EmpruntDAO(conn);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur connexion BDD : " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            throw new RuntimeException(ex);
        }

        // 2) Fenêtre principale
        setTitle("Gestion de bibliothèque");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        // 3) Onglets
        JTabbedPane onglets = new JTabbedPane();
        onglets.addTab("Livres",   createLivresPanel());
        onglets.addTab("Membres",  createMembresPanel());
        onglets.addTab("Emprunts", createEmpruntsPanel());
        add(onglets);

        setVisible(true);
    }

    // -------- Livres --------
    private JPanel createLivresPanel() {
        DefaultTableModel mod = new DefaultTableModel(
            new Object[]{"ID","Titre","Auteur","ISBN","Année","Genre"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(mod);

        JTextField tfRech = new JTextField(10);
        JTextField tfGenre = new JTextField(8);
        JTextField tfAn   = new JTextField(4);

        JButton bAdd   = new JButton("Ajouter"),
                bDel   = new JButton("Supprimer"),
                bMod   = new JButton("Modifier"),
                bAll   = new JButton("Tous"),
                bRech  = new JButton("Go"),
                bFG    = new JButton("Go"),
                bFA    = new JButton("Go");

        JPanel pnl1 = new JPanel();
        pnl1.add(bAdd); pnl1.add(bDel); pnl1.add(bMod); pnl1.add(bAll);
        JPanel pnl2 = new JPanel();
        pnl2.add(new JLabel("Recherche:")); pnl2.add(tfRech); pnl2.add(bRech);
        pnl2.add(new JLabel("Genre:"));     pnl2.add(tfGenre);pnl2.add(bFG);
        pnl2.add(new JLabel("Année:"));     pnl2.add(tfAn);   pnl2.add(bFA);

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(pnl1);
        north.add(pnl2);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(north, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshLivres(mod);

        bAll.addActionListener(e -> refreshLivres(mod));
        bAdd.addActionListener(e -> {
            LivreForm f = new LivreForm(this, "Ajouter un livre", null);
            Livre l = f.showDialog();
            if (l != null) {
                try { livreDao.ajouter(l); refreshLivres(mod); }
                catch (SQLException ex) { showError("Ajout livre", ex); }
            }
        });
        bDel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                int id = (int) mod.getValueAt(r,0);
                if (JOptionPane.showConfirmDialog(this,
                    "Supprimer livre ID " + id + " ?","Confirmer",
                    JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                    try { livreDao.supprimer(id); refreshLivres(mod); }
                    catch (SQLException ex) { showError("Suppression livre", ex); }
                }
            }
        });
        bMod.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                Livre orig = new Livre(
                    (int) mod.getValueAt(r,0),
                    (String) mod.getValueAt(r,1),
                    (String) mod.getValueAt(r,2),
                    (String) mod.getValueAt(r,3),
                    (int) mod.getValueAt(r,4),
                    (String) mod.getValueAt(r,5)
                );
                LivreForm f = new LivreForm(this,"Modifier livre",orig);
                Livre up = f.showDialog();
                if (up != null) {
                    try { livreDao.modifier(up); refreshLivres(mod); }
                    catch(SQLException ex){ showError("Modification livre", ex);}
                }
            }
        });
        bRech.addActionListener(e -> {
            try { afficherLivres(livreDao.rechercher(tfRech.getText().trim()), mod); }
            catch(SQLException ex){ showError("Recherche livre", ex);}
        });
        bFG.addActionListener(e -> {
            try { afficherLivres(livreDao.chercherParGenre(tfGenre.getText().trim()), mod); }
            catch(SQLException ex){ showError("Filtre genre", ex);}
        });
        bFA.addActionListener(e -> {
            try {
                int an = Integer.parseInt(tfAn.getText().trim());
                afficherLivres(livreDao.chercherParAnnee(an), mod);
            } catch(NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this,"Année invalide.");
            } catch(SQLException ex){ showError("Filtre année", ex);}
        });

        return panel;
    }

    private void refreshLivres(DefaultTableModel mod) {
        try {
            mod.setRowCount(0);
            for (Livre l : livreDao.listerTous()) {
                mod.addRow(new Object[]{
                    l.getId(), l.getTitre(), l.getAuteur(),
                    l.getIsbn(), l.getAnneePublication(), l.getGenre()
                });
            }
        } catch(SQLException ex){ showError("Lecture livres", ex); }
    }

    private void afficherLivres(List<Livre> lst, DefaultTableModel mod) {
        mod.setRowCount(0);
        for (Livre l : lst) {
            mod.addRow(new Object[]{
                l.getId(), l.getTitre(), l.getAuteur(),
                l.getIsbn(), l.getAnneePublication(), l.getGenre()
            });
        }
    }

    // -------- Membres --------
    private JPanel createMembresPanel() {
        DefaultTableModel mod = new DefaultTableModel(
            new Object[]{"ID","Nom","Prénom","Login","Coord.","Actif"}, 0
        ) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(mod);

        JButton bAdd = new JButton("Ajouter"),
                bDel = new JButton("Supprimer"),
                bDes = new JButton("Désactiver"),
                bMod = new JButton("Modifier"),
                bAll = new JButton("Tous");

        JPanel pnl = new JPanel();
        pnl.add(bAdd); pnl.add(bDel); pnl.add(bDes); pnl.add(bMod); pnl.add(bAll);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(pnl, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshMembres(mod);

        bAll.addActionListener(e -> refreshMembres(mod));
        bAdd.addActionListener(e -> {
            MembreForm f = new MembreForm(this,"Ajouter un membre",null);
            Membre m = f.showDialog();
            if(m!=null){
                try{ membreDao.ajouter(m); refreshMembres(mod); }
                catch(SQLException ex){ showError("Ajout membre", ex); }
            }
        });
        bDel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r>=0){
                int id = (int) mod.getValueAt(r,0);
                if(JOptionPane.showConfirmDialog(this,
                    "Supprimer membre ID "+id+"?","Confirmer",
                    JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                    try{ membreDao.supprimer(id); refreshMembres(mod); }
                    catch(SQLException ex){ showError("Suppression membre", ex); }
                }
            }
        });
        bDes.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r>=0){
                int id = (int)mod.getValueAt(r,0);
                try{ membreDao.desactiver(id); refreshMembres(mod); }
                catch(SQLException ex){ showError("Désactivation membre", ex); }
            }
        });
        bMod.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r>=0){
                Membre orig = new Membre(
                    (int)mod.getValueAt(r,0),
                    (String)mod.getValueAt(r,1),
                    (String)mod.getValueAt(r,2),
                    (String)mod.getValueAt(r,3),
                    (String)mod.getValueAt(r,4),
                    (Boolean)mod.getValueAt(r,5)
                );
                MembreForm f = new MembreForm(this,"Modifier membre",orig);
                Membre up = f.showDialog();
                if(up!=null){
                    try{ membreDao.modifier(up); refreshMembres(mod); }
                    catch(SQLException ex){ showError("Modification membre", ex); }
                }
            }
        });

        return panel;
    }

    private void refreshMembres(DefaultTableModel mod) {
        try {
            mod.setRowCount(0);
            for (Membre m : membreDao.listerTous()) {
                mod.addRow(new Object[]{
                    m.getId(), m.getNom(), m.getPrenom(),
                    m.getIdentifiant(), m.getCoordonnees(), m.isActif()
                });
            }
        } catch(SQLException ex){ showError("Lecture membres", ex); }
    }

    // -------- Emprunts --------
    private JPanel createEmpruntsPanel() {
        DefaultTableModel mod = new DefaultTableModel(
            new Object[]{"ID","Livre","Membre","Date Emp","Date Retour","Retourné"}, 0
        ) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(mod);

        JButton bNew    = new JButton("Emprunter"),
                bReturn = new JButton("Retourner"),
                bDel    = new JButton("Supprimer"),
                bAll    = new JButton("Tous");

        JPanel pnl = new JPanel();
        pnl.add(bNew); pnl.add(bReturn); pnl.add(bDel); pnl.add(bAll);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(pnl, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshEmprunts(mod);

        bAll.addActionListener(e -> refreshEmprunts(mod));
        bNew.addActionListener(e -> {
            EmpruntForm f = new EmpruntForm(this);
            Emprunt emp = f.showDialog();
            if(emp!=null){
                try{ empruntDao.ajouter(emp); refreshEmprunts(mod); }
                catch(SQLException ex){ showError("Ajout emprunt", ex); }
            }
        });
        bReturn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r>=0){
                int id = (int)mod.getValueAt(r,0);
                try{ empruntDao.retourner(id); refreshEmprunts(mod); }
                catch(SQLException ex){ showError("Retour emprunt", ex); }
            }
        });
        bDel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r>=0){
                int id = (int)mod.getValueAt(r,0);
                try{ empruntDao.supprimer(id); refreshEmprunts(mod); }
                catch(SQLException ex){ showError("Suppression emprunt", ex); }
            }
        });

        return panel;
    }

    private void refreshEmprunts(DefaultTableModel mod) {
        try {
            mod.setRowCount(0);
            for (Emprunt e : empruntDao.listerTous()) {
                Livre  l = livreDao.findById(e.getLivreId());
                Membre m = membreDao.findById(e.getMembreId());
                mod.addRow(new Object[]{
                    e.getId(),
                    l != null ? l.getTitre() : "?", 
                    m != null ? (m.getNom()+" "+m.getPrenom()) : "?",
                    e.getDateEmprunt(),
                    e.getDateRetourPrevue(),
                    e.isRetourne() ? "Oui" : "Non"
                });
            }
        } catch(SQLException ex){
            showError("Lecture emprunts", ex);
        }
    }

    private void showError(String titre, SQLException ex) {
        JOptionPane.showMessageDialog(this,
            titre + " : " + ex.getMessage(),
            "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
              UIManager.getSystemLookAndFeelClassName()
            );
        } catch(Exception ignored){}
        SwingUtilities.invokeLater(App::new);
    }
}

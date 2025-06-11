package com.monapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialog pour ajouter ou modifier un Livre.
 */
public class LivreForm extends JDialog {
    private JTextField tfTitre = new JTextField(20);
    private JTextField tfAuteur = new JTextField(20);
    private JTextField tfIsbn = new JTextField(20);
    private JTextField tfAnnee = new JTextField(5);
    private JTextField tfGenre = new JTextField(15);
    private Livre resultat = null;

    public LivreForm(JFrame parent, String titreFenetre, Livre initial) {
        super(parent, titreFenetre, true);
        setLayout(new GridLayout(6, 2, 5, 5));

        // Préremplir si modification
        if (initial != null) {
            tfTitre.setText(initial.getTitre());
            tfAuteur.setText(initial.getAuteur());
            tfIsbn.setText(initial.getIsbn());
            tfAnnee.setText(String.valueOf(initial.getAnneePublication()));
            tfGenre.setText(initial.getGenre());
        }

        add(new JLabel("Titre:"));     add(tfTitre);
        add(new JLabel("Auteur:"));    add(tfAuteur);
        add(new JLabel("ISBN:"));      add(tfIsbn);
        add(new JLabel("Année:"));     add(tfAnnee);
        add(new JLabel("Genre:"));     add(tfGenre);

        JButton ok = new JButton("OK");
        JButton annuler = new JButton("Annuler");
        add(ok); add(annuler);

        ok.addActionListener((ActionEvent e) -> {
            // Lecture et validation simple
            try {
                String titre = tfTitre.getText().trim();
                String auteur = tfAuteur.getText().trim();
                String isbn = tfIsbn.getText().trim();
                int annee = Integer.parseInt(tfAnnee.getText().trim());
                String genre = tfGenre.getText().trim();
                if (titre.isEmpty() || auteur.isEmpty()) {
                    throw new IllegalArgumentException("Titre et auteur non vides");
                }
                if (initial == null) {
                    resultat = new Livre(titre, auteur, isbn, annee, genre);
                } else {
                    resultat = new Livre(
                        initial.getId(), titre, auteur, isbn, annee, genre
                    );
                }
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur saisie: " + ex.getMessage());
            }
        });

        annuler.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(parent);
    }

    /** Affiche le dialog et retourne le Livre saisi ou null si annulation. */
    public Livre showDialog() {
        setVisible(true);
        return resultat;
    }
}

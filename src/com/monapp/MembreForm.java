// src/com/monapp/MembreForm.java
package com.monapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialog Swing pour ajouter/modifier un Membre.
 */
public class MembreForm extends JDialog {
    private final JTextField tfNom         = new JTextField(15);
    private final JTextField tfPrenom      = new JTextField(15);
    private final JTextField tfIdentifiant = new JTextField(15);
    private final JTextField tfCoord       = new JTextField(15);
    private final JCheckBox  cbActif       = new JCheckBox("Actif");

    private Membre resultat = null;

    public MembreForm(JFrame parent, String title, Membre initial) {
        super(parent, title, true);
        setLayout(new GridLayout(6, 2, 5, 5));

        // Préremplissage si édition
        if (initial != null) {
            tfNom.setText(initial.getNom());
            tfPrenom.setText(initial.getPrenom());
            tfIdentifiant.setText(initial.getIdentifiant());
            tfCoord.setText(initial.getCoordonnees());
            cbActif.setSelected(initial.isActif());
        } else {
            cbActif.setSelected(true);
        }

        add(new JLabel("Nom :"));           add(tfNom);
        add(new JLabel("Prénom :"));        add(tfPrenom);
        add(new JLabel("Identifiant :"));   add(tfIdentifiant);
        add(new JLabel("Coordonnées :"));   add(tfCoord);
        add(new JLabel("État :"));          add(cbActif);

        JButton ok = new JButton("OK");
        JButton ann = new JButton("Annuler");
        add(ok); add(ann);

        ok.addActionListener((ActionEvent e) -> {
            try {
                // Lecture & validation
                String nom  = tfNom.getText().trim();
                String pre  = tfPrenom.getText().trim();
                String iden = tfIdentifiant.getText().trim();
                String coo  = tfCoord.getText().trim();
                if (nom.isEmpty() || pre.isEmpty() || iden.isEmpty()) {
                    throw new IllegalArgumentException("Nom, prénom et identifiant requis");
                }
                if (initial == null) {
                    resultat = new Membre(nom, pre, iden, coo);
                } else {
                    resultat = new Membre(
                        initial.getId(), nom, pre, iden, coo, cbActif.isSelected()
                    );
                }
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur saisie : " + ex.getMessage());
            }
        });

        ann.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(parent);
    }

    /** Affiche le dialog et retourne le Membre saisi (ou null si annulation). */
    public Membre showDialog() {
        setVisible(true);
        return resultat;
    }
}

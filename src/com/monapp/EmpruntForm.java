package com.monapp;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class EmpruntForm extends JDialog {
    private final JTextField tfMembre = new JTextField(5);
    private final JTextField tfLivre  = new JTextField(5);
    private Emprunt resultat = null;

    public EmpruntForm(JFrame parent) {
        super(parent, "Nouvel emprunt", true);
        setLayout(new GridLayout(3,2,5,5));

        add(new JLabel("ID Membre :")); add(tfMembre);
        add(new JLabel("ID Livre :"));  add(tfLivre);

        JButton ok = new JButton("OK");
        JButton ann = new JButton("Annuler");
        add(ok); add(ann);

        ok.addActionListener(e -> {
            try {
                int mid = Integer.parseInt(tfMembre.getText().trim());
                int lid = Integer.parseInt(tfLivre.getText().trim());
                resultat = new Emprunt(mid, lid, LocalDate.now());
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID invalide");
            }
        });
        ann.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(parent);
    }

    public Emprunt showDialog() {
        setVisible(true);
        return resultat;
    }
}

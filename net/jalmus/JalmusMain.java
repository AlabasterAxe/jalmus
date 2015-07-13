package net.jalmus;

import java.awt.Dimension;

import javax.swing.JFrame;

public final class JalmusMain {

  public static void main(String[] args) {
    // Event pour la gestion des Evenements et principalement le message EXIT
    // Constructions de la frame

    Jalmus jalmus = new Jalmus();
    // Initialization
    if (args.length == 0) {
      jalmus.init("");
    } else {
      jalmus.init(args[0]);
    }

    // Force the window size
    Dimension dim = new Dimension(790, 590);
    jalmus.ui.setSize(800, 600);
    jalmus.ui.setMinimumSize(dim);

    // Draw
    jalmus.ui.repaint();

    jalmus.ui.setVisible(true);
    jalmus.ui.setFocusable(true);

    //jalmus.setResizable(false);

    jalmus.ui.setTitle("Jalmus"); // Give the application a title

    jalmus.ui.setLocationRelativeTo(null); // Center the window on the display

    jalmus.ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit when frame closed
  }
}

package net.jalmus;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public final class JalmusMain {

  public static void main(String[] args) {
    // Event pour la gestion des Evenements et principalement le message EXIT
    // Constructions de la frame

    Jalmus jalmus = new Jalmus();
    SwingNoteReadingGame game = new SwingNoteReadingGame();
    List<SwingGame> games = new ArrayList<>();
    games.add(game);
    JalmusUi ui = new JalmusUi(jalmus, games);
    game.setUi(ui);
    jalmus.setUi(ui);
    // Initialization
    if (args.length == 0) {
      jalmus.init("");
    } else {
      jalmus.init(args[0]);
    }

    // Force the window size
    Dimension dim = new Dimension(790, 590);
    ui.setSize(800, 600);
    ui.setMinimumSize(dim);

    // Draw
    ui.repaint();

    ui.setVisible(true);
    ui.setFocusable(true);

    //jalmus.setResizable(false);

    ui.setTitle("Jalmus"); // Give the application a title

    ui.setLocationRelativeTo(null); // Center the window on the display

    ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit when frame closed
  }
}

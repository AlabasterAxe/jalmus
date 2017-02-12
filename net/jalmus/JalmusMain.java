package net.jalmus;

import javax.swing.*;
import java.awt.*;

public final class JalmusMain {

  public static void main(String[] args) {
    // Event pour la gestion des Evenements et principalement le message EXIT
    // Constructions de la frame

    SwingNoteReadingGame noteGame = new SwingNoteReadingGame();
    SwingRhythmReadingGame rhythmGame = new SwingRhythmReadingGame();
    SwingScoreReadingGame scoreGame = new SwingScoreReadingGame();
    Jalmus jalmus = new Jalmus(noteGame, rhythmGame, scoreGame);
    SwingJalmus ui = new SwingJalmus(jalmus, noteGame, rhythmGame, scoreGame);

    // Initialization
    if (args.length == 0) {
      ui.init("");
    } else {
      ui.init(args[0]);
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

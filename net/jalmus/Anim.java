package net.jalmus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

class Anim extends JPanel {

  /**
   * A reference to the jalmus that contains this Anim.
   */
  protected final SwingJalmus ui;

  private static final long serialVersionUID = 1L;

  int width = 680;
  int height = 480;

  public Anim(SwingJalmus jalmus) {
    this.ui = jalmus;
    setPreferredSize(new Dimension(width, height));
    setDoubleBuffered(true);
  }

  @Override
    public void paintComponent(Graphics g) {
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      Dimension d = getSize();

      //System.out.println(this);
      //System.out.println(this.ui);
      //System.out.println(this.ui.jalmus);
      //System.out.println(this.ui.jalmus.selectedGame);
      if (this.ui.jalmus.selectedGame == Jalmus.NOTEREADING) {
        // throw new IllegalStateException("If selected game is notereading, a NoteAnim is required to handle that shit.");
      } else if (this.ui.jalmus.selectedGame == Jalmus.FIRSTSCREEN) {

        g.drawImage(this.ui.jbackground, 0, 0, d.width, d.height, this);

        Color color = new Color(5, 5, 100);
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("Jalmus", (d.width / 2) - 95, (d.height / 2) - 35);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Java Lecture Musicale", (d.width / 2) - 155, (d.height / 2) + 15);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Copyright (C) 2003-2011 RICHARD Christophe", 10, d.height - 40);
      } else if (this.ui.jalmus.selectedGame == Jalmus.RHYTHMREADING) {
        g.setColor(Color.white);
        g.fillRect(0, 0, d.width, d.height);
        this.ui.gameButtonPanel.setBackground(Color.white);

        this.ui.drawScore(g);
        this.ui.drawKeys(g);
        this.ui.drawTimeSignature(g);

        /* Show cursor if enabled */
        if (this.ui.jalmus.rhythmGame.rhythmLevel.getMetronomeBeats()) {
          g.setColor(Color.orange);
          g.fillRect(this.ui.rhythmGame.rhythmCursorXStartPos, this.ui.rhythmGame.rhythmAnswerScoreYpos - 31, 
              (int) this.ui.rhythmGame.rhythmCursorXpos - this.ui.rhythmGame.rhythmCursorXStartPos, 3);
        }

        if (this.ui.paintRhythms) {
            this.ui.rhythmGame.drawNotesAndAnswers(g, this.ui.musiSync);
        }
      } else if (this.ui.jalmus.selectedGame == Jalmus.SCOREREADING) {
        g.setColor(Color.white);
        g.fillRect(0, 0, d.width, d.height);
        this.ui.gameButtonPanel.setBackground(Color.white);

        this.ui.drawScore(g);
        this.ui.drawKeys(g);
        this.ui.drawTimeSignature(g);

        ui.numberOfRows = ((getSize().height - ui.scoreYpos - 50) / ui.rowsDistance)+1;
        ui.jalmus.scoreGame.scoreLevel.getCurrentTonality().paint(3,
          ui.jalmus.scoreGame.scoreLevel.getKey(), g, ui.musiSync,
          ui.windowMargin + ui.keyWidth, ui.scoreYpos, ui.rowsDistance,
          ui.numberOfRows, this, ui.bundle);

        if (this.ui.scoreGame.metronomeShowCheckBox.isSelected()) {
          g.setColor(Color.orange);
          g.fillRect(this.ui.rhythmGame.rhythmCursorXStartPos, this.ui.rhythmGame.rhythmAnswerScoreYpos - 31,
            (int) this.ui.rhythmGame.rhythmCursorXpos - this.ui.rhythmGame.rhythmCursorXStartPos, 3);
        }

        if (this.ui.paintRhythms) {
          this.ui.scoreGame.drawNotesAndAnswers(g, this.ui.musiSync);
        }
      }
    }
  }

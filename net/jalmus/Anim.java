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
	private final SwingJalmus ui;

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

      System.out.println(this);
      System.out.println(this.ui);
      System.out.println(this.ui.jalmus);
      System.out.println(this.ui.jalmus.selectedGame);
      if (this.ui.jalmus.selectedGame == Jalmus.NOTEREADING) {
        super.paintComponent(g);

        g.setColor(Color.white);
        g.fillRect(0, 0, d.width, d.height);

        if (this.ui.jalmus.gameStarted && !this.ui.jalmus.paused && (this.ui.noteGame.noteLevel.isNormalgame() || this.ui.noteGame.noteLevel.isLearninggame())) {
          if (this.ui.noteGame.noteLevel.isNotesgame() || this.ui.noteGame.noteLevel.isAccidentalsgame() || this.ui.noteGame.noteLevel.isCustomNotesgame()) {
            this.ui.noteGame.drawNote(this.ui.noteGame.currentNote, g, this.ui.musiSync, Color.black);
          } else if (this.ui.noteGame.noteLevel.isChordsgame()) {
            //on affiche la note que lorsque la partie a commencï¿½e
            this.ui.noteGame.drawChord(this.ui.noteGame.currentChord, g, true);
          } else if (this.ui.noteGame.noteLevel.isIntervalsgame()) {
            this.ui.noteGame.drawInterval(this.ui.noteGame.currentInterval, g, true);
          }
        } else if ((this.ui.jalmus.gameStarted && !this.ui.jalmus.paused && this.ui.noteGame.noteLevel.isInlinegame())) {
          this.ui.noteGame.drawInlineNotes(g, this.ui.musiSync);
        }

        this.ui.drawInlineGame(g);
        this.ui.drawKeys(g);
        this.ui.noteGame.noteLevel.getCurrentTonality().paint(1,
            this.ui.noteGame.noteLevel.getKey(), g, this.ui.musiSync,
            this.ui.noteMargin + this.ui.keyWidth, this.ui.scoreYpos,
            this.ui.rowsDistance, 1, this, this.ui.bundle);

        if (!this.ui.noteGame.noteLevel.isLearninggame()) {
          this.ui.jalmus.currentScore.paint(g, d.width);
        }

        Note basenotet1 = new Note(0,0,0);
        Note basenotet2 = new Note(0,0,0);

        Note basenoteb1 = new Note(0,0,0);
        Note basenoteb2 = new Note(0,0,0);

        if (this.ui.noteGame.noteLevel.isCurrentKeyTreble()) {
          basenotet1.setHeight(this.ui.scoreYpos+this.ui.noteGame.noteLevel.getBasetreble()-(this.ui.noteGame.noteLevel.getNbnotesunder()*5));
          basenotet1.updateNote(this.ui.noteGame.noteLevel, this.ui.scoreYpos, this.ui.bundle);
          basenotet2.setHeight(this.ui.scoreYpos+this.ui.noteGame.noteLevel.getBasetreble()+(this.ui.noteGame.noteLevel.getNbnotesupper()*5));
          basenotet2.updateNote(this.ui.noteGame.noteLevel, this.ui.scoreYpos, this.ui.bundle);
        } else if (this.ui.noteGame.noteLevel.isCurrentKeyBass()) {
          basenoteb1.setHeight(this.ui.scoreYpos+this.ui.noteGame.noteLevel.getBasebass()-(this.ui.noteGame.noteLevel.getNbnotesunder()*5));
          basenoteb1.updateNote(this.ui.noteGame.noteLevel, this.ui.scoreYpos, this.ui.bundle);
          basenoteb2.setHeight(this.ui.scoreYpos+this.ui.noteGame.noteLevel.getBasebass()+(this.ui.noteGame.noteLevel.getNbnotesupper()*5));
          basenoteb2.updateNote(this.ui.noteGame.noteLevel, this.ui.scoreYpos, this.ui.bundle);                 
        } else if (this.ui.noteGame.noteLevel.isCurrentKeyBoth()) {
          basenotet1.setHeight(this.ui.scoreYpos+this.ui.noteGame.noteLevel.getBasetreble()-(this.ui.noteGame.noteLevel.getNbnotesunder()*5));
          basenotet1.updateNote(this.ui.noteGame.noteLevel, this.ui.scoreYpos, this.ui.bundle);
          basenotet2.setHeight(this.ui.scoreYpos+this.ui.noteGame.noteLevel.getBasetreble()+(this.ui.noteGame.noteLevel.getNbnotesupper()*5));
          basenotet2.updateNote(this.ui.noteGame.noteLevel, this.ui.scoreYpos, this.ui.bundle);
          basenoteb1.setHeight(this.ui.scoreYpos+this.ui.noteGame.noteLevel.getBasebass()+90-(this.ui.noteGame.noteLevel.getNbnotesunder()*5));
          basenoteb1.updateNote(this.ui.noteGame.noteLevel, this.ui.scoreYpos, this.ui.bundle);
          basenoteb2.setHeight(this.ui.scoreYpos+this.ui.noteGame.noteLevel.getBasebass()+90+(this.ui.noteGame.noteLevel.getNbnotesupper()*5));
          basenoteb2.updateNote(this.ui.noteGame.noteLevel, this.ui.scoreYpos, this.ui.bundle);
        }

        if (this.ui.noteGame.noteLevel.isLearninggame()) {
          if (this.ui.noteGame.noteLevel.isNotesgame() || this.ui.noteGame.noteLevel.isAccidentalsgame() || this.ui.noteGame.noteLevel.isCustomNotesgame()) {
            this.ui.jalmus.piano.paint(g, d.width, !this.ui.jalmus.isLessonMode & !this.ui.jalmus.gameStarted, basenotet1.getPitch(), basenotet2.getPitch(),
                basenoteb1.getPitch(), basenoteb2.getPitch(), this.ui.noteGame.currentNote.getPitch(), 0, 0, this.ui.noteGame.noteLevel.isCustomNotesgame(), this.ui.noteGame.noteLevel.getPitcheslist());
          } else if (this.ui.noteGame.noteLevel.isIntervalsgame()) {
            this.ui.jalmus.piano.paint(g, d.width, false, basenotet1.getPitch(), basenotet2.getPitch(),basenoteb1.getPitch(), 
                basenoteb2.getPitch(), this.ui.noteGame.currentInterval.getNote(0).getPitch(),
                this.ui.noteGame.currentInterval.getNote(1).getPitch(), 0,this.ui.noteGame.noteLevel.isCustomNotesgame(), this.ui.noteGame.noteLevel.getPitcheslist());
          } else if (this.ui.noteGame.noteLevel.isChordsgame()) {
            this.ui.jalmus.piano.paint(g, d.width, false, basenotet1.getPitch(), basenotet2.getPitch(),basenoteb1.getPitch(), 
                basenoteb2.getPitch(), this.ui.noteGame.currentChord.getNote(0).getPitch(),
                this.ui.noteGame.currentChord.getNote(1).getPitch(),
                this.ui.noteGame.currentChord.getNote(2).getPitch(),
                this.ui.noteGame.noteLevel.isCustomNotesgame(), this.ui.noteGame.noteLevel.getPitcheslist());
          }
          this.ui.noteGame.applyButtonColor();
        } else {
          this.ui.jalmus.piano.paint(g, d.width, !this.ui.jalmus.isLessonMode && !this.ui.jalmus.gameStarted && (this.ui.noteGame.noteLevel.isNotesgame()|| this.ui.noteGame.noteLevel.isAccidentalsgame()),
              basenotet1.getPitch(), basenotet2.getPitch(),basenoteb1.getPitch(), basenoteb2.getPitch(),  0, 0, 0,
              this.ui.noteGame.noteLevel.isCustomNotesgame(), this.ui.noteGame.noteLevel.getPitcheslist());
        }
      } else if (this.ui.jalmus.selectedGame == Jalmus.FIRSTSCREEN) {

        g.drawImage(this.ui.jbackground, 0, 0, d.width, d.height, this);

        Color color = new Color(5, 5, 100);
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("Jalmus", (d.width/2) - 95, (d.height / 2) - 35);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Java Lecture Musicale", (d.width/2) - 155, (d.height / 2) + 15);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Copyright (C) 2003-2011 RICHARD Christophe", 10, d.height - 40);
      } else if (this.ui.jalmus.selectedGame == Jalmus.RHYTHMREADING || this.ui.jalmus.selectedGame==Jalmus.SCOREREADING) {

        g.setColor(Color.white);
        g.fillRect(0, 0, d.width, d.height);
        this.ui.gameButtonPanel.setBackground(Color.white);

        if ((this.ui.jalmus.selectedGame == Jalmus.RHYTHMREADING &&
             this.ui.jalmus.rhythmGame.rhythmLevel.getTriplet()) ||
            (this.ui.jalmus.selectedGame == Jalmus.SCOREREADING &&
             this.ui.jalmus.scoreGame.scoreLevel.getTriplet())) {
          //rowsDistance = 130;
          this.ui.rowsDistance = 100;
        } else {
          this.ui.rowsDistance = 100;
        }

        this.ui.drawScore(g);
        this.ui.drawKeys(g);
        this.ui.drawTimeSignature(g);

        if (this.ui.jalmus.selectedGame == Jalmus.SCOREREADING) {
          ui.numberOfRows = ((getSize().height - ui.scoreYpos - 50) / ui.rowsDistance)+1;
          ui.jalmus.scoreGame.scoreLevel.getCurrentTonality().paint(3,
              ui.jalmus.scoreGame.scoreLevel.getKey(), g, ui.musiSync,
              ui.windowMargin + ui.keyWidth, ui.scoreYpos, ui.rowsDistance,
              ui.numberOfRows, this, ui.bundle);
        }
        /* Show cursor if enabled */
        if ((this.ui.jalmus.selectedGame == Jalmus.RHYTHMREADING &&
             this.ui.jalmus.rhythmGame.rhythmLevel.getMetronomeBeats()) ||
            (this.ui.jalmus.selectedGame == Jalmus.SCOREREADING &&
             this.ui.scoreGame.metronomeShowCheckBox.isSelected())) {
          g.setColor(Color.orange);
          g.fillRect(this.ui.rhythmCursorXStartPos, this.ui.rhythmAnswerScoreYpos - 31, 
              (int) this.ui.rhythmCursorXpos - this.ui.rhythmCursorXStartPos, 3);
        }

        if (this.ui.paintRhythms) {
          this.ui.scoreGame.drawNotesAndAnswers(g, this.ui.musiSync);
        }
      }
    }
  }
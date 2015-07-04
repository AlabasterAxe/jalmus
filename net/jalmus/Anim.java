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
	 * 
	 */
	private final Jalmus jalmus;

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    int width = 680, height=480;

    public Anim(Jalmus jalmus) {
      this.jalmus = jalmus;
	setPreferredSize(new Dimension(width, height));
      setDoubleBuffered(true);

    }

    public void paintComponent(Graphics g) {
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      Dimension d = getSize();

      if (this.jalmus.selectedGame == Jalmus.NOTEREADING) {

        super.paintComponent(g);

        g.setColor(Color.white);
        g.fillRect(0, 0, d.width, d.height);

        if (this.jalmus.gameStarted && !this.jalmus.paused && (this.jalmus.noteLevel.isNormalgame() || this.jalmus.noteLevel.isLearninggame())) {
          if (this.jalmus.noteLevel.isNotesgame() || this.jalmus.noteLevel.isAccidentalsgame() || this.jalmus.noteLevel.isCustomNotesgame()) {
            this.jalmus.drawNote(this.jalmus.ncourante, g, this.jalmus.MusiSync, Color.black);
          }
          //on affiche la note que lorsque la partie a commencï¿½e
          else if (this.jalmus.noteLevel.isChordsgame()) {
            this.jalmus.drawChord(this.jalmus.acourant, g, true);
          } else if (this.jalmus.noteLevel.isIntervalsgame()) {
            this.jalmus.drawInterval(this.jalmus.icourant, g, true);
          }
        } else if ((this.jalmus.gameStarted && !this.jalmus.paused && this.jalmus.noteLevel.isInlinegame())) {
          this.jalmus.drawInlineNotes(g, this.jalmus.MusiSync);
        }

        this.jalmus.drawInlineGame(g);
        this.jalmus.drawKeys(g);
        this.jalmus.noteLevel.getCurrentTonality().paint(1,this.jalmus.noteLevel.getKey(), g, this.jalmus.MusiSync, this.jalmus.noteMargin + this.jalmus.keyWidth, this.jalmus.scoreYpos, this.jalmus.rowsDistance, 1, this, this.jalmus.ui.bundle);

        if (!this.jalmus.noteLevel.isLearninggame()) {
          this.jalmus.currentScore.paint(g, d.width);
        }

        Note basenotet1 = new Note(0,0,0);
        Note basenotet2 = new Note(0,0,0);

        Note basenoteb1 = new Note(0,0,0);
        Note basenoteb2 = new Note(0,0,0);

        if (this.jalmus.noteLevel.isCurrentKeyTreble() ) {
          basenotet1.setHeight(this.jalmus.scoreYpos+this.jalmus.noteLevel.getBasetreble()-(this.jalmus.noteLevel.getNbnotesunder()*5));
          basenotet1.updateNote(this.jalmus.noteLevel, this.jalmus.scoreYpos, this.jalmus.ui.bundle);
          basenotet2.setHeight(this.jalmus.scoreYpos+this.jalmus.noteLevel.getBasetreble()+(this.jalmus.noteLevel.getNbnotesupper()*5));
          basenotet2.updateNote(this.jalmus.noteLevel, this.jalmus.scoreYpos, this.jalmus.ui.bundle);
        }
        else if (this.jalmus.noteLevel.isCurrentKeyBass()) {
          basenoteb1.setHeight(this.jalmus.scoreYpos+this.jalmus.noteLevel.getBasebass()-(this.jalmus.noteLevel.getNbnotesunder()*5));
          basenoteb1.updateNote(this.jalmus.noteLevel, this.jalmus.scoreYpos, this.jalmus.ui.bundle);
          basenoteb2.setHeight(this.jalmus.scoreYpos+this.jalmus.noteLevel.getBasebass()+(this.jalmus.noteLevel.getNbnotesupper()*5));
          basenoteb2.updateNote(this.jalmus.noteLevel, this.jalmus.scoreYpos, this.jalmus.ui.bundle);                 
        }
        else if (this.jalmus.noteLevel.isCurrentKeyBoth()){
          basenotet1.setHeight(this.jalmus.scoreYpos+this.jalmus.noteLevel.getBasetreble()-(this.jalmus.noteLevel.getNbnotesunder()*5));
          basenotet1.updateNote(this.jalmus.noteLevel, this.jalmus.scoreYpos, this.jalmus.ui.bundle);
          basenotet2.setHeight(this.jalmus.scoreYpos+this.jalmus.noteLevel.getBasetreble()+(this.jalmus.noteLevel.getNbnotesupper()*5));
          basenotet2.updateNote(this.jalmus.noteLevel, this.jalmus.scoreYpos, this.jalmus.ui.bundle);
          basenoteb1.setHeight(this.jalmus.scoreYpos+this.jalmus.noteLevel.getBasebass()+90-(this.jalmus.noteLevel.getNbnotesunder()*5));
          basenoteb1.updateNote(this.jalmus.noteLevel, this.jalmus.scoreYpos, this.jalmus.ui.bundle);
          basenoteb2.setHeight(this.jalmus.scoreYpos+this.jalmus.noteLevel.getBasebass()+90+(this.jalmus.noteLevel.getNbnotesupper()*5));
          basenoteb2.updateNote(this.jalmus.noteLevel, this.jalmus.scoreYpos, this.jalmus.ui.bundle);
        }

        if (this.jalmus.noteLevel.isLearninggame() ) {
          if (this.jalmus.noteLevel.isNotesgame() || this.jalmus.noteLevel.isAccidentalsgame() || this.jalmus.noteLevel.isCustomNotesgame()) {
            this.jalmus.piano.paint(g, d.width, !this.jalmus.isLessonMode & !this.jalmus.gameStarted, basenotet1.getPitch(), basenotet2.getPitch(),
                basenoteb1.getPitch(), basenoteb2.getPitch(), this.jalmus.ncourante.getPitch(), 0, 0, this.jalmus.noteLevel.isCustomNotesgame(), this.jalmus.noteLevel.getPitcheslist());
          } else if (this.jalmus.noteLevel.isIntervalsgame()) {
            this.jalmus.piano.paint(g, d.width, false, basenotet1.getPitch(), basenotet2.getPitch(),basenoteb1.getPitch(), 
                basenoteb2.getPitch(), this.jalmus.icourant.getNote(0).getPitch(),
                this.jalmus.icourant.getNote(1).getPitch(), 0,this.jalmus.noteLevel.isCustomNotesgame(), this.jalmus.noteLevel.getPitcheslist());
          } else if (this.jalmus.noteLevel.isChordsgame()) {
            this.jalmus.piano.paint(g, d.width, false, basenotet1.getPitch(), basenotet2.getPitch(),basenoteb1.getPitch(), 
                basenoteb2.getPitch(), this.jalmus.acourant.getNote(0).getPitch(),
                this.jalmus.acourant.getNote(1).getPitch(),
                this.jalmus.acourant.getNote(2).getPitch(),
                this.jalmus.noteLevel.isCustomNotesgame(), this.jalmus.noteLevel.getPitcheslist());
          }
          this.jalmus.applyButtonColor();
        } 
        else {
          this.jalmus.piano.paint(g, d.width, !this.jalmus.isLessonMode & !this.jalmus.gameStarted & (this.jalmus.noteLevel.isNotesgame()|| this.jalmus.noteLevel.isAccidentalsgame()),
              basenotet1.getPitch(), basenotet2.getPitch(),basenoteb1.getPitch(), basenoteb2.getPitch(),  0, 0, 0,
              this.jalmus.noteLevel.isCustomNotesgame(), this.jalmus.noteLevel.getPitcheslist());
        }

      } else if (this.jalmus.selectedGame == Jalmus.FIRSTSCREEN) {

        g.drawImage(this.jalmus.jbackground, 0, 0, d.width, d.height, this);

        Color color = new Color(5, 5, 100);
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("JALMUS", (d.width/2) - 95, (d.height / 2) - 35);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Java Lecture Musicale", (d.width/2) - 155, (d.height / 2) + 15);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Copyright (C) 2003-2011 RICHARD Christophe", 10, d.height - 40);


      } else if (this.jalmus.selectedGame == Jalmus.RHYTHMREADING || this.jalmus.selectedGame==Jalmus.SCOREREADING) {

        g.setColor(Color.white);
        g.fillRect(0, 0, d.width, d.height);
        this.jalmus.ui.pgamebutton.setBackground(Color.white);

        if ((this.jalmus.selectedGame == Jalmus.RHYTHMREADING && this.jalmus.rhythmLevel.getTriplet()) ||
            (this.jalmus.selectedGame == Jalmus.SCOREREADING && this.jalmus.scoreLevel.getTriplet()))
          //rowsDistance = 130;
          this.jalmus.rowsDistance = 100;
        else
          this.jalmus.rowsDistance = 100;

        this.jalmus.drawScore(g);
        this.jalmus.drawKeys(g);
        this.jalmus.drawTimeSignature(g);

        if (this.jalmus.selectedGame == Jalmus.SCOREREADING) {
          this.jalmus.numberOfRows = ((getSize().height - this.jalmus.scoreYpos - 50) / this.jalmus.rowsDistance)+1;    
          this.jalmus.scoreLevel.getCurrentTonality().paint(3, this.jalmus.scoreLevel.getKey(), g, this.jalmus.MusiSync, this.jalmus.windowMargin + this.jalmus.keyWidth, this.jalmus.scoreYpos, this.jalmus.rowsDistance, this.jalmus.numberOfRows, this, this.jalmus.ui.bundle);
        }
        /* Show cursor if enabled */
        if ((this.jalmus.selectedGame == Jalmus.RHYTHMREADING && this.jalmus.rhythmLevel.getMetronomeBeats()) ||
            (this.jalmus.selectedGame == Jalmus.SCOREREADING && this.jalmus.ui.scoreMetronomeShowCheckBox.isSelected())) {
          g.setColor(Color.orange);
          g.fillRect(this.jalmus.rhythmCursorXStartPos, this.jalmus.rhythmAnswerScoreYpos - 31, (int)this.jalmus.rhythmCursorXpos - this.jalmus.rhythmCursorXStartPos, 3);
            }

        if (this.jalmus.paintrhythms)
          this.jalmus.drawNotesAndAnswers(g, this.jalmus.MusiSync);
      }
    }
  }
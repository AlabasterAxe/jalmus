package net.jalmus;

import java.awt.*;

public class NoteAnim extends Anim {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public NoteAnim(SwingJalmus jalmus) {
    super(jalmus);
  }

  @Override
  public void paintComponent(Graphics g) {
    Dimension d = getSize();
    super.paintComponent(g);

    g.setColor(Color.white);
    g.fillRect(0, 0, d.width, d.height);

    if (ui.gameStarted() && !ui.jalmus.paused && (ui.noteGame.noteLevel.isNormalgame() || ui.noteGame.noteLevel.isLearningGame())) {
      if (ui.noteGame.noteLevel.isNotesgame() || ui.noteGame.noteLevel.isAccidentalsgame() || ui.noteGame.noteLevel.isCustomNotesgame()) {
        ui.noteGame.drawNote(ui.noteGame.currentNote, g, ui.musiSync, Color.black);
      } else if (ui.noteGame.noteLevel.isChordsgame()) {
        //on affiche la note que lorsque la partie a commencï¿½e
        ui.noteGame.drawChord(ui.noteGame.currentChord, g, true);
      } else if (ui.noteGame.noteLevel.isIntervalsgame()) {
        ui.noteGame.drawInterval(ui.noteGame.currentInterval, g, true);
      }
    } else if ((ui.gameStarted() && !ui.jalmus.paused && ui.noteGame.noteLevel.isInlinegame())) {
      ui.noteGame.drawInlineNotes(g, ui.musiSync);
    }

    ui.noteGame.drawInlineGame(g);
    ui.noteGame.drawKeys(g);
    ui.noteGame.noteLevel.getCurrentTonality().paint(1,
        ui.noteGame.noteLevel.getKey(), g, ui.musiSync,
        ui.noteMargin + ui.keyWidth, ui.scoreYpos,
        ui.rowsDistance, 1, this, ui.bundle);

    if (!ui.noteGame.noteLevel.isLearningGame()) {
      ui.noteGame.currentScore.paint(g, d.width);
    }

    Note basenotet1 = new Note(0, 0, 0);
    Note basenotet2 = new Note(0, 0, 0);

    Note basenoteb1 = new Note(0, 0, 0);
    Note basenoteb2 = new Note(0, 0, 0);

    if (ui.noteGame.noteLevel.isCurrentKeyTreble()) {
      basenotet1.setHeight(ui.scoreYpos + ui.noteGame.noteLevel.getBasetreble() - (ui.noteGame.noteLevel.getNbnotesunder() * 5));
      basenotet1.updateNote(ui.noteGame.noteLevel, ui.scoreYpos, ui.bundle);
      basenotet2.setHeight(ui.scoreYpos + ui.noteGame.noteLevel.getBasetreble() + (ui.noteGame.noteLevel.getNbnotesupper() * 5));
      basenotet2.updateNote(ui.noteGame.noteLevel, ui.scoreYpos, ui.bundle);
    } else if (ui.noteGame.noteLevel.isCurrentKeyBass()) {
      basenoteb1.setHeight(ui.scoreYpos + ui.noteGame.noteLevel.getBasebass() - (ui.noteGame.noteLevel.getNbnotesunder() * 5));
      basenoteb1.updateNote(ui.noteGame.noteLevel, ui.scoreYpos, ui.bundle);
      basenoteb2.setHeight(ui.scoreYpos + ui.noteGame.noteLevel.getBasebass() + (ui.noteGame.noteLevel.getNbnotesupper() * 5));
      basenoteb2.updateNote(ui.noteGame.noteLevel, ui.scoreYpos, ui.bundle);
    } else if (ui.noteGame.noteLevel.isCurrentKeyBoth()) {
      basenotet1.setHeight(ui.scoreYpos + ui.noteGame.noteLevel.getBasetreble() - (ui.noteGame.noteLevel.getNbnotesunder() * 5));
      basenotet1.updateNote(ui.noteGame.noteLevel, ui.scoreYpos, ui.bundle);
      basenotet2.setHeight(ui.scoreYpos + ui.noteGame.noteLevel.getBasetreble() + (ui.noteGame.noteLevel.getNbnotesupper() * 5));
      basenotet2.updateNote(ui.noteGame.noteLevel, ui.scoreYpos, ui.bundle);
      basenoteb1.setHeight(ui.scoreYpos + ui.noteGame.noteLevel.getBasebass() + 90 - (ui.noteGame.noteLevel.getNbnotesunder() * 5));
      basenoteb1.updateNote(ui.noteGame.noteLevel, ui.scoreYpos, ui.bundle);
      basenoteb2.setHeight(ui.scoreYpos + ui.noteGame.noteLevel.getBasebass() + 90 + (ui.noteGame.noteLevel.getNbnotesupper() * 5));
      basenoteb2.updateNote(ui.noteGame.noteLevel, ui.scoreYpos, ui.bundle);
    }

    if (ui.noteGame.noteLevel.isLearningGame()) {
      if (ui.noteGame.noteLevel.isNotesgame() ||
          ui.noteGame.noteLevel.isAccidentalsgame() ||
          ui.noteGame.noteLevel.isCustomNotesgame()) {
        ui.jalmus.piano.paint(g, d.width, !ui.jalmus.isLessonMode & !ui.gameStarted(), basenotet1.getPitch(), basenotet2.getPitch(),
            basenoteb1.getPitch(), basenoteb2.getPitch(), ui.noteGame.currentNote.getPitch(), 0, 0, ui.noteGame.noteLevel.isCustomNotesgame(), ui.noteGame.noteLevel.getPitcheslist());
      } else if (ui.noteGame.noteLevel.isIntervalsgame()) {
        ui.jalmus.piano.paint(g, d.width, false, basenotet1.getPitch(), basenotet2.getPitch(), basenoteb1.getPitch(),
            basenoteb2.getPitch(), ui.noteGame.currentInterval.getNote(0).getPitch(),
            ui.noteGame.currentInterval.getNote(1).getPitch(), 0, ui.noteGame.noteLevel.isCustomNotesgame(), ui.noteGame.noteLevel.getPitcheslist());
      } else if (ui.noteGame.noteLevel.isChordsgame()) {
        ui.jalmus.piano.paint(g, d.width, false, basenotet1.getPitch(), basenotet2.getPitch(), basenoteb1.getPitch(),
            basenoteb2.getPitch(), ui.noteGame.currentChord.getNote(0).getPitch(),
            ui.noteGame.currentChord.getNote(1).getPitch(),
            ui.noteGame.currentChord.getNote(2).getPitch(),
            ui.noteGame.noteLevel.isCustomNotesgame(), ui.noteGame.noteLevel.getPitcheslist());
      }
      ui.noteGame.applyButtonColor();
    } else {
      ui.jalmus.piano.paint(g, d.width, !ui.jalmus.isLessonMode && !ui.gameStarted() && (ui.noteGame.noteLevel.isNotesgame() || ui.noteGame.noteLevel.isAccidentalsgame()),
          basenotet1.getPitch(), basenotet2.getPitch(), basenoteb1.getPitch(), basenoteb2.getPitch(), 0, 0, 0,
          ui.noteGame.noteLevel.isCustomNotesgame(), ui.noteGame.noteLevel.getPitcheslist());
    }
  }
}

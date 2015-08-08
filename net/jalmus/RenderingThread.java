package net.jalmus;

class RenderingThread extends Thread {

  private final SwingJalmus ui;

  /**
   * @param jalmus
   */
  RenderingThread(SwingJalmus ui) {
    this.ui = ui;
  }

  /**
   *  This thread calls Jalmus window refresh every 10ms
   */
  public void run() {
    while (true) {
      try {
        if (ui.jalmus.noteGame.noteLevel.isNotesgame() ||
            ui.jalmus.noteGame.noteLevel.isAccidentalsgame() ||
            ui.jalmus.noteGame.noteLevel.isCustomNotesgame()) {
          if (ui.jalmus.noteGame.noteLevel.isInlinegame()) {
            sleep(ui.jalmus.noteGame.noteLevel.getSpeed()+4);
          } else {
            sleep(ui.jalmus.noteGame.noteLevel.getSpeed());
          }
        } else if (ui.jalmus.noteGame.noteLevel.isIntervalsgame()) {
          if (ui.jalmus.noteGame.noteLevel.isInlinegame()) {
            sleep(ui.jalmus.noteGame.noteLevel.getSpeed()*3/2 + 4);
          } else {
            sleep(ui.jalmus.noteGame.noteLevel.getSpeed()*3/2);
          }
        } else if (ui.jalmus.noteGame.noteLevel.isChordsgame()) {
          if (ui.jalmus.noteGame.noteLevel.isInlinegame()) {
            sleep(ui.jalmus.noteGame.noteLevel.getSpeed()*2 + 4);
          } else {
            sleep(ui.jalmus.noteGame.noteLevel.getSpeed()*2);
          }
        } else { //why ?
          if (ui.jalmus.noteGame.noteLevel.isInlinegame()) {
            sleep(ui.jalmus.noteGame.noteLevel.getSpeed()+18 + 6);
          } else {
            sleep(ui.jalmus.noteGame.noteLevel.getSpeed()+18);
          }
        }

        if (ui.gameStarted() && !ui.jalmus.paused) {
          if ((ui.jalmus.noteGame.noteLevel.isNormalgame() ||
                ui.jalmus.noteGame.noteLevel.isLearninggame()) &&
              (ui.jalmus.noteGame.noteLevel.isNotesgame() ||
               ui.jalmus.noteGame.noteLevel.isAccidentalsgame() ||
               ui.jalmus.noteGame.noteLevel.isCustomNotesgame())) {
            ui.jalmus.noteGame.currentNote.setX(ui.jalmus.noteGame.currentNote.getX()+1);
          } else if ((ui.jalmus.noteGame.noteLevel.isNormalgame() ||
                ui.jalmus.noteGame.noteLevel.isLearninggame()) &&
              ui.jalmus.noteGame.noteLevel.isChordsgame()) {
            ui.jalmus.noteGame.currentChord.move(1);
          } else if ((ui.jalmus.noteGame.noteLevel.isNormalgame() ||
                ui.jalmus.noteGame.noteLevel.isLearninggame()) &&
              ui.jalmus.noteGame.noteLevel.isIntervalsgame()) {
            ui.jalmus.noteGame.currentInterval.move(1);
          } else if (ui.jalmus.noteGame.noteLevel.isInlinegame() &&
              (ui.jalmus.noteGame.noteLevel.isNotesgame() ||
               ui.jalmus.noteGame.noteLevel.isAccidentalsgame() ||
               ui.jalmus.noteGame.noteLevel.isCustomNotesgame())) {
            for (int i = 0; i<ui.jalmus.noteGame.line.length; i++) {
              ui.jalmus.noteGame.line[i].setX(ui.jalmus.noteGame.line[i].getX()-1);
            }
          } else if (ui.jalmus.noteGame.noteLevel.isInlinegame() &&
              ui.jalmus.noteGame.noteLevel.isChordsgame()) {
            for (int i = 0; i<ui.jalmus.noteGame.lineacc.length; i++) {
              ui.jalmus.noteGame.lineacc[i].move(-1);
            }
          } else if (ui.jalmus.noteGame.noteLevel.isInlinegame() &&
              ui.jalmus.noteGame.noteLevel.isIntervalsgame()) {
            for (int i = 0; i<ui.jalmus.noteGame.lineint.length; i++) {
              ui.jalmus.noteGame.lineint[i].move(-1);
            }
          }
        }

        ui.activePanel().repaint();

        int tmpdiv = 1; 
        //thread for rhythm game move the rhythm cursor according to tempo
        if (ui.jalmus.selectedGame == Jalmus.RHYTHMREADING ||
            ui.jalmus.selectedGame == Jalmus.SCOREREADING) {
          if (ui.jalmus.selectedGame == Jalmus.RHYTHMREADING &&
              ui.rhythmGame.cursorstart && ui.muteRhythms) {
            ui.jalmus.tempo = ui.rhythmGame.rhythmLevel.getspeed();
            tmpdiv = ui.rhythmGame.rhythmLevel.getTimeDivision();
          } else if (ui.scoreGame.cursorstart && ui.muteRhythms) {
            ui.jalmus.tempo = ui.jalmus.scoreGame.scoreLevel.getspeed();
            tmpdiv = ui.jalmus.scoreGame.scoreLevel.getTimeDivision();
          }

          if (ui.jalmus.timestart != 0) {
            ui.rhythmCursorXpos = ui.rhythmCursorXStartPos +
              ((System.currentTimeMillis()-ui.jalmus.timestart)*
               (ui.noteDistance*tmpdiv))/(60000/ui.jalmus.tempo);
          }

          if (ui.rhythmCursorXpos >= ui.rhythmCursorXlimit - ui.notesShift) {
            if (ui.rhythmAnswerScoreYpos < ui.scoreYpos + (ui.rowsDistance *
                  (ui.numberOfRows - 2))) {
              ui.rhythmAnswerScoreYpos += ui.rowsDistance;
              ui.rhythmCursorXStartPos = ui.firstNoteXPos - ui.notesShift;
              ui.rhythmCursorXpos = ui.rhythmCursorXStartPos;
              ui.jalmus.timestart = System.currentTimeMillis();
            } else { //end of game
              ui.showResult();
              ui.jalmus.stopRhythmGame();
              ui.jalmus.gameStarted = false;
              ui.repaint();
            }
          }
          sleep(10); // cursor moves every 10 milliseconds
        }
      } catch (Exception e) {
        // What exceptions are these? should we care?
      }
    }
  }
}

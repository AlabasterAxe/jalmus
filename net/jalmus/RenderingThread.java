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
        if (ui.jalmus.noteGame.noteLevel.isNotesgame() || this.ui.jalmus.noteGame.noteLevel.isAccidentalsgame() ||
            this.ui.jalmus.noteGame.noteLevel.isCustomNotesgame()) {
          if (this.ui.jalmus.noteGame.noteLevel.isInlinegame()) {
            sleep(this.ui.jalmus.noteGame.noteLevel.getSpeed()+4);
          } else {
            sleep(this.ui.jalmus.noteGame.noteLevel.getSpeed());
          }
        } else if (this.ui.jalmus.noteGame.noteLevel.isIntervalsgame()) {
          if (this.ui.jalmus.noteGame.noteLevel.isInlinegame()) sleep(this.ui.jalmus.noteGame.noteLevel.getSpeed()*3/2 + 4);
          else  sleep(this.ui.jalmus.noteGame.noteLevel.getSpeed()*3/2);
        } else if (this.ui.jalmus.noteGame.noteLevel.isChordsgame()) {
          if (this.ui.jalmus.noteGame.noteLevel.isInlinegame()) sleep(this.ui.jalmus.noteGame.noteLevel.getSpeed()*2 + 4);
          else  sleep(this.ui.jalmus.noteGame.noteLevel.getSpeed()*2);

        } else { //why ?
          if (this.ui.jalmus.noteGame.noteLevel.isInlinegame()) sleep(this.ui.jalmus.noteGame.noteLevel.getSpeed()+18 + 6);
          else sleep(this.ui.jalmus.noteGame.noteLevel.getSpeed()+18);
        }

          if (this.ui.jalmus.gameStarted && !this.ui.jalmus.paused) {
            if ((this.ui.jalmus.noteGame.noteLevel.isNormalgame() || this.ui.jalmus.noteGame.noteLevel.isLearninggame()) &&
                (this.ui.jalmus.noteGame.noteLevel.isNotesgame() || this.ui.jalmus.noteGame.noteLevel.isAccidentalsgame() || this.ui.jalmus.noteGame.noteLevel.isCustomNotesgame())) {
              this.ui.jalmus.noteGame.currentNote.setX(this.ui.jalmus.noteGame.currentNote.getX()+1);
            } else if ((this.ui.jalmus.noteGame.noteLevel.isNormalgame() || this.ui.jalmus.noteGame.noteLevel.isLearninggame()) &&
              this.ui.jalmus.noteGame.noteLevel.isChordsgame()) {
              this.ui.jalmus.noteGame.currentChord.move(1);
            } else if ((this.ui.jalmus.noteGame.noteLevel.isNormalgame() || this.ui.jalmus.noteGame.noteLevel.isLearninggame()) &&
              this.ui.jalmus.noteGame.noteLevel.isIntervalsgame()) {
              this.ui.jalmus.noteGame.currentInterval.move(1);
            } else if (this.ui.jalmus.noteGame.noteLevel.isInlinegame() &&
                    (this.ui.jalmus.noteGame.noteLevel.isNotesgame() ||
                     this.ui.jalmus.noteGame.noteLevel.isAccidentalsgame() ||
                     this.ui.jalmus.noteGame.noteLevel.isCustomNotesgame())) {
              for (int i = 0; i<this.ui.jalmus.noteGame.line.length; i++) {
                this.ui.jalmus.noteGame.line[i].setX(this.ui.jalmus.noteGame.line[i].getX()-1);
              }
            } else if (this.ui.jalmus.noteGame.noteLevel.isInlinegame() &&
                this.ui.jalmus.noteGame.noteLevel.isChordsgame()) {
              for (int i = 0; i<this.ui.jalmus.noteGame.lineacc.length; i++) {
                this.ui.jalmus.noteGame.lineacc[i].move(-1);
              }
            } else if (this.ui.jalmus.noteGame.noteLevel.isInlinegame() &&
                this.ui.jalmus.noteGame.noteLevel.isIntervalsgame()) {
              for (int i = 0; i<this.ui.jalmus.noteGame.lineint.length; i++) {
                this.ui.jalmus.noteGame.lineint[i].move(-1);
              }
            }
          }

          this.ui.panelanim.repaint();

          int tmpdiv = 1; 
          //thread for rhythm game move the rhythm cursor according to tempo
          if (this.ui.jalmus.selectedGame == Jalmus.RHYTHMREADING ||
               this.ui.jalmus.selectedGame == Jalmus.SCOREREADING) {
            if (this.ui.jalmus.selectedGame == Jalmus.RHYTHMREADING &&
                this.ui.rhythmGame.cursorstart && this.ui.muteRhythms) {
              this.ui.jalmus.tempo = this.ui.rhythmGame.rhythmLevel.getspeed();
              tmpdiv = this.ui.rhythmGame.rhythmLevel.getTimeDivision();
            } else if (this.ui.scoreGame.cursorstart && this.ui.muteRhythms) {
              this.ui.jalmus.tempo = this.ui.jalmus.scoreGame.scoreLevel.getspeed();
              tmpdiv = this.ui.jalmus.scoreGame.scoreLevel.getTimeDivision();
            }

            if (this.ui.jalmus.timestart != 0) {
              this.ui.rhythmCursorXpos = this.ui.rhythmCursorXStartPos +
                  ((System.currentTimeMillis()-this.ui.jalmus.timestart)*
                  (this.ui.noteDistance*tmpdiv))/(60000/this.ui.jalmus.tempo);
            }

            if (this.ui.rhythmCursorXpos >= this.ui.rhythmCursorXlimit - this.ui.notesShift) {
              if (this.ui.rhythmAnswerScoreYpos < this.ui.scoreYpos + (this.ui.rowsDistance *
                  (this.ui.numberOfRows - 2))) {
                this.ui.rhythmAnswerScoreYpos += this.ui.rowsDistance;
                this.ui.rhythmCursorXStartPos = this.ui.firstNoteXPos - this.ui.notesShift;
                this.ui.rhythmCursorXpos = this.ui.rhythmCursorXStartPos;
                this.ui.jalmus.timestart = System.currentTimeMillis();
              } else { //end of game
                this.ui.showResult();
                this.ui.jalmus.stopRhythmGame();
                this.ui.jalmus.gameStarted = false;
                this.ui.repaint();
              }
            }
            sleep(10); // cursor moves every 10 milliseconds
          }
        } catch (Exception e) {}
      }
    }
  }
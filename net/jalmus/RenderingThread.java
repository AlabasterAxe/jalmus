package net.jalmus;

class RenderingThread extends Thread {

	private final Jalmus jalmus;

	/**
	 * @param jalmus
	 */
	RenderingThread(Jalmus jalmus) {
		this.jalmus = jalmus;
	}

  /**
   *  This thread calls Jalmus window refresh every 10ms
   */
  public void run() {
    while (true) {
      try {
        if (jalmus.game.noteLevel.isNotesgame() || this.jalmus.game.noteLevel.isAccidentalsgame()|| this.jalmus.game.noteLevel.isCustomNotesgame()) {
          if (this.jalmus.game.noteLevel.isInlinegame()) sleep(this.jalmus.game.noteLevel.getSpeed()+4);
          else sleep(this.jalmus.game.noteLevel.getSpeed());

        } else if (this.jalmus.game.noteLevel.isIntervalsgame()) {
          if (this.jalmus.game.noteLevel.isInlinegame()) sleep(this.jalmus.game.noteLevel.getSpeed()*3/2 + 4);
          else  sleep(this.jalmus.game.noteLevel.getSpeed()*3/2);
        } else if (this.jalmus.game.noteLevel.isChordsgame()) {
          if (this.jalmus.game.noteLevel.isInlinegame()) sleep(this.jalmus.game.noteLevel.getSpeed()*2 + 4);
          else  sleep(this.jalmus.game.noteLevel.getSpeed()*2);

        } else { //why ?
          if (this.jalmus.game.noteLevel.isInlinegame()) sleep(this.jalmus.game.noteLevel.getSpeed()+18 + 6);
          else sleep(this.jalmus.game.noteLevel.getSpeed()+18);
        }

          if (this.jalmus.gameStarted && !this.jalmus.paused) {
            if ((this.jalmus.game.noteLevel.isNormalgame() || this.jalmus.game.noteLevel.isLearninggame()) &&
                (this.jalmus.game.noteLevel.isNotesgame() || this.jalmus.game.noteLevel.isAccidentalsgame() || this.jalmus.game.noteLevel.isCustomNotesgame())) {
              this.jalmus.currentNote.setX(this.jalmus.currentNote.getX()+1);
            } else if ((this.jalmus.game.noteLevel.isNormalgame() || this.jalmus.game.noteLevel.isLearninggame()) &&
              this.jalmus.game.noteLevel.isChordsgame()) {
              this.jalmus.currentChord.move(1);
            } else if ((this.jalmus.game.noteLevel.isNormalgame() || this.jalmus.game.noteLevel.isLearninggame()) &&
              this.jalmus.game.noteLevel.isIntervalsgame()) {
              this.jalmus.currentInterval.move(1);
            } else if (this.jalmus.game.noteLevel.isInlinegame() &&
                    (this.jalmus.game.noteLevel.isNotesgame() ||
                     this.jalmus.game.noteLevel.isAccidentalsgame() ||
                     this.jalmus.game.noteLevel.isCustomNotesgame())) {
              for (int i = 0; i<this.jalmus.line.length; i++) {
                this.jalmus.line[i].setX(this.jalmus.line[i].getX()-1);
              }
            } else if (this.jalmus.game.noteLevel.isInlinegame() &&
                this.jalmus.game.noteLevel.isChordsgame()) {
              for (int i = 0; i<this.jalmus.lineacc.length; i++) {
                this.jalmus.lineacc[i].move(-1);
              }
            } else if (this.jalmus.game.noteLevel.isInlinegame() &&
                this.jalmus.game.noteLevel.isIntervalsgame()) {
              for (int i = 0; i<this.jalmus.lineint.length; i++) {
                this.jalmus.lineint[i].move(-1);
              }
            }
          }

          this.jalmus.panelanim.repaint();

          int tmpdiv = 1; 
          //thread for rhythm game move the rhythm cursor according to tempo
          if ((this.jalmus.selectedGame == Jalmus.RHYTHMREADING || this.jalmus.selectedGame==Jalmus.SCOREREADING) && this.jalmus.rhythmgame == 0 && this.jalmus.muterhythms && this.jalmus.cursorstart) {

            if (this.jalmus.selectedGame == Jalmus.RHYTHMREADING){
              this.jalmus.tempo = this.jalmus.rhythmLevel.getspeed();
              tmpdiv = this.jalmus.rhythmLevel.getTimeDivision();
            }
            else {
              this.jalmus.tempo = this.jalmus.scoreLevel.getspeed();
              tmpdiv = this.jalmus.scoreLevel.getTimeDivision();
            }


            if (this.jalmus.timestart != 0) {
              this.jalmus.ui.rhythmCursorXpos = this.jalmus.ui.rhythmCursorXStartPos + ((System.currentTimeMillis()-this.jalmus.timestart)*(this.jalmus.ui.noteDistance*tmpdiv))/(60000/this.jalmus.tempo);
              //System.out.println(rhythmCursorXpos);
            }

            if (this.jalmus.ui.rhythmCursorXpos >= this.jalmus.ui.rhythmCursorXlimit - this.jalmus.ui.notesShift) {
              if (this.jalmus.ui.rhythmAnswerScoreYpos < this.jalmus.ui.scoreYpos + (this.jalmus.rowsDistance * (this.jalmus.numberOfRows - 2))) {
                this.jalmus.ui.rhythmAnswerScoreYpos += this.jalmus.rowsDistance;
                this.jalmus.ui.rhythmCursorXStartPos = this.jalmus.ui.firstNoteXPos - this.jalmus.ui.notesShift;
                this.jalmus.ui.rhythmCursorXpos = this.jalmus.ui.rhythmCursorXStartPos;
                this.jalmus.timestart = System.currentTimeMillis();
              }
              else { //end of game
                this.jalmus.showResult();
                this.jalmus.stopRhythmGame();
                this.jalmus.gameStarted = false;
                this.jalmus.ui.repaint();
              }
            }
            sleep(10); // cursor moves every 10 milliseconds
          }
        }

        catch (Exception e) {
        }
      }

    }
  }
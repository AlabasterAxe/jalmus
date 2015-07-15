package net.jalmus;

class RenderingThread extends Thread {

    /**
	 * 
	 */
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

          if (this.jalmus.noteLevel.isNotesgame() || this.jalmus.noteLevel.isAccidentalsgame()|| this.jalmus.noteLevel.isCustomNotesgame()) {
            if (this.jalmus.noteLevel.isInlinegame()) sleep(this.jalmus.noteLevel.getSpeed()+4);
            else sleep(this.jalmus.noteLevel.getSpeed());

          } else if (this.jalmus.noteLevel.isIntervalsgame()) {
            if (this.jalmus.noteLevel.isInlinegame()) sleep(this.jalmus.noteLevel.getSpeed()*3/2 + 4);
            else  sleep(this.jalmus.noteLevel.getSpeed()*3/2);
          } else if (this.jalmus.noteLevel.isChordsgame()) {
            if (this.jalmus.noteLevel.isInlinegame()) sleep(this.jalmus.noteLevel.getSpeed()*2 + 4);
            else  sleep(this.jalmus.noteLevel.getSpeed()*2);

          } else { //why ?
            if (this.jalmus.noteLevel.isInlinegame()) sleep(this.jalmus.noteLevel.getSpeed()+18 + 6);
            else  sleep(this.jalmus.noteLevel.getSpeed()+18);
          }

          if (this.jalmus.gameStarted && !this.jalmus.paused) {
            if ((this.jalmus.noteLevel.isNormalgame() || this.jalmus.noteLevel.isLearninggame()) &&
                (this.jalmus.noteLevel.isNotesgame() || this.jalmus.noteLevel.isAccidentalsgame() || this.jalmus.noteLevel.isCustomNotesgame())) {
              this.jalmus.currentNote.setX(this.jalmus.currentNote.getX()+1);
            } else
              if ((this.jalmus.noteLevel.isNormalgame() || this.jalmus.noteLevel.isLearninggame()) &&
                  this.jalmus.noteLevel.isChordsgame()) {
                this.jalmus.currentChord.move(1);
              } else
                if ((this.jalmus.noteLevel.isNormalgame() || this.jalmus.noteLevel.isLearninggame()) &&
                    this.jalmus.noteLevel.isIntervalsgame()) {
                  this.jalmus.currentInterval.move(1);
                } else if (this.jalmus.noteLevel.isInlinegame() &&
                    (this.jalmus.noteLevel.isNotesgame() || this.jalmus.noteLevel.isAccidentalsgame() || this.jalmus.noteLevel.isCustomNotesgame())) {
                  for (int i = 0; i<this.jalmus.line.length; i++) {

                    this.jalmus.line[i].setX(this.jalmus.line[i].getX()-1);
                  }
                } else
                  if (this.jalmus.noteLevel.isInlinegame() && this.jalmus.noteLevel.isChordsgame()) {
                    for (int i = 0; i<this.jalmus.lineacc.length; i++) {
                      this.jalmus.lineacc[i].move(-1);
                    }
                  } else
                    if (this.jalmus.noteLevel.isInlinegame() && this.jalmus.noteLevel.isIntervalsgame()) {
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
              this.jalmus.rhythmCursorXpos = this.jalmus.rhythmCursorXStartPos + ((System.currentTimeMillis()-this.jalmus.timestart)*(this.jalmus.noteDistance*tmpdiv))/(60000/this.jalmus.tempo);
              //System.out.println(rhythmCursorXpos);
            }

            if (this.jalmus.rhythmCursorXpos >= this.jalmus.rhythmCursorXlimit - this.jalmus.notesShift) {
              if (this.jalmus.rhythmAnswerScoreYpos < this.jalmus.scoreYpos + (this.jalmus.rowsDistance * (this.jalmus.numberOfRows - 2))) {
                this.jalmus.rhythmAnswerScoreYpos += this.jalmus.rowsDistance;
                this.jalmus.rhythmCursorXStartPos = this.jalmus.firstNoteXPos - this.jalmus.notesShift;
                this.jalmus.rhythmCursorXpos = this.jalmus.rhythmCursorXStartPos;
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
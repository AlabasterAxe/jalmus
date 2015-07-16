package net.jalmus;

public class NoteReadingGame implements Game {

  NoteLevel noteLevel = new NoteLevel();
  
  boolean gameStarted;
  boolean paused;

  int prevNote;
  int noteCount;

  Score currentScore = new Score();

  @Override
  public void initGame() {
    currentScore.initScore();

    prevNote = 0;
    noteCount = 1;

    paused = false;
  }

  @Override
  public void startGame() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void stopGame() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void showResult() {
    // TODO Auto-generated method stub
    
  }

}

package net.jalmus;

import java.util.ArrayList;

public abstract class RhythmReadingGame implements Game {

  boolean gameStarted;
  boolean paused;
  boolean sameRhythms = false;
  RhythmLevel rhythmLevel = new RhythmLevel();
  protected ArrayList<Rhythm> rhythms = new ArrayList<Rhythm>(); 
  int rhythmIndex = -1;
  protected ArrayList<RhythmAnswer> answers = new ArrayList<RhythmAnswer>();
  protected int precision = 10; //precision on control between note and answer
  boolean cursorstart = false;


  @Override
  public void initGame() {
    // TODO Auto-generated method stub
    
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
  public void startLevel() {
    // TODO Auto-generated method stub
    
  }

  void setTripletValue(int val) {
    rhythms.get(rhythms.size() - 1).setTripletValue(val);
  }

  boolean isBeginMeasure(int i) {
    double d = 0;
    int id = 0;
    for (int j = 0; j < i; j++) {
      //   d += 4.0/rhythms.get(j).getDuration();
      d += rhythms.get(j).getDuration();
    }
    id = (int) Math.round(d); // we should round because of 0.33 triplet need to be fixed

    int tmpnum = rhythmLevel.getTimeSignNumerator();

    boolean reponse = false;
    for (int k = 1; k<tmpnum * 2; k++) {
      if (id == tmpnum*k) {
        reponse = true;
      }
    }
    return reponse;
  }

  @Override
  public void nextGame() {
    // TODO Auto-generated method stub
    
  }
}

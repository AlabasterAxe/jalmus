package net.jalmus;

import java.util.ArrayList;

public abstract class ScoreReadingGame implements Game {

  ScoreLevel scoreLevel = new ScoreLevel();
  boolean gameStarted = false;
  boolean sameRhythms = true;
  int rhythmIndex = -1;
  boolean cursorstart = false;
  protected int precision = 10; //precision on control between note and answer
  protected ArrayList<RhythmAnswer> answers = new ArrayList<RhythmAnswer>();
  protected ArrayList<Rhythm> rhythms = new ArrayList<Rhythm>();

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

  void updateTonality() {
    String stmp;

    // to change tonality when randomly
    if (scoreLevel.getRandomTonality()) {
      int i = (int) Math.round((Math.random()*7));
      double tmp = Math.random();
      if (tmp < 0.1) {
        stmp = "";
      } else if (tmp >= 0.1 && tmp < 0.6) {
        stmp = "#";
      } else {
        stmp = "b";
      }

      scoreLevel.getCurrentTonality().init(i, stmp);
    }

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

    int tmpnum = scoreLevel.getTimeSignNumerator();

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

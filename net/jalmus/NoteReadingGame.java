package net.jalmus;

public abstract class NoteReadingGame implements Game {

  NoteLevel noteLevel = new NoteLevel();

  boolean gameStarted;
  boolean isLessonMode;
  boolean paused;
  boolean alterationOk;

  int prevNote;
  int noteCount;

  int position; // position of the current note in the list
  int posnote = 1; // current position of the note within a chor or an interval

  Note[] line = new Note[40]; // array of notes
  Chord[] lineacc = new Chord[40]; // array of chords
  Interval[] lineint = new Interval[40];

  Note currentNote = new Note(0, 25, 0);
  Chord currentChord = new Chord(currentNote, currentNote, currentNote, "", 0);
  Interval currentInterval = new Interval(currentNote, currentNote, "");

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
  public void startLevel() {
    // nothing
  }

  @Override
  public void stopGame() {
    currentNote = new Note(0, 25, 0);
    currentChord = new Chord(currentNote, currentNote, currentNote, "", 0);
    currentInterval = new Interval(currentNote, currentNote, "");
  }

  void updateTonality() {
    String stmp;

    // to change tonality when randomly
    if ((noteLevel.getRandomTonality())) {
      int i = (int) Math.round((Math.random() * 7));
      double tmp = Math.random();
      if (tmp < 0.1) {
        stmp = "";
      } else if (tmp >= 0.1 && tmp < 0.6) {
        stmp = "#";
      } else {
        stmp = "b";
      }

      noteLevel.getCurrentTonality().init(i, stmp);
    } else if (!isLessonMode && noteLevel.getCurrentTonality().getAlterationsNumber() == 0) {
      // Do Major when tonality is no sharp no flat
      double tmp = Math.random();
      if (tmp < 0.5) {
        stmp = "#";
      } else {
        stmp = "b";
      }
      noteLevel.getCurrentTonality().init(0, stmp);
    }
  }

  @Override
  public void nextGame() {
    // TODO Auto-generated method stub

  }


}

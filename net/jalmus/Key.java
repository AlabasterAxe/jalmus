package net.jalmus;

/**
 * <p>Title: Java Lecture Musicale</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author RICHARD Christophe
 * @version 1.0
 */

import java.awt.Rectangle;

public class Key extends Rectangle {

  private static final long serialVersionUID = 1L;

  private static enum State { ON, OFF };
  private State noteState = State.OFF;
  int kNum;
  int offx; // just for black keys since they have an irregular pattern

  public Key(int x, int y, int width, int height, int num, int offset) {
    super(x, y, width, height);
    kNum = num;
    offx = offset;
  }

  public int getKNum() {
    return this.kNum;
  }
    
  public int getXoffset() {
    return this.offx;
  }

  public boolean isNoteOn() {
    return noteState == State.ON;
  }

  public void turnOn(ChannelData cc, boolean midiok) {
    setNoteState(State.ON);
    cc.playNote(midiok, kNum);
  }

  public void turnOff(ChannelData cc, boolean midiok) {
    setNoteState(State.OFF);
    cc.stopNote(midiok, kNum);
  }

  public void setNoteState(State state) {
    noteState = state;
  }
} // End class Key

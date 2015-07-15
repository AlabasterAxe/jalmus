package net.jalmus;

/**
 * <p>Title: Java Lecture Musicale</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author RICHARD Christophe
 * @version 1.0
 */

import javax.sound.midi.MidiChannel;

public class ChannelData {

  MidiChannel channel;
  boolean solo;
  boolean mono;
  boolean mute;
  boolean sustain;
  int velocity;
  int pressure; 
  int bend;
  int reverb;
  int row;
  int col;
  int num;

  public ChannelData(MidiChannel channel, int num) {
    this.channel = channel;
    this.num = num;
    velocity = pressure = bend = reverb = 64;
  }

  public MidiChannel getChannel() {
    return this.channel;
  }

  public void playNote(boolean midiok, int kNum) {
    playNote(midiok, kNum, 25);
  }
        
  public void playNote(boolean midiok, int kNum, int i) {
    if (midiok) {
      this.channel.noteOn(kNum, i);
    }
  }

  public void stopNote(boolean midiok, int kNum) {
    if (midiok) {
      this.channel.noteOff(kNum, 25);
    }
  }

  public void stopnotes(){
    this.channel.allNotesOff();
  }
}

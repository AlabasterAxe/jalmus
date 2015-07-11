package net.jalmus.domain;

import java.util.SortedMap;
import java.util.TreeMap;

public class Measure {

  SortedMap<Double, Schedulable> notes = new TreeMap<>();
  
  private double tempo;
  private TimeSignature timeSignature;
  private KeySignature keySignature;
  
  private Measure(double tempo, TimeSignature timeSignature,
      KeySignature keySignature) {
    this.tempo = tempo;
    this.timeSignature = timeSignature;
    this.keySignature = keySignature;
  }
  
  public Measure getMeasure(double tempo, TimeSignature timeSignature, KeySignature keySignature) {
    return new Measure(tempo, timeSignature, keySignature);
  }
  
  public double getTempo() {
    return tempo;
  }

  public TimeSignature getTimeSignature() {
    return timeSignature;
  }

  public KeySignature getKeySignature() {
    return keySignature;
  }
}

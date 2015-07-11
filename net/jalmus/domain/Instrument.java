package net.jalmus.domain;

import java.util.List;

public class Instrument {

  private final List<Measure> measures;
  private final Clef clef;

  private Instrument(List<Measure> measures, Clef clef) {
    this.measures = measures;
    this.clef = clef;
  }
  
  public Instrument getInstrument(List<Measure> measures, Clef clef) {
    return new Instrument(measures, clef);
  }
  
  public Iterable<Measure> getMeasures() {
    return measures;
  }
  
  public Clef getClef() {
    return clef;
  }
}

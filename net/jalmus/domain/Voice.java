package net.jalmus.domain;

import java.util.List;

public class Voice {
  private final List<Measure> measures;

  ;
  private final Clef clef;
  public Voice(List<Measure> measures, Clef clef) {
    this.measures = measures;
    this.clef = clef;
  }

  public static enum Clef {TREBLE, BASS}
}

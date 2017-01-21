package net.jalmus.domain;

public class PitchRange {

  private Pitch highPitch;
  private Pitch lowPitch;

  public PitchRange(Pitch p1, Pitch p2) {
    highPitch = (p1.compareTo(p2) > 0) ? p1 : p2;
    lowPitch = (p2.compareTo(p1) < 0) ? p2 : p1;
  }
}

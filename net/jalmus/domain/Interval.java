package net.jalmus.domain;

public class Interval {

  private Pitch highPitch;
  private Pitch lowPitch;

  public Interval(Pitch p1, Pitch p2) {
    highPitch = (p1.compareTo(p2) > 0) ? p1 : p2;
    lowPitch = (p2.compareTo(p1) < 0) ? p2 : p1;
  }

  public Pitch getHighPitch() {
    return highPitch;
  }

  public Pitch getLowPitch() {
    return lowPitch;
  }
}

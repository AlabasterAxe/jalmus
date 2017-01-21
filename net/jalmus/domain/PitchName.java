package net.jalmus.domain;

public enum PitchName {
  C(0), D(2), E(4), F(5), G(7), A(9), B(11);
  
  private final int semitonesAboveC;
  
  private PitchName(int semitones) {
    this.semitonesAboveC = semitones;
  }
  
  public int getSemitonesAboveC() {
    return semitonesAboveC;
  }
}
package net.jalmus.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scale {

  private final ArrayList<Pitch> pitches;
  
  private Scale(Iterable<Pitch> pitches) {
    this.pitches = new ArrayList<>();
    for (Pitch pitch : pitches) {
      this.pitches.add(pitch);
    }
  }
  
  public Scale getScale(Iterable<Pitch> pitches) {
    return new Scale(pitches);
  }
  
  public Iterable<Pitch> getPitchesUp() {
    return pitches;
  }

  public Iterable<Pitch> getPitchesDown() {
    ArrayList<Pitch> reversePitches = (ArrayList<Pitch>) pitches.clone();
    Collections.reverse(reversePitches);
    return reversePitches;
  }
}

package net.jalmus.domain;

import java.util.ArrayList;

/**
 * Class for representing a series of {@link Pitch} objects that can be ascended
 * and descended.
 */
public class Scale {

  public static enum Mode { MAJOR, MINOR }
  
  private final Mode mode;
  private final Pitch root;
  private final ArrayList<Pitch> upPitches;
  private final ArrayList<Pitch> downPitches;
  
  private Scale(Iterable<Pitch> upPitches, Iterable<Pitch> downPitches,
      Mode mode, Pitch root) {
    this.upPitches = new ArrayList<>();
    for (Pitch pitch : upPitches) {
      this.upPitches.add(pitch);
    }

    this.downPitches = new ArrayList<>();
    for (Pitch pitch : downPitches) {
      this.downPitches.add(pitch);
    }
    
    this.mode = mode;
    this.root = root;
  }
  
  /**
   * Symmetric scale static factory method.
   * 
   * This constructs a scale that ascends with the same notes
   * as it descends with. 
   * 
   * @param pitches the series of pitches that make up the scale.
   * @return the Scale object
   */
  public static Scale getScale(Iterable<Pitch> pitches) {
    return new Scale(pitches, pitches, null, null);
  }
  
  public static Scale getScale(Pitch pitch, Mode mode) {
    throw new UnsupportedOperationException();
  }
  
  /**
   * Asymmetric scale static factory method.
   * 
   * This constructs a scale the ascends with different notes
   * than it descends with.
   * 
   * @param upPitches the notes as the scale ascends.
   * @param downPitches the notes as the scale descends.
   * @return the Scale object.
   */
  public static Scale getScale(Iterable<Pitch> upPitches, Iterable<Pitch> downPitches) {
    return new Scale(upPitches, downPitches, null, null);
  }
  
  /**
   * The pitches that make up the scale as it ascends.
   * 
   * @return the {@link Iterable} of the {@link Pitch} objects.
   */
  public Iterable<Pitch> getPitchesUp() {
    return upPitches;
  }

  /**
   * The pitches that make up the scale as it descends.
   * 
   * @return the {@link Iterable} of the {@link Pitch} objects.
   */
  public Iterable<Pitch> getPitchesDown() {
    return downPitches;
  }
}

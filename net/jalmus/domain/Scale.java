package net.jalmus.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for representing a series of {@link Pitch} objects that can be ascended
 * and descended.
 */
public class Scale {

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
   * <p>
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
    List<Pitch> pitches = new ArrayList<>();
    pitches.add(pitch);

    int i = 0;
    for (int tone : mode.getTones()) {
      int minOrdinalDistance = -1;
      Pitch closestPitch = null;
      int previousPitchOrdinal = pitches.get(i).getName().ordinal();
      for (Pitch possiblePitch : pitch.getSemitoneSum(tone)) {
        // TODO: find a better way to pick amongst the possible notes.
        int possiblePitchOrdinal = possiblePitch.getName().ordinal();
        int ordinalDistance = Math.abs(previousPitchOrdinal - possiblePitchOrdinal);
        if (previousPitchOrdinal != possiblePitchOrdinal
            && (minOrdinalDistance == -1 || ordinalDistance < minOrdinalDistance)) {
          minOrdinalDistance = ordinalDistance;
          closestPitch = possiblePitch;
        }
      }
      if (closestPitch == null) {
        throw new AssertionError("Shit's fucked up.");
      }
      pitches.add(closestPitch);
      i++;
    }
    return getScale(pitches);
  }

  /**
   * Asymmetric scale static factory method.
   * <p>
   * This constructs a scale the ascends with different notes
   * than it descends with.
   *
   * @param upPitches   the notes as the scale ascends.
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

  public static enum Mode {
    MAJOR(2, 4, 5, 7, 9, 11, 12),
    MINOR(2, 3, 5, 7, 8, 10, 12),
    HARMONIC_MINOR(2, 3, 5, 7, 8, 11, 12);

    private ArrayList<Integer> tones;

    private Mode(int... semitones) {
      for (int semitone : semitones) {
        tones.add(semitone);
      }
    }

    public ArrayList<Integer> getTones() {
      return tones;
    }

    public Integer getTones(int integer) {
      return tones.get(integer);
    }
  }
}

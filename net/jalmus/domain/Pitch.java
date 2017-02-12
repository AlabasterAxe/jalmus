package net.jalmus.domain;

import java.util.ArrayList;
import java.util.List;

/*
 * The Pitch class is used to contain frequency 
 * information in Jalmus. This class is the
 * western scale baked in but it could easily be refactored
 * to include different semitone names and numbers.
 */
public final class Pitch implements Comparable<Pitch> {

  private static final int SEMITONES_PER_OCTAVE = 12;
  private static final Pitch REFERENCE_PITCH = new Pitch(Name.A, 4, Modifier.NONE);

  private static final int REFERENCE_FREQUENCY = 440;
  private final Name name;
  private final int octave;
  private final Modifier modifier;

  private Pitch(Name name, int octave, Modifier modifier) {
    this.name = name;
    this.octave = octave;
    this.modifier = modifier;
  }

  public static double getSemitoneFactor() {
    return Math.pow(2, (1 / (double) SEMITONES_PER_OCTAVE));
  }

  public static Pitch getPitch(Name name, int octave) {
    return getPitch(name, octave, Modifier.NONE);
  }

  public static Pitch getPitch(Name name, int octave, Modifier modifier) {
    return new Pitch(name, octave, modifier);
  }

  public static Pitch getPitchFromFrequency(double frequency) {
    throw new UnsupportedOperationException();
  }

  public static Pitch getUnmodifiedPitchFromAbsoluteSemitones(int semitone) {
    int octave = semitone / SEMITONES_PER_OCTAVE;
    Name name = Name.getNameFromSemitonesAboveC(semitone % SEMITONES_PER_OCTAVE);

    if (name != null) {
      return getPitch(name, octave);
    } else {
      return null;
    }
  }

  /**
   * This converts semitones into a set of pitches that
   *
   * @param semitone
   * @return
   */
  public static List<Pitch> getPitchesFromAbsoluteSemitones(int semitone) {
    List<Pitch> result = new ArrayList<>();
    for (Modifier modifier : Modifier.values()) {
      Pitch pitch = getUnmodifiedPitchFromAbsoluteSemitones(semitone - modifier.getModification());
      if (pitch != null) {
        result.add(new Pitch(pitch.getName(), pitch.getOctave(), modifier));
      }
    }
    return result;
  }

  public Name getName() {
    return name;
  }

  public int getOctave() {
    return octave;
  }

  public Modifier getModifier() {
    return modifier;
  }

  public int getAbsoluteSemitones() {
    return this.octave * SEMITONES_PER_OCTAVE
        + this.name.getSemitonesAboveC()
        + this.modifier.getModification();
  }

  public int getSemitoneDifference(Pitch p) {
    return this.getAbsoluteSemitones() - p.getAbsoluteSemitones();
  }

  public Iterable<Pitch> getSemitoneSum(int addend) {
    return getPitchesFromAbsoluteSemitones(getAbsoluteSemitones() + addend);
  }

  public double getFrequency() {
    int semitoneDifference = getSemitoneDifference(REFERENCE_PITCH);
    return REFERENCE_FREQUENCY * Math.pow(getSemitoneFactor(), semitoneDifference);
  }

  @Override
  public int compareTo(Pitch pitch) {
    return getSemitoneDifference(pitch);
  }

  public static enum Name {
    C(0), D(2), E(4), F(5), G(7), A(9), B(11);

    private final int semitonesAboveC;

    Name(int semitones) {
      this.semitonesAboveC = semitones;
    }

    public static Name getNameFromSemitonesAboveC(int semitones) {
      Name[] names = values();
      for (Name name : names) {
        if (name.getSemitonesAboveC() == semitones) {
          return name;
        }
      }
      return null;
    }

    public int getSemitonesAboveC() {
      return semitonesAboveC;
    }
  }

  public static enum Modifier {
    DOUBLE_FLAT(-2),
    FLAT(-1),
    NONE(0),
    SHARP(1),
    DOUBLE_SHARP(2);

    private final int modification;

    Modifier(int modification) {
      this.modification = modification;
    }

    public int getModification() {
      return modification;
    }
  }
}

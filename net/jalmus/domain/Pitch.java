package net.jalmus.domain;

/*
 * The Pitch class is used to contain frequency 
 * information in Jalmus. This class is the
 * western scale baked in but it could easily be refactored
 * to include different semitone names and numbers.
 */
public final class Pitch implements Comparable<Pitch> {
  public enum LetterName {
    C(0), D(2), E(4), F(5), G(7), A(9), B(11);
    
    private final int semitonesAboveC;
    
    private LetterName(int semitones) {
      this.semitonesAboveC = semitones;
    }
    
    public int getSemitonesAboveC() {
      return semitonesAboveC;
    }
  };

  public enum Modifier {
    DOUBLE_FLAT(-2), 
    FLAT(-1), 
    NONE(0), 
    SHARP(1), 
    DOUBLE_SHARP(2);
    
    private final int modification;

    private Modifier(int modification) {
      this.modification = modification;
    }
    
    public int getModification() {
      return modification;
    }
  };
  
  private static final int SEMITONES_PER_OCTAVE = 12;
  private static final Pitch REFERENCE_PITCH = new Pitch(LetterName.A, 4, Modifier.NONE);
  private static final int REFERENCE_FREQUENCY = 440;
  
  public static double getSemitoneFactor() {
    return Math.pow(2, (1/(double) SEMITONES_PER_OCTAVE));
  }
  
  private final LetterName name;
  private final int octave;
  private final Modifier modifier;
	
  private Pitch(LetterName name, int octave, Modifier modifier) {
    this.name = name;
    this.octave = octave;
    this.modifier = modifier;
  }
	
  public Pitch getPitch(LetterName name, int octave, Modifier modifier) {
    return new Pitch(name, octave, modifier);
  }
  
  public Pitch getPitchFromFrequency(double frequency) {
    throw new UnsupportedOperationException();
  }
	
  public LetterName getName() {
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
  
  public double getFrequency() {
    int semitoneDifference = getSemitoneDifference(REFERENCE_PITCH);
    return REFERENCE_FREQUENCY * Math.pow(getSemitoneFactor(), semitoneDifference);
  }

  @Override
  public int compareTo(Pitch pitch) {
    return getSemitoneDifference(pitch);
  }
}

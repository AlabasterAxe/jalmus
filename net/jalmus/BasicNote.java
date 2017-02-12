package net.jalmus;

public final class BasicNote {
  private final LetterName name;

  ;
  private final int octave;

  ;
  private final Modifier modifier;

  private BasicNote(LetterName name, int octave, Modifier modifier) {
    this.name = name;
    this.octave = octave;
    this.modifier = modifier;
  }

  public static BasicNote getNoteFromFrequency(double frequency) {
    throw new UnsupportedOperationException();
  }

  public BasicNote getNote(LetterName name, int octave, Modifier modifier) {
    return new BasicNote(name, octave, modifier);
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

  public double getFrequency() {
    throw new UnsupportedOperationException();
  }

  public enum LetterName {
    A, B, C, D, E, F, G
  }

  public enum Modifier {
    NONE, FLAT, SHARP, DOUBLE_FLAT, DOUBLE_SHARP, NATURAL
  }
}

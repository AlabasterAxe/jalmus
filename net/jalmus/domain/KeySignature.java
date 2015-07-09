package net.jalmus.domain;

import java.util.Set;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import net.jalmus.domain.Pitch.LetterName;;

public class KeySignature {

  // TODO: Make this configurable via a KeySignature factory.
  // TODO: Represent this with some sort of a cyclic data structure.
  private static final Set<LetterName> modificationOrder = new LinkedHashSet<LetterName>();

  private static final Map<LetterName, Integer> flatTrebleAccidentalPitches = new HashMap<LetterName, Integer>();
  private static final Map<LetterName, Integer> flatBassAccidentalPitches = new HashMap<LetterName, Integer>();
  private static final Map<LetterName, Integer> sharpTrebleAccidentalPitches = new HashMap<LetterName, Integer>();
  private static final Map<LetterName, Integer> sharpBassAccidentalPitches = new HashMap<LetterName, Integer>();

  static {
    modificationOrder.add(LetterName.B);
    flatTrebleAccidentalPitches.put(LetterName.B, 4);
    flatBassAccidentalPitches.put(LetterName.B, 2);

    sharpTrebleAccidentalPitches.put(LetterName.B, 4);
    sharpBassAccidentalPitches.put(LetterName.B, 2);
    
    modificationOrder.add(LetterName.E);
    modificationOrder.add(LetterName.A);
    modificationOrder.add(LetterName.D);
    modificationOrder.add(LetterName.G);
    modificationOrder.add(LetterName.C);
    modificationOrder.add(LetterName.F);
  }

  // The direction around the key signature
  // spiral to go.
  private static enum Direction{ FLAT, SHARP }

  private final int magnitude;
  private final Direction direction;
  
  private KeySignature(int magnitude, Direction direction) {
    this.magnitude = magnitude;
    this.direction = direction;
  }
  
  public static KeySignature getKeySignature(int magnitude, Direction direction) {
    return new KeySignature(magnitude, direction);
  }
  
  public Iterable<Pitch> getTrebleAccidentals() {
    return null;
  }
  
  public Iterable<Pitch> getBassAccidentals() {
    return null;
  }
  
  public Scale getMajorScale(int octave) {
    return null;
  }
  
  public Scale getMinorScale(int octave) {
    return null;
  }
}

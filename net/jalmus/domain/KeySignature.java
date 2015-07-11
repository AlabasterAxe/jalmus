package net.jalmus.domain;

import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import net.jalmus.util.Cycle;

public class KeySignature {

  // TODO: Make this configurable via a KeySignature factory.
  // TODO: Represent this with some sort of a cyclic data structure.
  private static final Cycle<Pitch.Name> modificationOrder;

  static {
    modificationOrder = new Cycle<Pitch.Name>();
    modificationOrder.add(Pitch.Name.B);
    modificationOrder.add(Pitch.Name.E);
    modificationOrder.add(Pitch.Name.A);
    modificationOrder.add(Pitch.Name.D);
    modificationOrder.add(Pitch.Name.G);
    modificationOrder.add(Pitch.Name.C);
    modificationOrder.add(Pitch.Name.F);
  }

  // The direction around the key signature
  // spiral to go.

  private final List<Pitch.Name> modifications;
  private final Pitch.Modifier direction;
  
  private KeySignature(List<Pitch.Name> modifications, Pitch.Modifier direction) {
    this.modifications = modifications;
    this.direction = direction;
  }
  
  public static KeySignature getKeySignature(int magnitude, Pitch.Modifier direction) {
    List<Pitch.Name> modifications = new ArrayList<>();

    Iterator<Pitch.Name> iter;
    switch (direction) {
      case FLAT:
        iter = modificationOrder.iterator();
        break;
      case SHARP:
        iter = modificationOrder.reverseIterator();
        break;
      default:
        throw new AssertionError();
    }

    while(magnitude > 0) {
      modifications.add(iter.next());
      magnitude--;
    }

    return new KeySignature(modifications, direction);
  }
  
  public Scale getScale(int octave, Scale.Mode mode) {
    Pitch.Name pitchName = modificationOrder.get(modifications.size());
    
    Pitch.Modifier baseModifier;
    if(modifications.indexOf(pitchName) < 0) {
      baseModifier = direction;
    } else {
      baseModifier = Pitch.Modifier.NONE;
    }
    
    Pitch pitch = Pitch.getPitch(pitchName, octave, baseModifier);

    return Scale.getScale(pitch, mode);
  }
}

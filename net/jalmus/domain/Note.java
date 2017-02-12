package net.jalmus.domain;

/*
 * The note class is the association of a frequency
 * or a pitch with a duration in beats.
 */
public class Note implements Schedulable {

  private final Pitch pitch;

  private final double beats;

  private Note(Pitch pitch, double beats) {
    this.pitch = pitch;
    this.beats = beats;
  }

  public Note getNote(Pitch pitch, double beats) {
    return new Note(pitch, beats);
  }

  public Pitch getPitch() {
    return pitch;
  }

  public double getLengthInBeats() {
    return beats;
  }

  public double getLengthInSeconds(double tempo) {
    return this.beats / tempo;
  }
}

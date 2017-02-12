package net.jalmus.domain;

import java.util.ArrayList;
import java.util.List;

/*
 * The Chord class is used to group a collection
 * of Notes that all start at the same time.
 * 
 * Apologies if this offends peoples sensibilities
 * but this Chord class allows any number of Notes
 * greater than 1, so even though technically two
 * notes at the same time is not a chord as far as
 * music theory chord is concerned, it can, and 
 * should, be represented with this Chord class,
 * assuming that the pitches have durations.
 */
public class Chord implements Schedulable {

  private final List<Note> notes;

  private Chord(List<Note> notes) {
    this.notes = notes;
  }

  public Iterable<Note> getNotes() {
    return notes;
  }

  public class Builder {
    private final List<Note> notes;

    public Builder() {
      this.notes = new ArrayList<>();
    }

    public Builder addNote(Note note) {
      notes.add(note);
      return this;
    }

    public Chord build() {
      return new Chord(this.notes);
    }
  }
}

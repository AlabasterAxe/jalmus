package net.jalmus.domain;

import java.util.Collection;
import java.util.List;

public class Score {
  private final List<Instrument> instruments;
  private final String title;
  
  private Score(List<Instrument> instruments, String title) {
    this.instruments = instruments;
    this.title = title;
  }
  
  public Score getScore(List<Instrument> instruments, String title) {
    return new Score(instruments, title);
  }
  
  public Collection<Instrument> getInstruments() {
    return instruments;
  }
  
  public String getTitle() {
    return title;
  }
}

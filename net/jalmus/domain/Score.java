package net.jalmus.domain;

import java.util.List;

public class Score {
  private final List<Voice> voices;
  private final String title;
  
  public Score(List<Voice> voices, String title) {
    this.voices = voices;
    this.title = title;
  }
}

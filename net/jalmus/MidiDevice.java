package net.jalmus;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class MidiDevice {
  static final int PULSES_PER_QUARTER_NOTE = 1000;
  Sequence sequence;
  Sequencer sm_sequencer;
}

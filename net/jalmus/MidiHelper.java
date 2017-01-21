package net.jalmus;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

public class MidiHelper {
  
  static final int PULSES_PER_QUARTER_NOTE = 1000;
  Sequence sequence;
  Sequencer smSequencer;
  long latency; // synthesizer latency
  boolean midierror;
  
  MidiDevice inputDevice;
  MidiDevice outputDevice = null;
  private Synthesizer syn;
  Instrument[] instruments;
  int noteDuration = 2000;
  ChannelData currentChannel; // current channel
  boolean open;

  boolean initialize() {
    try {
      if (syn == null) {
        if ((syn = MidiSystem.getSynthesizer())==null) {
          System.out.println("getSynthesizer() failed!");

          return false;
        }
      }
      syn.open();
    } catch (MidiUnavailableException e) {
      System.out.println("Midiunavailable : sortie MIDI occupee - fermez toutes les autres applications pour avoir du son. "+e);
      midierror = true;
    }

    if (!midierror) {
      Soundbank sb = syn.getDefaultSoundbank();
      if (sb != null) {
        instruments = syn.getDefaultSoundbank().getInstruments();

        if (instruments != null) {
          syn.loadInstrument(instruments[0]);
        } else {
          midierror = true;
          System.out.println("Soundbank null");
        }
      }

      MidiChannel[] mc = syn.getChannels();

      ChannelData[] channels = new ChannelData[mc.length];
      for (int i = 0; i < channels.length; i++) {
        channels[i] = new ChannelData(mc[i], i);
      }
      currentChannel = channels[0];
    }
    return true;
  }

  void synthNote(int nNoteNumber, int nVelocity, int nDuration) {
    currentChannel.playNote(!midierror, nNoteNumber);
  }

  void addEvent(Track track, int type, byte[] data, long tick) {
    MetaMessage message = new MetaMessage();
    try {
      message.setMessage(type, data, data.length);
      MidiEvent event = new MidiEvent(message, tick);
      track.add(event);
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    }
  }

  static MidiEvent createNoteOnEvent(int nKey, int velocity, long lTick) {
    return createNoteEvent(ShortMessage.NOTE_ON, nKey, velocity, lTick);
  }

  static MidiEvent createNoteOffEvent(int nKey, long lTick) {
    return createNoteEvent(ShortMessage.NOTE_OFF, nKey, 0, lTick);
  }

  static MidiEvent createNoteEvent(int nCommand, int nKey, int nVelocity, long lTick) {
    ShortMessage message = new ShortMessage();
    try {
      // always on channel 1
      message.setMessage(nCommand, 0, nKey, nVelocity);
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return new MidiEvent(message, lTick);
  }
  
  void stopSound() {
    currentChannel.stopnotes();
  }
}

package net.jalmus;

import javax.sound.midi.*;

class DumpReceiver implements Receiver {

  /**
   *
   */
  private final SwingJalmus ui;

  public DumpReceiver(SwingJalmus jalmus) {
    this.ui = jalmus;
  }

  public void send(MidiMessage event, long time) {


    if (ui.midiHelper.outputDevice != null) {
      try {
        ui.midiHelper.outputDevice.getReceiver().send(event, time);
      } catch (MidiUnavailableException e) {
        e.printStackTrace();
      }
    }

    String output = "";
    if (ui.jalmus.selectedGame == Jalmus.NOTEREADING ||
        ui.jalmus.selectedGame == Jalmus.RHYTHMREADING ||
        ui.jalmus.selectedGame == Jalmus.SCOREREADING) {

      if (event instanceof ShortMessage) {
        if (!ui.midiHelper.open) {
          ui.midiHelper.open = true;
        }

        switch (event.getStatus() & 0xf0) {
          case 0x90:
            output = ("   Note On Key: " + ((ShortMessage) event).getData1() +
                " Velocity: " + ((ShortMessage) event).getData2());
            //pitch de la note jouï¿½e
            int notejouee = ((ShortMessage) event).getData1() +
                ((Number) ui.transpositionSpinner.getValue()).intValue();

            if (ui.jalmus.selectedGame == Jalmus.NOTEREADING) {
              if (!ui.jalmus.gameStarted && (((ShortMessage) event).getData2() != 0) &&
                  ((ShortMessage) event).getData1() == 60) {
                System.out.println("C3");
                if (ui.levelMessage.isVisible()) {
                  System.out.println("levelmessage");
                  ui.oklevelMessage.doClick();
                } else if (ui.scoreMessage.isVisible()) {
                  ui.okscoreMessage.doClick();
                } else {
                  ui.requestFocus();
                  ui.jalmus.startNoteGame();
                  if (!ui.renderingThread.isAlive()) {
                    ui.renderingThread.start();
                  }
                }
              } else {
                if (ui.keyboardsoundCheckBox.isSelected()) {
                  if (((ShortMessage) event).getData2() != 0) {
                    ui.jalmus.piano.playNote(ui.midiHelper.currentChannel,
                        !ui.midiHelper.midierror, notejouee, 1);
                  } else {
                    ui.jalmus.piano.playNote(ui.midiHelper.currentChannel,
                        !ui.midiHelper.midierror, notejouee, 0);
                  }
                }

                ui.repaint();

                if (((ShortMessage) event).getData2() != 0 && ui.jalmus.gameStarted &&
                    !ui.jalmus.paused) {
                  if (SwingNote.samePitch(((ShortMessage) event).getData1(),
                      ui.jalmus.noteGame.currentNote.getPitch(),
                      (int) ui.transpositionSpinner.getValue())) {
                    ui.noteGame.rightAnswer();
                  } else {
                    System.out.println("Input:" + ((ShortMessage) event).getData1() +
                        " Correct note:" + ui.jalmus.noteGame.currentNote.getPitch());
                    ui.noteGame.wrongAnswer();
                  }
                  ui.repaint();
                }
              }
            }

            if (ui.jalmus.selectedGame == Jalmus.RHYTHMREADING && ui.jalmus.gameStarted) {
              if (((ShortMessage) event).getData2() != 0) {
                ui.rhythmGame.rhythmKeyPressed(71);
              } else {
                ui.rhythmGame.rhythmKeyReleased(71);
              }
            }

            if (ui.jalmus.selectedGame == Jalmus.SCOREREADING && ui.jalmus.gameStarted) {
              if (((ShortMessage) event).getData2() != 0) {
                ui.rhythmGame.rhythmKeyPressed(((ShortMessage) event).getData1());
              } else {
                ui.rhythmGame.rhythmKeyReleased(((ShortMessage) event).getData1());
              }
            }

            break;
          case 0x80:
            output = ("   Note Off  Key: " + ((ShortMessage) event).getData1() +
                " Velocity: " + ((ShortMessage) event).getData2());
            break;
          case 0xb0:
            if (((ShortMessage) event).getData1() < 120) {
              output = ("   Controller No.: " +
                  ((ShortMessage) event).getData1() +
                  " Value: " + ((ShortMessage) event).getData2());
            } else {
              output = ("   ChannelMode Message No.: " +
                  ((ShortMessage) event).getData1() + " Value: " +
                  ((ShortMessage) event).getData2());
            }
            break;
          case 0xe0:
            output = ("   pitch lsb: " + ((ShortMessage) event).getData1() +
                " msb: " + ((ShortMessage) event).getData2());
            break;
          case 0xc0:
            output = ("   Program Change No: " +
                ((ShortMessage) event).getData1() +
                " Just for Test: " + ((ShortMessage) event).getData2());
            break;
          case 0xd0:
            output = ("   Channel Aftertouch Pressure: " +
                ((ShortMessage) event).getData1() + " Just for Test: " +
                ((ShortMessage) event).getData2());
            break;
        }
      } else if (event instanceof SysexMessage) {
        output = ("   SysexMessage: " + (event.getStatus() - 256));
        byte[] data = ((SysexMessage) event).getData();
        for (int x = 0; x < data.length; x++) {
          output = (" " + Integer.toHexString(data[x]));
        }
      } else {
        output = ("   MetaEvent");
      }
      if (output != "") {
        System.out.println(output);
      }
    }
  }

  public void close() {
  }
}
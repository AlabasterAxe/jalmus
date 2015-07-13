package net.jalmus;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

class DumpReceiver implements Receiver {

  /**
	 * 
	 */
	private final Jalmus jalmus;
	public DumpReceiver(Jalmus jalmus) {
		this.jalmus = jalmus;

    }

    public void send(MidiMessage event, long time) {

      String output = "";

      if (this.jalmus.outputDevice != null)
      {
        try {
          this.jalmus.outputDevice.getReceiver().send(event, time);
        } catch (MidiUnavailableException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      if (this.jalmus.selectedGame == Jalmus.NOTEREADING || this.jalmus.selectedGame==Jalmus.RHYTHMREADING || this.jalmus.selectedGame==Jalmus.SCOREREADING) {

        if (event instanceof ShortMessage) {
          if (!this.jalmus.open) {
            this.jalmus.open = true;

          }
          switch (event.getStatus()&0xf0) {
            case 0x90:
              output = ("   Note On Key: "+((ShortMessage)event).getData1()+
                  " Velocity: "+((ShortMessage)event).getData2());
              //pitch de la note jouï¿½e
              int notejouee = ((ShortMessage)event).getData1()+((Number)this.jalmus.ui.transpositionSpinner.getValue()).intValue();

              //System.out.println(((ShortMessage)event).getData2());

              // touche C3 pour lancer le jeu au clavier

              if (this.jalmus.selectedGame == Jalmus.NOTEREADING) {

                if (!this.jalmus.gameStarted & (((ShortMessage)event).getData2() != 0) & ((ShortMessage)event).getData1() == 60) {
                  System.out.println("C3");
                  if (this.jalmus.ui.levelMessage.isVisible()) {
                    System.out.println("levelmessage");

                    this.jalmus.ui.oklevelMessage.doClick();
                  } else if (this.jalmus.ui.scoreMessage.isVisible()) {

                    this.jalmus.ui.okscoreMessage.doClick();

                  } else {
                    this.jalmus.ui.requestFocus();
                    this.jalmus.startNoteGame();
                    if (!this.jalmus.renderingThread.isAlive()) {
                      this.jalmus.renderingThread.start();
                    }
                  }
                } else {
                  if (this.jalmus.ui.keyboardsoundCheckBox.isSelected()) {
                    if (((ShortMessage)event).getData2() != 0) {
                      this.jalmus.piano.playNote(this.jalmus.currentChannel, !this.jalmus.midierror, notejouee, 1);
                    } else {
                      this.jalmus.piano.playNote(this.jalmus.currentChannel, !this.jalmus.midierror, notejouee, 0);
                    }
                  }

                  this.jalmus.ui.repaint();

                  if (((ShortMessage)event).getData2() != 0&this.jalmus.gameStarted&!this.jalmus.paused) {
                    //  System.out.print(((ShortMessage)event).getData1());
                    //  System.out.println("-"+ncourante.getPitch());

                    if (this.jalmus.isSameNote(((ShortMessage)event).getData1(), this.jalmus.ncourante.getPitch())) {
                      this.jalmus.rightAnswer();
                                    } else {
                        System.out.println("Input:" + ((ShortMessage)event).getData1() +" Correct note:" + this.jalmus.ncourante.getPitch() );
                        this.jalmus.wrongAnswer();
                      }

                    this.jalmus.ui.repaint();
                  }
                }
              }

              if (this.jalmus.selectedGame == Jalmus.RHYTHMREADING && this.jalmus.gameStarted) {
                if (((ShortMessage)event).getData2() != 0)
                  this.jalmus.rhythmKeyPressed(71);
                else  {
                  this.jalmus.rhythmKeyReleased(71);
                  // System.out.println ("released");
                }

              }

              if (this.jalmus.selectedGame == Jalmus.SCOREREADING && this.jalmus.gameStarted) {
                if (((ShortMessage)event).getData2() != 0)
                  this.jalmus.rhythmKeyPressed(((ShortMessage)event).getData1());
                else  {
                  this.jalmus.rhythmKeyReleased(((ShortMessage)event).getData1());
                  //  System.out.println ("released");
                }

              }

              break;
            case 0x80:
              output = ("   Note Off  Key: "+((ShortMessage)event).getData1()+
                  " Velocity: "+((ShortMessage)event).getData2());
              break;
            case 0xb0:
              if (((ShortMessage)event).getData1()<120) {
                output = ("   Controller No.: "+
                    ((ShortMessage)event).getData1()+
                    " Value: "+((ShortMessage)event).getData2());
              } else {
                output = ("   ChannelMode Message No.: "+
                    ((ShortMessage)event).getData1()+" Value: "+
                    ((ShortMessage)event).getData2());
              }
              break;
            case 0xe0:
              output = ("   Pitch lsb: "+((ShortMessage)event).getData1()+
                  " msb: "+((ShortMessage)event).getData2());
              break;
            case 0xc0:
              output = ("   Program Change No: "+
                  ((ShortMessage)event).getData1()+
                  " Just for Test: "+((ShortMessage)event).getData2());
              break;
            case 0xd0:
              output = ("   Channel Aftertouch Pressure: "+
                  ((ShortMessage)event).getData1()+" Just for Test: "+
                  ((ShortMessage)event).getData2());
              break;
          }
        } else if (event instanceof SysexMessage) {
          output = ("   SysexMessage: "+(event.getStatus()-256));
          byte[] data = ((SysexMessage)event).getData();
          for (int x = 0; x<data.length; x++) {
            output = (" "+Integer.toHexString(data[x]));
          }
        } else {
          output = ("   MetaEvent");
        }
        if (output != "") {
                    System.out.println(output);
                }
      }
    }
    public void close() {}
  }
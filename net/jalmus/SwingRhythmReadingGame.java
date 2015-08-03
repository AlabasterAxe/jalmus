package net.jalmus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SwingRhythmReadingGame extends RhythmReadingGame implements SwingGame {

  private SwingJalmus ui;
  
  JComboBox<String> gameTypeComboBox;
  JComboBox<String> speedComboBox;
    
  JCheckBox wholeCheckBox;
  JCheckBox halfCheckBox;
  JCheckBox dottedhalfCheckBox;
  JCheckBox quarterCheckBox;
  JCheckBox eighthCheckBox;
  JCheckBox restCheckBox;
  JCheckBox tripletCheckBox;
    
  private JLabel timeSignLabel;
  private JComboBox<String> timeSignComboBox;

  JCheckBox metronomeCheckBox;
  JCheckBox metronomeShowCheckBox;
  
  private Track track;
  private Track mutetrack;
  Track metronome;

	void setUi(SwingJalmus ui) {
    this.ui = ui;
	}
	
	@Override
	public JPanel getPreferencesPanel() {
	    /* 1st panel - type of game */

    gameTypeComboBox = new JComboBox<String>();
    gameTypeComboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        // TODO: Remove this?
      }
      
    });

    speedComboBox = new JComboBox<String>();
    speedComboBox.addItem("Largo");
    speedComboBox.addItem("Adagio");
    speedComboBox.addItem("Moderato");
    speedComboBox.addItem("Allegro");
    speedComboBox.addItem("Presto");
    speedComboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        // TODO: Turn this into a JSpinner.
        if (speedComboBox.getSelectedIndex() == 0) {
          rhythmLevel.setSpeed(40);
        } else if (speedComboBox.getSelectedIndex() == 1) {
          rhythmLevel.setSpeed(60);
        } else if (speedComboBox.getSelectedIndex() == 2) {
          rhythmLevel.setSpeed(100);
        } else if (speedComboBox.getSelectedIndex() == 3) {
          rhythmLevel.setSpeed(120);
        } else if (speedComboBox.getSelectedIndex() == 4) {
          rhythmLevel.setSpeed(160);
        } 
      }
    });

    JPanel gamePanel = new JPanel();
    gamePanel.add(gameTypeComboBox);
    gamePanel.add(speedComboBox);
    ui.localizables.add(new Localizable.NamedGroup(gamePanel, "_menuExercises"));

	    /* 2nd panel - RHYTHM */

    wholeCheckBox = new JCheckBox("", true);
    wholeCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        rhythmLevel.setWholeNote(wholeCheckBox.isSelected());
      }
    });

    halfCheckBox = new JCheckBox("", true);
    halfCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        rhythmLevel.setHalfNote(halfCheckBox.isSelected());
      }
    }); 

    dottedhalfCheckBox = new JCheckBox("", false);
    dottedhalfCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        rhythmLevel.setDottedHalfNote(dottedhalfCheckBox.isSelected());
      }
    });

    quarterCheckBox = new JCheckBox("", false);
    quarterCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        rhythmLevel.setQuarterNote(quarterCheckBox.isSelected());
      }
    });

    eighthCheckBox = new JCheckBox("", false);
    eighthCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        rhythmLevel.setEighthNote(eighthCheckBox.isSelected());
      }
    });

    restCheckBox = new JCheckBox("", true);
    restCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        rhythmLevel.setSilence(restCheckBox.isSelected());
      }
    });

    tripletCheckBox = new JCheckBox("", false);
    tripletCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        rhythmLevel.setTriplet(tripletCheckBox.isSelected());
      }
    });

    timeSignComboBox = new JComboBox<String>();
    timeSignComboBox.setPreferredSize(new Dimension(100, 25));
    timeSignComboBox.addItem("4/4");
    timeSignComboBox.addItem("3/4");
    timeSignComboBox.addItem("2/4");
    timeSignComboBox.addItem("6/8");
    timeSignComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        // TODO: try to test equality with timeSignComboBox
        if (e.getSource() instanceof JComboBox<?>) {
          JComboBox<?> cb = (JComboBox<?>)e.getSource();
          int sel = cb.getSelectedIndex();
          System.out.println("Rhythm time signature changed. Selected: "+ sel);
          switch (sel) {
            case 0:
              wholeCheckBox.setEnabled(true);
              wholeCheckBox.setSelected(true);
              quarterCheckBox.setSelected(true);
              dottedhalfCheckBox.setSelected(false);
              dottedhalfCheckBox.setEnabled(true);
              rhythmLevel.setTimeSignNumerator(4);
              rhythmLevel.setTimeSignDenominator(4);
              rhythmLevel.setTimeDivision(1);
              break;
            case 1:
              wholeCheckBox.setSelected(false);
              wholeCheckBox.setEnabled(false);
              dottedhalfCheckBox.setSelected(true);
              dottedhalfCheckBox.setEnabled(true);
              quarterCheckBox.setSelected(true);
              rhythmLevel.setTimeSignNumerator(3);
              rhythmLevel.setTimeSignDenominator(4);
              rhythmLevel.setTimeDivision(1);
              break;
            case 2:
              wholeCheckBox.setSelected(false);
              wholeCheckBox.setEnabled(false);
              dottedhalfCheckBox.setSelected(false);
              dottedhalfCheckBox.setEnabled(false);
              quarterCheckBox.setSelected(true);
              rhythmLevel.setTimeSignNumerator(2);
              rhythmLevel.setTimeSignDenominator(4);
              rhythmLevel.setTimeDivision(1);
              break;
            case 3:
              wholeCheckBox.setSelected(false);
              wholeCheckBox.setEnabled(false);
              dottedhalfCheckBox.setSelected(false);
              dottedhalfCheckBox.setEnabled(false);
              quarterCheckBox.setSelected(true);
              rhythmLevel.setTimeSignNumerator(6);
              rhythmLevel.setTimeSignDenominator(8);
              rhythmLevel.setTimeDivision(2);
              break;
            default:
              throw new AssertionError();
          }
        }
      }
    });

    JPanel rhythmsPanel = new JPanel();
    rhythmsPanel.add(wholeCheckBox);
    rhythmsPanel.add(dottedhalfCheckBox);
    rhythmsPanel.add(halfCheckBox);
    rhythmsPanel.add(quarterCheckBox);
    rhythmsPanel.add(eighthCheckBox);
    rhythmsPanel.add(restCheckBox);
    rhythmsPanel.add(tripletCheckBox);

    JPanel timeSignPanel = new JPanel();
    timeSignLabel = new JLabel();
    timeSignPanel.add(timeSignLabel);
    timeSignPanel.add(timeSignComboBox);

    JPanel rhythmAndTimePanel = new JPanel();
    rhythmAndTimePanel.setLayout(new BorderLayout());
    rhythmAndTimePanel.add(timeSignPanel, BorderLayout.NORTH);
    rhythmAndTimePanel.add(rhythmsPanel, BorderLayout.CENTER);
    ui.localizables.add(new Localizable.NamedGroup(rhythmAndTimePanel, "_menuRythms"));

    /* 3rd panel - metronome */

    metronomeCheckBox = new JCheckBox("", true);
    metronomeCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        rhythmLevel.setMetronome(metronomeCheckBox.isSelected());
      }
    });

    metronomeShowCheckBox = new JCheckBox("", true);
    metronomeShowCheckBox.setSelected(false);
    metronomeShowCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        rhythmLevel.setMetronomeBeats(metronomeShowCheckBox.isSelected());
      }
    });

    JPanel metronomePanel = new JPanel();
    metronomePanel.add(metronomeCheckBox);
    metronomePanel.add(metronomeShowCheckBox);
    ui.localizables.add(new Localizable.NamedGroup(metronomePanel, "_menuMetronom"));

    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(3, 1));
    panel.add(gamePanel);
    panel.add(rhythmAndTimePanel);
    panel.add(metronomePanel);

    return panel;
	}

	@Override
	public String getPreferencesIconResource() {
		return "/images/rhythm.png";
	}

	@Override
	public String getPreferencesLocalizable() {
		return "_menuRythmreading";
	}

	@Override
	public void updateLanguage(ResourceBundle bundle) {
		// TODO Auto-generated method stub

	  wholeCheckBox.setText(bundle.getString("_wholenote"));
    halfCheckBox.setText(bundle.getString("_halfnote"));
    dottedhalfCheckBox.setText(bundle.getString("_dottedhalfnote"));
    quarterCheckBox.setText(bundle.getString("_quarternote"));
    eighthCheckBox.setText(bundle.getString("_eighthnote"));
    restCheckBox.setText(bundle.getString("_rest"));
    tripletCheckBox.setText(bundle.getString("_triplet"));

    metronomeCheckBox.setText(bundle.getString("_menuMetronom"));
    metronomeShowCheckBox.setText(bundle.getString("_menuShowMetronom"));

    timeSignLabel.setText(bundle.getString("_timeSignature"));

    gameTypeComboBox.removeAllItems();
    //  rhythmGameTypeComboBox.addItem(bundle.getString("_learninggame"));
    gameTypeComboBox.addItem(bundle.getString("_normalgame"));

	}
	
	@Override
	public void startGame() {
		super.startGame();
		ui.midiHelper.sm_sequencer.start();
	    int tmpdiv = 1;

      tmpdiv = rhythmLevel.getTimeDivision(); 

	    if (ui.muteRhythms) {
	      ui.midiHelper.sm_sequencer.setTrackMute(1, true); 
	      ui.midiHelper.sm_sequencer.setTrackMute(0, false); 
	    } else {
	      ui.midiHelper.sm_sequencer.setTrackMute(1, false); 
	      ui.midiHelper.sm_sequencer.setTrackMute(0, true); 
	    }

	    ui.jalmus.gameStarted = true; // start game
	    ui.startButton.setText(ui.bundle.getString("_stop"));
	    ui.rhythmCursorXpos = ui.firstNoteXPos - (ui.noteDistance * tmpdiv);

	    cursorstart = false;
	}
	
	@Override
	public void stopGame() {
        super.stopGame();
	    int tmpdiv = rhythmLevel.getTimeDivision(); 

	    ui.startButton.setText(ui.bundle.getString("_start"));

	    ui.scoreYpos = 110;
	    ui.rhythmCursorXpos = ui.firstNoteXPos - (ui.noteDistance * tmpdiv);
	    ui.rhythmCursorXStartPos = ui.firstNoteXPos - (ui.noteDistance * tmpdiv);
	    ui.rhythmAnswerScoreYpos = 100;
	    cursorstart = false;
	    ui.jalmus.metronomeCount = 0;
	    ui.metronomeYPos = 100;

	    if (ui.midiHelper.sm_sequencer != null) {
	      ui.midiHelper.sm_sequencer.close();
	    }
	    //repaint();
	}
	
	@Override
	public void initGame() {

	    System.out.println("[initRhythmGame] latency: " + ui.midiHelper.latency);

	    ui.midiHelper.initialize(); 
	    if (!ui.renderingThread.isAlive()) {
	      ui.renderingThread.start();
	    }  
	    stopGame(); // stop previous game

	    if (!ui.jalmus.samerhythms) {
	      createSequence();
	    }

	    try {
	      ui.midiHelper.sm_sequencer = MidiSystem.getSequencer();
	    } catch (MidiUnavailableException e) {
	      e.printStackTrace();
	      System.exit(1);
	    }
	    if (ui.midiHelper.sm_sequencer == null) {
	      System.out.println("Can't get a Sequencer");
	      System.exit(1);
	    }

	    try {
	      ui.midiHelper.sm_sequencer.open();
	    } catch (MidiUnavailableException e) {
	      e.printStackTrace();
	      System.exit(1);
	    }

	    try {
	      ui.midiHelper.sm_sequencer.setSequence(ui.midiHelper.sequence);
	    } catch (InvalidMidiDataException e) {
	      e.printStackTrace();
	      System.exit(1);
	    }

	    if (!(ui.midiHelper.sm_sequencer instanceof Synthesizer)) {

	      try {
	        Synthesizer sm_synthesizer = MidiSystem.getSynthesizer();
	        sm_synthesizer.open();
	        Receiver synthReceiver = sm_synthesizer.getReceiver();
	        Transmitter seqTransmitter = ui.midiHelper.sm_sequencer.getTransmitter();
	        seqTransmitter.setReceiver(synthReceiver);
	        //latency = sm_synthesizer.getLatency()/1000;  
	        ui.midiHelper.latency = ui.latencySlider.getValue();
	        System.out.println("MIDI latency " + ui.midiHelper.latency);
	      } catch (MidiUnavailableException e) {
	        e.printStackTrace();
	      }
	    }

	    ui.midiHelper.sm_sequencer.addMetaEventListener(new MetaEventListener() {
	      public void meta(MetaMessage meta) {
	        byte[] abData = meta.getData();
	        String strText = new String(abData);

          int tmpnum = rhythmLevel.getTimeSignNumerator();
          int tmpdiv = rhythmLevel.getTimeDivision();

	    if ("departthread".equals(strText)) {
	      System.out.println("Cursor started");
	      ui.rhythmCursorXlimit = ui.firstNoteXPos + 
	          (tmpnum * ui.numberOfMeasures * ui.noteDistance);
	      cursorstart = true;
	      ui.jalmus.timestart = System.currentTimeMillis();
	    } 

	    if ("depart".equals(strText)) {
	      System.out.println("Game start");
	      rhythmIndex = 0;
	      ui.repaint();
	    } else if ("beat".equals(strText)) {
	      // show metronome beats
	      answers.add(new RhythmAnswer(ui.firstNoteXPos + 
	          (ui.jalmus.metronomeCount%((tmpnum/tmpdiv) * ui.numberOfMeasures)) *
	          (ui.noteDistance * tmpdiv), ui.metronomeYPos - 30, true, 3));
	      ui.jalmus.metronomeCount++;
	      if (ui.jalmus.metronomeCount == ((tmpnum/tmpdiv) * ui.numberOfMeasures) && 
	          ui.metronomeYPos < ui.scoreYpos + (ui.numberOfRows * ui.rowsDistance)) {
	        ui.metronomeYPos += ui.rowsDistance;
	        ui.jalmus.metronomeCount = 0;
	      }
	    } else {
	      nextRhythm();
	      ui.repaint();
	    }
	      }
	    });

      ui.jalmus.tempo = rhythmLevel.getspeed(); 

	    ui.midiHelper.sm_sequencer.setTempoInBPM(ui.jalmus.tempo);
	    System.out.println("[initRhythmGame] tempo : " + ui.jalmus.tempo);

	    //init line answers
	    answers.clear();
	}

    @Override
    public void startLevel() {
      if (!rhythmLevel.isMessageEmpty()) {
        ui.textlevelMessage.setText("  " + rhythmLevel.getMessage() + "  ");
        ui.levelMessage.setTitle(ui.bundle.getString("_information"));
        ui.levelMessage.pack();
        ui.levelMessage.setLocationRelativeTo(ui);
        ui.levelMessage.setVisible(true);
      } else {
        ui.startButton.doClick();
      }
    }

    @Override
    public void changeScreen() {
      ui.gameButtonPanel.setVisible(true);
      ui.noteGame.noteButtonPanel.setVisible(false);
      ui.newButton.setVisible(true);
      ui.listenButton.setVisible(true);
      ui.principal.setVisible(true);
    }
  
  @Override
  public void showResult() {

    int nbgood = 0;
    int nbnotefalse = 0;
    int nbrhythmfalse = 0;
    int nbrhythms = 0;

    for (int i = 0; i < answers.size(); i++) {
      if (answers.get(i).allgood() && !answers.get(i).isnull()) nbgood = nbgood +1;
      if (!answers.get(i).isnull() && answers.get(i).badnote()) nbnotefalse = nbnotefalse +1;
      if (!answers.get(i).isnull() && answers.get(i).badrhythm() ) nbrhythmfalse = nbrhythmfalse +1;
    }

    //Nb rhythms
    for (int i = 0; i < rhythms.size(); i++) {
      if (!rhythms.get(i).isSilence() && !rhythms.get(i).isNull()) nbrhythms =  nbrhythms +1;
    }

    if (nbrhythms ==  nbgood) {
      ui.scoreMessage.setTitle(ui.bundle.getString("_congratulations"));
    } else {
      ui.scoreMessage.setTitle(ui.bundle.getString("_sorry"));
    }

    ui.textscoreMessage.setText("  " + nbrhythms + " " + ui.bundle.getString("_menuRythms") + 
                                " : " + nbgood + " " + ui.bundle.getString("_correct") +
                                " / " + nbnotefalse + " " + ui.bundle.getString("_wrong") +
                                "  " + nbrhythmfalse + " " + ui.bundle.getString("_wrongrhythm") + "  ");
    ui.scoreMessage.pack();
    ui.scoreMessage.setLocationRelativeTo(ui);
    ui.scoreMessage.setVisible(true);
  }

  @Override
  public int[] serializePrefs() {
    int[] prefs = new int[29];

    prefs[8] = gameTypeComboBox.getSelectedIndex();
    prefs[9] = speedComboBox.getSelectedIndex();
    if (wholeCheckBox.isSelected()) {
      prefs[10] = 1;
    } else {
      prefs[10] = 0;
    }
    if (halfCheckBox.isSelected()) {
      prefs[11] = 1;
    } else {
      prefs[11] = 0;
    }
    if (dottedhalfCheckBox.isSelected()) {
      prefs[28] = 1;
    } else {
      prefs[28] = 0;
    }
    if (quarterCheckBox.isSelected()) {
      prefs[12] = 1;
    } else {
      prefs[12] = 0;
    }
    if (eighthCheckBox.isSelected()) {
      prefs[13] = 1;
    } else {
      prefs[13] = 0;
    }
    if (restCheckBox.isSelected()) {
      prefs[14] = 1;
    } else {
      prefs[14] = 0;
    }
    if (metronomeCheckBox.isSelected()) {
      prefs[15] = 1;
    } else {
      prefs[15] = 0;
    }
    if (tripletCheckBox.isSelected()) {
      prefs[26] = 1;
    } else {
      prefs[26] = 0;
    }
    return prefs;
  }

  @Override
  public void deserializePrefs(int[] prefs) {
    // TODO Auto-generated method stub
    gameTypeComboBox.setSelectedIndex(prefs[8]);
    speedComboBox.setSelectedIndex(prefs[9]);
    if (prefs[10] == 1) {
      wholeCheckBox.setSelected(true);
    } else {
      wholeCheckBox.setSelected(false);
    }
    if (prefs[11] == 1) {
      halfCheckBox.setSelected(true);
    } else {
      halfCheckBox.setSelected(false);
    }
    if (prefs[28] == 1) {
      dottedhalfCheckBox.setSelected(true);
    } else {
      dottedhalfCheckBox.setSelected(false);
    }
    if (prefs[12] == 1) {
      quarterCheckBox.setSelected(true);
    } else {
      quarterCheckBox.setSelected(false);
    }
    if (prefs[13] == 1) {
      eighthCheckBox.setSelected(true);
    } else {
      eighthCheckBox.setSelected(false);
    }
    if (prefs[14] == 1) {
      restCheckBox.setSelected(true);
    } else {
      restCheckBox.setSelected(false);
    }
    if (prefs[15] == 1) {
      metronomeCheckBox.setSelected(true);
    } else {
      metronomeCheckBox.setSelected(false);
    }
  }

  private void nextRhythm() {
    System.out.println ("rhytm xpos: " + rhythms.get(rhythmIndex).getPosition() + 
        " pitch: " + rhythms.get(rhythmIndex).getPitch() + 
        " index: " + rhythmIndex);

    if (rhythms.get(rhythmIndex).getDuration() != 0) {
      if (rhythmIndex<rhythms.size()-1) {
        rhythmIndex++;
        ui.repaint();
      }
    }
  }
  
  private void createMetronome() {
    final int TEXT = 0x01;
    int nbpulse;

    try {
      int tmpnum = rhythmLevel.getTimeSignNumerator();
      int tmpden = rhythmLevel.getTimeSignDenominator();
      int tmpdiv = rhythmLevel.getTimeDivision();

      System.out.println("[createMetronome] timeSignNumerator =  " + tmpnum + ", timeSignDenominator = " + tmpden);

      String textd = "depart";
      ui.midiHelper.addEvent(metronome, TEXT, textd.getBytes(), (int)(tmpnum/tmpdiv)*MidiHelper.pulsesPerQuarterNote);

      String textdt = "departthread"; //one beat before rhythms
      ui.midiHelper.addEvent(metronome, TEXT, textdt.getBytes(), (int)((tmpnum/tmpdiv)-1)*MidiHelper.pulsesPerQuarterNote);

      if (rhythmLevel.getMetronome()) {
        nbpulse = (tmpnum * ui.numberOfMeasures * ui.numberOfRows) + tmpnum;
      } else {
        nbpulse = tmpnum; //only few first to indicate pulse
      }

      nbpulse /= tmpdiv;

      for (int i = 0; i < nbpulse; i++) {
        ShortMessage mess = new ShortMessage();
        ShortMessage mess2 = new ShortMessage();
        mess.setMessage(ShortMessage.NOTE_ON, 9, 76, 40); // can use 37 as well, but it has reverb

        metronome.add(new MidiEvent(mess, i * MidiHelper.pulsesPerQuarterNote));
        mess2.setMessage(ShortMessage.NOTE_OFF, 9, 77, 0);
        metronome.add(new MidiEvent(mess2, (i * MidiHelper.pulsesPerQuarterNote)+1));

        if (rhythmLevel.getMetronomeBeats() && i > ((tmpnum / tmpdiv) - 1)) {
          //System.out.println("adding metronome beat : "+i + "tmpnum : " + tmpnum + "tmpdiv : "+tmpdiv);
          String textb = "beat";
          ui.midiHelper.addEvent(metronome, TEXT, textb.getBytes(), (int) i * MidiHelper.pulsesPerQuarterNote);
        }
      }
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  void rhythmKeyReleased(int pitch) {
	    if (ui.keyboardsoundCheckBox.isSelected()) {
	      ui.midiHelper.currentChannel.stopNote(true,pitch);
	    }

	    float rhythmCursorXposcorrected;
	    if (cursorstart) {
	      rhythmCursorXposcorrected = ui.rhythmCursorXStartPos + 
	          ((System.currentTimeMillis()-ui.jalmus.timestart - ui.midiHelper.latency)*
	          ui.noteDistance)/(60000/ui.jalmus.tempo);
	    } else {
	      rhythmCursorXposcorrected = ui.rhythmCursorXpos;
	    }

	    System.out.println ("rhythmCursorXpos" + rhythmCursorXposcorrected);
	    if (cursorstart) {
	      // key should be released at the end of the rhythm
	      if ((rhythmIndex >= 0) && (rhythmIndex < rhythms.size()) 
	          && (!rhythms.get(rhythmIndex).isSilence()) && (rhythms.get(rhythmIndex).duration != 0)
	          && ((int)rhythmCursorXposcorrected < rhythms.get(rhythmIndex).getPosition() + 8/rhythms.get(rhythmIndex).duration * 27 - precision) 
	          && ((int)rhythmCursorXposcorrected > rhythms.get(rhythmIndex).getPosition() + precision)) {
	        answers.add(new RhythmAnswer((int)rhythmCursorXposcorrected, ui.rhythmAnswerScoreYpos -15 , true, 2 ));
	      }
	      //key should be released just before a silent  
	      if ((rhythmIndex >= 0) && (rhythms.get(rhythmIndex).isSilence()) && (rhythmIndex-1 >= 0) 
	          && (!rhythms.get(rhythmIndex-1).isSilence())	
	          && ((int)rhythmCursorXposcorrected > rhythms.get(rhythmIndex).getPosition() + precision) 
	          && ((int)rhythmCursorXposcorrected < rhythms.get(rhythmIndex).getPosition() + 8/rhythms.get(rhythmIndex).duration * 27 - precision)) {
	        answers.add(new RhythmAnswer((int)rhythmCursorXposcorrected, ui.rhythmAnswerScoreYpos -15 , true, 2 ));
	      }
	    }
	  }

    void rhythmKeyPressed(int pitch) {
	    int result = 0;
	    boolean goodnote = false;

	    if (ui.keyboardsoundCheckBox.isSelected()) {
	      //currentChannel.stopnotes();
	      ui.midiHelper.currentChannel.playNote(true, pitch, 2000);
	    }
	    //  System.out.println("time sound" + System.currentTimeMillis());
	    float rhythmCursorXposcorrected;

	    if (cursorstart) {
	      rhythmCursorXposcorrected = ui.rhythmCursorXStartPos + 
	          ((System.currentTimeMillis() - ui.jalmus.timestart - ui.midiHelper.latency)*ui.noteDistance)/
	          (60000/ui.jalmus.tempo);
	    } else {
	      rhythmCursorXposcorrected = ui.rhythmCursorXpos;
	    }

	    System.out.println ("rhythmCursorXpos" + rhythmCursorXposcorrected);

	    if (((rhythmIndex >= 0) 
	         && ((int) rhythmCursorXposcorrected < rhythms.get(rhythmIndex).getPosition() + precision) 
	         && ((int) rhythmCursorXposcorrected > rhythms.get(rhythmIndex).getPosition() - precision) 
	         && !rhythms.get(rhythmIndex).isSilence())) {
	      if (pitch == rhythms.get(rhythmIndex).getPitch()) {
	        result = 0;
	        goodnote = true;
	      } else {
	        result = 0;
	        goodnote = false;
	      }
	    //to resolve problem with eight on fast tempo 
	    } else if (((rhythmIndex - 1 >= 0) 
	        && ((int) rhythmCursorXposcorrected < rhythms.get(rhythmIndex-1).getPosition() + precision) 
	        && ((int) rhythmCursorXposcorrected > rhythms.get(rhythmIndex-1).getPosition() - precision) 
	        && !rhythms.get(rhythmIndex-1).isSilence())) {
	      if (pitch == rhythms.get(rhythmIndex).getPitch()) {
	        result = 0;
	        goodnote = true;
	      } else {
	        result = 0;
	        goodnote = false;
	      }
	    } else {
	      if (rhythmIndex >= 0 
	        && pitch== rhythms.get(rhythmIndex).getPitch()) {
	        result = 1;
	        goodnote = true;
	      } else {
	        result = 1;
	        goodnote = false;
	      }
	    }
	    answers.add(new RhythmAnswer((int)rhythmCursorXposcorrected, ui.rhythmAnswerScoreYpos - 15,
	        goodnote, result));
	  }

      private void regroupNotes() {
	    for (int i = 0; i < rhythms.size()-1; i++) {
	      if (rhythms.get(i).getDuration() == 0.5 && rhythms.get(i+1).getDuration()==0.5 &&  //TO BE FIX  FOR 8
	          !rhythms.get(i+1).isSilence() && !rhythms.get(i).isSilence() &&
	          !isBeginMeasure(i+1)  && !rhythms.get(i).isGroupee()) {
	        rhythms.get(i).setGroupee(1);
	        rhythms.get(i+1).setGroupee(2);
	      }
	    }
	  }
      
      void createSequence() {
    	    ui.repaint();
    	      // int tmpden = 4;
    	    int currentTick = 0;
    	    int rowCount = 0; // measures counter
    	    double tpsmes = 0; // number of quarters 
    	    int currentXPos = ui.windowMargin + ui.keyWidth + ui.alterationWidth + ui.timeSignWidth + ui.notesShift;
    	    int pitch;
    	    boolean stemup = true;
    	    // Dimension size = getSize();

            boolean wholeNote = rhythmLevel.getWholeNote();
            boolean halfNote = rhythmLevel.getHalfNote();
            boolean dottedhalfNote = rhythmLevel.getDottedHalfNote();
            boolean quarterNote = rhythmLevel.getQuarterNote();
            boolean eighthNote = rhythmLevel.getEighthNote();
            boolean triplet = rhythmLevel.getTriplet();

            int tmpnum = rhythmLevel.getTimeSignNumerator();
            int tmpdiv = rhythmLevel.getTimeDivision();

    	    currentTick = (int)((tmpnum/tmpdiv)*ui.midiHelper.pulsesPerQuarterNote);

    	    // INITIALIZE Sequence and tracks
    	    try {
    	      ui.midiHelper.sequence = new Sequence(Sequence.PPQ, ui.midiHelper.pulsesPerQuarterNote);
    	    } catch (InvalidMidiDataException e) {
    	      e.printStackTrace();
    	      System.exit(1);
    	    }

    	    mutetrack = ui.midiHelper.sequence.createTrack();
    	    track = ui.midiHelper.sequence.createTrack();
    	    metronome = ui.midiHelper.sequence.createTrack();

    	    rhythms.clear();

    	    createMetronome();

    	    try {
    	      ShortMessage sm = new ShortMessage();
    	      sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, ui.instrumentsComboBox.getSelectedIndex(), 0);
    	      track.add(new MidiEvent(sm, 0));
    	    } catch (InvalidMidiDataException e) {
    	      e.printStackTrace();
    	      System.exit(1);
    	    }

    	    for (int r = 1; r <= (ui.numberOfMeasures * ui.numberOfRows); r++) { // creates all the measures
    	      while (tpsmes != tmpnum) {
    	        //System.out.println("tpsmes : " + tpsmes);
    	        double tmp = Math.random();
                pitch = 71;
                stemup = true;

    	        if (wholeNote && tpsmes+4 <= tmpnum && tmp<0.2) { // ronde, whole
    	          tpsmes += 4;
    	          currentTick = addRhythm(4, pitch, stemup, currentTick, rowCount, currentXPos);
    	          currentXPos += (ui.noteDistance*4);
    	        } else if (dottedhalfNote && tpsmes + 3 <= tmpnum && tmp < 0.4) { // blanche pointee, dotted half
    	          tpsmes += 3;
    	          currentTick = addRhythm(3, pitch, stemup, currentTick, rowCount, currentXPos);
    	          currentXPos += (ui.noteDistance*3);
    	        } else if (halfNote && tpsmes + 2 <= tmpnum && tmp < 0.4) { // blanche, half
    	          tpsmes += 2;
    	          currentTick = addRhythm(2, pitch, stemup, currentTick, rowCount, currentXPos);
    	          currentXPos += (ui.noteDistance*2);
    	        } else if (quarterNote && tpsmes + 1 <= tmpnum && tmp < 0.6) { // noire, quarter
    	          tpsmes += 1;
    	          currentTick = addRhythm(1, pitch, stemup, currentTick, rowCount, currentXPos);
    	          currentXPos += ui.noteDistance;
    	        } else if (eighthNote && tpsmes + 0.5 <= tmpnum && tmp < 0.8) { // croche, eighth
    	          tpsmes += 0.5;
    	          currentTick = addRhythm(0.5, pitch, stemup, currentTick, rowCount, currentXPos);
    	          currentXPos += (ui.noteDistance/2);
    	        } else if (triplet && tpsmes+1 <= tmpnum && tmp<0.9) { // triplet
    	          int[] tripletPitches = { pitch, 71, 71 };
    	          int lowestPitch = tripletPitches[0];
    	          for (int i = 1; i < 3; i++) {
    	            if (tripletPitches[i] < lowestPitch && !stemup)
    	              lowestPitch = tripletPitches[i];
    	            else if (tripletPitches[i] > lowestPitch && stemup)
    	              lowestPitch = tripletPitches[i];
    	          }

    	          System.out.println("Triplet pitches: " + tripletPitches[0] + ", " + tripletPitches[1] + ", " + tripletPitches[2]);
    	          System.out.println("Triplet lowest: " + lowestPitch);

    	          currentTick = addRhythm(0.333, pitch, stemup, currentTick, rowCount, currentXPos);
    	          setTripletValue(lowestPitch);
    	          currentXPos += (ui.noteDistance/3);
    	          currentTick = addRhythm(0.333, tripletPitches[1], stemup, currentTick, rowCount, currentXPos);
    	          setTripletValue(100 + lowestPitch);
    	          currentXPos += (ui.noteDistance/3);
    	          currentTick = addRhythm(0.333, tripletPitches[2], stemup, currentTick, rowCount, currentXPos);
    	          setTripletValue(100 + lowestPitch);
    	          tpsmes += 1;
    	          currentXPos += (ui.noteDistance/3);
    	        }
    	      }

          tpsmes = 0;
          if ((r % ui.numberOfMeasures) == 0) {
            currentXPos = ui.windowMargin + ui.keyWidth + ui.alterationWidth + ui.timeSignWidth + ui.notesShift;
            rowCount++;
          }
    	  }
        regroupNotes(); //not workin with Scorereading yet
    	}

      private int addRhythm(double duration, int pitch, boolean stemup, int currentTick, int row, int newXPos) {
    	    int tick = currentTick;
    	    int velocity = 71;

    	    final int TEXT = 0x01;
    	    String text = "off";

            boolean silence = rhythmLevel.getSilence();
            int tmpdiv =  rhythmLevel.getTimeDivision();
            int tmpnum =  rhythmLevel.getTimeSignNumerator();

    	    if (duration == 0.333) { // do not handle pauses into triplets for now 
    	      silence = false;
    	    }

    	    System.out.println("[addRhythm] pitch: " + pitch + "duration: " + duration + "stemup " + stemup);

    	    double tmpsilence = Math.random();
    	    if (!silence 
    	        || (silence && tmpsilence < 0.85) 
    	        || (duration == 3 && tmpnum != 3)) {
    	      rhythms.add(new Rhythm(duration, newXPos, pitch,  row, stemup, false, false, 0));
    	      track.add(MidiHelper.createNoteOnEvent(pitch, velocity, tick));
    	      mutetrack.add(MidiHelper.createNoteOnEvent(pitch, 0, tick));
    	      tick += (int)((duration*tmpdiv) * MidiHelper.pulsesPerQuarterNote);
    	      ui.midiHelper.addEvent(track, TEXT, text.getBytes(), tick);
    	      ui.midiHelper.addEvent(mutetrack, TEXT, text.getBytes(), tick);
    	      track.add(MidiHelper.createNoteOffEvent(pitch, tick));
    	      mutetrack.add(MidiHelper.createNoteOffEvent(pitch, tick));
    	    } else { // silence
    	      rhythms.add(new Rhythm(duration, newXPos, pitch, row, false, false, true, 0));
    	      track.add(MidiHelper.createNoteOffEvent(pitch, tick));
    	      mutetrack.add(MidiHelper.createNoteOffEvent(pitch, tick));
    	      tick += (int)((duration*tmpdiv) * MidiHelper.pulsesPerQuarterNote);
    	      ui.midiHelper.addEvent(track, TEXT, text.getBytes(), tick);
    	      ui.midiHelper.addEvent(mutetrack, TEXT, text.getBytes(), tick);
    	    }
    	    return tick;
    	  }

    @Override
    public void nextGame() {
      rhythmLevel.copy((RhythmLevel) ui.jalmus.currentLesson.getLevel());

      ui.jalmus.selectedGame = Jalmus.RHYTHMREADING;
      initGame();

      ui.changeScreen(ui.jalmus.isLessonMode, ui.jalmus.currentLesson, ui.jalmus.selectedGame);
      rhythmLevel.printtest();
      ui.newButton.doClick();
      startLevel();
    }
}

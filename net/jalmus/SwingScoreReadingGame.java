package net.jalmus;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SwingScoreReadingGame extends ScoreReadingGame implements SwingGame {

  SwingJalmus ui;
  
  JComboBox<String> gameTypeComboBox; //type of games
  JComboBox<String> speedComboBox; // button to choose the speed

  JCheckBox wholeCheckBox;
  JCheckBox halfCheckBox;
  JCheckBox dottedhalfCheckBox;
  JCheckBox quarterCheckBox;
  JCheckBox eighthCheckBox;
  JCheckBox restCheckBox;
  JCheckBox tripletCheckBox;

  JLabel    timeSignLabel;
  JComboBox<String> timeSignComboBox;

  JCheckBox metronomeCheckBox;
  JCheckBox metronomeShowCheckBox;
  
  JComboBox<String> keyComboBox; //  drop down combo box to select the key
  JComboBox<String> notesComboBox; // drop down combo box to choose the number of notes
  JComboBox<String> alterationsComboBox; // drop down combo box to choose the pitch
  
  JDialog notesDialog;
  ChooseNotePanel chooseNotePanel;
  
  private Track track;
  private Track mutetrack;
  Track metronome;

  boolean stemup;

  JPanel principal = new JPanel(); // panel principal

  JButton startButton;    // button to start or stop game
  JButton listenButton;    // button for listen exercise in rhythm game
  JButton newButton;    // button for new exercise in rhythm game
  JButton preferencesButton;  // button to access game preferences
  JPanel gameButtonPanel = new JPanel();
  Anim animationPanel;

  int rhythmAnswerScoreYpos = 100; //distance to paint answer
  float rhythmCursorXpos; 
  int rhythmCursorXStartPos; 
  int rhythmCursorXlimit;
  int alterationWidth = 0; // width of alterations symbols. None by default

  public void setUi(final SwingJalmus ui) {
    this.ui = ui;

    startButton = new JButton();
    startButton.setFocusable(false);
    ui.localizables.add(new Localizable.Button(startButton, "_start"));
    startButton.setPreferredSize(new Dimension(150, 20));
    startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleStartButtonClicked();
      }
    });

    listenButton = new JButton();
    listenButton.setFocusable(false);
    ui.localizables.add(new Localizable.Button(listenButton, "_listen"));
    listenButton.setPreferredSize(new Dimension(150, 20));
    listenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handleListenButtonClicked();
      }
    });

    newButton = new JButton();
    newButton.setFocusable(false);
    ui.localizables.add(new Localizable.Button(newButton, "_new"));
    newButton.setPreferredSize(new Dimension(150, 20));
    newButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handleNewButtonClicked();
      }
    });

    preferencesButton = new JButton();
    preferencesButton.setFocusable(false);
    ui.localizables.add(new Localizable.Button(preferencesButton, "_menuPreferences"));
    preferencesButton.setPreferredSize(new Dimension(150, 20));
    preferencesButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ui.handlePreferencesClicked();
      }
    });

    rhythmCursorXpos = ui.firstNoteXPos - ui.noteDistance;
    rhythmCursorXStartPos = ui.firstNoteXPos - ui.noteDistance;

    gameButtonPanel.setLayout(new FlowLayout());
    gameButtonPanel.add(startButton);
    gameButtonPanel.add(newButton);
    gameButtonPanel.add(listenButton);
    gameButtonPanel.add(preferencesButton);
    gameButtonPanel.setBackground(Color.white);

    principal.setLayout(new BorderLayout());
    animationPanel = new Anim(ui);
    animationPanel.setBackground(Color.white);
    animationPanel.setVisible(true);
    principal.add(gameButtonPanel, BorderLayout.NORTH);
    principal.add(animationPanel, BorderLayout.CENTER);
    principal.setVisible(true);
  }

  @Override
  public JPanel getPreferencesPanel() {

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
          scoreLevel.setSpeed(40);
        } else if (speedComboBox.getSelectedIndex() == 1) {
          scoreLevel.setSpeed(60);
        } else if (speedComboBox.getSelectedIndex() == 2) {
          scoreLevel.setSpeed(100);
        } else if (speedComboBox.getSelectedIndex() == 3) {
          scoreLevel.setSpeed(120);
        } else if (speedComboBox.getSelectedIndex() == 4) {
          scoreLevel.setSpeed(160);
        } 
      }
    });

    JPanel scoregamePanel = new JPanel();
    scoregamePanel.add(gameTypeComboBox);
    scoregamePanel.add(speedComboBox);
    ui.localizables.add(new Localizable.NamedGroup(scoregamePanel, "_menuExercises"));

    /* 2nd panel - Key & notes */

    keyComboBox = new JComboBox<String>();
    keyComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        if (keyComboBox.getSelectedIndex() == 0) {
          scoreLevel.setCurrentKey("treble");
          scoreLevel.initPitcheslist(9);
          initGame();
        } else if (keyComboBox.getSelectedIndex() == 1) {
          scoreLevel.setCurrentKey("bass");
          scoreLevel.initPitcheslist(9);
          initGame();
        }
      }
    });

    notesComboBox = new JComboBox<String>();
    notesComboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        if (notesComboBox.getSelectedIndex() == 0) {
          scoreLevel.setNotetype("notes");
          scoreLevel.setNbnotes(9);
        } else if (notesComboBox.getSelectedIndex() == 1) {
          scoreLevel.setNotetype("notes");
          scoreLevel.setNbnotes(15);
        } else if (notesComboBox.getSelectedIndex() == 2) {
          scoreLevel.setNbnotes(0);
          scoreLevel.setNotetype("custom");

          chooseNotePanel = new ChooseNotePanel(scoreLevel.getKey(), Jalmus.SCOREREADING,
              ui.bundle);
          chooseNotePanel.updateTable(scoreLevel.getPitcheslist());
          chooseNotePanel.setOpaque(true); //content panes must be opaque 
          chooseNotePanel.setVisible(true);
          chooseNotePanel.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            //Execute when button is pressed
              if (!chooseNotePanel.atLeast3Pitches()) {
                JOptionPane.showMessageDialog(null, "Choose at least three notes", "Warning", JOptionPane.ERROR_MESSAGE); 
              } else {
                notesDialog.setVisible(false);
                scoreLevel.setPitcheslist(chooseNotePanel.getPitches());
              }
            }
          });    

          notesDialog.setContentPane(chooseNotePanel);
          notesDialog.setSize(650, 220);
          notesDialog.setLocationRelativeTo(ui);
          notesDialog.setVisible(true);
          chooseNotePanel.setVisible(true);

          ui.add(notesDialog);
        } 
      }
      
    });

    alterationsComboBox = new JComboBox<String>();
    alterationsComboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub
        if (alterationsComboBox.getSelectedIndex() == 15) {
          // random choice of tonality when game start
          scoreLevel.setRandomTonality(true);
          scoreLevel.getCurrentTonality().init(0, "r");
        } else {
          scoreLevel.setRandomTonality(false);
          int index = alterationsComboBox.getSelectedIndex();
          
          String modifier = "";
          if (index > 7) {
            index -= 7;
            modifier = "b";
          }

          scoreLevel.getCurrentTonality().init(index, modifier);
        }
      }
      
    });

    JPanel scoreKeyPanel = new JPanel(); // panel pour la Key du premier jeu
    scoreKeyPanel.add(keyComboBox);
    scoreKeyPanel.add(alterationsComboBox);
    scoreKeyPanel.add(notesComboBox);
    ui.localizables.add(new Localizable.NamedGroup(scoreKeyPanel, "_menuNotes"));

    /* 3rd panel - RYTHM */

    wholeCheckBox = new JCheckBox("", true);
    wholeCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        scoreLevel.setWholeNote(wholeCheckBox.isSelected());
      }
    });

    halfCheckBox = new JCheckBox("", true);
    halfCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        scoreLevel.setHalfNote(halfCheckBox.isSelected());
      }
    }); 

    dottedhalfCheckBox = new JCheckBox("", false);
    dottedhalfCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        scoreLevel.setDottedHalfNote(dottedhalfCheckBox.isSelected());
      }
    });

    quarterCheckBox = new JCheckBox("", false);
    quarterCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        scoreLevel.setQuarterNote(quarterCheckBox.isSelected());
      }
    });

    eighthCheckBox = new JCheckBox("", false);
    eighthCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        scoreLevel.setEighthNote(eighthCheckBox.isSelected());
      }
    });

    restCheckBox = new JCheckBox("", true);
    restCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        scoreLevel.setSilence(restCheckBox.isSelected());
      }
    });

    tripletCheckBox = new JCheckBox("", false);
    tripletCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        scoreLevel.setTriplet(tripletCheckBox.isSelected());
      }
    });


    JPanel scorerhytmsPanel = new JPanel();
    scorerhytmsPanel.add(wholeCheckBox);
    scorerhytmsPanel.add(dottedhalfCheckBox);
    scorerhytmsPanel.add(halfCheckBox);
    scorerhytmsPanel.add(quarterCheckBox);
    scorerhytmsPanel.add(eighthCheckBox);
    scorerhytmsPanel.add(restCheckBox);
    scorerhytmsPanel.add(tripletCheckBox);

    timeSignComboBox = new JComboBox<String>();
    timeSignComboBox.setPreferredSize(new Dimension(100, 25));
    timeSignComboBox.addItem("4/4");
    timeSignComboBox.addItem("3/4");
    timeSignComboBox.addItem("2/4");
    timeSignComboBox.addItem("6/8");
    timeSignComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JComboBox<?> cb = (JComboBox<?>)e.getSource();
        int sel = cb.getSelectedIndex();
        System.out.println("Rhythm time signature changed. Selected: "+ sel);
        switch (sel) {
          case 0:
            wholeCheckBox.setEnabled(true);
            wholeCheckBox.setSelected(true);
            quarterCheckBox.setSelected(true);
            dottedhalfCheckBox.setEnabled(true);
            dottedhalfCheckBox.setSelected(false);
            scoreLevel.setTimeSignNumerator(4);
            scoreLevel.setTimeSignDenominator(4);
            scoreLevel.setTimeDivision(1);
            break;
          case 1: 
            wholeCheckBox.setSelected(false);
            wholeCheckBox.setEnabled(false);
            dottedhalfCheckBox.setSelected(true);
            dottedhalfCheckBox.setEnabled(true);
            quarterCheckBox.setSelected(true);
            scoreLevel.setTimeSignNumerator(3);
            scoreLevel.setTimeSignDenominator(4);
            scoreLevel.setTimeDivision(1);
            break;
          case 2:
            wholeCheckBox.setSelected(false);
            dottedhalfCheckBox.setSelected(false);
            dottedhalfCheckBox.setEnabled(false);
            quarterCheckBox.setSelected(true);
            scoreLevel.setTimeSignNumerator(2);
            scoreLevel.setTimeSignDenominator(4);
            scoreLevel.setTimeDivision(1);
            break;
          case 3:
            wholeCheckBox.setSelected(false);
            wholeCheckBox.setEnabled(false);
            dottedhalfCheckBox.setSelected(false);
            dottedhalfCheckBox.setEnabled(false);
            quarterCheckBox.setSelected(true);
            scoreLevel.setTimeSignNumerator(6);
            scoreLevel.setTimeSignDenominator(8);
            scoreLevel.setTimeDivision(2);
            break;
        }
      }
    });

    JPanel timeSignPanel = new JPanel();
    timeSignLabel = new JLabel();
    timeSignPanel.add(timeSignLabel);
    timeSignPanel.add(timeSignComboBox);

    JPanel scoreRhythmAndTimePanel = new JPanel();
    scoreRhythmAndTimePanel.setLayout(new BorderLayout());
    scoreRhythmAndTimePanel.add(timeSignPanel, BorderLayout.NORTH);
    scoreRhythmAndTimePanel.add(scorerhytmsPanel, BorderLayout.CENTER);
    ui.localizables.add(new Localizable.NamedGroup(scoreRhythmAndTimePanel, "_menuRythms"));

    /* 4th panel - sound */

    metronomeCheckBox = new JCheckBox("", true);
    metronomeCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        scoreLevel.setMetronome(metronomeCheckBox.isSelected());
      }
    });


    metronomeShowCheckBox = new JCheckBox("", true);
    metronomeShowCheckBox.setSelected(false);
    metronomeShowCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent evt) {
        scoreLevel.setMetronomeBeats(metronomeShowCheckBox.isSelected());
      }
    });

    JPanel scoremetronomePanel = new JPanel();
    scoremetronomePanel.add(metronomeCheckBox);
    scoremetronomePanel.add(metronomeShowCheckBox);
    ui.localizables.add(new Localizable.NamedGroup(scoremetronomePanel, "_menuMetronom"));

    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(4, 1));
    panel.add(scoregamePanel);
    panel.add(scoreKeyPanel);
    panel.add(scoreRhythmAndTimePanel);
    panel.add(scoremetronomePanel);
    //panel.add(latencyPanel);

    return panel;
  }

  JPanel getGamePanel() {
    return principal;
  }

  @Override
  public String getPreferencesIconResource() {
    return "/images/score.png";
  }

  @Override
  public String getPreferencesLocalizable() {
    return "_menuScorereading";
  }

  @Override
  public void updateLanguage(ResourceBundle bundle) {
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
    
    keyComboBox.removeAllItems();
    keyComboBox.addItem(bundle.getString("_trebleclef"));
    keyComboBox.addItem(bundle.getString("_bassclef"));

    gameTypeComboBox.removeAllItems();
    gameTypeComboBox.addItem(bundle.getString("_normalgame"));

    notesComboBox.removeAllItems();
    notesComboBox.addItem("9 "+bundle.getString("_menuNotes"));
    notesComboBox.addItem("15 "+bundle.getString("_menuNotes"));
    notesComboBox.addItem(bundle.getString("_customnotes"));
    
    alterationsComboBox.removeAllItems();
    alterationsComboBox.addItem(bundle.getString("_nosharpflat"));
    alterationsComboBox.addItem("1 " + bundle.getString("_sharp"));
    alterationsComboBox.addItem("2 " + bundle.getString("_sharp"));
    alterationsComboBox.addItem("3 " + bundle.getString("_sharp"));
    alterationsComboBox.addItem("4 " + bundle.getString("_sharp"));
    alterationsComboBox.addItem("5 " + bundle.getString("_sharp"));
    alterationsComboBox.addItem("6 " + bundle.getString("_sharp"));
    alterationsComboBox.addItem("7 " + bundle.getString("_sharp"));
    alterationsComboBox.addItem("1 " + bundle.getString("_flat"));
    alterationsComboBox.addItem("2 " + bundle.getString("_flat"));
    alterationsComboBox.addItem("3 " + bundle.getString("_flat"));
    alterationsComboBox.addItem("4 " + bundle.getString("_flat"));
    alterationsComboBox.addItem("5 " + bundle.getString("_flat"));
    alterationsComboBox.addItem("6 " + bundle.getString("_flat"));
    alterationsComboBox.addItem("7 " + bundle.getString("_flat"));
    alterationsComboBox.addItem(bundle.getString("_random"));

    notesDialog.setTitle("Choose notes to study");
  }

  @Override
  public void changeScreen() {
    gameButtonPanel.setVisible(true);
    newButton.setVisible(true);
    listenButton.setVisible(true);
    principal.setVisible(true);
    principal.repaint();
    principal.revalidate();
  }
  
  @Override
  public void startGame() {
    super.startGame();
    
  }
  
  @Override
  public void startLevel() {
    super.startLevel();
    if (!scoreLevel.isMessageEmpty()) {
      ui.textlevelMessage.setText("  "+scoreLevel.getMessage()+"  ");
      ui.levelMessage.setTitle(ui.bundle.getString("_information"));
      ui.levelMessage.pack();
      ui.levelMessage.setLocationRelativeTo(ui);
      ui.levelMessage.setVisible(true);
    } else {
      ui.startButton.doClick();
    }
  }

  @Override
  public int[] serializePrefs() {
    int[] prefs = new int[29];
    // TODO Auto-generated method stub
    prefs[16] = gameTypeComboBox.getSelectedIndex();
    prefs[17] = speedComboBox.getSelectedIndex();
    if (wholeCheckBox.isSelected()) {
      prefs[18] = 1;
    } else {
      prefs[18] = 0;
    }
    if (halfCheckBox.isSelected()) {
      prefs[19] = 1;
    } else {
      prefs[19] = 0;
    }
    if (quarterCheckBox.isSelected()) {
      prefs[20] = 1;
    } else {
      prefs[20] = 0;
    }
    if (eighthCheckBox.isSelected()) {
      prefs[21] = 1;
    } else {
      prefs[21] = 0;
    }
    if (restCheckBox.isSelected()) {
      prefs[22] = 1;
    } else {
      prefs[22] = 0;
    }
    if (metronomeCheckBox.isSelected()) {
      prefs[23] = 1;
    } else {
      prefs[23] = 0;
    }
    prefs[24] = keyComboBox.getSelectedIndex();
    prefs[25] = alterationsComboBox.getSelectedIndex();
    if (tripletCheckBox.isSelected()) {
      prefs[27] = 1;
    } else {
      prefs[27] = 0;
    }
    if (dottedhalfCheckBox.isSelected()) {
      prefs[28] = 1;
    } else {
      prefs[28] = 0;
    }
    
    return prefs;
  }

  @Override
  public void deserializePrefs(int[] prefs) {
    // TODO Auto-generated method stub
    gameTypeComboBox.setSelectedIndex(prefs[16]);
    speedComboBox.setSelectedIndex(prefs[17]);
    if (prefs[18] == 1) {
      wholeCheckBox.setSelected(true);
    } else {
      wholeCheckBox.setSelected(false);
    }
    if (prefs[19] == 1) {
      halfCheckBox.setSelected(true);
    } else {
      halfCheckBox.setSelected(false);
    }
    if (prefs[28] == 1) {
      dottedhalfCheckBox.setSelected(true);
    } else {
      dottedhalfCheckBox.setSelected(false);
    }
    if (prefs[20] == 1) {
      quarterCheckBox.setSelected(true);
    } else {
      quarterCheckBox.setSelected(false);
    }
    if (prefs[21] == 1) {
      eighthCheckBox.setSelected(true);
    } else {
      eighthCheckBox.setSelected(false);
    }
    if (prefs[22] == 1) {
      restCheckBox.setSelected(true);
    } else {
      restCheckBox.setSelected(false);
    }
    if (prefs[23] == 1) {
      metronomeCheckBox.setSelected(true);
    } else {
      metronomeCheckBox.setSelected(false);
    }
    keyComboBox.setSelectedIndex(prefs[24]);
    alterationsComboBox.setSelectedIndex(prefs[25]);
    if (prefs[26] == 1) {
      tripletCheckBox.setSelected(true);
    } else {
      tripletCheckBox.setSelected(false);
    }
    if (prefs[27] == 1) {
      tripletCheckBox.setSelected(true);
    } else {
      tripletCheckBox.setSelected(false);
    }
  }
  
  private void createMetronome() {
    final int TEXT = 0x01;
    int nbpulse;

    try {
      int tmpnum = scoreLevel.getTimeSignNumerator();
      int tmpden = scoreLevel.getTimeSignDenominator();
      int tmpdiv = scoreLevel.getTimeDivision();

      System.out.println("[createMetronome] timeSignNumerator =  " + tmpnum +
          ", timeSignDenominator = " + tmpden);

      String textd = "depart";
      ui.midiHelper.addEvent(metronome, TEXT, textd.getBytes(), (int)(tmpnum/tmpdiv) *
          MidiHelper.PULSES_PER_QUARTER_NOTE);

      String textdt = "departthread"; //one beat before rhythms
      ui.midiHelper.addEvent(metronome, TEXT, textdt.getBytes(), (int)((tmpnum/tmpdiv)-1) *
          MidiHelper.PULSES_PER_QUARTER_NOTE);

      if ((scoreLevel.getMetronome())) {
        nbpulse = (tmpnum * ui.numberOfMeasures * ui.numberOfRows) + tmpnum;
      } else {
        nbpulse = tmpnum; //only few first to indicate pulse
      }

      nbpulse /= tmpdiv;

      for (int i = 0; i < nbpulse; i++) {
        ShortMessage mess = new ShortMessage();
        ShortMessage mess2 = new ShortMessage();
        mess.setMessage(ShortMessage.NOTE_ON, 9, 76, 40); // can use 37 as well, but it has reverb

        metronome.add(new MidiEvent(mess, i*MidiHelper.PULSES_PER_QUARTER_NOTE));
        mess2.setMessage(ShortMessage.NOTE_OFF, 9, 77, 0);
        metronome.add(new MidiEvent(mess2, (i*MidiHelper.PULSES_PER_QUARTER_NOTE)+1));

        if ((scoreLevel.getMetronomeBeats()) && i > ((tmpnum / tmpdiv) - 1)) {
          String textb = "beat";
          ui.midiHelper.addEvent(metronome, TEXT, textb.getBytes(), (int)i *
              MidiHelper.PULSES_PER_QUARTER_NOTE);
        }
      }
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  public void showResult() {
    int nbgood = 0;
    int nbnotefalse = 0;
    int nbrhythmfalse = 0;
    int nbrhythms = 0;

    for (int i = 0; i < answers.size(); i++) {
      if (answers.get(i).allGood() && !answers.get(i).isnull()) nbgood = nbgood +1;
      if (!answers.get(i).isnull() && answers.get(i).badNote()) nbnotefalse = nbnotefalse +1;
      if (!answers.get(i).isnull() && answers.get(i).badRhythm() ) nbrhythmfalse = nbrhythmfalse +1;
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

  void drawNotesAndAnswers(Graphics g, Font f) {

    // paint answers: red = wrong, green = good
    for (int i = 0; i < answers.size(); i++) {
      if (!answers.get(i).isnull()) answers.get(i).paint(g);
    }

    for (int i = 0; i < rhythms.size(); i++) {
      // System.out.println(i);
      if (rhythms.get(i).getDuration() != 0) {
        if ((i != rhythmIndex) || (ui.muteRhythms)) { //only paint note in learning mode
          rhythms.get(i).paint(g, ui.jalmus.selectedGame, f, scoreLevel, 9, ui.rowsDistance,
            false, ui.scoreYpos, ui);
        } else {
          rhythms.get(i).paint(g, ui.jalmus.selectedGame, f, scoreLevel, 9, ui.rowsDistance,
            true, ui.scoreYpos, ui);
        }
      }
    }
  }
  
  public void drawKeys(Graphics g) {
    if (scoreLevel.isCurrentKeyTreble()) {
      for (int rowNum = 0; rowNum < ui.numberOfRows; rowNum++) {
        g.setFont(ui.musiSync.deriveFont(70f));
        g.drawString("G", ui.windowMargin, ui.scoreYpos+42+rowNum*ui.rowsDistance);
      }
    } else if (scoreLevel.isCurrentKeyBass()) {
      for (int rowNum = 0; rowNum < ui.numberOfRows; rowNum++) {
        g.setFont(ui.musiSync.deriveFont(60f));
        g.drawString("?", ui.windowMargin, ui.scoreYpos+40+rowNum*ui.rowsDistance);
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
	      rhythmCursorXposcorrected = rhythmCursorXStartPos + 
	          ((System.currentTimeMillis() - ui.jalmus.timestart - ui.midiHelper.latency)
	          * ui.noteDistance) / (60000/ui.jalmus.tempo);
	    } else {
	      rhythmCursorXposcorrected = rhythmCursorXpos;
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
	    answers.add(new RhythmAnswer((int)rhythmCursorXposcorrected, rhythmAnswerScoreYpos - 15,
	        goodnote, result));
	  }
  
  void createSequence() {
	    ui.repaint();
	      // int tmpden = 4;
	    int currentTick = 0;
	    int rowCount = 0; // measures counter
	    double tpsmes = 0; // number of quarters 
	    int currentXPos = ui.windowMargin + ui.keyWidth + alterationWidth + ui.timeSignWidth + ui.notesShift;
	    int pitch;
	    // Dimension size = getSize();

	    boolean wholeNote = scoreLevel.getWholeNote();
	    boolean halfNote = scoreLevel.getHalfNote();
	    boolean dottedhalfNote = scoreLevel.getDottedHalfNote();
	    boolean quarterNote = scoreLevel.getQuarterNote();
	    boolean eighthNote = scoreLevel.getEighthNote();
	    boolean triplet = scoreLevel.getTriplet();
	    int tmpnum = scoreLevel.getTimeSignNumerator();
	      //tmpden = scoreLevel.getTimeSignDenominator();
	    int tmpdiv = scoreLevel.getTimeDivision();

	    currentTick = (int)((tmpnum/tmpdiv) * MidiHelper.PULSES_PER_QUARTER_NOTE);

	    // INITIALIZE Sequence and tracks
	    try {
	      ui.midiHelper.sequence = new Sequence(Sequence.PPQ, MidiHelper.PULSES_PER_QUARTER_NOTE);
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

	    updateTonality(); //when selected random tonality

	    if (!scoreLevel.isCustomNotes()) {
	      scoreLevel.initPitcheslist( scoreLevel.getNbnotes());
	    }

	    for (int r = 1; r <= (ui.numberOfMeasures * ui.numberOfRows); r++) { // creates all the measures
	      while (tpsmes != tmpnum) {
	        //System.out.println("tpsmes : " + tpsmes);
	        double tmp = Math.random();
            pitch = scoreLevel.getRandomPitch();
            if (scoreLevel.isCurrentKeyTreble() && pitch >= 71) {
              stemup = false; //SI
            } else if (scoreLevel.isCurrentKeyTreble() && pitch < 71) {
              stemup = true;
            } else if (scoreLevel.isCurrentKeyBass() && pitch >= 50) {
              stemup = false; //RE
            } else if (scoreLevel.isCurrentKeyBass() && pitch < 50) {
              stemup = true;
            }

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
              tripletPitches[1] = scoreLevel.tripletRandomPitch(tripletPitches[0]);
              tripletPitches[2] = scoreLevel.tripletRandomPitch(tripletPitches[0]);
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
	        currentXPos = ui.windowMargin + ui.keyWidth + alterationWidth + ui.timeSignWidth + ui.notesShift;
	        rowCount++;
	      }
	  }
	}
  
  private int addRhythm(double duration, int pitch, boolean stemup, int currentTick, int row, int newXPos) {
	    int tick = currentTick;
	    int velocity = 71;

	    final int TEXT = 0x01;
	    String text = "off";

        boolean silence = scoreLevel.getSilence();
        int tmpdiv =  scoreLevel.getTimeDivision();
        int tmpnum =  scoreLevel.getTimeSignNumerator();

	    if (duration == 0.333) { // do not handle pauses into triplets for now 
	      silence = false;
	    }

	    System.out.println("[addRhythm] pitch: " + pitch + "duration: " + duration + "stemUp " + stemup);

	    double tmpsilence = Math.random();
	    if (!silence || (silence && tmpsilence < 0.85) || (duration == 3 && tmpnum != 3)) {
	      rhythms.add(new Rhythm(duration, newXPos, pitch,  row, stemup, false, false, 0));
	      track.add(MidiHelper.createNoteOnEvent(pitch, velocity, tick));
	      mutetrack.add(MidiHelper.createNoteOnEvent(pitch, 0, tick));
	      tick += (int)((duration*tmpdiv)*MidiHelper.PULSES_PER_QUARTER_NOTE);
	      ui.midiHelper.addEvent(track, TEXT, text.getBytes(), tick);
	      ui.midiHelper.addEvent(mutetrack, TEXT, text.getBytes(), tick);
	      track.add(MidiHelper.createNoteOffEvent(pitch, tick));
	      mutetrack.add(MidiHelper.createNoteOffEvent(pitch, tick));
	    } else { // silence
	      rhythms.add(new Rhythm(duration, newXPos, pitch, row, false, false, true, 0));
	      track.add(MidiHelper.createNoteOffEvent(pitch, tick));
	      mutetrack.add(MidiHelper.createNoteOffEvent(pitch, tick));
	      tick += (int)((duration*tmpdiv)*MidiHelper.PULSES_PER_QUARTER_NOTE);
	      ui.midiHelper.addEvent(track, TEXT, text.getBytes(), tick);
	      ui.midiHelper.addEvent(mutetrack, TEXT, text.getBytes(), tick);
	    }
	    return tick;
	  }
  
  @Override
  public void initGame() {
    chooseNotePanel = new ChooseNotePanel(scoreLevel.getKey(), Jalmus.SCOREREADING, ui.bundle);
    chooseNotePanel.setOpaque(true); //content panes must be opaque 
    chooseNotePanel.setVisible(true);
    chooseNotePanel.okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        //Execute when button is pressed
        if (!chooseNotePanel.atLeast3Pitches()) {
          JOptionPane.showMessageDialog(null, "Choose at least three notes", "Warning",
              JOptionPane.ERROR_MESSAGE); 
        } else {
          notesDialog.setVisible(false);
          scoreLevel.setPitcheslist(chooseNotePanel.getPitches());
        }
      }
    });

    if (!sameRhythms) {
      createSequence();
    }

    notesDialog = new JDialog(ui, true);
    //    notesDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    notesDialog.setResizable(false);
  }
  
  private void handleStartButtonClicked() {
    if (gameStarted) {
      stopGame();
      gameStarted = false;
    } else if (ui.paintRhythms) {
      sameRhythms = true;
      ui.muteRhythms = true;
      initGame();
      startGame();
    }
  }
  
  private void handleListenButtonClicked() {
    sameRhythms = true;
    ui.muteRhythms = false;
    initGame();
    startGame();
  }

  private void handleNewButtonClicked() {
    sameRhythms = false;
    ui.muteRhythms = false;
    initGame();
    ui.paintRhythms = true; 
    ui.repaint(); //only to paint exercise
    gameStarted = false;
  }
  
  void drawScore(Graphics g) {
    Dimension size = ui.getSize();
    g.setColor(Color.black);
    alterationWidth = scoreLevel.getCurrentTonality().getAlterationsNumber() * 12;
    int tmpnum = scoreLevel.getTimeSignNumerator();

    int scoreLineWidth = ui.keyWidth + alterationWidth + ui.timeSignWidth;
    ui.firstNoteXPos = ui.windowMargin + ui.keyWidth + alterationWidth + ui.timeSignWidth + ui.notesShift;
    ui.numberOfMeasures = (size.width - (ui.windowMargin * 2) - scoreLineWidth) / (tmpnum * ui.noteDistance);
    ui.numberOfRows = (size.height - ui.scoreYpos - 50) / ui.rowsDistance; // 50 = window bottom margin
    int yPos = ui.scoreYpos;
    int vXPos = ui.windowMargin + scoreLineWidth + (tmpnum * ui.noteDistance);

    scoreLineWidth += ui.windowMargin + (ui.numberOfMeasures * (tmpnum * ui.noteDistance));

    for (int r = 0; r < ui.numberOfRows; r++) {
      // draw vertical separators first
      for (int v = 0; v < ui.numberOfMeasures; v++) {
        g.drawLine(vXPos + v * (tmpnum * ui.noteDistance), yPos, vXPos + v * (tmpnum * ui.noteDistance), yPos+40);
      }
      // draw the score 5 rows 
      for (int l = 0; l < 5; l++, yPos += 10) {
        g.drawLine(ui.windowMargin, yPos, scoreLineWidth, yPos);
      }
      yPos += (ui.rowsDistance - 50);
    }
  }
  
  void drawTimeSignature(Graphics g) {
    g.setFont(ui.musiSync.deriveFont(58f));

    int tmpnum = scoreLevel.getTimeSignNumerator();
    int tmpden = scoreLevel.getTimeSignDenominator();

    for (int rowNum = 0; rowNum < ui.numberOfRows; rowNum++) {
      String t = "";
      if (tmpnum == 4 && tmpden == 4) {
        t = "$";
      } else if (tmpnum == 3 && tmpden == 4) {
        t = "#";
      } else if (tmpnum == 2 && tmpden == 4) {
        t = "@";
      } else if (tmpnum == 6 && tmpden == 8) {
        t = "P";
      }
      g.drawString(t, ui.windowMargin + ui.keyWidth + alterationWidth,
          ui.scoreYpos+41 + rowNum * ui.rowsDistance);
    }
  }
}

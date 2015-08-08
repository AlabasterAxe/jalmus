package net.jalmus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.ColorUIResource;

public class SwingNoteReadingGame extends NoteReadingGame implements SwingGame {

  private SwingJalmus ui;

  private final ActionListener noteButtonActionListener = new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent e) {
      if ((gameStarted && ui.jalmus.selectedGame == Jalmus.NOTEREADING && !ui.jalmus.paused)) {
        if (!currentNote.getAlteration().equals("")) {  // NOTES AVEC ALTERATION
          if (((JButton)e.getSource()).getText().equals(currentNote.getAlteration())) {
            alterationOk = true;
          } else if (alterationOk &&
              ((JButton)e.getSource()).getText().equals(currentNote.getNom())) {
            rightAnswer();
          } else {
            wrongAnswer();
          }
        } else if (currentNote.getAlteration().equals("")) { // NOTE SANS ALTERATION
          if (((JButton)e.getSource()).getText() == currentNote.getNom()) {
            rightAnswer();
          } else {
            wrongAnswer();
          }
        }
      }
      ui.repaint();
    }
    
  };

  String DO;
  String RE;
  String MI;
  String FA;
  String SOL;
  String LA;
  String SI;

  final JButton doButton1 = new JButton();
  final JButton reButton = new JButton();
  final JButton miButton = new JButton();
  final JButton faButton = new JButton();
  final JButton solButton = new JButton();
  final JButton laButton = new JButton();
  final JButton siButton = new JButton();
  final JButton doButton2 = new JButton();
  final JButton flatButton1 = new JButton();
  final JButton sharpButton1 = new JButton();
  final JButton flatButton2 = new JButton();
  final JButton sharpButton2 = new JButton();
  JPanel noteButtonPanel = new JPanel();

  JPanel noteReadingNotesPanel = new JPanel(); // panel for choose type on fonts on first exercise
  private JComboBox<String> noteGameTypeComboBox;
  private JComboBox<String> noteGameSpeedComboBox;
  private JPanel gamePanel;

  private JComboBox<String> keyComboBox;
  private JComboBox<String> keySignatureCheckBox;
  private JPanel KeyPanel;
  private JComboBox<String> noteGroupComboBox;
  private JComboBox<String> noteCountComboBox;
  private JComboBox<String> intervalComboBox;
  private JComboBox<String> chordTypeComboBox;

  ChooseNotePanel chooseNoteP;
  JDialog notesDialog;
  JButton startButton = new JButton();    // button to start or stop game
  JButton preferencesButton = new JButton();  // button to access game preferences
  JPanel principal = new JPanel(); // panel principal
  Anim animationPanel;
  JPanel gameButtonPanel = new JPanel();

  int notecounter = 1;

  int scoreYpos = 110;
  
  int noteMargin;
  
  public void setUi(final SwingJalmus ui) {
    this.ui = ui;

    notesDialog = new JDialog(ui, true);
    notesDialog.setResizable(false);   

    doButton1.addActionListener(noteButtonActionListener);
    noteButtonPanel.add(doButton1);

    reButton.addActionListener(noteButtonActionListener);
    noteButtonPanel.add(reButton);

    miButton.addActionListener(noteButtonActionListener);
    noteButtonPanel.add(miButton);

    faButton.addActionListener(noteButtonActionListener);
    noteButtonPanel.add(faButton);

    solButton.addActionListener(noteButtonActionListener);
    noteButtonPanel.add(solButton);

    laButton.addActionListener(noteButtonActionListener);
    noteButtonPanel.add(laButton);

    siButton.addActionListener(noteButtonActionListener);
    noteButtonPanel.add(siButton);

    doButton2.addActionListener(noteButtonActionListener);
    noteButtonPanel.add(doButton2);

    // BOUTONS POUR ACCORDS
    sharpButton1.setText("#");
    sharpButton1.addActionListener(noteButtonActionListener);

    flatButton1.setText("b");
    flatButton1.addActionListener(noteButtonActionListener);

    sharpButton2.setText("#");
    sharpButton2.addActionListener(noteButtonActionListener);

    flatButton2.setText("b");
    flatButton2.addActionListener(noteButtonActionListener);

    startButton.setFocusable(false);
    ui.localizables.add(new Localizable.Button(startButton, "_start"));
    startButton.setPreferredSize(new Dimension(150, 20));
    startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleStartButtonClicked();
      }
    });
    
    preferencesButton.setFocusable(false);
    ui.localizables.add(new Localizable.Button(preferencesButton, "_menuPreferences"));
    preferencesButton.setPreferredSize(new Dimension(150, 20));
    preferencesButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ui.handlePreferencesClicked();
      }
    });
    
    noteButtonPanel.add(sharpButton1, 0);
    noteButtonPanel.add(flatButton1, 5);
    noteButtonPanel.add(flatButton2, 6);
    noteButtonPanel.add(sharpButton2, 11);
    noteButtonPanel.setLayout(new GridLayout(2, 4));
    
    gameButtonPanel.setLayout(new FlowLayout());
    gameButtonPanel.add(startButton);
    gameButtonPanel.add(noteButtonPanel);
    gameButtonPanel.add(preferencesButton);
    gameButtonPanel.setBackground(Color.white);
    
    principal.setLayout(new BorderLayout());

    animationPanel = new Anim(ui);
    principal.add(gameButtonPanel, BorderLayout.NORTH);
    principal.add(animationPanel, BorderLayout.CENTER);
    principal.setVisible(false);
  }

  @Override
  public void initGame() {
    super.initGame();
    
    noteButtonPanel.setPreferredSize(new Dimension(450, 40));
    noteButtonPanel.setBackground(Color.white);

    // BOUTONS INVISIBLES EN MODE NORMAL
    sharpButton1.setVisible(false);
    sharpButton2.setVisible(false);
    flatButton1.setVisible(false);
    flatButton2.setVisible(false);
    
    scoreYpos = 110;
    
    ColorUIResource def = new ColorUIResource(238, 238, 238);
    doButton1.setBackground(def);
    reButton.setBackground(def);
    miButton.setBackground(def);
    faButton.setBackground(def);
    solButton.setBackground(def);
    laButton.setBackground(def);
    siButton.setBackground(def); 
    
    if (noteLevel.isNormalgame() || noteLevel.isLearninggame()) {
      noteMargin = 220;
      ui.repaint();
    } else if (noteLevel.isInlinegame()) {
      noteMargin = 30;
      ui.repaint();
    }

    chooseNoteP = new ChooseNotePanel(noteLevel.getKey(), Jalmus.NOTEREADING, ui.bundle);
    chooseNoteP.setOpaque(true); //content panes must be opaque 
    chooseNoteP.setVisible(true);
    chooseNoteP.okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        //Execute when button is pressed
        if (! chooseNoteP.atLeast3Pitches()) {
          JOptionPane.showMessageDialog(null, "Choose at least three notes", "Warning", JOptionPane.ERROR_MESSAGE); 
        } else {
          notesDialog.setVisible(false);
          noteLevel.setPitcheslist(chooseNoteP.getPitches());
        }
      }
    }); 
  }

  public JPanel getPreferencesPanel() {
    /* 1er panel - type de jeu */

    noteGameTypeComboBox = new JComboBox<>();
    noteGameTypeComboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        switch (noteGameTypeComboBox.getSelectedIndex()) {
          case 0:
            noteLevel.setGametype("normal");
            break;
          case 1:
            noteLevel.setGametype("inline");
            break;
          case 2:
            noteLevel.setGametype("learning");
            break;
          default:
            throw new AssertionError("THE WALLS ARE MELTING!");
        }
      }
    });

    noteGameSpeedComboBox = new JComboBox<>();
    noteGameSpeedComboBox.addItem("Largo");
    noteGameSpeedComboBox.addItem("Adagio");
    noteGameSpeedComboBox.addItem("Moderato");
    noteGameSpeedComboBox.addItem("Allegro");
    noteGameSpeedComboBox.addItem("Presto");
    noteGameSpeedComboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent evt) {
        if (evt.getItemSelectable() == noteGameSpeedComboBox) {
          if (noteGameSpeedComboBox.getSelectedIndex() == 0) {
            noteLevel.setSpeed(28);
          } else if (noteGameSpeedComboBox.getSelectedIndex() == 1) {
            noteLevel.setSpeed(22);
          } else if (noteGameSpeedComboBox.getSelectedIndex() == 2) {
            noteLevel.setSpeed(16);
          } else if (noteGameSpeedComboBox.getSelectedIndex() == 3) {
            noteLevel.setSpeed(12);
          } else if (noteGameSpeedComboBox.getSelectedIndex() == 4) {
            noteLevel.setSpeed(8);
          }
        }
      }
    });

    gamePanel = new JPanel();
    gamePanel.add(noteGameTypeComboBox);
    gamePanel.add(noteGameSpeedComboBox);
    Localizable thing = new Localizable.NamedGroup(gamePanel, "_menuExercises");
    ui.localizables.add(thing);

    /* 2nd panel - Key */

    keyComboBox = new JComboBox<>();
    keyComboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent evt) {
        if (evt.getItemSelectable() == keyComboBox) {
          if (keyComboBox.getSelectedIndex() == 0) {
            noteLevel.setCurrentKey("treble");
            noteLevel.resetPitcheslist();
          } else if (keyComboBox.getSelectedIndex() == 1) {
            noteLevel.setCurrentKey("bass");
            noteLevel.resetPitcheslist();
          } else if (keyComboBox.getSelectedIndex() == 2) {
            noteLevel.setCurrentKey("both");
            noteLevel.resetPitcheslist();
          }
        }
      }
    });

    keySignatureCheckBox = new JComboBox<>();
    keySignatureCheckBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        initGame();

        if (keySignatureCheckBox.getSelectedIndex() == 0) {
          double tmp = Math.random();  // to choice same alteration for alterated notes
          String stmp;
          if (tmp < 0.5) {
            stmp = "#";
          } else {
            stmp = "b";
          }
          noteLevel.setRandomtonality(false);
          noteLevel.getCurrentTonality().init(0, stmp);
        } else if (keySignatureCheckBox.getSelectedIndex() == 15) {
          // choix de la tonalite au hasard au lancement du jeu
          noteLevel.setRandomtonality(true);
          noteLevel.getCurrentTonality().init(0, "r");
        } else {
          noteLevel.setRandomtonality(false);
          int index = keySignatureCheckBox.getSelectedIndex();
          
          String tonalityString = "#";
          if (index > 7) {
            index -= 7;
            tonalityString = "b";
          }

          noteLevel.getCurrentTonality().init(index, tonalityString);
          
        }}});

    KeyPanel = new JPanel(); // panel pour la Key du premier jeu
    KeyPanel.add(keyComboBox);
    KeyPanel.add(keySignatureCheckBox);
    ui.localizables.add(new Localizable.NamedGroup(KeyPanel, "_menuClef"));

    /* 3rd panel - Notes */

 // Game type choice
    
    noteGroupComboBox = new JComboBox<>();
    noteGroupComboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        if (noteGroupComboBox.getSelectedIndex() == 0) {
          noteLevel.setNotetype("notes");
          noteReadingNotesPanel.removeAll();
          noteReadingNotesPanel.add(noteGroupComboBox);
          noteReadingNotesPanel.add(noteCountComboBox);
          noteReadingNotesPanel.repaint();
          ui.preferencesDialog.repaint();
        } else if (noteGroupComboBox.getSelectedIndex() == 1) {
          noteLevel.setNotetype("accidentals");
          noteReadingNotesPanel.removeAll();
          noteReadingNotesPanel.add(noteGroupComboBox);
          noteReadingNotesPanel.add(noteCountComboBox);
          noteReadingNotesPanel.repaint();
          ui.preferencesDialog.repaint();
        } else if (noteGroupComboBox.getSelectedIndex() == 2) {
          noteLevel.setNotetype("custom");
          noteReadingNotesPanel.removeAll();
          noteReadingNotesPanel.add(noteGroupComboBox);
          ui.preferencesDialog.repaint();
          chooseNoteP = new ChooseNotePanel(noteLevel.getKey(), Jalmus.NOTEREADING, ui.bundle);
          chooseNoteP.updateTable(noteLevel.getPitcheslist());
          chooseNoteP.setOpaque(true); //content panes must be opaque 
          chooseNoteP.setVisible(true);
          chooseNoteP.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              //Execute when button is pressed
              if (!chooseNoteP.atLeast3Pitches()) {
                JOptionPane.showMessageDialog(null, "Choose at least three notes", "Warning", JOptionPane.ERROR_MESSAGE); 
              } else {
                notesDialog.setVisible(false);
                noteLevel.setPitcheslist(chooseNoteP.getPitches());
              }
            }
          });   

          notesDialog.setContentPane(chooseNoteP);
          notesDialog.setSize(650, 220);
          notesDialog.setLocationRelativeTo(ui);
          notesDialog.setVisible(true);

          chooseNoteP.setVisible(true);

          ui.add(notesDialog);

        } else if (noteGroupComboBox.getSelectedIndex() == 3) {
          noteLevel.setNotetype("intervals");

          noteReadingNotesPanel.removeAll();
          noteReadingNotesPanel.add(noteGroupComboBox);
          noteReadingNotesPanel.add(intervalComboBox);
          noteReadingNotesPanel.repaint();
          ui.preferencesDialog.repaint();

        } else if (noteGroupComboBox.getSelectedIndex() == 4) {
          noteLevel.setNotetype("chords");

          noteReadingNotesPanel.removeAll();
          noteReadingNotesPanel.add(noteGroupComboBox);
          noteReadingNotesPanel.add(chordTypeComboBox);
          noteReadingNotesPanel.repaint();
          ui.preferencesDialog.repaint();
        }
      }
    });

    noteCountComboBox = new JComboBox<>();
    noteCountComboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        
        if (noteCountComboBox.getSelectedIndex() == 0) {
          noteLevel.setNbnotes(3);
        } else if (noteCountComboBox.getSelectedIndex() == 1) {
          noteLevel.setNbnotes(5);
        } else if (noteCountComboBox.getSelectedIndex() == 2) {
          noteLevel.setNbnotes(9);
        } else if (noteCountComboBox.getSelectedIndex() == 3) {
          noteLevel.setNbnotes(15);
        } else if (noteCountComboBox.getSelectedIndex() == 4) {
          noteLevel.setNbnotes(0);
        }
      }
    });

    chordTypeComboBox = new JComboBox<>();
    chordTypeComboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        if (chordTypeComboBox.getSelectedIndex() == 0) {
          noteLevel.setChordtype("root");
        } else if (chordTypeComboBox.getSelectedIndex() == 1) {
          noteLevel.setChordtype("inversion");
        }
      }
    });

    intervalComboBox = new JComboBox<>();
    intervalComboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        if (intervalComboBox.getSelectedIndex() == 0) {
          noteLevel.setIntervaltype("second");
        } else if (intervalComboBox.getSelectedIndex() == 1) {
          noteLevel.setIntervaltype("third");
        } else if (intervalComboBox.getSelectedIndex() == 2) {
          noteLevel.setIntervaltype("fourth");
        } else if (intervalComboBox.getSelectedIndex() == 3) {
          noteLevel.setIntervaltype("fifth");
        } else if (intervalComboBox.getSelectedIndex() == 4) {
          noteLevel.setIntervaltype("sixth");
        } else if (intervalComboBox.getSelectedIndex() == 5) {
          noteLevel.setIntervaltype("seventh");
        } else if (intervalComboBox.getSelectedIndex() == 6) {
          noteLevel.setIntervaltype("octave");
        } else if (intervalComboBox.getSelectedIndex() == 7) {
          noteLevel.setIntervaltype("random");
        } else if (intervalComboBox.getSelectedIndex() == 8) {
          noteLevel.setIntervaltype("all");
        }
      }
    });

    ui.noteReadingNotesPanel.add(noteGroupComboBox);
    ui.noteReadingNotesPanel.add(noteCountComboBox);

    ui.localizables.add(new Localizable.NamedGroup(ui.noteReadingNotesPanel, "_menuNotes"));

    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(3, 1));
    panel.add(gamePanel);
    panel.add(KeyPanel);
    panel.add(ui.noteReadingNotesPanel);

    return panel;
  }

  @Override
  public String getPreferencesIconResource() {
    return "/images/rhythm.png";
  }

  @Override
  public String getPreferencesLocalizable() {
    return "_menuNotereading";
  }

  @Override
  public void updateLanguage(ResourceBundle bundle) {
    keyComboBox.removeAllItems();
    keyComboBox.addItem(bundle.getString("_trebleclef"));
    keyComboBox.addItem(bundle.getString("_bassclef"));
    keyComboBox.addItem(bundle.getString("_bothclefs"));
    
    noteGroupComboBox.removeAllItems();
    noteGroupComboBox.addItem(bundle.getString("_notes"));
    noteGroupComboBox.addItem(bundle.getString("_alterednotes"));
    noteGroupComboBox.addItem(bundle.getString("_customnotes"));
    noteGroupComboBox.addItem(bundle.getString("_intervals"));
    noteGroupComboBox.addItem(bundle.getString("_chords"));

    noteGameTypeComboBox.removeAllItems();
    noteGameTypeComboBox.addItem(bundle.getString("_normalgame"));
    noteGameTypeComboBox.addItem(bundle.getString("_linegame"));
    noteGameTypeComboBox.addItem(bundle.getString("_learninggame"));

    noteCountComboBox.removeAllItems();
    noteCountComboBox.addItem("3 " + bundle.getString("_menuNotes"));
    noteCountComboBox.addItem("5 " + bundle.getString("_menuNotes"));
    noteCountComboBox.addItem("9 " + bundle.getString("_menuNotes"));
    noteCountComboBox.addItem("15 " + bundle.getString("_menuNotes"));
    noteCountComboBox.addItem(bundle.getString("_all"));

    keySignatureCheckBox.removeAllItems();
    keySignatureCheckBox.addItem(bundle.getString("_nosharpflat"));
    keySignatureCheckBox.addItem("1 " + bundle.getString("_sharp"));
    keySignatureCheckBox.addItem("2 " + bundle.getString("_sharp"));
    keySignatureCheckBox.addItem("3 " + bundle.getString("_sharp"));
    keySignatureCheckBox.addItem("4 " + bundle.getString("_sharp"));
    keySignatureCheckBox.addItem("5 " + bundle.getString("_sharp"));
    keySignatureCheckBox.addItem("6 " + bundle.getString("_sharp"));
    keySignatureCheckBox.addItem("7 " + bundle.getString("_sharp"));
    keySignatureCheckBox.addItem("1 " + bundle.getString("_flat"));
    keySignatureCheckBox.addItem("2 " + bundle.getString("_flat"));
    keySignatureCheckBox.addItem("3 " + bundle.getString("_flat"));
    keySignatureCheckBox.addItem("4 " + bundle.getString("_flat"));
    keySignatureCheckBox.addItem("5 " + bundle.getString("_flat"));
    keySignatureCheckBox.addItem("6 " + bundle.getString("_flat"));
    keySignatureCheckBox.addItem("7 " + bundle.getString("_flat"));
    keySignatureCheckBox.addItem(bundle.getString("_random"));
    
    intervalComboBox.removeAllItems();
    intervalComboBox.addItem(bundle.getString("_second"));
    intervalComboBox.addItem(bundle.getString("_third"));
    intervalComboBox.addItem(bundle.getString("_fourth"));
    intervalComboBox.addItem(bundle.getString("_fifth"));
    intervalComboBox.addItem(bundle.getString("_sixth"));
    intervalComboBox.addItem(bundle.getString("_seventh"));
    intervalComboBox.addItem(bundle.getString("_octave"));
    intervalComboBox.addItem(bundle.getString("_random"));
    intervalComboBox.addItem(bundle.getString("_all"));

    chordTypeComboBox.removeAllItems();
    chordTypeComboBox.addItem(bundle.getString("_rootposition"));
    chordTypeComboBox.addItem(bundle.getString("_inversion"));

    notesDialog.setTitle("Choose notes to study");

    DO = bundle.getString("_do");
    RE = bundle.getString("_re");
    MI = bundle.getString("_mi");
    FA = bundle.getString("_fa");
    SOL = bundle.getString("_sol");
    LA = bundle.getString("_la");
    SI = bundle.getString("_si");

    doButton1.setText(bundle.getString("_do"));
    reButton.setText(bundle.getString("_re"));
    miButton.setText(bundle.getString("_mi"));
    faButton.setText(bundle.getString("_fa"));
    solButton.setText(bundle.getString("_sol"));
    laButton.setText(bundle.getString("_la"));
    siButton.setText(bundle.getString("_si"));
    doButton2.setText(bundle.getString("_do"));
  }

  @Override
  public void changeScreen() {
    ui.getContentPane().add(principal);
    noteButtonPanel.setVisible(true);
    gameButtonPanel.setVisible(true);
    principal.setVisible(true);
    System.out.println(noteLevel.getNbnotes());
    if (noteLevel.isNotesgame() && noteLevel.getCurrentTonality().getAlterationsNumber() == 0) {
      sharpButton1.setVisible(false);
      sharpButton2.setVisible(false);
      flatButton1.setVisible(false);
      flatButton2.setVisible(false);
      noteButtonPanel.validate();
    } else {
      sharpButton1.setVisible(true);
      sharpButton2.setVisible(true);
      flatButton1.setVisible(true);
      flatButton2.setVisible(true);
      noteButtonPanel.validate();
    }
  }
  
  public int[] serializePrefs() {
    int[] prefs = new int[8];
    prefs[0] = noteGameTypeComboBox.getSelectedIndex();
    prefs[1] = noteGameSpeedComboBox.getSelectedIndex();
    prefs[2] = keyComboBox.getSelectedIndex();
    prefs[4] = keySignatureCheckBox.getSelectedIndex();
    prefs[5] = noteGameSpeedComboBox.getSelectedIndex();
    prefs[6] = noteGroupComboBox.getSelectedIndex();
    if (noteGroupComboBox.getSelectedIndex() == 0 
      || noteGroupComboBox.getSelectedIndex() == 1) {
      prefs[7] = noteCountComboBox.getSelectedIndex();
    } else if (noteGroupComboBox.getSelectedIndex() == 2) {
      prefs[7] = intervalComboBox.getSelectedIndex();
    } else if (noteGroupComboBox.getSelectedIndex() == 3) {
      prefs[7] = chordTypeComboBox.getSelectedIndex();
    }
    return prefs;
  }
  
  public void deserializePrefs(int[] prefs) {
    noteGameTypeComboBox.setSelectedIndex(prefs[0]);
    noteGameSpeedComboBox.setSelectedIndex(prefs[1]);
    keyComboBox.setSelectedIndex(prefs[2]);
    keySignatureCheckBox.setSelectedIndex(prefs[4]);
    noteGameSpeedComboBox.setSelectedIndex(prefs[5]);
    noteGroupComboBox.setSelectedIndex(prefs[6]);
    if (noteGroupComboBox.getSelectedIndex() == 0 || noteGroupComboBox.getSelectedIndex()==1) {
      noteCountComboBox.setSelectedIndex(prefs[7]);
    } else if (noteGroupComboBox.getSelectedIndex() == 2) {
      intervalComboBox.setSelectedIndex(prefs[7]);
    } else if (noteGroupComboBox.getSelectedIndex() == 3) {
      chordTypeComboBox.setSelectedIndex(prefs[7]);
    }
  }
  
  @Override
  public void showResult() {
    if (currentScore.isWin()) {
      ui.scoreMessage.setTitle(ui.bundle.getString("_congratulations"));

      ui.textscoreMessage.setText("  "+currentScore.getNbtrue()+" "+ui.bundle.getString("_correct")+
          " / "+currentScore.getNbfalse()+" "+
          ui.bundle.getString("_wrong")+"  ");
      ui.scoreMessage.pack();
      ui.scoreMessage.setLocationRelativeTo(ui);

      ui.scoreMessage.setVisible(true);

      stopGame();
    } else if (currentScore.isLost()) {
      ui.scoreMessage.setTitle(ui.bundle.getString("_sorry"));

      ui.textscoreMessage.setText("  "+currentScore.getNbtrue()+" "+ui.bundle.getString("_correct")+
          " / "+currentScore.getNbfalse()+" "+
          ui.bundle.getString("_wrong")+"  ");
      ui.scoreMessage.pack();
      ui.scoreMessage.setLocationRelativeTo(ui);
      ui.scoreMessage.setVisible(true);

      stopGame();
    }
  }
  
  @Override
  public void startGame() {
    super.startGame();
    initGame();    // to stop last game

    if (noteLevel.isNormalgame() || noteLevel.isLearninggame()) {
      if (noteLevel.isNotesgame() || noteLevel.isAccidentalsgame() ||
          noteLevel.isCustomNotesgame()) {
        newNote();
      } else if (noteLevel.isChordsgame()) {
        newChord();
      } else if (noteLevel.isIntervalsgame()) {
        newinterval();
      }
    } else if (noteLevel.isInlinegame()) {
      createLine();
    }

    gameStarted = true;        // start of game
    startButton.setText(ui.bundle.getString("_stop"));
  }
  
  @Override
  public void startLevel() {
    if (!noteLevel.isMessageEmpty()) {
      ui.textlevelMessage.setText("  " + noteLevel.getMessage() + "  ");
      ui.levelMessage.setTitle(ui.bundle.getString("_information"));
      ui.levelMessage.pack();
      ui.levelMessage.setLocationRelativeTo(ui);
      ui.levelMessage.setVisible(true);
    } else {
      startButton.doClick();
    }        
  }
  
  @Override
  public void stopGame() {
    super.stopGame();

    gameStarted = false;
    startButton.setText(ui.bundle.getString("_start"));

    resetButtonColor();

    ui.midiHelper.stopSound();
  }
  
  void rightAnswer() {
    if (noteLevel.isLearninggame()) {
      if (noteLevel.isChordsgame() || noteLevel.isIntervalsgame()) {
        nextnote();
      } else if (ui.jalmus.isLessonMode && notecounter == noteLevel.getLearningduration()) {
        gameStarted = false;
        startButton.setText(ui.bundle.getString("_start"));
        ui.jalmus.nextLevel();
      } else {
        newNote();
      }
      resetButtonColor();
    } else {
      currentScore.addNbtrue(1);

      if (noteLevel.isNotesgame() || noteLevel.isAccidentalsgame() ||
          noteLevel.isCustomNotesgame()) {
        currentScore.addPoints(10);
      } else if (noteLevel.isChordsgame() || noteLevel.isIntervalsgame()) {
        currentScore.addPoints(5);
      }

      if (currentScore.isWin()) {
        gameStarted = false;
        startButton.setText(ui.bundle.getString("_start"));
        showResult();
      }

      if (noteLevel.isInlinegame() && position == line.length-1) {
        currentScore.setWin();
        gameStarted = false;
        startButton.setText(ui.bundle.getString("_start"));
        showResult();
      }

      if (noteLevel.isChordsgame() || noteLevel.isIntervalsgame()) {
        nextnote();
      } else {
        newNote();
      }
    }
  }
  
  void wrongAnswer() {
    alterationOk = false;

    if (!noteLevel.isLearninggame()) {
      currentScore.addNbfalse(1);
      // if (soundOnCheckBox.getState()) sonerreur.play();

      if (noteLevel.isNotesgame() || noteLevel.isAccidentalsgame() ||
          noteLevel.isCustomNotesgame()) {
        currentScore.addPoints(-20);
      } else if (noteLevel.isChordsgame() || noteLevel.isIntervalsgame()) {
        currentScore.addPoints(-10);
      }

      if (currentScore.isLost()) {
        gameStarted = false;
        startButton.setText(ui.bundle.getString("_start"));
        showResult();
      }
    }
  }
  
  /**
   * To choose a random height for a note according to base note
   *
   * @param nbupper1 and nbunder1 are the number of notes upper or under the base note for alone Key
   * @param nbupper2 and nbunder2 are the number of notes upper or under the base note for bass Key when here are both Keys
   */
  private int setNoteHeight(int nbupper1, int nbunder1, int nbupper2, int nbunder2) {
    int i;
    int h = 0;
    double tmp;

    // FIRST CASE alone Key

    if (noteLevel.isCurrentKeyTreble() || noteLevel.isCurrentKeyBass()) {
      tmp = Math.random();
      if (tmp < 0.5) {
        i = (int) Math.round((Math.random()*nbupper1));
      } else {
        i = -(int) Math.round((Math.random()*nbunder1));
      }
      // negative number between under note and 0 
      if (noteLevel.isCurrentKeyTreble()) {
        h = (ui.scoreYpos+noteLevel.getBasetreble()) - (i*5); // 20 for trebble key
      } else {
        h = (ui.scoreYpos+noteLevel.getBasebass()) - (i*5); // 4 far bass key
      }
    } else if (noteLevel.isCurrentKeyBoth()) {
    // SECOND CASE double Key
      int belowBase;
      if (nbupper2 < 0) {
        belowBase = nbupper2;
      } else {
        belowBase = 0;
      }

      double tmpcle = Math.random();
      if (tmpcle < 0.5) { // treble key
        tmp = Math.random();
        if (tmp < 0.5) {
          i = (int) Math.round((Math.random()*nbupper1));
        } else {
        // between 0 and upper note 
          i = -(int) Math.round((Math.random()*nbunder1));
        }
        // negative number between under note and 0 
        h = ui.scoreYpos + noteLevel.getBasetreble()-(i*5);
      } else {
        tmp = Math.random();
        if (tmp < 0.5) {
          i = (int) Math.round((Math.random()*nbupper2) + belowBase);
        } else {
          i = -(int) Math.round((Math.random()*nbunder2)) + belowBase;
        }
        h = ui.scoreYpos + noteLevel.getBasebass()+90-(i*5);
      }
    }
    return h;
  }
  
  void newinterval() {
	    ui.midiHelper.stopSound();
	    currentInterval.copy(intervalChoice());
	    if (noteLevel.isNormalgame() || noteLevel.isLearninggame()) {
	      posnote = 0;
	      currentNote = currentInterval.getNote(posnote);
	      if (ui.soundOnCheckBox.isSelected()) {
	        ui.midiHelper.synthNote(currentNote.getPitch(), 80, ui.midiHelper.noteDuration);
	      }
	    } else if (noteLevel.isInlinegame()) {
	      if (position < line.length-1) {
	        position += 1;
	        currentInterval.copy(lineint[position]);

	        posnote = 0;
	        //acourant.convertir(clecourante, typeaccord);
	        currentNote = currentInterval.getNote(posnote);
	        if (ui.soundOnCheckBox.isSelected()) {
	          ui.midiHelper.synthNote(currentNote.getPitch(), 80, ui.midiHelper.noteDuration);
	        }
	      }
	    }
	  }

  void newNote() {
	    if ((noteLevel.isNormalgame() || noteLevel.isLearninggame()) & gameStarted) {
	      notecounter++;
	      if (prevNote != 0 & ui.soundOnCheckBox.isSelected()) {
	        ui.midiHelper.stopSound();
	      }
	      currentNote.init();

	      if (noteLevel.isNotesgame() || noteLevel.isAccidentalsgame()) {
	        //choosing note with height to do change to choose note with pitch
	        currentNote.setHeight(setNoteHeight(noteLevel.getNbnotesupper(),
	                                  noteLevel.getNbnotesunder(),
	                                  noteLevel.getNbnotesupper(),
	                                  noteLevel.getNbnotesunder()));
	        while (currentNote.getHeight() == prevNote) {
	          currentNote.setHeight(setNoteHeight(noteLevel.getNbnotesupper(),
	              noteLevel.getNbnotesunder(), noteLevel.getNbnotesupper(),
	              noteLevel.getNbnotesunder()));
	        }
	        currentNote.updateNote(noteLevel, ui.scoreYpos, ui.bundle);
	        currentNote.updateAccidental(noteLevel, ui.bundle);
	        prevNote = currentNote.getHeight();
	      } else if (noteLevel.isCustomNotesgame()) {
	        // choosing note with pitch

	        currentNote.setPitch(noteLevel.getRandomPitch());
	        currentNote.updateNotePitch(noteLevel, ui.scoreYpos, ui.bundle);
	        prevNote = currentNote.getHeight();
	      }

	      currentNote.setX(ui.noteMargin+98);
	      System.out.println(currentNote.getNom());
	      System.out.println(currentNote.getHeight());
	      System.out.println(currentNote.getPitch());
	      //if (soundOnCheckBox.isSelected()) sons[indiceson(ncourante.getHeight())].play();

	      if (ui.soundOnCheckBox.isSelected()) {
	        ui.midiHelper.synthNote(currentNote.getPitch(), 80, ui.midiHelper.noteDuration);
	      }
	    } else if (noteLevel.isInlinegame()) {
	      //sons[indiceson(ncourante.getHeight())].stop();
	      if (prevNote != 0 & ui.soundOnCheckBox.isSelected()) {
	        ui.midiHelper.stopSound();
	      }
	      if (position < line.length-1) {
	        position += 1;
	        currentNote.copy(line[position]);
	      }
	      //if (soundOnCheckBox.isSelected()) sons[indiceson(ncourante.getHeight())].play();
	      if (ui.soundOnCheckBox.isSelected()) {
	        ui.midiHelper.synthNote(currentNote.getPitch(), 80, ui.midiHelper.noteDuration);
	      }
	    }
	  }

    void nextnote() {

	    if (noteLevel.isChordsgame()) {
	      if (posnote < 2) {
	        posnote += 1;

	        currentNote = currentChord.getNote(currentChord.realposition(posnote));
	        alterationOk = false;
	        if (ui.soundOnCheckBox.isSelected()) {
	          ui.midiHelper.synthNote(currentNote.getPitch(), 80, ui.midiHelper.noteDuration);
	        }
	      } else {
	        if (isLessonMode && notecounter == noteLevel.getLearningduration()) {
	          gameStarted = false;
	          startButton.setText(ui.bundle.getString("_start"));
	          ui.jalmus.nextLevel();
	        } else {
	          newChord();
	          notecounter++;
	        }
	      }
	    } else if (noteLevel.isIntervalsgame()) {
	      if (posnote == 0) {
	        posnote += 1;
	        currentNote = currentInterval.getNote(posnote);
	        alterationOk = false;
	        if (ui.soundOnCheckBox.isSelected()) {
	          ui.midiHelper.synthNote(currentNote.getPitch(), 80, ui.midiHelper.noteDuration);
	        }
	      } else {
	        if (isLessonMode && notecounter == noteLevel.getLearningduration()) {
	          gameStarted = false;
	          startButton.setText(ui.bundle.getString("_start"));
	          ui.jalmus.nextLevel();
	        } else {
	          newinterval();
	          notecounter++;
	        }
	      }
	    }
	  }
  
  void newChord() {

	    if (noteLevel.isNormalgame() || noteLevel.isLearninggame()) {
	      posnote = 0;
	      currentChord.copy(chordchoice());
	      currentChord.convert(noteLevel);
	      currentNote = currentChord.getNote(currentChord.realposition(posnote));
	      if (ui.soundOnCheckBox.isSelected()) {
	        ui.midiHelper.synthNote(currentNote.getPitch(), 80, ui.midiHelper.noteDuration);
	      }
	    } else if (noteLevel.isInlinegame()) {
	      if (position < line.length-1) {
	        position += 1;
	        currentChord.copy(lineacc[position]);

	        posnote = 0;
	        //acourant.convertir(clecourante,typeaccord);
	        currentNote = currentChord.getNote(currentChord.realposition(posnote));
	        if (ui.soundOnCheckBox.isSelected()) {
	          ui.midiHelper.synthNote(currentNote.getPitch(), 80, ui.midiHelper.noteDuration);
	        }
	      }
	    }
	  }

  private Interval intervalChoice() {
    int i = 1;
    if (noteLevel.isSecondInterval()) {
      i = 1;
    } else if (noteLevel.isThirdInterval()) {
      i = 2;
    } else if (noteLevel.isFourthInterval()) {
      i = 3;
    } else if (noteLevel.isFifthInterval()) {
      i = 4;
    } else if (noteLevel.isSixthInterval()) {
      i = 5;
    } else if (noteLevel.isSeventhInterval()) {
      i = 6;
    } else if (noteLevel.isOctaveInterval()) {
      i = 7;
    } else if (noteLevel.isRandomInterval()) {
      i = (int)Math.round((Math.random()*6))+1;
    }

    int h;
    if (noteLevel.isCurrentKeyBoth()) {
      h = setNoteHeight(13-i, 5, 6-i, 10);
      while (h == prevNote) {
        h = setNoteHeight(13-i, 5, 6-i, 10);
      }
    } else {
      h = setNoteHeight(13-i, 8, 13-i, 8);
      while (h == prevNote) {
        h = setNoteHeight(13-i, 8, 13-i, 8);
      }
    }

    Note n1 = new Note(h, ui.noteMargin + 98, 0);
    n1.updateNote(noteLevel, ui.scoreYpos, ui.bundle);
    n1.updateAccidental(noteLevel, ui.bundle);

    Note n2 = new Note(h-i*5, ui.noteMargin + 98, 0);
    n2.updateNote(noteLevel, ui.scoreYpos, ui.bundle);
    n2.updateAccidental(noteLevel, ui.bundle);

    String chordtype = "";
    int pitchDifference = n2.getPitch() - n1.getPitch();
    if (pitchDifference == 0 && i==1) {
      chordtype = "_seconddim";
    } else if (pitchDifference == 1 && i==1) {
      chordtype = "_secondmin";
    } else if (pitchDifference == 2 && i==1) {
      chordtype = "_secondmaj";
    } else if (pitchDifference == 3 && i==1) {
      chordtype = "_secondaug";
    } else if (pitchDifference == 2 && i==2) {
      chordtype = "_thirddim";
    } else if (pitchDifference == 3 && i==2) {
      chordtype = "_thirdmin";
    } else if (pitchDifference == 4 && i==2) {
      chordtype = "_thirdmaj";
    } else if (pitchDifference == 5 && i==2) {
      chordtype = "_thirdaug";
    } else if (pitchDifference == 4 && i==3) {
      chordtype = "_fourthdim";
    } else if (pitchDifference == 5 && i==3) {
      chordtype = "_fourthper";
    } else if (pitchDifference == 6 && i==3) {
      chordtype = "_fourthaug";
    } else if (pitchDifference == 6 && i==4) {
      chordtype = "_fifthdim";
    } else if (pitchDifference == 7 && i==4) {
      chordtype = "_fifthper";
    } else if (pitchDifference == 8 && i==4) {
      chordtype = "_fifthaug";
    } else if (pitchDifference == 7 && i==5) {
      chordtype = "_sixthdim";
    } else if (pitchDifference == 8 && i==5) {
      chordtype = "_sixthmin";
    } else if (pitchDifference == 9 && i==5) {
      chordtype = "_sixthmaj";
    } else if (pitchDifference == 10 && i==5) {
      chordtype = "_sixthaug";
    } else if (pitchDifference == 9 && i==6) {
      chordtype = "_seventhdim";
    } else if (pitchDifference == 10 && i==6) {
      chordtype = "_seventhmin";
    } else if (pitchDifference == 11 && i==6) {
      chordtype = "_seventhmaj";
    } else if (pitchDifference == 12 && i==6) {
      chordtype = "_seventhaug";//inusitï¿½e
    } else if (pitchDifference == 11 && i==7) {
      chordtype = "_octavedim";
    } else if (pitchDifference == 12 && i==7) {
      chordtype = "_octaveper";
    } else if (pitchDifference == 13 && i==7) {
      chordtype = "_octaveaug";
    }
    Interval inter = new Interval(n1, n2, ui.bundle.getString(chordtype));
    prevNote = n1.getHeight();

    return inter;
  }
  
  private Chord chordchoice() {
    int h;
    Note n1 = new Note(0, 0, 0);
    Note n2 = new Note(0, 0, 0);
    Note n3 = new Note(0, 0, 0);

    if (noteLevel.isCurrentKeyBoth()) {
      h = setNoteHeight(6, 5, -2, 10);
      while (h == prevNote) {
        h = setNoteHeight(6, 5, -2, 10);
      }
    } else {
      h = setNoteHeight(6, 8, 6, 8);
      while (h == prevNote) {
        h = setNoteHeight(6, 8, 6, 8);
      }
    }

    String minmaj = "";
    String salt = "";
    boolean ok = false;
    while (!ok) {

      n1 = new Note(h, ui.noteMargin + 98, 0);
      n1.updateNote(noteLevel, ui.scoreYpos, ui.bundle);
      n1.updateAccidental(noteLevel, ui.bundle);

      n2 = new Note(h-2*5, ui.noteMargin + 98, 0);
      n2.updateNote(noteLevel, ui.scoreYpos, ui.bundle);
      n2.updateAccidentalInChord(noteLevel.getCurrentTonality(), n1.getPitch(), 2, ui.bundle); //deuxieme note

      n3 = new Note(h-4*5, ui.noteMargin+98, 0);
      n3.updateNote(noteLevel, ui.scoreYpos, ui.bundle);
      n3.updateAccidentalInChord(noteLevel.getCurrentTonality(), n1.getPitch(), 3, ui.bundle); //troisieme note

      if (n2.getPitch()-n1.getPitch() == 3 && n3.getPitch()-n1.getPitch()==7) {
        minmaj = ui.minor;
        ok = true;
      } else if (n2.getPitch()-n1.getPitch() == 3 && n3.getPitch()-n1.getPitch()==6) {
        minmaj = "dim";
        ok = true;
      } else if (n2.getPitch()-n1.getPitch() == 4 && n3.getPitch()-n1.getPitch()==7) {
        minmaj = ui.major;
        ok = true;
      } else if (n2.getPitch()-n1.getPitch() == 4 && n3.getPitch()-n1.getPitch()==8) {
        minmaj = "aug";
        ok = true;
      } else {
        ok = false;
      }

      if (n1.getAlteration() == "n") {
        salt = "";
      } else {
        salt = n1.getAlteration();
      }
    }
    Chord a = new Chord(n1, n2, n3, n1.getNom()+salt+" "+minmaj, 0);
    prevNote = n1.getHeight();
    return a;
  }
  
  @Override
  public void nextGame() {
    super.nextGame();
    noteLevel.copy((NoteLevel) ui.jalmus.currentLesson.getLevel());
    noteLevel.updatenbnotes(ui.jalmus.piano);

    ui.jalmus.selectedGame = Jalmus.NOTEREADING ;
    initGame();
    ui.changeScreen(isLessonMode, ui.jalmus.currentLesson, ui.jalmus.selectedGame);
    noteLevel.printtest();

    startLevel();
  }

  void createLine() {
    Dimension size = ui.getSize();
    Chord a = new Chord(currentNote, currentNote, currentNote, "", 0);
    Interval inter = new Interval(currentNote, currentNote, "");

    if (noteLevel.isNotesgame() || noteLevel.isAccidentalsgame()) {
      line[0] = new Note(setNoteHeight(noteLevel.getNbnotesupper(),
          noteLevel.getNbnotesunder(), noteLevel.getNbnotesupper(),
          noteLevel.getNbnotesunder()), size.width-ui.noteMargin, 0);
      line[0].updateNote(noteLevel, ui.scoreYpos, ui.bundle);
      line[0].updateAccidental(noteLevel, ui.bundle);

      for (int i = 1; i < line.length; i++) {
        int tmph = setNoteHeight(noteLevel.getNbnotesupper(),
            noteLevel.getNbnotesunder(), noteLevel.getNbnotesupper(),
            noteLevel.getNbnotesunder());
        while (tmph == line[i-1].getHeight()) {
          tmph = setNoteHeight(noteLevel.getNbnotesupper(),
              noteLevel.getNbnotesunder(), noteLevel.getNbnotesupper(),
              noteLevel.getNbnotesunder()); // pour �viter les r�p�titions
        }

        line[i] = new Note(tmph, size.width-ui.noteMargin+i*35, 0);
        line[i].updateNote(noteLevel, ui.scoreYpos, ui.bundle);
        line[i].updateAccidental(noteLevel, ui.bundle);
      }
    } else if (noteLevel.isCustomNotesgame()) {

      line[0] = new Note(0, size.width-ui.noteMargin, noteLevel.getRandomPitch() );
      line[0].updateNotePitch(noteLevel, ui.scoreYpos, ui.bundle);

      for (int i = 1; i < line.length; i++) {
        int tmpp = noteLevel.getRandomPitch();
        while (tmpp == line[i-1].getPitch()) {
          tmpp = noteLevel.getRandomPitch(); // to avoid same pitch
        }

        line[i] = new Note(0, size.width-ui.noteMargin+i*35, tmpp);
        line[i].updateNotePitch(noteLevel, ui.scoreYpos, ui.bundle);
      }

      position = 0;
      currentNote = line[position]; // initialisa tion avec la premiï¿½re note

      if (ui.soundOnCheckBox.isSelected()) {
        ui.midiHelper.synthNote(currentNote.getPitch(), 80, ui.midiHelper.noteDuration);
      }
    } else if (noteLevel.isChordsgame()) {
      // voir pour precedant
      for (int i = 0; i < line.length; i++) {
        a.copy(chordchoice());
        a.updatex(size.width-ui.noteMargin+i*50);
        lineacc[i] = new Chord(a.getNote(0), a.getNote(1), a.getNote(2), a.getName(),
            a.getInversion());
        lineacc[i].convert(noteLevel);
      }

      position = 0;
      posnote = 0;
      currentChord.copy(lineacc[position]);
      currentNote = currentChord.getNote(currentChord.realposition(posnote));
      if (ui.soundOnCheckBox.isSelected()) {
        ui.midiHelper.synthNote(currentNote.getPitch(), 80, ui.midiHelper.noteDuration);
      }
    } else if (noteLevel.isIntervalsgame()) {
      // voir pour precedant
      for (int i = 0; i < line.length; i++) {
        inter.copy(intervalChoice());
        //i = nouvelintervalle();
        inter.updatex(size.width - ui.noteMargin + i*65);
        lineint[i] = new Interval(inter.getNote(0), inter.getNote(1), inter.getName());
      }
      position = 0;
      posnote = 0;

      currentInterval.copy(lineint[position]);
      currentNote = currentInterval.getNote(posnote); //0
      if (ui.soundOnCheckBox.isSelected()) {
        ui.midiHelper.synthNote(currentNote.getPitch(), 80, ui.midiHelper.noteDuration);
      }
    }
  }
  
  void resetButtonColor() {
    ColorUIResource def = new ColorUIResource(238, 238, 238);
    doButton1.setBackground(def);
    reButton.setBackground(def);
    miButton.setBackground(def);
    faButton.setBackground(def);
    solButton.setBackground(def);
    laButton.setBackground(def);
    siButton.setBackground(def);
    sharpButton1.setBackground(def);
    flatButton2.setBackground(def);
  }
  
  void applyButtonColor() {
    resetButtonColor();

    Color red = new Color(242, 179, 112);
    if (currentNote.getNom().equals(doButton1.getText())) {
      doButton1.setBackground(red);
    } else if (currentNote.getNom().equals(reButton.getText())) {
      reButton.setBackground(red);
    } else if (currentNote.getNom().equals(miButton.getText())) {
      miButton.setBackground(red);
    } else if (currentNote.getNom().equals(faButton.getText())) {
      faButton.setBackground(red);
    } else if (currentNote.getNom().equals(solButton.getText())) {
      solButton.setBackground(red);
    } else if (currentNote.getNom().equals(laButton.getText())) {
      laButton.setBackground(red);
    } else if (currentNote.getNom().equals(siButton.getText())) {
      siButton.setBackground(red);
    }

    if (currentNote.getAlteration().equals(sharpButton1.getText())) {
      sharpButton1.setBackground(red);
    } else if (currentNote.getAlteration().equals(flatButton1.getText())) {
      flatButton2.setBackground(red);
    }
  }

  void drawChord(Chord a, Graphics g, boolean accordcourant) {
    Dimension d = ui.getSize();

    if (a.getNote(posnote).getX() < d.width-noteMargin &&
        a.getNote(posnote).getX() >= noteMargin+98 && gameStarted) {
      // NOTE DANS LIMITES
      a.paint(posnote, noteLevel, g, ui.musiSync, accordcourant, ui,
          scoreYpos, ui.bundle);
      //g.drawString("Renv" + a.renvst,100,100);
    } else {
      if (noteLevel.isNormalgame()) {
        currentScore.addPoints(-20);

        if (currentScore.isLost()) {
          gameStarted = false;
          startButton.setText(ui.bundle.getString("_start"));
          ui.midiHelper.stopSound();
          showResult();
        }

        if (gameStarted) {
          newChord();
        }
      } else if (noteLevel.isLearninggame()) {
        newChord();
        resetButtonColor();
      } else if (noteLevel.isInlinegame() && gameStarted && noteLevel.isChordsgame() && 
          lineacc[position].getNote(0).getX() < noteMargin+98) {
        // If the current note (except the last) touch the limit
        currentScore.setPoints(0);
        currentScore.setLost();
        gameStarted = false;
        startButton.setText(ui.bundle.getString("_start"));
        ui.midiHelper.stopSound();
        showResult();
      }
    }
  }
  
  void drawNote(Note note, Graphics g, Font f, Color couleur) {
    Dimension size = ui.getSize();

    g.setColor(couleur);
    if (note.getX() < size.width - noteMargin && note.getX() >= noteMargin + 98 && gameStarted) { // NOTE DANS LIMITES
      if (noteLevel.isAccidentalsgame() || noteLevel.isCustomNotesgame()) {
        note.paint(noteLevel, g, f, 9, 0, scoreYpos, ui, couleur, ui.bundle);
      } else {
        note.paint(noteLevel, g, f, 0, 0, scoreYpos, ui, couleur, ui.bundle);
      }
    } else {
      if (noteLevel.isNormalgame()) {
        currentScore.addPoints(-20);
        if (currentScore.isLost()) {
          gameStarted = false;
          startButton.setText(ui.bundle.getString("_start"));
          showResult();
        }
        newNote();
      } else if (noteLevel.isLearninggame()) {
        newNote();
        resetButtonColor();
      } else if (noteLevel.isInlinegame() && gameStarted) {
        if (line[position].getX() < noteMargin+98) { // Si la note courant (sauf la derniï¿½re)dï¿½passe la limite ici marge +25
          currentScore.setPoints(0);
          currentScore.setLost();
          gameStarted = false;
          startButton.setText(ui.bundle.getString("_start"));
          showResult();
        }
      }
    }
  }

  void drawInterval(Interval inter, Graphics g, boolean Intervallecourant) {
    Dimension size = ui.getSize();

    if (inter.getNote(posnote).getX() < size.width - noteMargin &&
        inter.getNote(posnote).getX() >= noteMargin + 98 && gameStarted) {
      // NOTE DANS LIMITES
      inter.paint(posnote, noteLevel, g, ui.musiSync, scoreYpos,
          ui.bundle, Intervallecourant, ui);
      //g.drawString("Renv" + a.renvst,100,100);
    } else {
      if (noteLevel.isNormalgame()) {
        currentScore.addPoints(-20);
        if (currentScore.isLost()) {
          gameStarted = false;
          startButton.setText(ui.bundle.getString("_start"));
          ui.midiHelper.stopSound();
          showResult();
        }

        if (gameStarted) {
          newinterval();
        }
      } else if (noteLevel.isLearninggame()) {
        newinterval();
        resetButtonColor();
      } else if (noteLevel.isInlinegame() 
             && gameStarted 
             && lineint[position].getNote(0).getX() < noteMargin+98) {
        // Si la note courant dï¿½passe la limite ici marge +25
        currentScore.setPoints(0);
        currentScore.setLost();
        gameStarted = false;
        startButton.setText(ui.bundle.getString("_start"));
        ui.midiHelper.stopSound();
        showResult();
      }
    }
  }

  void drawInlineNotes(Graphics g, Font f) {
    for (int i = position; i < line.length; i++) {
      // n'affiche que la ligne ï¿½ partir de la position
      if (noteLevel.isNotesgame() || noteLevel.isAccidentalsgame() ||
          noteLevel.isCustomNotesgame()) {
        drawNote(line[i], g, f, Color.black);
      } else if (noteLevel.isChordsgame()) {
        drawChord(lineacc[i], g, i == position);
      } else if (noteLevel.isIntervalsgame()) {
        drawInterval(lineint[i], g, i == position);
      }
    }
  }
  
  void handleStartButtonClicked() {
    if (gameStarted) {
      stopGame();
      initGame(); //stop the game before restart
    } else {
      ui.requestFocus();
      startGame();
      if (!ui.renderingThread.isAlive()) {
        ui.renderingThread.start();
      }
    }
  }
  
  public JPanel getGamePanel() {
    return principal;
  }
  
  public void handleKeyTyped(KeyEvent evt) {
    char ch = evt.getKeyChar();  // The character typed.
    
    if (gameStarted) {
      if (ch == 'P' || ch == 'p') {
        paused = true;

        int n = JOptionPane.showConfirmDialog(ui, "", ui.bundle.getString("_gamepaused"),
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (n == 0) {
          paused = false;
        }
      }

      if (!paused && noteLevel.isNotesgame()) {
        if (ch == 'Q' || ch=='q' || ch=='A' || ch=='a' || ch=='S' || ch=='s' || ch == 'D' ||
            ch=='d' || ch=='F' || ch=='f' || ch=='G' || ch=='g' || ch == 'H' || ch=='h' || ch=='J' ||
            ch=='j' || ch=='K' || ch=='k') {

          if (((ui.language == "fr" && (ch=='Q' || ch=='q'))
                || ((ui.language == "en" || ui.language =="es" || ui.language =="de") && (ch=='A' || ch=='a')))
              && currentNote.getNom() == DO)
          {
            rightAnswer();
          } else if ((ch == 'S' || ch=='s') && currentNote.getNom().equals(RE)) {
            rightAnswer();
          } else if ((ch == 'D' || ch=='d') && currentNote.getNom().equals(MI)) {
            rightAnswer();
          } else if ((ch == 'F' || ch=='f') && currentNote.getNom().equals(FA)) {
            rightAnswer();
          } else if ((ch == 'G' || ch=='g') && currentNote.getNom().equals(SOL)) {
            rightAnswer();
          } else if ((ch == 'H' || ch=='h') && currentNote.getNom().equals(LA)) {
            rightAnswer();
          } else if ((ch == 'J' || ch=='j') && currentNote.getNom().equals(SI)) {
            rightAnswer();
          } else if ((ch == 'K' || ch=='k') && currentNote.getNom().equals(DO)) {
            rightAnswer();
          } else {
            wrongAnswer();
          }
        }
      }
    }
  }
}

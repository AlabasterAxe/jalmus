package net.jalmus;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.ColorUIResource;

public class SwingNoteReadingGame extends NoteReadingGame implements SwingGame {

  private JalmusUi ui;
  
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

  int scoreYpos = 110;
  
  int noteMargin;
  
  public void setUi(JalmusUi ui) {
    this.ui = ui;
    notesDialog = new JDialog(ui, true);
    //    notesDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    notesDialog.setResizable(false);   
  }
  
  @Override
  public void initGame() {
    super.initGame();
    
    scoreYpos = 110;
    
    ColorUIResource def = new ColorUIResource(238, 238, 238);
    ui.doButton1.setBackground(def);
    ui.reButton.setBackground(def);
    ui.miButton.setBackground(def);
    ui.faButton.setBackground(def);
    ui.solButton.setBackground(def);
    ui.laButton.setBackground(def);
    ui.siButton.setBackground(def); 
    
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
    ui.localizables.add(new Localizable.NamedGroup(gamePanel, "_menuExercises"));

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
    String seconde = bundle.getString("_second");
    String tierce = bundle.getString("_third");
    String quarte = bundle.getString("_fourth");
    String quinte = bundle.getString("_fifth");
    String sixte = bundle.getString("_sixth");
    String septieme = bundle.getString("_seventh");
    String octave = bundle.getString("_octave");

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
    intervalComboBox.addItem(seconde);
    intervalComboBox.addItem(tierce);
    intervalComboBox.addItem(quarte);
    intervalComboBox.addItem(quinte);
    intervalComboBox.addItem(sixte);
    intervalComboBox.addItem(septieme);
    intervalComboBox.addItem(octave);
    intervalComboBox.addItem(bundle.getString("_random"));
    intervalComboBox.addItem(bundle.getString("_all"));
    
    chordTypeComboBox.removeAllItems();
    chordTypeComboBox.addItem(bundle.getString("_rootposition"));
    chordTypeComboBox.addItem(bundle.getString("_inversion"));

    notesDialog.setTitle("Choose notes to study");
  }
  
  void changeScreen() {
    ui.gameButtonPanel.setVisible(true);
    ui.noteButtonPanel.setVisible(true);
    ui.principal.setVisible(true);
    System.out.println(noteLevel.getNbnotes());
    if (noteLevel.isNotesgame() && noteLevel.getCurrentTonality().getAlterationsNumber() == 0) {
      ui.sharpButton1.setVisible(false);
      ui.sharpButton2.setVisible(false);
      ui.flatButton1.setVisible(false);
      ui.flatButton2.setVisible(false);
      ui.noteButtonPanel.validate();
    } else {
      ui.sharpButton1.setVisible(true);
      ui.sharpButton2.setVisible(true);
      ui.flatButton1.setVisible(true);
      ui.flatButton2.setVisible(true);
      ui.noteButtonPanel.validate();
    }
  }
  
  int[] serializePrefs() {
    int[] prefs = {0,0,0,0,0,0,0,0};
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
  
  void deserializePrefs(int[] prefs) {
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
}

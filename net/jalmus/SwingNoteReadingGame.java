package net.jalmus;

import java.awt.GridLayout;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class SwingNoteReadingGame implements SwingGame {

  private JalmusUi ui;
  private NoteReadingGame game;
  
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
  
  public SwingNoteReadingGame () {
    this.game = new NoteReadingGame();
  }
  
  public void setUi(JalmusUi ui) {
    this.ui = ui;
  }
  
  public JPanel getPreferencesPanel() {
    /* 1er panel - type de jeu */

    noteGameTypeComboBox = new JComboBox<>();
    noteGameTypeComboBox.addItemListener(ui);

    noteGameSpeedComboBox = new JComboBox<>();
    noteGameSpeedComboBox.addItem("Largo");
    noteGameSpeedComboBox.addItem("Adagio");
    noteGameSpeedComboBox.addItem("Moderato");
    noteGameSpeedComboBox.addItem("Allegro");
    noteGameSpeedComboBox.addItem("Presto");
    noteGameSpeedComboBox.addItemListener(ui);

    gamePanel = new JPanel();
    gamePanel.add(noteGameTypeComboBox);
    gamePanel.add(noteGameSpeedComboBox);
    ui.localizables.add(new Localizable.NamedGroup(gamePanel, "_menuExercises"));

    /* 2nd panel - Key */

    keyComboBox = new JComboBox<>();
    keyComboBox.addItemListener(ui);

    keySignatureCheckBox = new JComboBox<>();
    keySignatureCheckBox.addItemListener(ui);

    KeyPanel = new JPanel(); // panel pour la Key du premier jeu
    KeyPanel.add(keyComboBox);
    KeyPanel.add(keySignatureCheckBox);
    ui.localizables.add(new Localizable.NamedGroup(KeyPanel, "_menuClef"));

    /* 3rd panel - Notes */

    noteGroupComboBox = new JComboBox<>();
    noteGroupComboBox.addItemListener(ui);

    noteCountComboBox = new JComboBox<>();
    noteCountComboBox.addItemListener(ui);

    intervalComboBox = new JComboBox<>();
    intervalComboBox.addItemListener(ui);

    chordTypeComboBox = new JComboBox<>();
    chordTypeComboBox.addItemListener(ui);

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
  }
  
  
}

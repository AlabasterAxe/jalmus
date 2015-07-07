package net.jalmus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class JalmusUi {

  //----------------------------------------------------------------
  // Menu
  private final Jalmus jalmus;

  boolean selectmidi_forlang; // is true when combobox selection occurs during language initialization

  ResourceBundle bundle;
  private final Collection<Localizable> localizables = new ArrayList<Localizable>();

  private String seconde;
  private String tierce;
  private String quarte;
  private String quinte;
  private String sixte;
  private String septieme;

  private String octave;
  String minor;
  String major;

  String DO;
  String RE;
  String MI;
  String FA;
  String SOL;
  String LA;
  String SI;

  String tlicence;
  String tcredits;

  String[] pathsubdir = new String[16];
  //----------------------------------------------------------------
  // Menu

  // Mise en place du menu
  JMenu lessonsMenu;
  private JMenu[] lessonsMenuDir = new JMenu[16];
  JMenuItem[][] lessonsMenuItem = new JMenuItem[16][26];

  String pasclavier = "Pas de clavier MIDI             ";

  JMenuBar maBarre = new JMenuBar();
  private JMenu menuParameters = new JMenu();
  JMenuItem menuPrefs = new JMenuItem(new ImageIcon(getClass().getResource("/images/prefs.png")));
  JMenuItem menuMidi = new JMenuItem(new ImageIcon(getClass().getResource("/images/midi.png")));
  private JMenu languages = new JMenu();
  JRadioButtonMenuItem rblanguagefr = new JRadioButtonMenuItem();
  JRadioButtonMenuItem rblanguagede = new JRadioButtonMenuItem();
  JRadioButtonMenuItem rblanguagees = new JRadioButtonMenuItem();
  JRadioButtonMenuItem rblanguageen = new JRadioButtonMenuItem();
  JRadioButtonMenuItem rblanguageit = new JRadioButtonMenuItem();
  JRadioButtonMenuItem rblanguageda = new JRadioButtonMenuItem();
  JRadioButtonMenuItem rblanguagetr = new JRadioButtonMenuItem();
  JRadioButtonMenuItem rblanguagefi = new JRadioButtonMenuItem();
  JRadioButtonMenuItem rblanguageko = new JRadioButtonMenuItem();
  JRadioButtonMenuItem rblanguageeo = new JRadioButtonMenuItem();
  JRadioButtonMenuItem rblanguagepl = new JRadioButtonMenuItem();
  JRadioButtonMenuItem rblanguageiw = new JRadioButtonMenuItem();
  JRadioButtonMenuItem rblanguagegr = new JRadioButtonMenuItem();

  private JMenu helpMenu = new JMenu();
  JMenuItem helpSummary = new JMenuItem(new ImageIcon(getClass().getResource("/images/aide.png")));
  JMenuItem siteinternet = new JMenuItem(new ImageIcon(getClass().getResource("/images/internet.png")));
  JMenuItem aboutMenuItem = new JMenuItem(new ImageIcon(getClass().getResource("/images/about.png")));

  //----------------------------------------------------------------
  // GAME BUTTONS - NOTES/GO
  JPanel pgamebutton = new JPanel();

  JButton doButton1;
  JButton reButton;
  JButton miButton;
  JButton faButton;
  JButton solButton;
  JButton laButton;
  JButton siButton;
  JButton doButton2;
  JButton flatButton1;
  JButton sharpButton1;
  JButton flatButton2;
  JButton sharpButton2;
  JPanel pnotes = new JPanel();

  JButton startButton;    // button to start or stop game
  JButton listenButton;    // button for listen exercise in rhythm game
  JButton newButton;    // button for new exercise in rhythm game
  JButton preferencesButton;  // button to access game preferences

  //----------------------------------------------------------------
  // Dialogs

  JDialog preferencesDialog;
  static final int NOTE_READING_TAB = 0;
  static final int RHYTHM_READING_TAB = 1;
  static final int SCORE_READING_TAB = 2;

  JTabbedPane preferencesTabbedPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT); // panel pour les parametres
  JComboBox<String> noteGameTypeComboBox; //Game type
  JComboBox<String> noteGameSpeedComboBox; // button to choose speed
  JComboBox<String> keyComboBox; //  drop down combo to select the key
  JComboBox<String> keySignatureCheckBox; // button to choose tonality
  JPanel noteReadingNotesPanel = new JPanel(); // panel for choose type on fonts on first exercise
  JComboBox<String> noteGroupComboBox; // to choose notes, intervals or chords
  JComboBox<String> noteCountComboBox; //  to choose number of notes
  JComboBox<String> intervalComboBox; //  to choose intervals type
  JComboBox<String> chordTypeComboBox; // to choose chords type

  JComboBox<String> rhythmGameTypeComboBox;
  JComboBox<String> rhythmGameSpeedComboBox;
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

  JComboBox<String> scoreGameTypeComboBox; //type of games
  JComboBox<String> scoreGameSpeedComboBox; // button to choose the speed
  JComboBox<String> scoreKeyComboBox; //  drop down combo box to select the key
  JComboBox<String> scoreNotesComboBox; // drop down combo box to choose the number of notes
  JComboBox<String> scoreAlterationsComboBox; // drop down combo box to choose the pitch

  JCheckBox scorewholeCheckBox;
  JCheckBox scorehalfCheckBox;
  JCheckBox scoredottedhalfCheckBox;
  JCheckBox scorequarterCheckBox;
  JCheckBox scoreeighthCheckBox;
  JCheckBox scorerestCheckBox;
  JCheckBox scoreTripletCheckBox;
  JLabel    scoreTimeSignLabel;
  JComboBox<String> scoreTimeSignComboBox;
  JCheckBox scoreMetronomeCheckBox;
  JCheckBox scoreMetronomeShowCheckBox;

  //----SAVE DIALOG

  JDialog saveDialog = new JDialog();
  private JPanel labelPanel = new JPanel(new GridLayout(2, 1));
  private JPanel fieldPanel = new JPanel(new GridLayout(2, 1));

  private JPanel savePanel = new JPanel();
  JTextField lessonName = new JTextField(20);
  JTextField lessonMessage = new JTextField(20);
  private JButton oksaveButton = new JButton();

  //----

  JDialog levelMessage = new JDialog();
  private JPanel plevelMessage = new JPanel();
  JLabel textlevelMessage = new JLabel();
  private JPanel pButtonlevelMessage = new JPanel();
  JButton oklevelMessage = new JButton();

  // JDialog for score message
  JDialog scoreMessage = new JDialog();
  private JPanel pscoreMessage = new JPanel();
  JLabel textscoreMessage = new JLabel();
  private JPanel pButtonscoreMessage = new JPanel();
  JButton okscoreMessage = new JButton();

  //----

  JDialog midiOptionsDialog;

  JCheckBox soundOnCheckBox;
  JComboBox<String> instrumentsComboBox;
  JComboBox<String> keyboardLengthComboBox; // for length-number of touchs of keyboard
  //private JComboBox transpositionComboBox; // for transposition MIDI keyboard
  JSpinner transpositionSpinner; // for transposition MIDI keyboard
  private SpinnerNumberModel m_numberSpinnerModel;
  private JLabel transpositionLabel = new JLabel();

  JSlider latencySlider = new JSlider(JSlider.HORIZONTAL, 0, 250, 0);

  JCheckBox keyboardsoundCheckBox;

  JComboBox<String> midiInComboBox;
  JComboBox<String> midiOutComboBox;

  DefaultComboBoxModel<String> midiOutComboBoxModel = new DefaultComboBoxModel<String>();    
  DefaultComboBoxModel<String> midiInComboBoxModel = new DefaultComboBoxModel<String>();

  //----

  JDialog aboutDialog;
  JPanel aboutPanel = new JPanel();
  private JPanel paproposboutons = new JPanel(); // panel for buttons
  JTextArea aboutPanelTextArea;

  JButton bcredits;
  JButton blicence;
  JButton bfermer;

  //----

  //For table to choose notes on exercises
  JDialog notesDialog;
  ChooseNotePanel chooseNoteP;

  JDialog scoreNotesDialog;
  ChooseNotePanel scoreChooseNoteP;
  //----

  JPanel principal = new JPanel(); // panel principal

  Properties settings = new Properties();

  JalmusUi(Jalmus jalmus) {
    this.jalmus = jalmus;
  }

  void init(String paramlanguage) {
    try {
      settings.load(new FileInputStream("settings.properties"));
      //  System.out.println("language = " + settings.getProperty("language"));
      settings.list(System.out);
      //if no language in command line then search in settings file
      if ("".equals(paramlanguage)) {
        paramlanguage = settings.getProperty("language");
      }
    } catch (Exception e) {
      System.out.println(e);
    }

    startButton = new JButton();
    startButton.setFocusable(false);
    localizables.add(new Localizable.Button(startButton, "_start"));
    startButton.setPreferredSize(new Dimension(150, 20));
    startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jalmus.handleStartButtonClicked();
      }
    });

    listenButton = new JButton();
    listenButton.setFocusable(false);
    localizables.add(new Localizable.Button(listenButton, "_listen"));
    listenButton.setPreferredSize(new Dimension(150, 20));
    listenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jalmus.handleListenButtonClicked();
      }
    });

    newButton = new JButton();
    newButton.setFocusable(false);
    localizables.add(new Localizable.Button(newButton, "_new"));
    newButton.setPreferredSize(new Dimension(150, 20));
    newButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jalmus.handleNewButtonClicked();
      }
    });

    preferencesButton = new JButton();
    preferencesButton.setFocusable(false);
    localizables.add(new Localizable.Button(preferencesButton, "_menuPreferences"));
    preferencesButton.setPreferredSize(new Dimension(150, 20));
    preferencesButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jalmus.handlePreferencesClicked();
      }
    });

    bundle = ResourceBundle.getBundle("language", new Locale(this.jalmus.language));

    chooseNoteP = new ChooseNotePanel(this.jalmus.noteLevel.getKey(), this.jalmus.NOTEREADING, bundle);
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
          jalmus.noteLevel.setPitcheslist(chooseNoteP.getPitches());
        }
      }
    });   

    scoreChooseNoteP = new ChooseNotePanel(this.jalmus.scoreLevel.getKey(), this.jalmus.SCOREREADING, bundle);
    scoreChooseNoteP.setOpaque(true); //content panes must be opaque 
    scoreChooseNoteP.setVisible(true);
    scoreChooseNoteP.okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        //Execute when button is pressed
        if (! scoreChooseNoteP.atLeast3Pitches()) {
          JOptionPane.showMessageDialog(null, "Choose at least three notes", "Warning", JOptionPane.ERROR_MESSAGE); 
        } else {
          scoreNotesDialog.setVisible(false);
          jalmus.scoreLevel.setPitcheslist(scoreChooseNoteP.getPitches());
        }
      }
    });   

    doButton1 = new JButton();
    doButton1.addActionListener(this.jalmus);
    pnotes.add(doButton1);
    reButton = new JButton();
    reButton.addActionListener(this.jalmus);
    pnotes.add(reButton);
    miButton = new JButton();
    miButton.addActionListener(this.jalmus);
    pnotes.add(miButton);

    faButton = new JButton();
    faButton.addActionListener(this.jalmus);
    pnotes.add(faButton);

    solButton = new JButton();
    solButton.addActionListener(this.jalmus);
    pnotes.add(solButton);

    laButton = new JButton();
    laButton.addActionListener(this.jalmus);
    pnotes.add(laButton);

    siButton = new JButton();
    siButton.addActionListener(this.jalmus);
    pnotes.add(siButton);

    doButton2 = new JButton();
    doButton2.addActionListener(this.jalmus);
    pnotes.add(doButton2);

    // BOUTONS POUR ACCORDS
    sharpButton1 = new JButton();
    sharpButton1.setText("#");
    sharpButton1.addActionListener(this.jalmus);

    flatButton1 = new JButton();
    flatButton1.setText("b");
    flatButton1.addActionListener(this.jalmus);

    sharpButton2 = new JButton();
    sharpButton2.setText("#");
    sharpButton2.addActionListener(this.jalmus);

    flatButton2 = new JButton();
    flatButton2.setText("b");
    flatButton2.addActionListener(this.jalmus);

    pnotes.add(sharpButton1, 0);
    pnotes.add(flatButton1, 5);
    pnotes.add(flatButton2, 6);
    pnotes.add(sharpButton2, 11);
    pnotes.setLayout(new GridLayout(2, 4));

    // BOUTONS INVISIBLES EN MODE NORMAL
    sharpButton1.setVisible(false);
    sharpButton2.setVisible(false);
    flatButton1.setVisible(false);
    flatButton2.setVisible(false);

    pgamebutton.setLayout(new FlowLayout());
    pgamebutton.add(startButton);
    pnotes.setPreferredSize(new Dimension(450, 40));
    pgamebutton.add(pnotes);
    pgamebutton.add(preferencesButton);
    pnotes.setBackground(Color.white);
    pgamebutton.setBackground(Color.white);

    midiOptionsDialog = buildMidiOptionsDialog();
    transpositionSpinner.setValue(0);

    menuParameters.add(menuPrefs);
    menuPrefs.addActionListener(this.jalmus);
    menuParameters.add(menuMidi);
    menuMidi.addActionListener(this.jalmus);

    /************************************************************************/
    /******************************** MENU *********************************/
    /***********************************************************************/
    preferencesDialog = buildPreferencesDialog();


    aboutDialog = new JDialog(this.jalmus, true);
    //aboutDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    aboutDialog.setResizable(false);

    notesDialog = new JDialog(this.jalmus, true);
    //    notesDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    notesDialog.setResizable(false);   

    scoreNotesDialog = new JDialog(this.jalmus, true);
    //    notesDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    scoreNotesDialog.setResizable(false);   

    levelMessage = new JDialog(this.jalmus, true);
    //levelMessage.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    levelMessage.setResizable(false);

    scoreMessage = new JDialog(this.jalmus, true);
    //scoreMessage.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    scoreMessage.setResizable(false);

    saveDialog = new JDialog(this.jalmus, true);
    //scoreMessage.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    saveDialog.setResizable(false);

    helpMenu.setMnemonic(KeyEvent.VK_A);
    helpMenu.add(helpSummary);

    helpMenu.add(siteinternet);
    helpMenu.addSeparator();
    helpMenu.add(aboutMenuItem);
    helpSummary.addActionListener(this.jalmus);
    siteinternet.addActionListener(this.jalmus);

    aboutMenuItem.addActionListener(this.jalmus);

    maBarre.add(buildExercisesMenu());
    lessonsMenu = buildLessonsMenu();
    maBarre.add(lessonsMenu);
    maBarre.add(menuParameters);
    maBarre.add(helpMenu);

    this.jalmus.setJMenuBar(maBarre);
    maBarre.setVisible(true);

    /**************************************************************/
    /**************************************************************/
    /**************************************************************/

    /***************** FENETRE A PROPOS ******************************/
    aboutPanel.setVisible(true);
    bcredits = new JButton();

    bcredits.setIcon(new ImageIcon(getClass().getResource("/images/credits.png")));
    bcredits.addActionListener(this.jalmus);

    blicence = new JButton();

    blicence.setIcon(new ImageIcon(getClass().getResource("/images/licence.png")));
    blicence.addActionListener(this.jalmus);
    bfermer = new JButton();

    bfermer.setIcon(new ImageIcon(getClass().getResource("/images/cancel.png")));
    bfermer.addActionListener(this.jalmus);

    aboutDialog.setContentPane(aboutPanel);

    aboutPanelTextArea = new JTextArea(12, 25);
    JScrollPane ascenceur = new JScrollPane(aboutPanelTextArea);
    aboutPanelTextArea.setEditable(false);
    aboutPanelTextArea.setLineWrap(true);
    aboutPanelTextArea.setWrapStyleWord(true);
    aboutPanelTextArea.setFont(new Font("SansSerif", Font.BOLD, 14));
    aboutPanelTextArea.setBackground(this.jalmus.getBackground());

    aboutPanelTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    ascenceur.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    ascenceur.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    aboutPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
    aboutPanel.add(ascenceur);
    paproposboutons.add(bcredits);
    paproposboutons.add(blicence);
    blicence.setVisible(true);
    paproposboutons.add(bfermer);
    bfermer.setVisible(true);
    aboutPanel.add(paproposboutons);

    /*******************************************************************/

    plevelMessage.setVisible(true);
    plevelMessage.setLayout(new GridLayout(2, 1));

    oklevelMessage = new JButton();
    oklevelMessage.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jalmus.handleLevelOkClicked();
      }
    });
    oklevelMessage.setIcon(new ImageIcon(getClass().getResource("/images/ok.png")));
    localizables.add(new Localizable.Button(oklevelMessage, "_buttonok"));

    textlevelMessage.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
    plevelMessage.add(textlevelMessage);
    pButtonlevelMessage.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 5));

    pButtonlevelMessage.add(oklevelMessage);
    plevelMessage.add(pButtonlevelMessage);
    levelMessage.setContentPane(plevelMessage);
    levelMessage.setModal(false);
    levelMessage.setVisible(false);

    pscoreMessage.setVisible(true);
    pscoreMessage.setLayout(new GridLayout(2, 1));

    okscoreMessage = new JButton();
    okscoreMessage.addActionListener(this.jalmus);
    okscoreMessage.setIcon(new ImageIcon(getClass().getResource("/images/ok.png")));

    textscoreMessage.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
    pscoreMessage.add(textscoreMessage);
    pButtonscoreMessage.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 5));

    pButtonscoreMessage.add(okscoreMessage);
    pscoreMessage.add(pButtonscoreMessage);
    scoreMessage.setContentPane(pscoreMessage);
    scoreMessage.setModal(false);
    scoreMessage.setVisible(false);

    labelPanel.add( new JLabel(bundle.getString("_name"), JLabel.RIGHT));
    labelPanel.add( new JLabel(bundle.getString("_description"), JLabel.RIGHT));
    fieldPanel.add(lessonName);
    fieldPanel.add(lessonMessage);

    savePanel.add(labelPanel, BorderLayout.WEST);
    savePanel.add(fieldPanel, BorderLayout.CENTER);

    oksaveButton = new JButton();
    oksaveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jalmus.handleOKSave();
      }
    });
    oksaveButton.setIcon(new ImageIcon(getClass().getResource("/images/ok.png")));
    oksaveButton.setText(bundle.getString("_buttonok"));
    savePanel.add(oksaveButton);
    saveDialog.setContentPane(savePanel);
    saveDialog.setVisible(false);

    /*******************************************************************/

    principal.setLayout(new BorderLayout());

    principal.add(pgamebutton, BorderLayout.NORTH);
    principal.add(this.jalmus.panelanim, BorderLayout.CENTER);

    principal.setVisible(true);
    pgamebutton.setVisible(false);
    this.jalmus.getContentPane().add(principal);

    this.jalmus.panelanim.setVisible(true);
    this.jalmus.panelanim.setBackground(Color.white);


    ButtonGroup group = new ButtonGroup();

    rblanguagefr = new JRadioButtonMenuItem("Fran"+"\u00E7"+"ais");
    rblanguagefr.setMnemonic(KeyEvent.VK_F);
    group.add(rblanguagefr);
    rblanguagefr.addActionListener(this.jalmus);
    languages.add(rblanguagefr);

    rblanguagede = new JRadioButtonMenuItem("Deutsch");
    rblanguagede.setMnemonic(KeyEvent.VK_D);
    group.add(rblanguagede);
    rblanguagede.addActionListener(this.jalmus);
    languages.add(rblanguagede);

    rblanguagees = new JRadioButtonMenuItem("Espanol");
    rblanguagees.setMnemonic(KeyEvent.VK_S);
    group.add(rblanguagees);
    rblanguagees.addActionListener(this.jalmus);
    languages.add(rblanguagees);

    rblanguageen = new JRadioButtonMenuItem("English");
    rblanguageen.setMnemonic(KeyEvent.VK_E);
    rblanguageen.addActionListener(this.jalmus);
    group.add(rblanguageen);
    languages.add(rblanguageen);

    rblanguageit = new JRadioButtonMenuItem("Italiano");
    rblanguageit.setMnemonic(KeyEvent.VK_I);
    rblanguageit.addActionListener(this.jalmus);
    group.add(rblanguageit);
    languages.add(rblanguageit);

    rblanguageda = new JRadioButtonMenuItem("Dansk");
    rblanguageda.setMnemonic(KeyEvent.VK_A);
    rblanguageda.addActionListener(this.jalmus);
    group.add(rblanguageda);
    languages.add(rblanguageda);

    rblanguagetr = new JRadioButtonMenuItem("Turkish");
    rblanguagetr.setMnemonic(KeyEvent.VK_T);
    rblanguagetr.addActionListener(this.jalmus);
    group.add(rblanguagetr);
    languages.add(rblanguagetr);

    rblanguagefi = new JRadioButtonMenuItem("Finnish");
    rblanguagefi.setMnemonic(KeyEvent.VK_F);
    rblanguagefi.addActionListener(this.jalmus);
    group.add(rblanguagefi);
    languages.add(rblanguagefi);

    rblanguagepl = new JRadioButtonMenuItem("Polish");
    rblanguagepl.setMnemonic(KeyEvent.VK_O);
    rblanguagepl.addActionListener(this.jalmus);
    group.add(rblanguagepl);
    languages.add(rblanguagepl);

    rblanguageiw = new JRadioButtonMenuItem("Hebrew");
    rblanguageiw.setMnemonic(KeyEvent.VK_H);
    rblanguageiw.addActionListener(this.jalmus);
    group.add(rblanguageiw);
    languages.add(rblanguageiw);

    rblanguageko = new JRadioButtonMenuItem("Korean");
    rblanguageko.setMnemonic(KeyEvent.VK_K);
    rblanguageko.addActionListener(this.jalmus);
    group.add(rblanguageko);
    languages.add(rblanguageko);

    rblanguageeo = new JRadioButtonMenuItem("Esperanto");
    rblanguageeo.setMnemonic(KeyEvent.VK_N);
    rblanguageeo.addActionListener(this.jalmus);
    group.add(rblanguageeo);
    languages.add(rblanguageeo);

    rblanguagegr = new JRadioButtonMenuItem("Greek");
    rblanguagegr.setMnemonic(KeyEvent.VK_G);
    rblanguagegr.addActionListener(this.jalmus);
    group.add(rblanguagegr);
    languages.add(rblanguagegr);

    if ("es".equals(paramlanguage)) {
      rblanguagees.setSelected(true);
      this.jalmus.language = "es";
    } else if ("it".equals(paramlanguage)) {
      rblanguageit.setSelected(true);
      this.jalmus.language = "it";
    } else if ("de".equals(paramlanguage)) {
      rblanguagede.setSelected(true);
      this.jalmus.language = "de";
    } else if ("fr".equals(paramlanguage)) {
      rblanguagefr.setSelected(true);
      this.jalmus.language = "fr";
    } else if ("da".equals(paramlanguage)) {
      rblanguageda.setSelected(true);
      this.jalmus.language = "da";
    } else if ("tr".equals(paramlanguage)) {
      rblanguagetr.setSelected(true);
      this.jalmus.language = "tr";
    } else if ("fi".equals(paramlanguage)) {
      rblanguagefi.setSelected(true);
      this.jalmus.language = "fi";
    } else if ("ko".equals(paramlanguage)) {
      rblanguageko.setSelected(true);
      this.jalmus.language = "ko";
    } else if ("eo".equals(paramlanguage)) {
      rblanguageeo.setSelected(true);
      this.jalmus.language = "eo";
    } else if ("pl".equals(paramlanguage)) {
      rblanguagepl.setSelected(true);
      this.jalmus.language = "pl"; 
    } else if ("iw".equals(paramlanguage)) {
      rblanguageiw.setSelected(true);
      this.jalmus.language = "iw";
    }  else if ("gr".equals(paramlanguage)) {
      rblanguagegr.setSelected(true);
      this.jalmus.language = "gr";
    } else {
      // must be "en"
      rblanguageen.setSelected(true);
      this.jalmus.language = "en";
    }

    languages.setIcon(new ImageIcon(getClass().getResource("/images/language.png")));

    languages.addActionListener(this.jalmus);
    languages.setMnemonic(KeyEvent.VK_L);

    menuParameters.addSeparator();
    menuParameters.add(languages);
    menuParameters.setMnemonic(KeyEvent.VK_P);

  }
  //----------------------------------------------------------------

  JMenu buildExercisesMenu() {
    JMenuItem noteReadingMenuItem = new JMenuItem(new ImageIcon(getClass().getResource("/images/note.png")));
    localizables.add(new Localizable.Button(noteReadingMenuItem, "_menuNotereading"));
    noteReadingMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jalmus.handleNoteReadingMenuItem();
      }
    });

    JMenuItem rhythmReadingMenuItem = new JMenuItem(new ImageIcon(getClass().getResource("/images/rhythm.png")));
    localizables.add(new Localizable.Button(rhythmReadingMenuItem, "_menuRythmreading"));
    rhythmReadingMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jalmus.handleRhythmReadingMenuItem();
      }
    });

    JMenuItem scoreReadingMenuItem = new JMenuItem(new ImageIcon(getClass().getResource("/images/score.png")));
    localizables.add(new Localizable.Button(scoreReadingMenuItem, "_menuScorereading"));
    scoreReadingMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jalmus.handleScoreReadingMenuItem();
      }
    });

    JMenuItem exitMenuItem = new JMenuItem(new ImageIcon(getClass().getResource("/images/exit.png")));
    localizables.add(new Localizable.Button(exitMenuItem, "_menuExit"));
    exitMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jalmus.handleExitMenuItem();
      }
    });

    JMenu exercisesMenu = new JMenu();
    localizables.add(new Localizable.Button(exercisesMenu, "_menuExercises"));
    exercisesMenu.setMnemonic(KeyEvent.VK_E);
    exercisesMenu.add(noteReadingMenuItem);
    exercisesMenu.add(rhythmReadingMenuItem);  
    exercisesMenu.add(scoreReadingMenuItem);
    //  exercisesMenu.add(lessonsMenuItem);
    exercisesMenu.addSeparator();
    exercisesMenu.add(exitMenuItem);
    return exercisesMenu;
  }

  JMenu buildLessonsMenu() {
    JMenu result = new JMenu();
    localizables.add(new Localizable.Button(result, "_menuLessons"));
    result.setMnemonic(KeyEvent.VK_L);

    final String path = this.jalmus.currentlesson.getLessonPath(this.jalmus.language);
    File subdir = new File(path);

    if (subdir.isDirectory()) {
      File[] listsp = subdir.listFiles();
      Arrays.sort(listsp);
      if (listsp != null && listsp.length <=15) { //15 directory
        for (int i = 0; i<listsp.length; i++) {
          lessonsMenuDir[i] = new JMenu(listsp[i].getName());
          result.add(lessonsMenuDir[i]);

          pathsubdir[i] = path+File.separator+listsp[i].getName();
          File repertoire = new File(pathsubdir[i]);
          File[] list = repertoire.listFiles();
          Arrays.sort(list);
          if (list != null && list.length <=25) { //25 lessons max
            for (int i1 = 0; i1<list.length; i1++) {
              if ("xml".equals(FileTools.getFileExtension(list[i1]))) {
                lessonsMenuItem[i][i1] = new JMenuItem(FileTools.getFileNameWithoutExtension(list[i1]));
                lessonsMenuItem[i][i1].addActionListener(this.jalmus);
                lessonsMenuDir[i].add(lessonsMenuItem[i][i1]);
              }
            }
          }
        }
      }
    } else {
      System.err.println(subdir+" : Reading lessons files error.");
    }

    // final Desktop desktop = null;
    // Before more Desktop API is used, first check
    // whether the API is supported by this particular
    // virtual machine (VM) on this particular host.

    result.addSeparator();

    JMenuItem manageMenuItem = new JMenuItem(new ImageIcon(getClass().getResource("/images/folder.png")));
    localizables.add(new Localizable.Button(manageMenuItem, "_menuBrowse"));
    manageMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        File subdir = new File(path);
        jalmus.OpenDirectory(subdir);
      }
    });	
    result.add(manageMenuItem);

    //  path = path+"/Basic";
    //  File repertoire = new File(path);

    //  bLessons.removeAllItems();

    //localizables.add(new Localizable.Button(lessonsMenuItem, "_menuLessons"));
    return result;
  }
  //----------------------------------------------------------------
  private JDialog buildMidiOptionsDialog() {

    /* Sound panel */

    soundOnCheckBox = new JCheckBox("", true);

    instrumentsComboBox = new JComboBox<String>();
    if (this.jalmus.instruments != null) {
      for (int i = 0; i<20; i++) {
        instrumentsComboBox.addItem(this.jalmus.instruments[i].getName());
      }
    } else {
      instrumentsComboBox.addItem("No instrument available");
      System.out.println("No soundbank file : http://java.sun.com/products/java-media/sound/soundbanks.html");
    }
    instrumentsComboBox.addItemListener(this.jalmus);
    /*
       audioDriverComboBox = new JComboBox(AsioDriver.getDriverNames().toArray());
       audioDriverComboBox.addItem("WDM Java Sound");
       audioDriverComboBox.addItemListener(this);
       */
    JPanel soundPanel = new JPanel(); // panel midi keyboard
    localizables.add(new Localizable.NamedGroup(soundPanel, "_sound"));

    keyboardsoundCheckBox = new JCheckBox("", false);

    JPanel keyboardSoundPanel = new JPanel();
    keyboardSoundPanel.add(soundOnCheckBox);
    keyboardSoundPanel.add(keyboardsoundCheckBox);
    keyboardSoundPanel.add(instrumentsComboBox);

    soundPanel.setLayout(new BorderLayout());
    //soundPanel.add(audioDriverComboBox, BorderLayout.NORTH);
    soundPanel.add(keyboardSoundPanel, BorderLayout.CENTER);

    /* Latency - Cursor Speed panel */

    JPanel latencyPanel = new JPanel();
    latencyPanel.add(latencySlider);
    latencySlider.setMajorTickSpacing(50);
    latencySlider.setMinorTickSpacing(10);
    latencySlider.setPaintTicks(true);
    latencySlider.setPaintLabels(true);
    /*
       latencyPanel.add(speedcursorSlider);
       speedcursorSlider.setMajorTickSpacing(50);
       speedcursorSlider.setMinorTickSpacing(10);
       speedcursorSlider.setPaintTicks(true);
       speedcursorSlider.setPaintLabels(true);
       */        
    try {
      latencySlider.setValue(Integer.parseInt(settings.getProperty("latency")));  
      //speedcursorSlider.setValue(Integer.parseInt(settings.getProperty("speedcursor")));   
    } catch (Exception e) {
      System.out.println(e);
    }
    localizables.add(new Localizable.NamedGroup(latencyPanel, "_latency"));

    // ----

    midiInComboBoxModel.addElement(pasclavier);
    midiOutComboBoxModel.addElement(pasclavier);
    MidiDevice.Info[] aInfos = MidiSystem.getMidiDeviceInfo();
    for (int i = 0; i<aInfos.length; i++) {
      try {
        MidiDevice device = MidiSystem.getMidiDevice(aInfos[i]);
        boolean bAllowsInput = (device.getMaxTransmitters()!=0);
        boolean bAllowsOutput = (device.getMaxReceivers()!=0);

        if (bAllowsInput) {
          midiInComboBoxModel.addElement(aInfos[i].getName());
        }

        if (bAllowsOutput) {
          midiOutComboBoxModel.addElement(aInfos[i].getName());
        }
      } catch (MidiUnavailableException e) {
        System.err.println("Midi Unavailable!");
      }
    }

    midiInComboBox = new JComboBox<String>();
    midiInComboBox.setModel(midiInComboBoxModel);
    midiInComboBox.addItemListener(this.jalmus);

    midiOutComboBox = new JComboBox<String>();
    midiOutComboBox.setModel(midiOutComboBoxModel);
    midiOutComboBox.addItemListener(this.jalmus);

    keyboardLengthComboBox = new JComboBox<String>();
    keyboardLengthComboBox.addItemListener(this.jalmus);

    Integer current = new Integer(0);
    Integer min = new Integer(-24);
    Integer max = new Integer(24);
    Integer step = new Integer(1);
    m_numberSpinnerModel = new SpinnerNumberModel(current, min, max, step);
    transpositionSpinner = new JSpinner(m_numberSpinnerModel);
    transpositionSpinner.setSize(40, 40);
    transpositionLabel.setText("Tansposition");

    JPanel keyboardPanel = new JPanel();
    keyboardPanel.add(keyboardLengthComboBox);
    keyboardPanel.add(transpositionLabel);
    keyboardPanel.add(transpositionSpinner);

    JPanel midiInPanel = new JPanel();
    localizables.add(new Localizable.NamedGroup(midiInPanel, "_midiclavier"));
    midiInPanel.setLayout(new BorderLayout());
    midiInPanel.add(midiInComboBox, BorderLayout.NORTH);
    midiInPanel.add(keyboardPanel, BorderLayout.CENTER);

    JPanel midiOutPanel = new JPanel();
    midiOutPanel.setName("Midi out");
    localizables.add(new Localizable.NamedGroup(midiOutPanel, "_midiout"));
    midiOutPanel.setLayout(new BorderLayout());
    midiOutPanel.add(midiOutComboBox, BorderLayout.NORTH);

    // ----

    JButton okButton = new JButton();
    localizables.add(new Localizable.Button(okButton, "_buttonok"));
    okButton.setIcon(new ImageIcon(getClass().getResource("/images/ok.png")));
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jalmus.handleMidiOptionsOkClicked();
      }
    });

    JButton cancelButton = new JButton();
    localizables.add(new Localizable.Button(cancelButton, "_buttoncancel"));
    cancelButton.setIcon(new ImageIcon(getClass().getResource("/images/cancel.png")));
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jalmus.handleMidiOptionsCancelClicked();
      }
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    // ----

    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new GridLayout(5,1,4,4));
    contentPanel.add(soundPanel);
    contentPanel.add(midiInPanel);
    contentPanel.add(midiOutPanel);
    contentPanel.add(latencyPanel);
    contentPanel.add(buttonPanel);

    JDialog dialog = new JDialog(this.jalmus, true);
    localizables.add(new Localizable.Dialog(dialog, "_menuMidi"));
    dialog.setContentPane(contentPanel);
    dialog.setSize(450, 550);
    //dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    //dialog.setResizable(false);

    return dialog;
  }

  //----------------------------------------------------------------
  private JDialog buildPreferencesDialog() {

    JPanel noteReadingPanel = buildNoteReadingPreferencesPanel();
    JPanel rhythmReadingPanel = buildRhythmReadingPreferencesPanel();
    JPanel scoreReadingPanel = buildScoreReadingPreferencesPanel();

    preferencesTabbedPane.addTab(null, new ImageIcon(getClass().getResource("/images/note.png")), noteReadingPanel);
    localizables.add(new Localizable.Tab(preferencesTabbedPane, NOTE_READING_TAB, "_menuNotereading"));
    preferencesTabbedPane.addTab(null, new ImageIcon(getClass().getResource("/images/rhythm.png")), rhythmReadingPanel);
    localizables.add(new Localizable.Tab(preferencesTabbedPane, RHYTHM_READING_TAB, "_menuRythmreading"));
    preferencesTabbedPane.addTab(null, new ImageIcon(getClass().getResource("/images/score.png")), scoreReadingPanel);
    localizables.add(new Localizable.Tab(preferencesTabbedPane, SCORE_READING_TAB, "_menuScorereading"));

    // buttons below tabs

    JButton okButton = new JButton();
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jalmus.handlePreferencesOkClicked();
      }
    });
    okButton.setIcon(new ImageIcon(getClass().getResource("/images/ok.png")));
    localizables.add(new Localizable.Button(okButton, "_buttonok"));

    JButton cancelButton = new JButton();
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jalmus.handlePreferencesCancelClicked();
      }
    });
    cancelButton.setIcon(new ImageIcon(getClass().getResource("/images/cancel.png")));
    localizables.add(new Localizable.Button(cancelButton, "_buttoncancel"));

    JButton saveButton = new JButton();
    saveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jalmus.handlePreferencesSaveClicked();
      }
    });
    saveButton.setIcon(new ImageIcon(getClass().getResource("/images/save.png")));
    localizables.add(new Localizable.Button(saveButton, "_buttonsave"));

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);

    JPanel contentPanel = new JPanel();
    // contentPanel.setLayout(new GridLayout(2, 2));

    contentPanel.add(preferencesTabbedPane,BorderLayout.CENTER);
    preferencesTabbedPane.setPreferredSize(new Dimension(560, 430));

    contentPanel.add(buttonPanel,BorderLayout.LINE_END);

    JDialog dialog = new JDialog(this.jalmus, true);
    localizables.add(new Localizable.Dialog(dialog, "_menuPreferences"));
    //dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    //dialog.setResizable(false);
    dialog.setContentPane(contentPanel);
    dialog.setSize(580, 550);

    return dialog;
  }

  //----------------------------------------------------------------
  private JPanel buildRhythmReadingPreferencesPanel() {

    /* 1st panel - type of game */

    rhythmGameTypeComboBox = new JComboBox<String>();
    // rhythmGameTypeComboBox.addItem("Learning");
    // rhythmGameTypeComboBox.addItem("Normal");
    rhythmGameTypeComboBox.addItemListener(this.jalmus);

    rhythmGameSpeedComboBox = new JComboBox<String>();
    rhythmGameSpeedComboBox.addItem("Largo");
    rhythmGameSpeedComboBox.addItem("Adagio");
    rhythmGameSpeedComboBox.addItem("Moderato");
    rhythmGameSpeedComboBox.addItem("Allegro");
    rhythmGameSpeedComboBox.addItem("Presto");
    rhythmGameSpeedComboBox.addItemListener(this.jalmus);

    JPanel gamePanel = new JPanel();
    gamePanel.add(rhythmGameTypeComboBox);
    gamePanel.add(rhythmGameSpeedComboBox);
    localizables.add(new Localizable.NamedGroup(gamePanel, "_menuExercises"));

    /* 2nd panel - RHYTHM */

    wholeCheckBox = new JCheckBox("", true);
    wholeCheckBox.addItemListener(this.jalmus);
    halfCheckBox = new JCheckBox("", true);
    halfCheckBox.addItemListener(this.jalmus); 
    dottedhalfCheckBox = new JCheckBox("", false);
    dottedhalfCheckBox.addItemListener(this.jalmus);
    quarterCheckBox = new JCheckBox("", false);
    quarterCheckBox.addItemListener(this.jalmus);
    eighthCheckBox = new JCheckBox("", false);
    eighthCheckBox.addItemListener(this.jalmus);
    restCheckBox = new JCheckBox("", true);
    restCheckBox.addItemListener(this.jalmus);
    tripletCheckBox = new JCheckBox("", false);
    tripletCheckBox.addItemListener(this.jalmus);

    timeSignComboBox = new JComboBox<String>();
    timeSignComboBox.setPreferredSize(new Dimension(100, 25));
    timeSignComboBox.addItem("4/4");
    timeSignComboBox.addItem("3/4");
    timeSignComboBox.addItem("2/4");
    timeSignComboBox.addItem("6/8");
    timeSignComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JComboBox<?>) {
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
              jalmus.rhythmLevel.setTimeSignNumerator(4);
              jalmus.rhythmLevel.setTimeSignDenominator(4);
              jalmus.rhythmLevel.setTimeDivision(1);
              break;
            case 1:
              wholeCheckBox.setSelected(false);
              wholeCheckBox.setEnabled(false);
              dottedhalfCheckBox.setSelected(true);
              dottedhalfCheckBox.setEnabled(true);
              quarterCheckBox.setSelected(true);
              jalmus.rhythmLevel.setTimeSignNumerator(3);
              jalmus.rhythmLevel.setTimeSignDenominator(4);
              jalmus.rhythmLevel.setTimeDivision(1);
              break;
            case 2:
              wholeCheckBox.setSelected(false);
              wholeCheckBox.setEnabled(false);
              dottedhalfCheckBox.setSelected(false);
              dottedhalfCheckBox.setEnabled(false);
              quarterCheckBox.setSelected(true);
              jalmus.rhythmLevel.setTimeSignNumerator(2);
              jalmus.rhythmLevel.setTimeSignDenominator(4);
              jalmus.rhythmLevel.setTimeDivision(1);
              break;
            case 3:
              wholeCheckBox.setSelected(false);
              wholeCheckBox.setEnabled(false);
              scoredottedhalfCheckBox.setSelected(false);
              scoredottedhalfCheckBox.setEnabled(false);
              quarterCheckBox.setSelected(true);
              jalmus.rhythmLevel.setTimeSignNumerator(6);
              jalmus.rhythmLevel.setTimeSignDenominator(8);
              jalmus.rhythmLevel.setTimeDivision(2);
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
    localizables.add(new Localizable.NamedGroup(rhythmAndTimePanel, "_menuRythms"));

    /* 3rd panel - metronome */

    metronomeCheckBox = new JCheckBox("", true);
    metronomeCheckBox.addItemListener(this.jalmus);
    metronomeShowCheckBox = new JCheckBox("", true);
    metronomeShowCheckBox.setSelected(false);
    metronomeShowCheckBox.addItemListener(this.jalmus);

    JPanel metronomePanel = new JPanel();
    metronomePanel.add(metronomeCheckBox);
    metronomePanel.add(metronomeShowCheckBox);
    localizables.add(new Localizable.NamedGroup(metronomePanel, "_menuMetronom"));

    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(3, 1));
    panel.add(gamePanel);
    panel.add(rhythmAndTimePanel);
    panel.add(metronomePanel);

    return panel;
  }


  private JPanel buildScoreReadingPreferencesPanel() {

    scoreGameTypeComboBox = new JComboBox<String>();
    scoreGameTypeComboBox.addItemListener(this.jalmus);

    scoreGameSpeedComboBox = new JComboBox<String>();
    scoreGameSpeedComboBox.addItem("Largo");
    scoreGameSpeedComboBox.addItem("Adagio");
    scoreGameSpeedComboBox.addItem("Moderato");
    scoreGameSpeedComboBox.addItem("Allegro");
    scoreGameSpeedComboBox.addItem("Presto");
    scoreGameSpeedComboBox.addItemListener(this.jalmus);

    JPanel scoregamePanel = new JPanel();
    scoregamePanel.add(scoreGameTypeComboBox);
    scoregamePanel.add(scoreGameSpeedComboBox);
    localizables.add(new Localizable.NamedGroup(scoregamePanel, "_menuExercises"));

    /* 2nd panel - Key & notes */

    scoreKeyComboBox = new JComboBox<String>();
    scoreKeyComboBox.addItemListener(this.jalmus);

    scoreNotesComboBox = new JComboBox<String>();
    scoreNotesComboBox.addItemListener(this.jalmus);

    scoreAlterationsComboBox = new JComboBox<String>();
    scoreAlterationsComboBox.addItemListener(this.jalmus);

    JPanel scoreKeyPanel = new JPanel(); // panel pour la Key du premier jeu
    scoreKeyPanel.add(scoreKeyComboBox);
    scoreKeyPanel.add(scoreAlterationsComboBox);
    scoreKeyPanel.add(scoreNotesComboBox);
    localizables.add(new Localizable.NamedGroup(scoreKeyPanel, "_menuNotes"));

    /* 3rd panel - RYTHM */

    scorewholeCheckBox = new JCheckBox("", true);
    scorewholeCheckBox.addItemListener(this.jalmus);   
    scorehalfCheckBox = new JCheckBox("", true);
    scorehalfCheckBox.addItemListener(this.jalmus);
    scoredottedhalfCheckBox = new JCheckBox("", false);
    scoredottedhalfCheckBox.addItemListener(this.jalmus);  
    scorequarterCheckBox = new JCheckBox("", false);
    scorequarterCheckBox.addItemListener(this.jalmus);            
    scoreeighthCheckBox = new JCheckBox("", false);
    scoreeighthCheckBox.addItemListener(this.jalmus);          
    scorerestCheckBox = new JCheckBox("", true);
    scorerestCheckBox.addItemListener(this.jalmus);      
    scoreTripletCheckBox = new JCheckBox("", false);
    scoreTripletCheckBox.addItemListener(this.jalmus);

    JPanel scorerhytmsPanel = new JPanel();
    scorerhytmsPanel.add(scorewholeCheckBox);
    scorerhytmsPanel.add(scoredottedhalfCheckBox);
    scorerhytmsPanel.add(scorehalfCheckBox);
    scorerhytmsPanel.add(scorequarterCheckBox);
    scorerhytmsPanel.add(scoreeighthCheckBox);
    scorerhytmsPanel.add(scorerestCheckBox);
    scorerhytmsPanel.add(scoreTripletCheckBox);

    scoreTimeSignComboBox = new JComboBox<String>();
    scoreTimeSignComboBox.setPreferredSize(new Dimension(100, 25));
    scoreTimeSignComboBox.addItem("4/4");
    scoreTimeSignComboBox.addItem("3/4");
    scoreTimeSignComboBox.addItem("2/4");
    scoreTimeSignComboBox.addItem("6/8");
    scoreTimeSignComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JComboBox<?> cb = (JComboBox<?>)e.getSource();
        int sel = cb.getSelectedIndex();
        System.out.println("Rhythm time signature changed. Selected: "+ sel);
        switch (sel) {
          case 0:
            scorewholeCheckBox.setEnabled(true);
            scorewholeCheckBox.setSelected(true);
            scorequarterCheckBox.setSelected(true);
            scoredottedhalfCheckBox.setEnabled(true);
            scoredottedhalfCheckBox.setSelected(false);
            jalmus.scoreLevel.setTimeSignNumerator(4);
            jalmus.scoreLevel.setTimeSignDenominator(4);
            jalmus.scoreLevel.setTimeDivision(1);
            break;
          case 1: 
            scorewholeCheckBox.setSelected(false);
            scorewholeCheckBox.setEnabled(false);
            scoredottedhalfCheckBox.setSelected(true);
            scoredottedhalfCheckBox.setEnabled(true);
            scorequarterCheckBox.setSelected(true);
            jalmus.scoreLevel.setTimeSignNumerator(3);
            jalmus.scoreLevel.setTimeSignDenominator(4);
            jalmus.scoreLevel.setTimeDivision(1);
            break;
          case 2:
            scorewholeCheckBox.setSelected(false);
            scoredottedhalfCheckBox.setSelected(false);
            scoredottedhalfCheckBox.setEnabled(false);
            scorequarterCheckBox.setSelected(true);
            jalmus.scoreLevel.setTimeSignNumerator(2);
            jalmus.scoreLevel.setTimeSignDenominator(4);
            jalmus.scoreLevel.setTimeDivision(1);
            break;
          case 3:
            scorewholeCheckBox.setSelected(false);
            scorewholeCheckBox.setEnabled(false);
            scoredottedhalfCheckBox.setSelected(false);
            scoredottedhalfCheckBox.setEnabled(false);
            scorequarterCheckBox.setSelected(true);
            jalmus.scoreLevel.setTimeSignNumerator(6);
            jalmus.scoreLevel.setTimeSignDenominator(8);
            jalmus.scoreLevel.setTimeDivision(2);
            break;
        }
      }
    });

    JPanel timeSignPanel = new JPanel();
    scoreTimeSignLabel = new JLabel();
    timeSignPanel.add(scoreTimeSignLabel);
    timeSignPanel.add(scoreTimeSignComboBox);

    JPanel scoreRhythmAndTimePanel = new JPanel();
    scoreRhythmAndTimePanel.setLayout(new BorderLayout());
    scoreRhythmAndTimePanel.add(timeSignPanel, BorderLayout.NORTH);
    scoreRhythmAndTimePanel.add(scorerhytmsPanel, BorderLayout.CENTER);
    localizables.add(new Localizable.NamedGroup(scoreRhythmAndTimePanel, "_menuRythms"));

    /* 4th panel - sound */

    scoreMetronomeCheckBox = new JCheckBox("", true);
    scoreMetronomeShowCheckBox = new JCheckBox("", true);
    scoreMetronomeShowCheckBox.setSelected(false);
    scoreMetronomeCheckBox.addActionListener(this.jalmus);
    scoreMetronomeShowCheckBox.addActionListener(this.jalmus);

    JPanel scoremetronomePanel = new JPanel();
    scoremetronomePanel.add(scoreMetronomeCheckBox);
    scoremetronomePanel.add(scoreMetronomeShowCheckBox);
    localizables.add(new Localizable.NamedGroup(scoremetronomePanel, "_menuMetronom"));

    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(4, 1));
    panel.add(scoregamePanel);
    panel.add(scoreKeyPanel);
    panel.add(scoreRhythmAndTimePanel);
    panel.add(scoremetronomePanel);
    //panel.add(latencyPanel);

    return panel;
  }

  //----------------------------------------------------------------
  private JPanel buildNoteReadingPreferencesPanel() {

    /* 1er panel - type de jeu */

    noteGameTypeComboBox = new JComboBox<String>();
    noteGameTypeComboBox.addItemListener(this.jalmus);

    noteGameSpeedComboBox = new JComboBox<String>();
    noteGameSpeedComboBox.addItem("Largo");
    noteGameSpeedComboBox.addItem("Adagio");
    noteGameSpeedComboBox.addItem("Moderato");
    noteGameSpeedComboBox.addItem("Allegro");
    noteGameSpeedComboBox.addItem("Presto");
    noteGameSpeedComboBox.addItemListener(this.jalmus);

    JPanel gamePanel = new JPanel();
    gamePanel.add(noteGameTypeComboBox);
    gamePanel.add(noteGameSpeedComboBox);
    localizables.add(new Localizable.NamedGroup(gamePanel, "_menuExercises"));

    /* 2nd panel - Key */

    keyComboBox = new JComboBox<String>();
    keyComboBox.addItemListener(this.jalmus);

    keySignatureCheckBox = new JComboBox<String>();
    keySignatureCheckBox.addItemListener(this.jalmus);

    JPanel KeyPanel = new JPanel(); // panel pour la Key du premier jeu
    KeyPanel.add(keyComboBox);
    KeyPanel.add(keySignatureCheckBox);
    localizables.add(new Localizable.NamedGroup(KeyPanel, "_menuClef"));

    /* 3rd panel - Notes */

    noteGroupComboBox = new JComboBox<String>();
    noteGroupComboBox.addItemListener(this.jalmus);

    noteCountComboBox = new JComboBox<String>();
    noteCountComboBox.addItemListener(this.jalmus);

    intervalComboBox = new JComboBox<String>();
    intervalComboBox.addItemListener(this.jalmus);

    chordTypeComboBox = new JComboBox<String>();
    chordTypeComboBox.addItemListener(this.jalmus);

    noteReadingNotesPanel.add(noteGroupComboBox);
    noteReadingNotesPanel.add(noteCountComboBox);

    localizables.add(new Localizable.NamedGroup(noteReadingNotesPanel, "_menuNotes"));

    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(3, 1));
    panel.add(gamePanel);
    panel.add(KeyPanel);
    panel.add(noteReadingNotesPanel);

    return panel;
  }

  void changeLanguage() {
    bundle = ResourceBundle.getBundle("language", new Locale(this.jalmus.language));
    System.out.println(new Locale(this.jalmus.language));
    for (Iterator<Localizable> itr = localizables.iterator(); itr.hasNext();) {
      Localizable localizable = (Localizable)itr.next();
      localizable.update(bundle);
    }

    menuParameters.setText(bundle.getString("_menuSettings"));
    menuPrefs.setText(bundle.getString("_menuPreferences"));
    menuMidi.setText(bundle.getString("_menuMidi"));
    languages.setText(bundle.getString("_menuLanguage"));
    helpMenu.setText(bundle.getString("_menuHelp"));
    helpSummary.setText(bundle.getString("_menuContents"));
    siteinternet.setText(bundle.getString("_menuWeb"));
    aboutMenuItem.setText(bundle.getString("_menuAbout"));
    timeSignLabel.setText(bundle.getString("_timeSignature"));
    scoreTimeSignLabel.setText(bundle.getString("_timeSignature"));
    aboutDialog.setTitle(bundle.getString("_menuAbout"));
    notesDialog.setTitle("Choose notes to study");
    scoreNotesDialog.setTitle("Choose notes to study");

    tlicence = bundle.getString("_licence");
    tcredits = bundle.getString("_credits");

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

    rhythmGameTypeComboBox.removeAllItems();
    //  rhythmGameTypeComboBox.addItem(bundle.getString("_learninggame"));
    rhythmGameTypeComboBox.addItem(bundle.getString("_normalgame"));

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

    keyboardLengthComboBox.removeAllItems();
    keyboardLengthComboBox.addItem("73 "+bundle.getString("_keys"));
    keyboardLengthComboBox.addItem("61 "+bundle.getString("_keys"));

    seconde = bundle.getString("_second");
    tierce = bundle.getString("_third");
    quarte = bundle.getString("_fourth");
    quinte = bundle.getString("_fifth");
    sixte = bundle.getString("_sixth");
    septieme = bundle.getString("_seventh");
    octave = bundle.getString("_octave");
    minor = bundle.getString("_minor");
    major = bundle.getString("_major");

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

    soundOnCheckBox.setText(bundle.getString("_notessound"));
    keyboardsoundCheckBox.setText(bundle.getString("_keyboardsound"));
    metronomeCheckBox.setText(bundle.getString("_menuMetronom"));
    metronomeShowCheckBox.setText(bundle.getString("_menuShowMetronom"));

    selectmidi_forlang = true;
    int indextmp = midiInComboBox.getSelectedIndex();
    midiInComboBoxModel.removeElementAt(0);
    midiInComboBoxModel.insertElementAt(bundle.getString("_nomidiin"), 0);
    midiInComboBox.setSelectedIndex(indextmp);
    indextmp = midiOutComboBox.getSelectedIndex();
    midiOutComboBoxModel.removeElementAt(0);
    midiOutComboBoxModel.insertElementAt(bundle.getString("_nomidiin"), 0);
    midiOutComboBox.setSelectedIndex(indextmp);
    selectmidi_forlang = false;

    wholeCheckBox.setText(bundle.getString("_wholenote"));
    halfCheckBox.setText(bundle.getString("_halfnote"));
    dottedhalfCheckBox.setText(bundle.getString("_dottedhalfnote"));
    quarterCheckBox.setText(bundle.getString("_quarternote"));
    eighthCheckBox.setText(bundle.getString("_eighthnote"));
    restCheckBox.setText(bundle.getString("_rest"));
    tripletCheckBox.setText(bundle.getString("_triplet"));

    okscoreMessage.setText(bundle.getString("_buttonok"));
    bfermer.setText(bundle.getString("_buttonclose"));
    bcredits.setText(bundle.getString("_buttoncredits"));
    blicence.setText(bundle.getString("_buttonlicense"));

    DO = bundle.getString("_do");
    RE = bundle.getString("_re");
    MI = bundle.getString("_mi");
    FA = bundle.getString("_fa");
    SOL = bundle.getString("_sol");
    LA = bundle.getString("_la");
    SI = bundle.getString("_si");

    doButton1.setText(DO);
    reButton.setText(RE);
    miButton.setText(MI);
    faButton.setText(FA);
    solButton.setText(SOL);
    laButton.setText(LA);
    siButton.setText(SI);
    doButton2.setText(DO);

    scoreKeyComboBox.removeAllItems();
    scoreKeyComboBox.addItem(bundle.getString("_trebleclef"));
    scoreKeyComboBox.addItem(bundle.getString("_bassclef"));

    scoreGameTypeComboBox.removeAllItems();
    scoreGameTypeComboBox.addItem(bundle.getString("_normalgame"));

    scoreNotesComboBox.removeAllItems();
    scoreNotesComboBox.addItem("9 "+bundle.getString("_menuNotes"));
    scoreNotesComboBox.addItem("15 "+bundle.getString("_menuNotes"));
    scoreNotesComboBox.addItem(bundle.getString("_customnotes"));

    scoreAlterationsComboBox.removeAllItems();
    scoreAlterationsComboBox.addItem(bundle.getString("_nosharpflat"));
    scoreAlterationsComboBox.addItem("1 " + bundle.getString("_sharp"));
    scoreAlterationsComboBox.addItem("2 " + bundle.getString("_sharp"));
    scoreAlterationsComboBox.addItem("3 " + bundle.getString("_sharp"));
    scoreAlterationsComboBox.addItem("4 " + bundle.getString("_sharp"));
    scoreAlterationsComboBox.addItem("5 " + bundle.getString("_sharp"));
    scoreAlterationsComboBox.addItem("6 " + bundle.getString("_sharp"));
    scoreAlterationsComboBox.addItem("7 " + bundle.getString("_sharp"));
    scoreAlterationsComboBox.addItem("1 " + bundle.getString("_flat"));
    scoreAlterationsComboBox.addItem("2 " + bundle.getString("_flat"));
    scoreAlterationsComboBox.addItem("3 " + bundle.getString("_flat"));
    scoreAlterationsComboBox.addItem("4 " + bundle.getString("_flat"));
    scoreAlterationsComboBox.addItem("5 " + bundle.getString("_flat"));
    scoreAlterationsComboBox.addItem("6 " + bundle.getString("_flat"));
    scoreAlterationsComboBox.addItem("7 " + bundle.getString("_flat"));
    scoreAlterationsComboBox.addItem(bundle.getString("_random"));

    scorewholeCheckBox.setText(bundle.getString("_wholenote"));
    scorehalfCheckBox.setText(bundle.getString("_halfnote"));
    scoredottedhalfCheckBox.setText(bundle.getString("_dottedhalfnote"));
    scorequarterCheckBox.setText(bundle.getString("_quarternote"));
    scoreeighthCheckBox.setText(bundle.getString("_eighthnote"));
    scorerestCheckBox.setText(bundle.getString("_rest"));
    scoreTripletCheckBox.setText(bundle.getString("_triplet"));
    scoreMetronomeCheckBox.setText(bundle.getString("_menuMetronom"));
    scoreMetronomeShowCheckBox.setText(bundle.getString("_menuShowMetronom"));
  }

  void savesettings() {
    settings.setProperty("transposition",String.valueOf(transpositionSpinner.getValue())); 
    if (keyboardLengthComboBox.getSelectedIndex() == 1) {
      settings.setProperty("keyboardlength","61");
    } else {
      settings.setProperty("keyboardlength","73");
    }
    settings.setProperty("keyboard",String.valueOf(midiInComboBox.getSelectedIndex())); 
    settings.setProperty("midiout",String.valueOf(midiOutComboBox.getSelectedIndex()));
    settings.setProperty("instrument",String.valueOf(instrumentsComboBox.getSelectedIndex())); 

    if (soundOnCheckBox.isSelected()) {
      settings.setProperty("sound","on");
    } else {
      settings.setProperty("sound","off"); 
    }

    if (keyboardsoundCheckBox.isSelected()) {
      settings.setProperty("keyboardsound","on");
    } else {
      settings.setProperty("keyboardsound","off"); 
    }
    settings.setProperty("latency", String.valueOf(latencySlider.getValue())); 
    //settings.setProperty("speedcursor",String.valueOf(speedcursorSlider.getValue()));

    settings.setProperty("language", this.jalmus.language);

    try { 
      settings.store(new FileOutputStream("settings.properties"), null); 
      settings.list(System.out);
    } catch (IOException e) { } 
  }
}

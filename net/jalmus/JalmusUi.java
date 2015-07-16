package net.jalmus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
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

public class JalmusUi extends JFrame implements ActionListener, ItemListener, KeyListener {

  static final long serialVersionUID = 1;

  //----------------------------------------------------------------
  // Menu
  private final Jalmus jalmus;
  
  private SwingNoteReadingGame noteGame;

  boolean selectmidi_forlang; // is true when combobox selection occurs during language initialization

  ResourceBundle bundle;
  final Collection<Localizable> localizables = new ArrayList<Localizable>();

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

  int alterationWidth = 0; // width of alterations symbols. None by default
  int windowMargin = 50; // margin from the window border
  int keyWidth = 30; // width of score keys
  int noteMargin = 220; // margin for note reading
  int timeSignWidth = 30; // width of current score time signature symbol. This includes also the first note margin
  int notesShift = 10; // space in pixel to align notes to the score layout
  int noteDistance = 72; // distance in pixel between 1/4 notes
  int firstNoteXPos = windowMargin + keyWidth + alterationWidth + timeSignWidth + notesShift;
  int rhythmAnswerScoreYpos = 100; //distance to paint answer
  float rhythmCursorXpos = firstNoteXPos - noteDistance; // X position of the cursor on the score during rhythm game
  int rhythmCursorXStartPos = firstNoteXPos - noteDistance;
  int rhythmCursorXlimit;

  int scoreYpos = 110; // Y coordinate of the first row of the score

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
  JMenuItem menuPrefs =
      new JMenuItem(new ImageIcon(getClass().getResource("/images/prefs.png")));
  JMenuItem menuMidi =
      new JMenuItem(new ImageIcon(getClass().getResource("/images/midi.png")));
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
  JMenuItem helpSummary =
      new JMenuItem(new ImageIcon(getClass().getResource("/images/aide.png")));
  JMenuItem siteinternet =
      new JMenuItem(new ImageIcon(getClass().getResource("/images/internet.png")));
  JMenuItem aboutMenuItem =
      new JMenuItem(new ImageIcon(getClass().getResource("/images/about.png")));

  //----------------------------------------------------------------
  // GAME BUTTONS - NOTES/GO
  JPanel gameButtonPanel = new JPanel();

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
  JPanel noteButtonPanel = new JPanel();

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

  JTabbedPane preferencesTabbedPane =
      new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT); // panel pour les parametres
  JPanel noteReadingNotesPanel = new JPanel(); // panel for choose type on fonts on first exercise

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

  //For table to choose notes on exercises

  JDialog scoreNotesDialog;
  ChooseNotePanel scoreChooseNoteP;
  //----

  JPanel principal = new JPanel(); // panel principal

  Properties settings = new Properties();

  private int[] savePrefs = new int[30]; // for cancel button

  JalmusUi(Jalmus jalmus, SwingNoteReadingGame game) {
    this.jalmus = jalmus;
    rhythmCursorXlimit = firstNoteXPos + (4 * jalmus.numberOfMeasures * noteDistance);
    this.noteGame = game;
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

    scoreChooseNoteP = new ChooseNotePanel(this.jalmus.scoreLevel.getKey(), Jalmus.SCOREREADING, bundle);
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
    doButton1.addActionListener(this);
    noteButtonPanel.add(doButton1);
    reButton = new JButton();
    reButton.addActionListener(this);
    noteButtonPanel.add(reButton);
    miButton = new JButton();
    miButton.addActionListener(this);
    noteButtonPanel.add(miButton);

    faButton = new JButton();
    faButton.addActionListener(this);
    noteButtonPanel.add(faButton);

    solButton = new JButton();
    solButton.addActionListener(this);
    noteButtonPanel.add(solButton);

    laButton = new JButton();
    laButton.addActionListener(this);
    noteButtonPanel.add(laButton);

    siButton = new JButton();
    siButton.addActionListener(this);
    noteButtonPanel.add(siButton);

    doButton2 = new JButton();
    doButton2.addActionListener(this);
    noteButtonPanel.add(doButton2);

    // BOUTONS POUR ACCORDS
    sharpButton1 = new JButton();
    sharpButton1.setText("#");
    sharpButton1.addActionListener(this);

    flatButton1 = new JButton();
    flatButton1.setText("b");
    flatButton1.addActionListener(this);

    sharpButton2 = new JButton();
    sharpButton2.setText("#");
    sharpButton2.addActionListener(this);

    flatButton2 = new JButton();
    flatButton2.setText("b");
    flatButton2.addActionListener(this);

    noteButtonPanel.add(sharpButton1, 0);
    noteButtonPanel.add(flatButton1, 5);
    noteButtonPanel.add(flatButton2, 6);
    noteButtonPanel.add(sharpButton2, 11);
    noteButtonPanel.setLayout(new GridLayout(2, 4));

    // BOUTONS INVISIBLES EN MODE NORMAL
    sharpButton1.setVisible(false);
    sharpButton2.setVisible(false);
    flatButton1.setVisible(false);
    flatButton2.setVisible(false);

    gameButtonPanel.setLayout(new FlowLayout());
    gameButtonPanel.add(startButton);
    noteButtonPanel.setPreferredSize(new Dimension(450, 40));
    gameButtonPanel.add(noteButtonPanel);
    gameButtonPanel.add(preferencesButton);
    noteButtonPanel.setBackground(Color.white);
    gameButtonPanel.setBackground(Color.white);

    midiOptionsDialog = buildMidiOptionsDialog();
    transpositionSpinner.setValue(0);

    menuParameters.add(menuPrefs);
    menuPrefs.addActionListener(this);
    menuParameters.add(menuMidi);
    menuMidi.addActionListener(this);

    /************************************************************************/
    /******************************** MENU *********************************/
    /***********************************************************************/
    preferencesDialog = buildPreferencesDialog();


    aboutDialog = new JDialog(this, true);
    //aboutDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    aboutDialog.setResizable(false);

    scoreNotesDialog = new JDialog(this, true);
    //    notesDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    scoreNotesDialog.setResizable(false);   

    levelMessage = new JDialog(this, true);
    //levelMessage.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    levelMessage.setResizable(false);

    scoreMessage = new JDialog(this, true);
    //scoreMessage.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    scoreMessage.setResizable(false);

    saveDialog = new JDialog(this, true);
    //scoreMessage.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    saveDialog.setResizable(false);

    helpMenu.setMnemonic(KeyEvent.VK_A);
    helpMenu.add(helpSummary);

    helpMenu.add(siteinternet);
    helpMenu.addSeparator();
    helpMenu.add(aboutMenuItem);
    helpSummary.addActionListener(this);
    siteinternet.addActionListener(this);

    aboutMenuItem.addActionListener(this);

    maBarre.add(buildExercisesMenu());
    lessonsMenu = buildLessonsMenu();
    maBarre.add(lessonsMenu);
    maBarre.add(menuParameters);
    maBarre.add(helpMenu);

    this.setJMenuBar(maBarre);
    maBarre.setVisible(true);

    /**************************************************************/
    /**************************************************************/
    /**************************************************************/

    /***************** FENETRE A PROPOS ******************************/
    aboutPanel.setVisible(true);
    bcredits = new JButton();

    bcredits.setIcon(new ImageIcon(getClass().getResource("/images/credits.png")));
    bcredits.addActionListener(this);

    blicence = new JButton();

    blicence.setIcon(new ImageIcon(getClass().getResource("/images/licence.png")));
    blicence.addActionListener(this);
    bfermer = new JButton();

    bfermer.setIcon(new ImageIcon(getClass().getResource("/images/cancel.png")));
    bfermer.addActionListener(this);

    aboutDialog.setContentPane(aboutPanel);

    aboutPanelTextArea = new JTextArea(12, 25);
    JScrollPane ascenceur = new JScrollPane(aboutPanelTextArea);
    aboutPanelTextArea.setEditable(false);
    aboutPanelTextArea.setLineWrap(true);
    aboutPanelTextArea.setWrapStyleWord(true);
    aboutPanelTextArea.setFont(new Font("SansSerif", Font.BOLD, 14));
    aboutPanelTextArea.setBackground(this.getBackground());

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
    okscoreMessage.addActionListener(this);
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

    principal.add(gameButtonPanel, BorderLayout.NORTH);
    principal.add(this.jalmus.panelanim, BorderLayout.CENTER);

    principal.setVisible(true);
    gameButtonPanel.setVisible(false);
    this.getContentPane().add(principal);

    this.jalmus.panelanim.setVisible(true);
    this.jalmus.panelanim.setBackground(Color.white);


    ButtonGroup group = new ButtonGroup();

    rblanguagefr = new JRadioButtonMenuItem("Fran"+"\u00E7"+"ais");
    rblanguagefr.setMnemonic(KeyEvent.VK_F);
    group.add(rblanguagefr);
    rblanguagefr.addActionListener(this);
    languages.add(rblanguagefr);

    rblanguagede = new JRadioButtonMenuItem("Deutsch");
    rblanguagede.setMnemonic(KeyEvent.VK_D);
    group.add(rblanguagede);
    rblanguagede.addActionListener(this);
    languages.add(rblanguagede);

    rblanguagees = new JRadioButtonMenuItem("Espanol");
    rblanguagees.setMnemonic(KeyEvent.VK_S);
    group.add(rblanguagees);
    rblanguagees.addActionListener(this);
    languages.add(rblanguagees);

    rblanguageen = new JRadioButtonMenuItem("English");
    rblanguageen.setMnemonic(KeyEvent.VK_E);
    rblanguageen.addActionListener(this);
    group.add(rblanguageen);
    languages.add(rblanguageen);

    rblanguageit = new JRadioButtonMenuItem("Italiano");
    rblanguageit.setMnemonic(KeyEvent.VK_I);
    rblanguageit.addActionListener(this);
    group.add(rblanguageit);
    languages.add(rblanguageit);

    rblanguageda = new JRadioButtonMenuItem("Dansk");
    rblanguageda.setMnemonic(KeyEvent.VK_A);
    rblanguageda.addActionListener(this);
    group.add(rblanguageda);
    languages.add(rblanguageda);

    rblanguagetr = new JRadioButtonMenuItem("Turkish");
    rblanguagetr.setMnemonic(KeyEvent.VK_T);
    rblanguagetr.addActionListener(this);
    group.add(rblanguagetr);
    languages.add(rblanguagetr);

    rblanguagefi = new JRadioButtonMenuItem("Finnish");
    rblanguagefi.setMnemonic(KeyEvent.VK_F);
    rblanguagefi.addActionListener(this);
    group.add(rblanguagefi);
    languages.add(rblanguagefi);

    rblanguagepl = new JRadioButtonMenuItem("Polish");
    rblanguagepl.setMnemonic(KeyEvent.VK_O);
    rblanguagepl.addActionListener(this);
    group.add(rblanguagepl);
    languages.add(rblanguagepl);

    rblanguageiw = new JRadioButtonMenuItem("Hebrew");
    rblanguageiw.setMnemonic(KeyEvent.VK_H);
    rblanguageiw.addActionListener(this);
    group.add(rblanguageiw);
    languages.add(rblanguageiw);

    rblanguageko = new JRadioButtonMenuItem("Korean");
    rblanguageko.setMnemonic(KeyEvent.VK_K);
    rblanguageko.addActionListener(this);
    group.add(rblanguageko);
    languages.add(rblanguageko);

    rblanguageeo = new JRadioButtonMenuItem("Esperanto");
    rblanguageeo.setMnemonic(KeyEvent.VK_N);
    rblanguageeo.addActionListener(this);
    group.add(rblanguageeo);
    languages.add(rblanguageeo);

    rblanguagegr = new JRadioButtonMenuItem("Greek");
    rblanguagegr.setMnemonic(KeyEvent.VK_G);
    rblanguagegr.addActionListener(this);
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

    languages.addActionListener(this);
    languages.setMnemonic(KeyEvent.VK_L);

    menuParameters.addSeparator();
    menuParameters.add(languages);
    menuParameters.setMnemonic(KeyEvent.VK_P);

    Image icone;
    try {
      icone = ImageIO.read(getClass().getClassLoader().getResource("images/icon.png"));
      setIconImage(icone);
    } catch (Exception e) {
      System.out.println("Cannot load Jalmus icon");
    }

    addKeyListener(this);
    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {

        if (jalmus.selectedGame == Jalmus.NOTEREADING) {
          Key key = jalmus.piano.getKey(e.getPoint());

          if (jalmus.piano.Getprevkey() != null && jalmus.piano.Getprevkey()!=key) {
            jalmus.piano.Getprevkey().off(jalmus.currentChannel, soundOnCheckBox.isSelected() && !jalmus.midierror);
          }
          if (key != null && jalmus.piano.Getprevkey()!=key) {
            key.on(jalmus.currentChannel, false);
          }
          jalmus.piano.Setprevkey(key);
          repaint();
        }
      }
    });
    
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        System.out.println("Jalmus has been resized !");
        if (jalmus.selectedGame == Jalmus.RHYTHMREADING || jalmus.selectedGame==Jalmus.SCOREREADING) {
          jalmus.handleNewButtonClicked();
        }
      }
    });

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        requestFocus();

        Dimension d = getSize();

        if (jalmus.selectedGame == Jalmus.NOTEREADING) {
          if (jalmus.piano.rightbuttonpressed(e.getPoint(),d.width)) {
            noteGame.noteLevel.basenotetoRight(jalmus.piano);
          }
          if (jalmus.piano.leftbuttonpressed(e.getPoint(),d.width)) {
            noteGame.noteLevel.basenotetoLeft(jalmus.piano);
          }

          repaint();

          // System.out.println (e.getPoint());
          Key key = jalmus.piano.getKey(e.getPoint());
          jalmus.piano.Setprevkey(key);
          if (!jalmus.midierror)  key.on(jalmus.currentChannel,  !jalmus.midierror);
          if (key != null) {
            if (key.Getknum() == 60 && !jalmus.gameStarted) {
              requestFocus();
              jalmus.startNoteGame();
              if (!jalmus.renderingThread.isAlive()) {
                jalmus.renderingThread.start();
              }
            } else if (key != null && jalmus.gameStarted && !jalmus.paused) {
              key.on(jalmus.currentChannel, soundOnCheckBox.isSelected() && !jalmus.midierror);
              repaint();

              if (key.Getknum() == jalmus.currentNote.getPitch()) {
                jalmus.rightAnswer();
              } else {
                jalmus.wrongAnswer();
              }
            }
          }
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (jalmus.selectedGame == Jalmus.NOTEREADING) {
          if (jalmus.piano.Getprevkey() != null) {
            jalmus.piano.Getprevkey().off(jalmus.currentChannel, soundOnCheckBox.isSelected() && !jalmus.midierror);
            repaint();
          }
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        if (jalmus.selectedGame == Jalmus.NOTEREADING) {
          if (jalmus.piano.Getprevkey() != null) {
            jalmus.piano.Getprevkey().off(jalmus.currentChannel, soundOnCheckBox.isSelected() && !jalmus.midierror);
            repaint();
            jalmus.piano.Setprevkey(null);
          }
        }
      }
    });

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent evt) {
        savesettings();
        dispose();
        System.exit(0);
      }

      @Override
      public void windowClosing(WindowEvent evt) {
        savesettings();
        dispose();
        System.exit(0);
      }
    });

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
                lessonsMenuItem[i][i1].addActionListener(this);
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
    instrumentsComboBox.addItemListener(this);
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
    midiInComboBox.addItemListener(this);

    midiOutComboBox = new JComboBox<String>();
    midiOutComboBox.setModel(midiOutComboBoxModel);
    midiOutComboBox.addItemListener(this);

    keyboardLengthComboBox = new JComboBox<String>();
    keyboardLengthComboBox.addItemListener(this);

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

    JDialog dialog = new JDialog(this, true);
    localizables.add(new Localizable.Dialog(dialog, "_menuMidi"));
    dialog.setContentPane(contentPanel);
    dialog.setSize(450, 550);
    //dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    //dialog.setResizable(false);

    return dialog;
  }

  //----------------------------------------------------------------
  private JDialog buildPreferencesDialog() {

    JPanel rhythmReadingPanel = buildRhythmReadingPreferencesPanel();
    JPanel scoreReadingPanel = buildScoreReadingPreferencesPanel();

    preferencesTabbedPane.addTab(null, new ImageIcon(getClass().getResource(noteGame.getPreferencesIconResource())), noteGame.getPreferencesPanel());
    localizables.add(new Localizable.Tab(preferencesTabbedPane, NOTE_READING_TAB, noteGame.getPreferencesLocalizable()));
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

    JDialog dialog = new JDialog(this, true);
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
    rhythmGameTypeComboBox.addItemListener(this);

    rhythmGameSpeedComboBox = new JComboBox<String>();
    rhythmGameSpeedComboBox.addItem("Largo");
    rhythmGameSpeedComboBox.addItem("Adagio");
    rhythmGameSpeedComboBox.addItem("Moderato");
    rhythmGameSpeedComboBox.addItem("Allegro");
    rhythmGameSpeedComboBox.addItem("Presto");
    rhythmGameSpeedComboBox.addItemListener(this);

    JPanel gamePanel = new JPanel();
    gamePanel.add(rhythmGameTypeComboBox);
    gamePanel.add(rhythmGameSpeedComboBox);
    localizables.add(new Localizable.NamedGroup(gamePanel, "_menuExercises"));

    /* 2nd panel - RHYTHM */

    wholeCheckBox = new JCheckBox("", true);
    wholeCheckBox.addItemListener(this);
    halfCheckBox = new JCheckBox("", true);
    halfCheckBox.addItemListener(this); 
    dottedhalfCheckBox = new JCheckBox("", false);
    dottedhalfCheckBox.addItemListener(this);
    quarterCheckBox = new JCheckBox("", false);
    quarterCheckBox.addItemListener(this);
    eighthCheckBox = new JCheckBox("", false);
    eighthCheckBox.addItemListener(this);
    restCheckBox = new JCheckBox("", true);
    restCheckBox.addItemListener(this);
    tripletCheckBox = new JCheckBox("", false);
    tripletCheckBox.addItemListener(this);

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
    metronomeCheckBox.addItemListener(this);
    metronomeShowCheckBox = new JCheckBox("", true);
    metronomeShowCheckBox.setSelected(false);
    metronomeShowCheckBox.addItemListener(this);

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
    scoreGameTypeComboBox.addItemListener(this);

    scoreGameSpeedComboBox = new JComboBox<String>();
    scoreGameSpeedComboBox.addItem("Largo");
    scoreGameSpeedComboBox.addItem("Adagio");
    scoreGameSpeedComboBox.addItem("Moderato");
    scoreGameSpeedComboBox.addItem("Allegro");
    scoreGameSpeedComboBox.addItem("Presto");
    scoreGameSpeedComboBox.addItemListener(this);

    JPanel scoregamePanel = new JPanel();
    scoregamePanel.add(scoreGameTypeComboBox);
    scoregamePanel.add(scoreGameSpeedComboBox);
    localizables.add(new Localizable.NamedGroup(scoregamePanel, "_menuExercises"));

    /* 2nd panel - Key & notes */

    scoreKeyComboBox = new JComboBox<String>();
    scoreKeyComboBox.addItemListener(this);

    scoreNotesComboBox = new JComboBox<String>();
    scoreNotesComboBox.addItemListener(this);

    scoreAlterationsComboBox = new JComboBox<String>();
    scoreAlterationsComboBox.addItemListener(this);

    JPanel scoreKeyPanel = new JPanel(); // panel pour la Key du premier jeu
    scoreKeyPanel.add(scoreKeyComboBox);
    scoreKeyPanel.add(scoreAlterationsComboBox);
    scoreKeyPanel.add(scoreNotesComboBox);
    localizables.add(new Localizable.NamedGroup(scoreKeyPanel, "_menuNotes"));

    /* 3rd panel - RYTHM */

    scorewholeCheckBox = new JCheckBox("", true);
    scorewholeCheckBox.addItemListener(this);   
    scorehalfCheckBox = new JCheckBox("", true);
    scorehalfCheckBox.addItemListener(this);
    scoredottedhalfCheckBox = new JCheckBox("", false);
    scoredottedhalfCheckBox.addItemListener(this);  
    scorequarterCheckBox = new JCheckBox("", false);
    scorequarterCheckBox.addItemListener(this);            
    scoreeighthCheckBox = new JCheckBox("", false);
    scoreeighthCheckBox.addItemListener(this);          
    scorerestCheckBox = new JCheckBox("", true);
    scorerestCheckBox.addItemListener(this);      
    scoreTripletCheckBox = new JCheckBox("", false);
    scoreTripletCheckBox.addItemListener(this);

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
    scoreMetronomeCheckBox.addActionListener(this);
    scoreMetronomeShowCheckBox.addActionListener(this);

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

  void changeLanguage() {
    bundle = ResourceBundle.getBundle("language", new Locale(this.jalmus.language));
    System.out.println(new Locale(this.jalmus.language));
    for (Iterator<Localizable> itr = localizables.iterator(); itr.hasNext();) {
      Localizable localizable = (Localizable)itr.next();
      localizable.update(bundle);
    }
    
    noteGame.updateLanguage(bundle);

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
    scoreNotesDialog.setTitle("Choose notes to study");

    tlicence = bundle.getString("_licence");
    tcredits = bundle.getString("_credits");

    rhythmGameTypeComboBox.removeAllItems();
    //  rhythmGameTypeComboBox.addItem(bundle.getString("_learninggame"));
    rhythmGameTypeComboBox.addItem(bundle.getString("_normalgame"));

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
  
  @Override
  
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == rblanguageen) {
      jalmus.language = "en";
      changeLanguage();
      //To do : recharge lessons with new lang
    }

    if (e.getSource() == rblanguagede) {
      jalmus.language = "de";
      changeLanguage();
    }

    if (e.getSource() == rblanguagees) {
      jalmus.language = "es";
      changeLanguage();
    }

    if (e.getSource() == rblanguagefr) {
      jalmus.language = "fr";
      changeLanguage();
    }

    if (e.getSource() == rblanguageit) {
      jalmus.language = "it";
      changeLanguage();
    }

    if (e.getSource() == rblanguageda) {
      jalmus.language = "da";
      changeLanguage();
    }

    if (e.getSource() == rblanguagetr) {
      jalmus.language = "tr";
      changeLanguage();
    }

    if (e.getSource() == rblanguagefi) {
      jalmus.language = "fi";
      changeLanguage();
    } 

    if (e.getSource() == rblanguageko) {
      jalmus.language = "ko";
      changeLanguage();
    }

    if (e.getSource() == rblanguagepl) {
      jalmus.language = "pl";
      changeLanguage();
    }   

    if (e.getSource() == rblanguageiw) {
      jalmus.language = "iw";
      changeLanguage();
    }   

    if (e.getSource() == rblanguageeo) {
      jalmus.language = "eo";
      changeLanguage();
    } 

    if (e.getSource() == rblanguagegr) {
      jalmus.language = "gr";
      changeLanguage();
    } 

    for (int i0 = 0; i0 < lessonsMenuItem.length; i0++) {
      for (int i = 0; i < lessonsMenuItem[0].length; i++) {
        if (e.getSource() == lessonsMenuItem[i0][i]) {
          jalmus.handleLessonMenuItem(lessonsMenuItem[i0][i].getText(),i0);
          System.out.println("lesson " + i0 + i + lessonsMenuItem[i0][i].getText());
        }
      }
    }

    if (e.getSource() == menuPrefs) {
      jalmus.stopGames();
      backupPreferences();

      preferencesDialog.setLocationRelativeTo(this);
      preferencesDialog.setVisible(true);
    } else if (e.getSource() == helpSummary) {
      jalmus.stopGames();
      Object[] options = {bundle.getString("_yes"), bundle.getString("_no")};
      int n = JOptionPane.showOptionDialog(this,
          bundle.getString("_wikidoc"),
          "Information",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE,
          null,     //don't use a custom Icon
          options,  //the titles of buttons
          options[0]); //default button title
      if (n == 0) {
        jalmus.OpenURI("http://www.jalmus.net/pmwiki/pmwiki.php/"+jalmus.language);
      }
    } else if (e.getSource() == siteinternet) {
      jalmus.stopGames();
      jalmus.OpenURI("http://jalmus.net?lang = "+jalmus.language);
    } else if (e.getSource() == okscoreMessage) {
      scoreMessage.dispose();
      if (jalmus.isLessonMode) {
        if ((jalmus.currentlesson.isNoteLevel() & jalmus.currentScore.isWin()) 
             || jalmus.currentlesson.isRhythmLevel() 
             || jalmus.currentlesson.isScoreLevel()) {
          jalmus.nextLevel();
        } else {
          jalmus.startLevel();
        }
      }
    } else if (e.getSource() == menuMidi) {
      if (jalmus.gameStarted) {
        jalmus.paused = true;
      }
      jalmus.backupMidiOptions();
      midiOptionsDialog.setLocationRelativeTo(this);
      midiOptionsDialog.setVisible(true);
    } else if (e.getSource() == bfermer) {
      aboutDialog.setVisible(false);
    } else if (e.getSource() == aboutMenuItem) {
      jalmus.stopGames();
      aboutDialog.setContentPane(aboutPanel);
      aboutPanelTextArea.setText(tcredits);
      aboutDialog.setSize(400, 330);
      aboutDialog.setLocationRelativeTo(this);
      aboutDialog.setVisible(true);
    } else if (e.getSource() == blicence) {
      aboutPanelTextArea.setText(tlicence);
    } else if (e.getSource() == bcredits) {
      aboutPanelTextArea.setText(tcredits);
    } 

    //  SI LE LABEL DU BOUTON SELECTIONNE EST EGAL A LA NOTE COURANTE   ----> GAGNE

    if ((jalmus.gameStarted && jalmus.selectedGame == Jalmus.NOTEREADING && !jalmus.paused)
        && (e.getSource() == doButton1 || e.getSource() == reButton || e.getSource() == miButton || e.getSource() == faButton 
        || e.getSource() == solButton || e.getSource() == laButton || e.getSource() == siButton || e.getSource() == doButton2
        || e.getSource() == sharpButton1 || e.getSource() == sharpButton2 || e.getSource() == flatButton1 
        || e.getSource() == flatButton2)) {
      if (!jalmus.currentNote.getAlteration().equals("")) {  // NOTES AVEC ALTERATION
        if (((JButton)e.getSource()).getText().equals(jalmus.currentNote.getAlteration())) {
          jalmus.alterationOk = true;
        } else if (jalmus.alterationOk && ((JButton)e.getSource()).getText().equals(jalmus.currentNote.getNom())) {
          jalmus.rightAnswer();
        } else {
          jalmus.wrongAnswer();
        }
      } else if (jalmus.currentNote.getAlteration().equals("")) { // NOTE SANS ALTERATION
        if (((JButton)e.getSource()).getText() == jalmus.currentNote.getNom()) {
          jalmus.rightAnswer();
        } else {
          jalmus.wrongAnswer();
        }
      }
    }
    repaint();
  }
  
  @Override
  public void itemStateChanged(ItemEvent evt) {
    if (evt.getItemSelectable() == this.midiInComboBox && !this.selectmidi_forlang) {
      String smidiin = (String) this.midiInComboBox.getSelectedItem();
      if (smidiin != this.pasclavier) {
        if (jalmus.open) {
          jalmus.inputDevice.close();
          jalmus.open = false;
        }

        String midimessage = "Initialisation "+smidiin;

        MidiDevice.Info info = MidiCommon.getMidiDeviceInfo(smidiin, false);
        if (info == null) {
          midimessage = "nodevice";
          System.out.println(midimessage);
        } else {
          try {
            jalmus.inputDevice = MidiSystem.getMidiDevice(info);
            jalmus.inputDevice.open();
          } catch (MidiUnavailableException e) {
            midimessage = "nodevice";
            System.out.println(midimessage);
          }

          Receiver r = new DumpReceiver(this.jalmus);
          try {
            Transmitter t = jalmus.inputDevice.getTransmitter();
            t.setReceiver(r);
          } catch (MidiUnavailableException e) {
            midimessage = "wasn't able to connect the device's Transmitter to the Receiver:";
            System.out.println(e);
            jalmus.inputDevice.close();
            System.exit(1);
          }
          midimessage = "End initialisation";
        }
        if (jalmus.inputDevice.isOpen()) {
          System.out.println("Midi Device open : play a key, if this key don't change his color at screen, verify the MIDI port name");
        }
        jalmus.open = true;
      }
    }

    if (evt.getItemSelectable() == this.midiOutComboBox && !this.selectmidi_forlang) {
      String smidiout = (String) this.midiOutComboBox.getSelectedItem();
      if (smidiout != this.pasclavier) {
        String midimessage = "Initialisation " + smidiout;
        MidiDevice.Info info = MidiCommon.getMidiDeviceInfo(smidiout, true);
        if (info == null) {
          midimessage = "nodevice";
          System.out.println(midimessage);
        } else {
          try {
            jalmus.outputDevice = MidiSystem.getMidiDevice(info);
            jalmus.outputDevice.open();
          } catch (MidiUnavailableException e) {
            midimessage = "nodevice";
            System.out.println(midimessage);
          }

          //Receiver r = new DumpReceiver();
          //try {
          //    Receiver t = outputDevice.getReceiver();
          //}
          //catch (MidiUnavailableException e) {
          //    midimessage = "wasn't able to connect the device's Receiver to the Receiver:";
          //    System.out.println(e);
          //    inputDevice.close();
          //    System.exit(1);
          //}
          //midimessage = "End initialisation";
        }
        //if (inputDevice.isOpen()) {
        //    System.out.println("Midi Device open : play a key, if this key don't change his color at screen, verify the MIDI port name");
        //}
        //open = true;
      }
      }

      // For rhythm level update
      else if (evt.getItemSelectable() == this.wholeCheckBox) {
        if (this.wholeCheckBox.isSelected()) {
          jalmus.rhythmLevel.setWholeNote(true);
        } else {
          jalmus.rhythmLevel.setWholeNote(false);
        }
      } else if (evt.getItemSelectable() == this.halfCheckBox) {
        if (this.halfCheckBox.isSelected()) {
          jalmus.rhythmLevel.setHalfNote(true);
        } else {
          jalmus.rhythmLevel.setHalfNote(false);
        }
      } else if (evt.getItemSelectable() == this.dottedhalfCheckBox) {
        if (this.dottedhalfCheckBox.isSelected()) {
          jalmus.rhythmLevel.setDottedHalfNote(true);
        } else {
          jalmus.rhythmLevel.setDottedHalfNote(false);
        }
      } else if (evt.getItemSelectable() == this.quarterCheckBox) {
        if (this.quarterCheckBox.isSelected()) {
          jalmus.rhythmLevel.setQuarterNote(true);
        } else {
          jalmus.rhythmLevel.setQuarterNote(false);
        }
      } else if (evt.getItemSelectable() == this.eighthCheckBox) {
        if (this.eighthCheckBox.isSelected()) {
          jalmus.rhythmLevel.setEighthNote(true);
        } else {
          jalmus.rhythmLevel.setEighthNote(false);
        }
      } else if (evt.getItemSelectable() == this.restCheckBox) {
        if (this.restCheckBox.isSelected()) {
          jalmus.rhythmLevel.setSilence(true);
        } else {
          jalmus.rhythmLevel.setSilence(false);
        }
      } else if (evt.getItemSelectable() == this.tripletCheckBox) {
        if (this.tripletCheckBox.isSelected()) {
          jalmus.rhythmLevel.setTriplet(true);
        } else {
          jalmus.rhythmLevel.setTriplet(false);
        }
      } else if (evt.getItemSelectable() == this.metronomeCheckBox) {
        if (this.metronomeCheckBox.isSelected()) {
          jalmus.rhythmLevel.setMetronome(true);
        } else {
          jalmus.rhythmLevel.setMetronome(false);
        }
      } else if (evt.getItemSelectable() == this.metronomeShowCheckBox) {
        if (this.metronomeShowCheckBox.isSelected()) {
          jalmus.rhythmLevel.setMetronomeBeats(true);
        } else {
          jalmus.rhythmLevel.setMetronomeBeats(false);
        }
      } 

      // For score level update
      else if (evt.getItemSelectable() == this.scorewholeCheckBox) {
        if (this.scorewholeCheckBox.isSelected()) {
          jalmus.scoreLevel.setWholeNote(true);
        } else {
          jalmus.scoreLevel.setWholeNote(false);
        }
      } else if (evt.getItemSelectable() == this.scorehalfCheckBox) {
        if (this.scorehalfCheckBox.isSelected()) {
          jalmus.scoreLevel.setHalfNote(true);
        } else {
          jalmus.scoreLevel.setHalfNote(false);
        }
      } else if (evt.getItemSelectable() == this.scoredottedhalfCheckBox) {
        if (this.scoredottedhalfCheckBox.isSelected()) {
          jalmus.scoreLevel.setDottedHalfNote(true);
        } else {
          jalmus.scoreLevel.setDottedHalfNote(false);
        }
      } else if (evt.getItemSelectable() == this.scorequarterCheckBox) {
        if (this.scorequarterCheckBox.isSelected()) {
          jalmus.scoreLevel.setQuarterNote(true);
        } else {
          jalmus.scoreLevel.setQuarterNote(false);
        }
      } else if (evt.getItemSelectable() == this.scoreeighthCheckBox) {
        if (this.scoreeighthCheckBox.isSelected()) {
          jalmus.scoreLevel.setEighthNote(true);
        } else {
          jalmus.scoreLevel.setEighthNote(false);
        }
      } else if (evt.getItemSelectable() == this.scorerestCheckBox) {
        if (this.scorerestCheckBox.isSelected()) {
          jalmus.scoreLevel.setSilence(true);
        } else {
          jalmus.scoreLevel.setSilence(false);
        }
      } else if (evt.getItemSelectable() == this.scoreTripletCheckBox) {
        if (this.scoreTripletCheckBox.isSelected()) {
          jalmus.scoreLevel.setTriplet(true);
        } else {
          jalmus.scoreLevel.setTriplet(false);
        }
      } else if (evt.getItemSelectable() == this.scoreMetronomeCheckBox) {
        if (this.scoreMetronomeCheckBox.isSelected()) {
          jalmus.scoreLevel.setMetronome(true);
        } else {
          jalmus.scoreLevel.setMetronome(false);
        }
      } else if (evt.getItemSelectable() == this.scoreMetronomeShowCheckBox) {
        if (this.scoreMetronomeShowCheckBox.isSelected()) {
          jalmus.scoreLevel.setMetronomeBeats(true);
        } else {
          jalmus.scoreLevel.setMetronomeBeats(false);
        }
      } else if (evt.getItemSelectable() == this.instrumentsComboBox) {
        if (!jalmus.midierror && jalmus.instruments != null) {
          jalmus.currentChannel.getChannel().programChange(this.instrumentsComboBox.getSelectedIndex());
        }
      } else if (evt.getItemSelectable() == this.scoreKeyComboBox) {
        if (this.scoreKeyComboBox.getSelectedIndex() == 0) {
          jalmus.scoreLevel.setCurrentKey("treble");
          jalmus.scoreLevel.initPitcheslist(9);
          if (jalmus.selectedGame == Jalmus.SCOREREADING) {
            jalmus.initRhythmGame();
          }
        } else if (this.scoreKeyComboBox.getSelectedIndex() == 1) {
          jalmus.scoreLevel.setCurrentKey("bass");
          jalmus.scoreLevel.initPitcheslist(9);
          if (jalmus.selectedGame == Jalmus.SCOREREADING) {
            jalmus.initRhythmGame();
          }
        }
      }
      

      else if (evt.getItemSelectable() == this.rhythmGameTypeComboBox) {
        if (this.rhythmGameTypeComboBox.getSelectedIndex() == 0) {
          jalmus.rhythmgame = 0; // fix this with creating class level rhythm
        }/* else if (rhythmGameTypeComboBox.getSelectedIndex() == 1) {
        rhythmgame = 1;

        }*/
      }

      // Speed choice rhythm reading
      else if (evt.getItemSelectable() == this.rhythmGameSpeedComboBox) {
        if (this.rhythmGameSpeedComboBox.getSelectedIndex() == 0) {
          jalmus.rhythmLevel.setSpeed(40);
        } else if (this.rhythmGameSpeedComboBox.getSelectedIndex() == 1) {
          jalmus.rhythmLevel.setSpeed(60);
        } else if (this.rhythmGameSpeedComboBox.getSelectedIndex() == 2) {
          jalmus.rhythmLevel.setSpeed(100);
        } else if (this.rhythmGameSpeedComboBox.getSelectedIndex() == 3) {
          jalmus.rhythmLevel.setSpeed(120);
        } else if (this.rhythmGameSpeedComboBox.getSelectedIndex() == 4) {
          jalmus.rhythmLevel.setSpeed(160);
        }
      } else if (evt.getItemSelectable() == this.keyboardLengthComboBox) {
        if (this.keyboardLengthComboBox.getSelectedIndex() == 0) {
          jalmus.piano = new Piano(73, 40);
        } else if (this.keyboardLengthComboBox.getSelectedIndex() == 1) {
          jalmus.piano = new Piano(61, 90);
        }
      } else if (evt.getItemSelectable() == this.scoreGameSpeedComboBox) {
        if (this.scoreGameSpeedComboBox.getSelectedIndex() == 0) {
          jalmus.scoreLevel.setSpeed(40);
        } else if (this.scoreGameSpeedComboBox.getSelectedIndex() == 1) {
          jalmus.scoreLevel.setSpeed(60);
        } else if (this.scoreGameSpeedComboBox.getSelectedIndex() == 2) {
          jalmus.scoreLevel.setSpeed(100);
        } else if (this.scoreGameSpeedComboBox.getSelectedIndex() == 3) {
          jalmus.scoreLevel.setSpeed(120);
        } else if (this.scoreGameSpeedComboBox.getSelectedIndex() == 4) {
          jalmus.scoreLevel.setSpeed(160);
        }
      } else if (evt.getItemSelectable() == this.scoreNotesComboBox) {
        if (this.scoreNotesComboBox.getSelectedIndex() == 0) {
          jalmus.scoreLevel.setNotetype("notes");
          jalmus.scoreLevel.setNbnotes(9);
        } 
        if (this.scoreNotesComboBox.getSelectedIndex() == 1) {
          jalmus.scoreLevel.setNotetype("notes");
          jalmus.scoreLevel.setNbnotes(15);
        } 
        if (this.scoreNotesComboBox.getSelectedIndex() == 2) {
          jalmus.scoreLevel.setNbnotes(0);
          jalmus.scoreLevel.setNotetype("custom");

          this.scoreChooseNoteP = new ChooseNotePanel(jalmus.scoreLevel.getKey(), Jalmus.SCOREREADING, this.bundle);
          this.scoreChooseNoteP.updateTable(jalmus.scoreLevel.getPitcheslist());
          this.scoreChooseNoteP.setOpaque(true); //content panes must be opaque 
          this.scoreChooseNoteP.setVisible(true);
          this.scoreChooseNoteP.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            //Execute when button is pressed
              if (!scoreChooseNoteP.atLeast3Pitches()) {
                JOptionPane.showMessageDialog(null, "Choose at least three notes", "Warning", JOptionPane.ERROR_MESSAGE); 
              } else {
                scoreNotesDialog.setVisible(false);
                jalmus.scoreLevel.setPitcheslist(scoreChooseNoteP.getPitches());
              }
            }
          });    

          this.scoreNotesDialog.setContentPane(this.scoreChooseNoteP);
          this.scoreNotesDialog.setSize(650, 220);
          this.scoreNotesDialog.setLocationRelativeTo(this);
          this.scoreNotesDialog.setVisible(true);
          this.scoreChooseNoteP.setVisible(true);

          add(this.scoreNotesDialog);
        } 
      } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 15) {
        // random choice of tonality when game start
        jalmus.scoreLevel.setRandomTonality(true);
        jalmus.scoreLevel.getCurrentTonality().init(0, "r");
      } else {
        jalmus.scoreLevel.setRandomTonality(false);
        if (this.scoreAlterationsComboBox.getSelectedIndex() == 1) {
          jalmus.scoreLevel.getCurrentTonality().init(1, "#");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 2) {
          jalmus.scoreLevel.getCurrentTonality().init(2, "#");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 3) {
          jalmus.scoreLevel.getCurrentTonality().init(3, "#");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 4) {
          jalmus.scoreLevel.getCurrentTonality().init(4, "#");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 5) {
          jalmus.scoreLevel.getCurrentTonality().init(5, "#");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 6) {
          jalmus.scoreLevel.getCurrentTonality().init(6, "#");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 7) {
          jalmus.scoreLevel.getCurrentTonality().init(7, "#");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 8) {
          jalmus.scoreLevel.getCurrentTonality().init(1, "b");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 9) {
          jalmus.scoreLevel.getCurrentTonality().init(2, "b");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 10) {
          jalmus.scoreLevel.getCurrentTonality().init(3, "b");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 11) {
          jalmus.scoreLevel.getCurrentTonality().init(4, "b");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 12) {
          jalmus.scoreLevel.getCurrentTonality().init(5, "b");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 13) {
          jalmus.scoreLevel.getCurrentTonality().init(6, "b");
        } else if (this.scoreAlterationsComboBox.getSelectedIndex() == 14) {
          jalmus.scoreLevel.getCurrentTonality().init(7, "b");
        }
      }
    }
  
  @Override
  public void keyPressed(KeyEvent evt) {

    // Called when the user has pressed a key, which can be
    // a special key such as an arrow key.
    int key = evt.getKeyCode(); // keyboard code for the key that was pressed

    if (jalmus.isLessonMode && jalmus.gameStarted && key == KeyEvent.VK_ESCAPE) {
      jalmus.gameStarted = false;
      jalmus.nextLevel();
    }

    if (jalmus.selectedGame == Jalmus.NOTEREADING 
      && !jalmus.isLessonMode
        && !jalmus.gameStarted 
        && (noteGame.noteLevel.isNotesgame() 
            || noteGame.noteLevel.isAccidentalsgame() 
            || noteGame.noteLevel.isCustomNotesgame()) 
        && !noteGame.noteLevel.isAllnotesgame()) {
      if (key == KeyEvent.VK_LEFT) {
        noteGame.noteLevel.basenotetoLeft(jalmus.piano);
      } else if (key == KeyEvent.VK_RIGHT) {
        noteGame.noteLevel.basenotetoRight(jalmus.piano);
      }
    } else if (jalmus.selectedGame == Jalmus.RHYTHMREADING && 
        jalmus.rhythmgame == 0 && jalmus.muterhythms && jalmus.gameStarted) {
      if (key == KeyEvent.VK_SPACE) {
        jalmus.rhythmKeyPressed(71);
      }
    }
    repaint();
  } // end keyPressed()

  @Override
  public void keyReleased(KeyEvent evt) {
    // empty method, required by the KeyListener Interface
  }
  
  /** FONCTIONS POUR SAISIE AU CLAVIER */
  @Override
  public void keyTyped(KeyEvent evt) {
    char ch = evt.getKeyChar();  // The character typed.

    if (jalmus.selectedGame == Jalmus.NOTEREADING && jalmus.gameStarted) {
      if (ch == 'P' || ch == 'p') {
        if (!jalmus.paused) {
          jalmus.paused = true;
        }

        int n = JOptionPane.showConfirmDialog(this, "",
            this.bundle.getString("_gamepaused"),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (n == 0) {
          jalmus.paused = false;
        }
      }
    }

    if (jalmus.selectedGame == Jalmus.NOTEREADING && jalmus.gameStarted && !jalmus.paused && noteGame.noteLevel.isNotesgame()) {

      if (ch == 'Q' || ch=='q' || ch=='A' || ch=='a' || ch=='S' || ch=='s' ||
          ch == 'D' || ch=='d' || ch=='F' || ch=='f' || ch=='G' || ch=='g' ||
          ch == 'H' || ch=='h' || ch=='J' || ch=='j' || ch=='K' || ch=='k') {

        if (((jalmus.language == "fr" && (ch=='Q' || ch=='q'))
              || ((jalmus.language == "en" || jalmus.language =="es" || jalmus.language =="de") && (ch=='A' || ch=='a')))
            && jalmus.currentNote.getNom() == DO)
        {
          jalmus.rightAnswer();
        } else if ((ch == 'S' || ch=='s') && jalmus.currentNote.getNom().equals(RE)) {
          jalmus.rightAnswer();
        } else if ((ch == 'D' || ch=='d') && jalmus.currentNote.getNom().equals(MI)) {
          jalmus.rightAnswer();
        } else if ((ch == 'F' || ch=='f') && jalmus.currentNote.getNom().equals(FA)) {
          jalmus.rightAnswer();
        } else if ((ch == 'G' || ch=='g') && jalmus.currentNote.getNom().equals(SOL)) {
          jalmus.rightAnswer();
        } else if ((ch == 'H' || ch=='h') && jalmus.currentNote.getNom().equals(LA)) {
          jalmus.rightAnswer();
        } else if ((ch == 'J' || ch=='j') && jalmus.currentNote.getNom().equals(SI)) {
          jalmus.rightAnswer();
        } else if ((ch == 'K' || ch=='k') && jalmus.currentNote.getNom().equals(DO)) {
          jalmus.rightAnswer();
        } else {
          jalmus.wrongAnswer();
        }
          }
    }
  }  // end keyTyped()
  
  void changeScreen(boolean isLessonMode, Lessons currentlesson,
      int selectedGame) {
    if (isLessonMode) {     
      if (currentlesson.isNoteLevel()) {  
        startButton.setVisible(false);
        preferencesButton.setVisible(false);
        newButton.setVisible(false);
        listenButton.setVisible(false);
        menuPrefs.setEnabled(false);
      } else if (currentlesson.isRhythmLevel() || currentlesson.isScoreLevel()) { 
        gameButtonPanel.add(newButton);
        gameButtonPanel.add(listenButton);
        gameButtonPanel.add(startButton);
        gameButtonPanel.add(preferencesButton);
        scoreYpos = 110;
        if (currentlesson.isScoreLevel()) {
          alterationWidth = jalmus.scoreLevel.getCurrentTonality().getAlterationsNumber() * 12;
          firstNoteXPos = windowMargin + keyWidth + alterationWidth + timeSignWidth + notesShift;
//          numberOfMeasures = (size.width - (windowMargin * 2) - scoreLineWidth) / (scoreLevel.getTimeSignNumerator() * noteDistance);             
        } else if ( currentlesson.isRhythmLevel()) {
          Dimension size = getSize();
          int scoreLineWidth = keyWidth + alterationWidth + timeSignWidth;
          jalmus.numberOfMeasures = (size.width - (windowMargin * 2) - scoreLineWidth) / (jalmus.rhythmLevel.getTimeSignNumerator() * noteDistance);            
        }
        repaint();
        gameButtonPanel.setVisible(true);

        menuPrefs.setEnabled(false);
      }
    } else {
      startButton.setVisible(true);
      preferencesButton.setVisible(true);
      menuPrefs.setEnabled(true);
    }

    if (selectedGame == Jalmus.NOTEREADING) {
      gameButtonPanel.setVisible(true);
      noteButtonPanel.setVisible(true);
      principal.setVisible(true);
      System.out.println(noteGame.noteLevel.getNbnotes());
      if (noteGame.noteLevel.isNotesgame() && noteGame.noteLevel.getCurrentTonality().getAlterationsNumber() == 0) {
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
    } else if (selectedGame == Jalmus.RHYTHMREADING || 
               selectedGame == Jalmus.SCOREREADING) {
      gameButtonPanel.setVisible(true);
      noteButtonPanel.setVisible(false);
      newButton.setVisible(true);
      listenButton.setVisible(true);
      principal.setVisible(true);
    }
  }

  void restorePreferences() {
    noteGame.deserializePrefs(savePrefs);
    rhythmGameTypeComboBox.setSelectedIndex(savePrefs[8]);
    rhythmGameSpeedComboBox.setSelectedIndex(savePrefs[9]);
    if (savePrefs[10] == 1) {
      wholeCheckBox.setSelected(true);
    } else {
      wholeCheckBox.setSelected(false);
    }
    if (savePrefs[11] == 1) {
      halfCheckBox.setSelected(true);
    } else {
      halfCheckBox.setSelected(false);
    }
    if (savePrefs[28] == 1) {
      dottedhalfCheckBox.setSelected(true);
    } else {
      dottedhalfCheckBox.setSelected(false);
    }
    if (savePrefs[12] == 1) {
      quarterCheckBox.setSelected(true);
    } else {
      quarterCheckBox.setSelected(false);
    }
    if (savePrefs[13] == 1) {
      eighthCheckBox.setSelected(true);
    } else {
      eighthCheckBox.setSelected(false);
    }
    if (savePrefs[14] == 1) {
      restCheckBox.setSelected(true);
    } else {
      restCheckBox.setSelected(false);
    }
    if (savePrefs[15] == 1) {
      metronomeCheckBox.setSelected(true);
    } else {
      metronomeCheckBox.setSelected(false);
    }

    scoreGameTypeComboBox.setSelectedIndex(savePrefs[16]);
    scoreGameSpeedComboBox.setSelectedIndex(savePrefs[17]);
    if (savePrefs[18] == 1) {
      scorewholeCheckBox.setSelected(true);
    } else {
      scorewholeCheckBox.setSelected(false);
    }
    if (savePrefs[19] == 1) {
      scorehalfCheckBox.setSelected(true);
    } else {
      scorehalfCheckBox.setSelected(false);
    }
    if (savePrefs[28] == 1) {
      scoredottedhalfCheckBox.setSelected(true);
    } else {
      scoredottedhalfCheckBox.setSelected(false);
    }
    if (savePrefs[20] == 1) {
      scorequarterCheckBox.setSelected(true);
    } else {
      scorequarterCheckBox.setSelected(false);
    }
    if (savePrefs[21] == 1) {
      scoreeighthCheckBox.setSelected(true);
    } else {
      scoreeighthCheckBox.setSelected(false);
    }
    if (savePrefs[22] == 1) {
      scorerestCheckBox.setSelected(true);
    } else {
      scorerestCheckBox.setSelected(false);
    }
    if (savePrefs[23] == 1) {
      scoreMetronomeCheckBox.setSelected(true);
    } else {
      scoreMetronomeCheckBox.setSelected(false);
    }
    scoreKeyComboBox.setSelectedIndex(savePrefs[24]);
    scoreAlterationsComboBox.setSelectedIndex(savePrefs[25]);
    if (savePrefs[26] == 1) {
      tripletCheckBox.setSelected(true);
    } else {
      tripletCheckBox.setSelected(false);
    }
    if (savePrefs[27] == 1) {
      scoreTripletCheckBox.setSelected(true);
    } else {
      scoreTripletCheckBox.setSelected(false);
    }
  }
  
  void backupPreferences() {
    System.arraycopy(noteGame.serializePrefs(), 0, savePrefs, 0, 8);
    savePrefs[8] = rhythmGameTypeComboBox.getSelectedIndex();
    savePrefs[9] = rhythmGameSpeedComboBox.getSelectedIndex();
    if (wholeCheckBox.isSelected()) {
      savePrefs[10] = 1;
    } else {
      savePrefs[10] = 0;
    }
    if (halfCheckBox.isSelected()) {
      savePrefs[11] = 1;
    } else {
      savePrefs[11] = 0;
    }
    if (dottedhalfCheckBox.isSelected()) {
      savePrefs[28] = 1;
    } else {
      savePrefs[28] = 0;
    }
    if (quarterCheckBox.isSelected()) {
      savePrefs[12] = 1;
    } else {
      savePrefs[12] = 0;
    }
    if (eighthCheckBox.isSelected()) {
      savePrefs[13] = 1;
    } else {
      savePrefs[13] = 0;
    }
    if (restCheckBox.isSelected()) {
      savePrefs[14] = 1;
    } else {
      savePrefs[14] = 0;
    }
    if (metronomeCheckBox.isSelected()) {
      savePrefs[15] = 1;
    } else {
      savePrefs[15] = 0;
    }
    savePrefs[16] = scoreGameTypeComboBox.getSelectedIndex();
    savePrefs[17] = scoreGameSpeedComboBox.getSelectedIndex();
    if (scorewholeCheckBox.isSelected()) {
      savePrefs[18] = 1;
    } else {
      savePrefs[18] = 0;
    }
    if (scorehalfCheckBox.isSelected()) {
      savePrefs[19] = 1;
    } else {
      savePrefs[19] = 0;
    }
    if (scoredottedhalfCheckBox.isSelected()) {
      savePrefs[28] = 1;
    } else {
      savePrefs[28] = 0;
    }
    if (scorequarterCheckBox.isSelected()) {
      savePrefs[20] = 1;
    } else {
      savePrefs[20] = 0;
    }
    if (scoreeighthCheckBox.isSelected()) {
      savePrefs[21] = 1;
    } else {
      savePrefs[21] = 0;
    }
    if (scorerestCheckBox.isSelected()) {
      savePrefs[22] = 1;
    } else {
      savePrefs[22] = 0;
    }
    if (scoreMetronomeCheckBox.isSelected()) {
      savePrefs[23] = 1;
    } else {
      savePrefs[23] = 0;
    }
    savePrefs[24] = scoreKeyComboBox.getSelectedIndex();
    savePrefs[25] = scoreAlterationsComboBox.getSelectedIndex();
    if (tripletCheckBox.isSelected()) {
      savePrefs[26] = 1;
    } else {
      savePrefs[26] = 0;
    }
    if (scoreTripletCheckBox.isSelected()) {
      savePrefs[27] = 1;
    } else {
      savePrefs[27] = 0;
    }
  }
}

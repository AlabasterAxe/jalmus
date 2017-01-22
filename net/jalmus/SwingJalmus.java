package net.jalmus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class SwingJalmus extends JFrame implements ActionListener, ItemListener, KeyListener {

  static final long serialVersionUID = 1;

  //----------------------------------------------------------------
  // Menu
  final Jalmus jalmus;
  
  // is true when combobox selection occurs during language initialization
  boolean selectmidi_forlang; 

  ResourceBundle bundle;
  final Collection<Localizable> localizables = new ArrayList<Localizable>();

  Font musiSync; // font used to render scores

  String minor;
  String major;

  String tlicence;
  String tcredits;

  int windowMargin = 50; // margin from the window border
  int keyWidth = 30; // width of score keys
  int noteMargin = 220; // margin for note reading

  // width of current score time signature symbol. This includes also the first note margin
  int timeSignWidth = 30;

  // space in pixel to align notes to the score layout
  int notesShift = 10;

  // distance in pixel between 1/4 notes
  int noteDistance = 72;

  int firstNoteXPos = windowMargin + keyWidth + timeSignWidth + notesShift;

  int scoreYpos = 110; // Y coordinate of the first row of the score
  int metronomeYPos = 100;
  int rowsDistance = 100; // distance in pixel between staff rows

  boolean paintRhythms;
  boolean muteRhythms;

  private int[] sauvmidi = new int[16]; // for save midi options when cancel

  String[] pathsubdir = new String[16];
  //----------------------------------------------------------------
  // Menu

  // Mise en place du menu
  JMenu lessonsMenu;
  private JMenu[] lessonsMenuDir = new JMenu[16];
  JMenuItem[][] lessonsMenuItem = new JMenuItem[16][26];

  RenderingThread renderingThread;
  Anim animationPanel;
  Image jbackground;

  String pasclavier = "Pas de clavier MIDI             ";

  JMenuBar mainToolBar = new JMenuBar();
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

  JPanel principal = new JPanel(); // panel principal

  Properties settings = new Properties();

  private int[] savePrefs = new int[30]; // for cancel button

  SwingNoteReadingGame noteGame;
  SwingRhythmReadingGame rhythmGame;
  SwingScoreReadingGame scoreGame;

  MidiHelper midiHelper;

  int numberOfMeasures = 2; // number of measures in a single row
  int numberOfRows = 4; // number of score rows

  //----------------------
  // Translation variables

  String language = "en";

  SwingJalmus(Jalmus jalmus, SwingNoteReadingGame noteGame, SwingRhythmReadingGame rhythmGame,
      SwingScoreReadingGame scoreGame) {
    this.jalmus = jalmus;
    this.animationPanel = new Anim(this);
    this.renderingThread = new RenderingThread(this);
    this.noteGame = noteGame;
    this.noteGame.setUi(this);
    this.rhythmGame = rhythmGame;
    this.rhythmGame.setUi(this);
    this.scoreGame = scoreGame;
    this.scoreGame.setUi(this);
    midiHelper = new MidiHelper();
  }

  void init(String paramlanguage) {
    jalmus.init(paramlanguage);

    try {
      InputStream fInput = this.getClass().getResourceAsStream("/images/MusiSync.ttf");
      musiSync = Font.createFont(Font.PLAIN, fInput);
    } catch (Exception e) {
      System.out.println("Cannot load MusiSync font !!");
      System.exit(1);
    }

    if (!midiHelper.initialize()) {
      return;
    }

    try {
      jbackground = ImageIO.read(getClass().getClassLoader().getResource("images/bg1.png"));
    } catch (Exception e) {
      System.out.println("Cannot load background image");
    }

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
        handleStartButtonClicked();
      }
    });

    listenButton = new JButton();
    listenButton.setFocusable(false);
    localizables.add(new Localizable.Button(listenButton, "_listen"));
    listenButton.setPreferredSize(new Dimension(150, 20));
    listenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handleListenButtonClicked();
      }
    });

    newButton = new JButton();
    newButton.setFocusable(false);
    localizables.add(new Localizable.Button(newButton, "_new"));
    newButton.setPreferredSize(new Dimension(150, 20));
    newButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handleNewButtonClicked();
      }
    });

    preferencesButton = new JButton();
    preferencesButton.setFocusable(false);
    localizables.add(new Localizable.Button(preferencesButton, "_menuPreferences"));
    preferencesButton.setPreferredSize(new Dimension(150, 20));
    preferencesButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handlePreferencesClicked();
      }
    });

    bundle = ResourceBundle.getBundle("language", new Locale(language));

    gameButtonPanel.setLayout(new FlowLayout());
    gameButtonPanel.add(startButton);
    gameButtonPanel.add(preferencesButton);
    gameButtonPanel.setBackground(Color.white);

    midiOptionsDialog = buildMidiOptionsDialog();
    transpositionSpinner.setValue(0);

    menuParameters.add(menuPrefs);
    menuPrefs.addActionListener(this);
    menuParameters.add(menuMidi);
    menuMidi.addActionListener(this);

    /***********************************************************************/
    /******************************** MENU *********************************/
    /***********************************************************************/
    preferencesDialog = buildPreferencesDialog();

    aboutDialog = new JDialog(this, true);
    //aboutDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    aboutDialog.setResizable(false);

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

    mainToolBar.add(buildExercisesMenu());
    lessonsMenu = buildLessonsMenu();
    mainToolBar.add(lessonsMenu);
    mainToolBar.add(menuParameters);
    mainToolBar.add(helpMenu);

    this.setJMenuBar(mainToolBar);
    mainToolBar.setVisible(true);

    /**************************************************************/
    /***************** FENETRE A PROPOS ***************************/
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
    oklevelMessage.setIcon(new ImageIcon(getClass().getResource("/images/ok.png")));
    oklevelMessage.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleLevelOkClicked();
      }
    });
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

    labelPanel.add(new JLabel(bundle.getString("_name"), JLabel.RIGHT));
    labelPanel.add(new JLabel(bundle.getString("_description"), JLabel.RIGHT));

    fieldPanel.add(lessonName);
    fieldPanel.add(lessonMessage);

    savePanel.add(labelPanel, BorderLayout.WEST);
    savePanel.add(fieldPanel, BorderLayout.CENTER);

    oksaveButton = new JButton();
    oksaveButton.setIcon(new ImageIcon(getClass().getResource("/images/ok.png")));
    oksaveButton.setText(bundle.getString("_buttonok"));
    oksaveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleOKSave();
      }
    });

    savePanel.add(oksaveButton);
    saveDialog.setContentPane(savePanel);
    saveDialog.setVisible(false);

    /*******************************************************************/

    principal.setLayout(new BorderLayout());

    principal.add(gameButtonPanel, BorderLayout.NORTH);
    principal.add(animationPanel, BorderLayout.CENTER);

    principal.setVisible(true);
    gameButtonPanel.setVisible(false);
    this.getContentPane().add(principal);

    animationPanel.setVisible(true);
    animationPanel.setBackground(Color.white);

    ButtonGroup group = new ButtonGroup();

    rblanguagefr = new JRadioButtonMenuItem("Fran" + "\u00E7" + "ais");
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
      language = "es";
    } else if ("it".equals(paramlanguage)) {
      rblanguageit.setSelected(true);
      language = "it";
    } else if ("de".equals(paramlanguage)) {
      rblanguagede.setSelected(true);
      language = "de";
    } else if ("fr".equals(paramlanguage)) {
      rblanguagefr.setSelected(true);
      language = "fr";
    } else if ("da".equals(paramlanguage)) {
      rblanguageda.setSelected(true);
      language = "da";
    } else if ("tr".equals(paramlanguage)) {
      rblanguagetr.setSelected(true);
      language = "tr";
    } else if ("fi".equals(paramlanguage)) {
      rblanguagefi.setSelected(true);
      language = "fi";
    } else if ("ko".equals(paramlanguage)) {
      rblanguageko.setSelected(true);
      language = "ko";
    } else if ("eo".equals(paramlanguage)) {
      rblanguageeo.setSelected(true);
      language = "eo";
    } else if ("pl".equals(paramlanguage)) {
      rblanguagepl.setSelected(true);
      language = "pl";
    } else if ("iw".equals(paramlanguage)) {
      rblanguageiw.setSelected(true);
      language = "iw";
    }  else if ("gr".equals(paramlanguage)) {
      rblanguagegr.setSelected(true);
      language = "gr";
    } else {
      // must be "en"
      rblanguageen.setSelected(true);
      language = "en";
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

          if (jalmus.piano.getPrevKey() != null && jalmus.piano.getPrevKey() != key) {
            jalmus.piano.getPrevKey().turnOff(midiHelper.currentChannel,
              soundOnCheckBox.isSelected() && !midiHelper.midierror);
          }
          if (key != null && jalmus.piano.getPrevKey() != key) {
            key.turnOn(midiHelper.currentChannel, false);
          }
          jalmus.piano.setPrevKey(key);
          repaint();
        }
      }
    });

    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        System.out.println("Jalmus has been resized !");
        if (jalmus.selectedGame == Jalmus.RHYTHMREADING ||
            jalmus.selectedGame == Jalmus.SCOREREADING) {
          handleNewButtonClicked();
        }
      }
    });

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        requestFocus();

        Dimension d = getSize();

        if (jalmus.selectedGame == Jalmus.NOTEREADING) {
          if (jalmus.piano.rightButtonPressed(e.getPoint(), d.width)) {
            noteGame.noteLevel.basenotetoRight(jalmus.piano);
          }
          if (jalmus.piano.leftButtonPressed(e.getPoint(), d.width)) {
            noteGame.noteLevel.basenotetoLeft(jalmus.piano);
          }

          repaint();

          // System.out.println (e.getPoint());
          Key key = jalmus.piano.getKey(e.getPoint());
          jalmus.piano.setPrevKey(key);
          if (!midiHelper.midierror) {
            key.turnOn(midiHelper.currentChannel, !midiHelper.midierror);
          }
          if (key != null) {
            if (key.getKNum() == 60 && !jalmus.gameStarted) {
              requestFocus();
              jalmus.startNoteGame();
              if (!renderingThread.isAlive()) {
                renderingThread.start();
              }
            } else if (key != null && jalmus.gameStarted && !jalmus.paused) {
              key.turnOn(midiHelper.currentChannel,
                  soundOnCheckBox.isSelected() && !midiHelper.midierror);
              repaint();

              if (key.getKNum() == jalmus.noteGame.currentNote.getPitch()) {
                noteGame.rightAnswer();
              } else {
                noteGame.wrongAnswer();
              }
            }
          }
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (jalmus.selectedGame == Jalmus.NOTEREADING) {
          if (jalmus.piano.getPrevKey() != null) {
            jalmus.piano.getPrevKey().turnOff(midiHelper.currentChannel,
                soundOnCheckBox.isSelected() && !midiHelper.midierror);
            repaint();
          }
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        if (jalmus.selectedGame == Jalmus.NOTEREADING) {
          if (jalmus.piano.getPrevKey() != null) {
            jalmus.piano.getPrevKey().turnOff(midiHelper.currentChannel,
                soundOnCheckBox.isSelected() && !midiHelper.midierror);
            repaint();
            jalmus.piano.setPrevKey(null);
          }
        }
      }
    });

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent evt) {
        saveSettings();
        dispose();
        System.exit(0);
      }

      @Override
      public void windowClosing(WindowEvent evt) {
        saveSettings();
        dispose();
        System.exit(0);
      }
    });

    // load user preferences from settings file
    try {
      settings.load(new FileInputStream("settings.properties"));
      if ("on".equals(settings.getProperty("sound"))) {
        soundOnCheckBox.setSelected(true);
      } else if ("off".equals(settings.getProperty("sound"))) {
        soundOnCheckBox.setSelected(false);
      }

      if ("on".equals(settings.getProperty("keyboardsound"))) {
        keyboardsoundCheckBox.setSelected(true);
      } else if ("off".equals(settings.getProperty("keyboardsound"))) {
        keyboardsoundCheckBox.setSelected(false);
      }

      int ins = Integer.parseInt(settings.getProperty("instrument"));
      if (ins >= 0 & ins < 20) {
        instrumentsComboBox.setSelectedIndex(ins);
      }

      int k = Integer.parseInt(settings.getProperty("keyboard"));
      if (k > 0 & k < midiInComboBox.getItemCount()) {
        midiInComboBox.setSelectedIndex(k);
        midiInComboBoxModel.setSelectedItem(midiInComboBoxModel.getElementAt(k));
        System.out.println(midiInComboBox.getSelectedItem());
      }

      int ko = Integer.parseInt(settings.getProperty("midiout"));
      if (ko > 0 & ko < midiOutComboBox.getItemCount()) {
        midiOutComboBox.setSelectedIndex(ko);
        midiOutComboBoxModel.setSelectedItem(midiOutComboBoxModel.getElementAt(ko));
        System.out.println(midiOutComboBox.getSelectedItem());
      }

      int kl = Integer.parseInt(settings.getProperty("keyboardlength"));
      if (kl == 61) {
        keyboardLengthComboBox.setSelectedIndex(1);
      } else if (kl == 73) {
        keyboardLengthComboBox.setSelectedIndex(0);
      }

      int kt = Integer.parseInt(settings.getProperty("transposition"));
      if (kt >= -24 & kt < 24) {
        transpositionSpinner.setValue(kt);
      }
    } catch (Exception e) {
      System.out.println(e);
    }

    changeLanguage();
  }
  //----------------------------------------------------------------

  JMenu buildExercisesMenu() {
    JMenuItem noteReadingMenuItem =
        new JMenuItem(new ImageIcon(getClass().getResource("/images/note.png")));
    localizables.add(new Localizable.Button(noteReadingMenuItem, "_menuNotereading"));
    noteReadingMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleNoteReadingMenuItem();
      }
    });

    JMenuItem rhythmReadingMenuItem =
        new JMenuItem(new ImageIcon(getClass().getResource("/images/rhythm.png")));
    localizables.add(new Localizable.Button(rhythmReadingMenuItem, "_menuRythmreading"));
    rhythmReadingMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleRhythmReadingMenuItem();
      }
    });

    JMenuItem scoreReadingMenuItem =
        new JMenuItem(new ImageIcon(getClass().getResource("/images/score.png")));
    localizables.add(new Localizable.Button(scoreReadingMenuItem, "_menuScorereading"));
    scoreReadingMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleScoreReadingMenuItem();
      }
    });

    JMenuItem exitMenuItem =
        new JMenuItem(new ImageIcon(getClass().getResource("/images/exit.png")));
    localizables.add(new Localizable.Button(exitMenuItem, "_menuExit"));
    exitMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleExitMenuItem();
      }
    });

    JMenu exercisesMenu = new JMenu();
    localizables.add(new Localizable.Button(exercisesMenu, "_menuExercises"));
    exercisesMenu.setMnemonic(KeyEvent.VK_E);
    exercisesMenu.add(noteReadingMenuItem);
    exercisesMenu.add(rhythmReadingMenuItem);
    exercisesMenu.add(scoreReadingMenuItem);
    exercisesMenu.addSeparator();
    exercisesMenu.add(exitMenuItem);
    return exercisesMenu;
  }

  JMenu buildLessonsMenu() {
    JMenu result = new JMenu();
    localizables.add(new Localizable.Button(result, "_menuLessons"));
    result.setMnemonic(KeyEvent.VK_L);

    final String path = this.jalmus.currentLesson.getLessonPath(language);
    File subdir = new File(path);

    if (subdir.isDirectory()) {
      File[] listsp = subdir.listFiles();
      Arrays.sort(listsp);
      if (listsp != null && listsp.length <= 15) { //15 directory
        for (int i = 0; i < listsp.length; i++) {
          lessonsMenuDir[i] = new JMenu(listsp[i].getName());
          result.add(lessonsMenuDir[i]);

          pathsubdir[i] = path + File.separator + listsp[i].getName();
          File repertoire = new File(pathsubdir[i]);
          File[] list = repertoire.listFiles();
          Arrays.sort(list);
          if (list != null && list.length <=25) { //25 lessons max
            for (int i1 = 0; i1 < list.length; i1++) {
              if ("xml".equals(FileTools.getFileExtension(list[i1]))) {
                lessonsMenuItem[i][i1] =
                    new JMenuItem(FileTools.getFileNameWithoutExtension(list[i1]));
                lessonsMenuItem[i][i1].addActionListener(this);
                lessonsMenuDir[i].add(lessonsMenuItem[i][i1]);
              }
            }
          }
        }
      }
    } else {
      System.err.println(subdir + " : Reading lessons files error.");
    }

    // final Desktop desktop = null;
    // Before more Desktop API is used, first check
    // whether the API is supported by this particular
    // virtual machine (VM) on this particular host.

    result.addSeparator();

    JMenuItem manageMenuItem =
        new JMenuItem(new ImageIcon(getClass().getResource("/images/folder.png")));
    localizables.add(new Localizable.Button(manageMenuItem, "_menuBrowse"));
    manageMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        File subdir = new File(path);
        jalmus.OpenDirectory(subdir);
      }
    });
    result.add(manageMenuItem);

    return result;
  }
  //----------------------------------------------------------------
  private JDialog buildMidiOptionsDialog() {

    /* Sound panel */

    soundOnCheckBox = new JCheckBox("", true);

    instrumentsComboBox = new JComboBox<String>();
    if (this.midiHelper.instruments != null) {
      for (int i = 0; i < 20; i++) {
        instrumentsComboBox.addItem(this.midiHelper.instruments[i].getName());
      }
    } else {
      instrumentsComboBox.addItem("No instrument available");
      System.out.println(
          "No soundbank file : http://java.sun.com/products/java-media/sound/soundbanks.html");
    }
    instrumentsComboBox.addItemListener(this);
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

    try {
      latencySlider.setValue(Integer.parseInt(settings.getProperty("latency")));
    } catch (Exception e) {
      System.out.println(e);
    }
    localizables.add(new Localizable.NamedGroup(latencyPanel, "_latency"));

    // ----

    midiInComboBoxModel.addElement(pasclavier);
    midiOutComboBoxModel.addElement(pasclavier);
    MidiDevice.Info[] aInfos = MidiSystem.getMidiDeviceInfo();
    for (int i = 0; i < aInfos.length; i++) {
      try {
        MidiDevice device = MidiSystem.getMidiDevice(aInfos[i]);
        boolean bAllowsInput = (device.getMaxTransmitters() != 0);
        boolean bAllowsOutput = (device.getMaxReceivers() != 0);

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
        handleMidiOptionsOkClicked();
      }
    });

    JButton cancelButton = new JButton();
    localizables.add(new Localizable.Button(cancelButton, "_buttoncancel"));
    cancelButton.setIcon(new ImageIcon(getClass().getResource("/images/cancel.png")));
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleMidiOptionsCancelClicked();
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

    return dialog;
  }

  //----------------------------------------------------------------
  private JDialog buildPreferencesDialog() {

    preferencesTabbedPane.addTab(null,
        new ImageIcon(getClass().getResource(noteGame.getPreferencesIconResource())),
        noteGame.getPreferencesPanel());
    localizables.add(new Localizable.Tab(preferencesTabbedPane, NOTE_READING_TAB,
          noteGame.getPreferencesLocalizable()));
    preferencesTabbedPane.addTab(null,
        new ImageIcon(getClass().getResource(rhythmGame.getPreferencesIconResource())),
        rhythmGame.getPreferencesPanel());
    localizables.add(new Localizable.Tab(preferencesTabbedPane, RHYTHM_READING_TAB,
          rhythmGame.getPreferencesLocalizable()));
    preferencesTabbedPane.addTab(null,
        new ImageIcon(getClass().getResource(scoreGame.getPreferencesIconResource())),
        scoreGame.getPreferencesPanel());
    localizables.add(new Localizable.Tab(preferencesTabbedPane, SCORE_READING_TAB,
          scoreGame.getPreferencesLocalizable()));

    // buttons below tabs

    JButton okButton = new JButton();
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handlePreferencesOkClicked();
      }
    });
    okButton.setIcon(new ImageIcon(getClass().getResource("/images/ok.png")));
    localizables.add(new Localizable.Button(okButton, "_buttonok"));

    JButton cancelButton = new JButton();
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handlePreferencesCancelClicked();
      }
    });
    cancelButton.setIcon(new ImageIcon(getClass().getResource("/images/cancel.png")));
    localizables.add(new Localizable.Button(cancelButton, "_buttoncancel"));

    JButton saveButton = new JButton();
    saveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handlePreferencesSaveClicked();
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
    dialog.setContentPane(contentPanel);
    dialog.setSize(580, 550);

    return dialog;
  }

  //----------------------------------------------------------------

  void changeLanguage() {
    bundle = ResourceBundle.getBundle("language", new Locale(language));
    System.out.println(new Locale(language));
    for (Iterator<Localizable> itr = localizables.iterator(); itr.hasNext();) {
      Localizable localizable = (Localizable)itr.next();
      localizable.update(bundle);
    }

    noteGame.updateLanguage(bundle);
    rhythmGame.updateLanguage(bundle);
    scoreGame.updateLanguage(bundle);

    menuParameters.setText(bundle.getString("_menuSettings"));
    menuPrefs.setText(bundle.getString("_menuPreferences"));
    menuMidi.setText(bundle.getString("_menuMidi"));
    languages.setText(bundle.getString("_menuLanguage"));
    helpMenu.setText(bundle.getString("_menuHelp"));
    helpSummary.setText(bundle.getString("_menuContents"));
    siteinternet.setText(bundle.getString("_menuWeb"));
    aboutMenuItem.setText(bundle.getString("_menuAbout"));
    aboutDialog.setTitle(bundle.getString("_menuAbout"));

    tlicence = bundle.getString("_licence");
    tcredits = bundle.getString("_credits");

    keyboardLengthComboBox.removeAllItems();
    keyboardLengthComboBox.addItem("73 " + bundle.getString("_keys"));
    keyboardLengthComboBox.addItem("61 " + bundle.getString("_keys"));

    minor = bundle.getString("_minor");
    major = bundle.getString("_major");

    soundOnCheckBox.setText(bundle.getString("_notessound"));
    keyboardsoundCheckBox.setText(bundle.getString("_keyboardsound"));
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

    okscoreMessage.setText(bundle.getString("_buttonok"));
    bfermer.setText(bundle.getString("_buttonclose"));
    bcredits.setText(bundle.getString("_buttoncredits"));
    blicence.setText(bundle.getString("_buttonlicense"));
  }

  private void saveSettings() {
    settings.setProperty("transposition", String.valueOf(transpositionSpinner.getValue()));
    if (keyboardLengthComboBox.getSelectedIndex() == 1) {
      settings.setProperty("keyboardlength","61");
    } else {
      settings.setProperty("keyboardlength","73");
    }
    settings.setProperty("keyboard", String.valueOf(midiInComboBox.getSelectedIndex()));
    settings.setProperty("midiout", String.valueOf(midiOutComboBox.getSelectedIndex()));
    settings.setProperty("instrument", String.valueOf(instrumentsComboBox.getSelectedIndex()));

    if (soundOnCheckBox.isSelected()) {
      settings.setProperty("sound", "on");
    } else {
      settings.setProperty("sound", "off");
    }

    if (keyboardsoundCheckBox.isSelected()) {
      settings.setProperty("keyboardsound", "on");
    } else {
      settings.setProperty("keyboardsound", "off");
    }
    settings.setProperty("latency", String.valueOf(latencySlider.getValue()));
    settings.setProperty("language", language);

    try {
      settings.store(new FileOutputStream("settings.properties"), null);
      settings.list(System.out);
    } catch (IOException e) {
      // TODO(mattkeller): do we care about this update this comment to reflect
      //   why not or do something about it if we do.
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == rblanguageen) {
      language = "en";
      changeLanguage();
      //To do : recharge lessons with new lang
    }

    if (e.getSource() == rblanguagede) {
      language = "de";
      changeLanguage();
    }

    if (e.getSource() == rblanguagees) {
      language = "es";
      changeLanguage();
    }

    if (e.getSource() == rblanguagefr) {
      language = "fr";
      changeLanguage();
    }

    if (e.getSource() == rblanguageit) {
      language = "it";
      changeLanguage();
    }

    if (e.getSource() == rblanguageda) {
      language = "da";
      changeLanguage();
    }

    if (e.getSource() == rblanguagetr) {
      language = "tr";
      changeLanguage();
    }

    if (e.getSource() == rblanguagefi) {
      language = "fi";
      changeLanguage();
    }

    if (e.getSource() == rblanguageko) {
      language = "ko";
      changeLanguage();
    }

    if (e.getSource() == rblanguagepl) {
      language = "pl";
      changeLanguage();
    }

    if (e.getSource() == rblanguageiw) {
      language = "iw";
      changeLanguage();
    }

    if (e.getSource() == rblanguageeo) {
      language = "eo";
      changeLanguage();
    }

    if (e.getSource() == rblanguagegr) {
      language = "gr";
      changeLanguage();
    }

    for (int i = 0; i < lessonsMenuItem.length; i++) {
      for (int j = 0; j < lessonsMenuItem[0].length; j++) {
        if (e.getSource() == lessonsMenuItem[i][j]) {
          handleLessonMenuItem(lessonsMenuItem[i][j].getText(),i);
          System.out.println("lesson " + i + j + lessonsMenuItem[i][j].getText());
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
        jalmus.OpenURI("http://www.jalmus.net/pmwiki/pmwiki.php/" + language);
      }
    } else if (e.getSource() == siteinternet) {
      jalmus.stopGames();
      jalmus.OpenURI("http://jalmus.net?lang = " + language);
    } else if (e.getSource() == okscoreMessage) {
      scoreMessage.dispose();
      if (jalmus.isLessonMode) {
        if ((jalmus.currentLesson.isNoteLevel() && jalmus.noteGame.currentScore.isWin()) ||
            jalmus.currentLesson.isRhythmLevel() ||
            jalmus.currentLesson.isScoreLevel()) {
          jalmus.nextLevel();
        } else {
          jalmus.startLevel();
        }
      }
    } else if (e.getSource() == menuMidi) {
      if (jalmus.gameStarted) {
        jalmus.paused = true;
      }
      backupMidiOptions();
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
  }

  @Override
  public void itemStateChanged(ItemEvent evt) {
    if (evt.getItemSelectable() == this.midiInComboBox && !this.selectmidi_forlang) {
      String smidiin = (String) this.midiInComboBox.getSelectedItem();
      if (smidiin != this.pasclavier) {
        if (midiHelper.open) {
          midiHelper.inputDevice.close();
          midiHelper.open = false;
        }

        String midimessage = "Initialisation " + smidiin;

        MidiDevice.Info info = MidiCommon.getMidiDeviceInfo(smidiin, false);
        if (info == null) {
          midimessage = "nodevice";
          System.out.println(midimessage);
        } else {
          try {
            midiHelper.inputDevice = MidiSystem.getMidiDevice(info);
            midiHelper.inputDevice.open();
          } catch (MidiUnavailableException e) {
            midimessage = "nodevice";
            System.out.println(midimessage);
          }

          Receiver r = new DumpReceiver(this);
          try {
            Transmitter t = midiHelper.inputDevice.getTransmitter();
            t.setReceiver(r);
          } catch (MidiUnavailableException e) {
            midimessage = "wasn't able to connect the device's Transmitter to the Receiver:";
            System.out.println(e);
            midiHelper.inputDevice.close();
            System.exit(1);
          }
          midimessage = "End initialisation";
        }
        if (midiHelper.inputDevice.isOpen()) {
          System.out.println(
              "Midi Device open : play a key, if this key don't change his " +
              "color at screen, verify the MIDI port name");
        }
        midiHelper.open = true;
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
            midiHelper.outputDevice = MidiSystem.getMidiDevice(info);
            midiHelper.outputDevice.open();
          } catch (MidiUnavailableException e) {
            midimessage = "nodevice";
            System.out.println(midimessage);
          }
        }
      }

      } else if (evt.getItemSelectable() == this.instrumentsComboBox) {
        if (!midiHelper.midierror && midiHelper.instruments != null) {
          midiHelper.currentChannel.getChannel().programChange(this.instrumentsComboBox.getSelectedIndex());
        }
      } else if (evt.getItemSelectable() == this.keyboardLengthComboBox) {
        if (this.keyboardLengthComboBox.getSelectedIndex() == 0) {
          jalmus.piano = new Piano(73, 40);
        } else if (this.keyboardLengthComboBox.getSelectedIndex() == 1) {
          jalmus.piano = new Piano(61, 90);
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

    if (jalmus.selectedGame == Jalmus.NOTEREADING && !jalmus.isLessonMode && !jalmus.gameStarted 
        && (noteGame.noteLevel.isNotesgame() || noteGame.noteLevel.isAccidentalsgame() ||
        	noteGame.noteLevel.isCustomNotesgame()) && !noteGame.noteLevel.isAllnotesgame()) {
      if (key == KeyEvent.VK_LEFT) {
        noteGame.noteLevel.basenotetoLeft(jalmus.piano);
      } else if (key == KeyEvent.VK_RIGHT) {
        noteGame.noteLevel.basenotetoRight(jalmus.piano);
      }
    } else if (jalmus.selectedGame == Jalmus.RHYTHMREADING && muteRhythms  && jalmus.gameStarted) {
      if (key == KeyEvent.VK_SPACE) {
        rhythmGame.rhythmKeyPressed(71);
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
    if (jalmus.selectedGame == Jalmus.NOTEREADING) {
      noteGame.handleKeyTyped(evt);
    }
  }  // end keyTyped()
  
  void changeScreen(boolean isLessonMode, Lessons currentlesson, int selectedGame) {
    this.getContentPane().removeAll();
    if (isLessonMode) {     
      if (currentlesson.isNoteLevel()) {  
        this.getContentPane().add(noteGame.getGamePanel());
        noteGame.startButton.setVisible(false);
        noteGame.preferencesButton.setVisible(false);
        menuPrefs.setEnabled(false);
      } else if (currentlesson.isRhythmLevel()) {
        this.getContentPane().add(rhythmGame.getGamePanel());
      } else if (currentlesson.isScoreLevel()) { 
        this.getContentPane().add(principal);
        gameButtonPanel.add(newButton);
        gameButtonPanel.add(listenButton);
        gameButtonPanel.add(startButton);
        gameButtonPanel.add(preferencesButton);
        scoreYpos = 110;
        if (currentlesson.isScoreLevel()) {
          scoreGame.alterationWidth = jalmus.scoreGame.scoreLevel.getCurrentTonality().getAlterationsNumber() * 12;
          firstNoteXPos = windowMargin + keyWidth + scoreGame.alterationWidth + timeSignWidth + notesShift;
        } else if (currentlesson.isRhythmLevel()) {
          Dimension size = getSize();
          int scoreLineWidth = keyWidth + timeSignWidth;
          numberOfMeasures = (size.width - (windowMargin * 2) - scoreLineWidth) /
              (jalmus.rhythmGame.rhythmLevel.getTimeSignNumerator() * noteDistance);            
        }
        repaint();
        gameButtonPanel.setVisible(true);
        menuPrefs.setEnabled(false);
      }
    }

    if (selectedGame == Jalmus.NOTEREADING) {
      this.getContentPane().add(noteGame.getGamePanel());
      noteGame.changeScreen();
    } else if (selectedGame == Jalmus.RHYTHMREADING) {
      this.getContentPane().add(rhythmGame.getGamePanel());
      rhythmGame.changeScreen();
    } else if (selectedGame == Jalmus.SCOREREADING) {
      this.getContentPane().add(scoreGame.getGamePanel());
      scoreGame.changeScreen();
    }
  }

  void restorePreferences() {
    noteGame.deserializePrefs(savePrefs);
    rhythmGame.deserializePrefs(savePrefs);
    scoreGame.deserializePrefs(savePrefs);
  }
  
  void backupPreferences() {
    System.arraycopy(noteGame.serializePrefs(), 0, savePrefs, 0, 8);
    
    int[] rhythmPrefs = rhythmGame.serializePrefs();
    System.arraycopy(rhythmPrefs, 8, savePrefs, 8, 8);
    
    // Triplets
    savePrefs[26] = rhythmPrefs[26];
    
    // Dotted half notes
    savePrefs[28] = rhythmPrefs[28];
    
    int[] scorePrefs = scoreGame.serializePrefs();
    System.arraycopy(scorePrefs, 16, savePrefs, 16, 10);

    // Triplets
    savePrefs[27] = scorePrefs[27];

    // Dotted half notes
    savePrefs[28] = scorePrefs[28];
  }
  
  void handleExitMenuItem() {
    jalmus.stopGames();
    dispose();
  }
  

  void handleLessonMenuItem(String lesson, Integer i) {
    String parseError;

    jalmus.stopGames();
    jalmus.isLessonMode = true;

    try {
      // crÃ©ation d'un parseur SAX
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      jalmus.currentLesson = new Lessons();
      // lecture d'un fichier XML avec un DefaultHandler

      File lessonFile = new File(pathsubdir[i] + File.separator+lesson+".xml");
      parser.parse(lessonFile, jalmus.currentLesson);

      if (jalmus.currentLesson.isNoteLevel()) { 
        noteGame.noteLevel.copy((NoteLevel) jalmus.currentLesson.getLevel());
        noteGame.noteLevel.updatenbnotes(jalmus.piano);

        jalmus.selectedGame = Jalmus.NOTEREADING;
        noteGame.initGame();

        changeScreen(jalmus.isLessonMode, jalmus.currentLesson, jalmus.selectedGame);
        noteGame.noteLevel.printtest();
        jalmus.startLevel();
      } else if (jalmus.currentLesson.isRhythmLevel()) { 
        rhythmGame.rhythmLevel.copy((RhythmLevel)jalmus.currentLesson.getLevel());
        jalmus.selectedGame = Jalmus.RHYTHMREADING;       
        changeScreen(jalmus.isLessonMode, jalmus.currentLesson, jalmus.selectedGame);
        jalmus.initRhythmGame();
        rhythmGame.rhythmLevel.printtest();
        newButton.doClick();
        jalmus.startLevel();
      } else if (jalmus.currentLesson.isScoreLevel()) { 
        scoreGame.scoreLevel.copy((ScoreLevel)jalmus.currentLesson.getLevel());
        scoreGame.scoreLevel.printtest();
        jalmus.selectedGame = Jalmus.SCOREREADING;
        changeScreen(jalmus.isLessonMode, jalmus.currentLesson, jalmus.selectedGame);
        jalmus.initRhythmGame();
        newButton.doClick();
        jalmus.startLevel();
      }
    } catch (ParserConfigurationException pce) {
      parseError = "Configuration Parser error.";
      JOptionPane.showMessageDialog(this, parseError, "Warning", JOptionPane.WARNING_MESSAGE);
    } catch (SAXException se) {
      parseError = "Parsing error : "+se.getMessage();
      JOptionPane.showMessageDialog(this, parseError, "Warning", JOptionPane.WARNING_MESSAGE);
      se.printStackTrace();
    } catch (IOException ioe) {
      parseError = "I/O error : I/O error";
      JOptionPane.showMessageDialog(this, parseError, "Warning", JOptionPane.WARNING_MESSAGE);
    }
  }

  void handleRhythmReadingMenuItem() {
    jalmus.stopGames();

    scoreYpos = 110;
    repaint();

    jalmus.selectedGame = Jalmus.RHYTHMREADING;
    rhythmGame.newButton.doClick();
    if (jalmus.isLessonMode) {
      noteGame.noteLevel.init();
    }
    jalmus.isLessonMode = false;
    changeScreen(jalmus.isLessonMode, jalmus.currentLesson, jalmus.selectedGame);
  }

  void handleScoreReadingMenuItem() {
    jalmus.stopGames();

    gameButtonPanel.add(newButton);
    gameButtonPanel.add(listenButton);
    gameButtonPanel.add(startButton);
    gameButtonPanel.add(preferencesButton);
    scoreYpos = 110;
    repaint();

    jalmus.selectedGame = Jalmus.SCOREREADING;
    newButton.doClick();
    if (jalmus.isLessonMode) {
      noteGame.noteLevel.init();
    }
    jalmus.isLessonMode = false;
    changeScreen(jalmus.isLessonMode, jalmus.currentLesson, jalmus.selectedGame);
  }

  void handleNoteReadingMenuItem() {
    jalmus.stopGames();

    noteGame.initGame();
    if (jalmus.isLessonMode) {
      noteGame.noteLevel.init();
    }
    jalmus.selectedGame = Jalmus.NOTEREADING;
    jalmus.isLessonMode = false;
    changeScreen(jalmus.isLessonMode, jalmus.currentLesson, jalmus.selectedGame);
  }

  void handleMidiOptionsCancelClicked() {
    restoreMidiOptions();
    midiOptionsDialog.setVisible(false);
    jalmus.paused = false;
  }

  void handleMidiOptionsOkClicked() {
    midiOptionsDialog.setVisible(false);
    jalmus.paused = false;
  }
  
  boolean gameStarted() {
    switch (jalmus.selectedGame) {
    case Jalmus.NOTEREADING:
      return noteGame.gameStarted;
    case Jalmus.RHYTHMREADING:
    case Jalmus.SCOREREADING:
    default:
      return jalmus.gameStarted;
    }
  }
  
  JPanel activePanel() {
    switch (jalmus.selectedGame) {
    case Jalmus.NOTEREADING:
      return noteGame.animationPanel;
    case Jalmus.RHYTHMREADING:
      return rhythmGame.animationPanel;
    case Jalmus.SCOREREADING:
      return scoreGame.animationPanel;
    default:
      return animationPanel;
    }
  }

  void handleStartButtonClicked() {
    midiHelper.stopSound();
    if (jalmus.selectedGame == Jalmus.NOTEREADING) {
      throw new AssertionError("Should not be possible to click the general startbutton during the note reading game!!!!");
    } else if (jalmus.selectedGame == Jalmus.RHYTHMREADING) {
      if (jalmus.gameStarted) {
        jalmus.stopRhythmGame();
        jalmus.gameStarted = false;
      } else if (paintRhythms) {
        jalmus.samerhythms = true;
        muteRhythms = true;
        jalmus.initRhythmGame();
        jalmus.startRhythmGame();
      }
    } else if (jalmus.selectedGame == Jalmus.SCOREREADING) {
      throw new AssertionError("Should not be possible to click the general startbutton during the score reading game!!!!");
    }
  }

  void handleListenButtonClicked() {
    jalmus.samerhythms = true;
    muteRhythms = false;
    jalmus.initRhythmGame();
    jalmus.startRhythmGame();
  }

  void handleNewButtonClicked() {
    jalmus.samerhythms = false;
    muteRhythms = false;
    jalmus.initRhythmGame();
    paintRhythms = true; 
    repaint(); //only to paint exercise
    jalmus.gameStarted = false;
  }

  void handlePreferencesClicked() {
    if (jalmus.selectedGame == Jalmus.NOTEREADING) {
      preferencesTabbedPane.setSelectedIndex(SwingJalmus.NOTE_READING_TAB);
    } else if (jalmus.selectedGame == Jalmus.RHYTHMREADING) {
      preferencesTabbedPane.setSelectedIndex(SwingJalmus.RHYTHM_READING_TAB);     
    } else if (jalmus.selectedGame == Jalmus.SCOREREADING) {
      preferencesTabbedPane.setSelectedIndex(SwingJalmus.SCORE_READING_TAB);     
    }
    menuPrefs.doClick();
  }

  void handleLevelOkClicked() {
    levelMessage.dispose();
    if (jalmus.isLessonMode) {
      startButton.doClick();
    }
  }

  void handlePreferencesCancelClicked() {
    restorePreferences();
    preferencesDialog.setVisible(false);
  }

  void handlePreferencesSaveClicked() {
    saveDialog.setTitle(bundle.getString("_buttonsave"));
    //  ui.saveDialog.setLayout(new GridLayout(3, 1));    
    saveDialog.pack();
    saveDialog.setLocationRelativeTo(this);
    saveDialog.setVisible(true);
  }

  void handleOKSave() {
    try {
      if (lessonName.getText().length() != 0) {
        if (preferencesTabbedPane.getSelectedIndex() == 0) {
          noteGame.noteLevel.save(jalmus.currentLesson,lessonName.getText()+".xml",
              lessonMessage.getText(), language);
        } else if (preferencesTabbedPane.getSelectedIndex() == 1) {
          rhythmGame.rhythmLevel.printtest();
          rhythmGame.rhythmLevel.save(jalmus.currentLesson, lessonName.getText()+".xml",
              lessonMessage.getText(), language);
        } else if (preferencesTabbedPane.getSelectedIndex() == 2) {
          scoreGame.scoreLevel.printtest();
          scoreGame.scoreLevel.save(jalmus.currentLesson,lessonName.getText()+".xml",
              lessonMessage.getText(), language);
        }

        saveDialog.setVisible(false);

        mainToolBar.remove(lessonsMenu);
        lessonsMenu = buildLessonsMenu();
        mainToolBar.add(lessonsMenu, 1);
      } else {
        JOptionPane.showMessageDialog(null, "Give the name of the lesson", "Warning", JOptionPane.ERROR_MESSAGE); 
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void handlePreferencesOkClicked() {

    if (jalmus.selectedGame == Jalmus.NOTEREADING) {
      // update current level for note reading
      noteGame.noteLevel.inibasenote();
      noteGame.initGame();
      noteGame.noteLevel.updatenbnotes(jalmus.piano);
    } else if (jalmus.selectedGame == Jalmus.RHYTHMREADING) {
      newButton.doClick();
    } else if (jalmus.selectedGame == Jalmus.SCOREREADING) {
      scoreGame.scoreLevel.printtest();
      newButton.doClick();
    }

    // update screen
    changeScreen(jalmus.isLessonMode, jalmus.currentLesson, jalmus.selectedGame);
    preferencesDialog.setVisible(false);
    repaint();
  }

  void drawKeys(Graphics g) {
    if (jalmus.selectedGame == Jalmus.NOTEREADING) {
      noteGame.drawKeys(g);
    } else if (jalmus.selectedGame == Jalmus.SCOREREADING ) {
      scoreGame.drawKeys(g);
    }
  }

  void drawTimeSignature(Graphics g) {
    if (jalmus.selectedGame == Jalmus.RHYTHMREADING ) {
      rhythmGame.drawTimeSignature(g);
    } else if (jalmus.selectedGame == Jalmus.SCOREREADING ) {
      scoreGame.drawTimeSignature(g);
    } else {
      throw new IllegalStateException("Tried to draw time signature at an improper time.");
    }
  }

  // ROWS
  void drawScore(Graphics g) {
    if(jalmus.selectedGame == Jalmus.RHYTHMREADING) {
      rhythmGame.drawScore(g);
    } else if(jalmus.selectedGame == Jalmus.SCOREREADING) {
      scoreGame.drawScore(g);
    } else {
      throw new IllegalStateException("Asked to draw score on note reading game.");
    }
  }
  
  void backupMidiOptions() {
    //TODO: This approach does not work when midi device order is changed!!
    if (soundOnCheckBox.isSelected()) {
      sauvmidi[0] = 1;
    } else {
      sauvmidi[0] = 0;
    }
    sauvmidi[1] = instrumentsComboBox.getSelectedIndex();
    sauvmidi[2] = midiInComboBox.getSelectedIndex();
    sauvmidi[3] = ((Number)transpositionSpinner.getValue()).intValue();
    if (keyboardsoundCheckBox.isSelected()) {
      sauvmidi[4] = 1;
    } else {
      sauvmidi[4] = 0;
    }
    sauvmidi[5] = midiOutComboBox.getSelectedIndex();
  }

  void restoreMidiOptions() {
    //TODO: This approach does not work when midi device order is changed!!
    if (sauvmidi[0] == 1) {
      soundOnCheckBox.setSelected(true);
    } else {
      soundOnCheckBox.setSelected(false);
    }
    instrumentsComboBox.setSelectedIndex(sauvmidi[1]);
    midiInComboBox.setSelectedIndex(sauvmidi[2]);
    transpositionSpinner.setValue(sauvmidi[3]);
    if (sauvmidi[4] == 1) {
      keyboardsoundCheckBox.setSelected(true);
    } else {
      keyboardsoundCheckBox.setSelected(false);
    }
    midiOutComboBox.setSelectedIndex(sauvmidi[5]);
  }

  void showResult() {
    if (jalmus.selectedGame == Jalmus.NOTEREADING) {
      noteGame.showResult();
    } else if (jalmus.selectedGame == Jalmus.RHYTHMREADING) {
      rhythmGame.showResult();
    } else if (jalmus.selectedGame == Jalmus.SCOREREADING) {
      scoreGame.showResult();
    }
  }
  
  void nextLevel() {
    boolean isNextLevel = jalmus.nextLevel();
    
    if (!isNextLevel) {
      System.out.println("End level");
      JOptionPane.showMessageDialog(this, bundle.getString("_lessonfinished"),
          bundle.getString("_congratulations"), JOptionPane.INFORMATION_MESSAGE);

      jalmus.isLessonMode = false;
      jalmus.stopGames();
      repaint();
    }
  }
}

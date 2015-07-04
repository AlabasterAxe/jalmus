/* Jalmus is an application to learn or perfect its music reading.

  FOR MIDICOMMON AND DUMPRECEIVER CLASS (http://www.jsresources.org/)
Copyright (c) 1999 - 2001 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
Copyright (c) 2003 by Florian Bomers


Copyright (C) 2003-2011 RICHARD Christophe

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*/

/* FOR KEY AND PIANO CLASS

* @(#)MidiSynth.java 1.15	99/12/03
*
* Copyright (c) 1999 Sun Microsystems, Inc. All Rights Reserved.
*
* Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
* modify and redistribute this software in source and binary code form,
* provided that i) this copyright notice and license appear on all copies of
* the software; and ii) Licensee does not utilize the software in a manner
* which is disparaging to Sun.
*
* This software is provided "AS IS," without a warranty of any kind. ALL
* EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
* IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
* NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
* LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
* OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
* LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
* INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
* CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
* OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGES.
*
* This software is not designed or intended for use in on-line control of
* aircraft, air traffic, aircraft navigation or aircraft communications; or in
* the design, construction, operation or maintenance of any nuclear
* facility. Licensee represents and warrants that it will not use or
* redistribute the Software for such purposes.
*/

/********************/
/*FRENCH TRANSLATION*/
/********************/

/*Jalmus est un logiciel pour apprendre ou perfectionner sa lecture musicale.
Copyright (C) 2003-2010 RICHARD Christophe
Ce programme est libre, vous pouvez le redistribuer et/ou
le modifier selon les termes de la Licence Publique Gï¿½nï¿½rale GNU
publiï¿½e par la Free Software Foundation (version 2
ou bien toute autre version ultï¿½rieure choisie par vous).

Ce programme est distribuï¿½ car potentiellement utile,
mais SANS AUCUNE GARANTIE, ni explicite ni implicite, y compris les garanties de
commercialisation ou d'adaptation dans un but spï¿½cifique. Reportez-vous Ã  la
Licence Publique GÃ©nÃ©rale GNU pour plus de dï¿½tails.

Vous devez avoir reï¿½u une copie de la Licence Publique Gï¿½nï¿½rale GNU
en mï¿½me temps que ce programme ; si ce n'est pas le cas, ï¿½crivez ï¿½ la Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, Etats-Unis.
*/

/* Web : http://jalmus.netr
E-mail : cvrichard@infonie.fr */

/* NEED JDK 1.4.2 */

package net.jalmus;

import java.awt.Color;import java.awt.Dimension;import java.awt.Font;import java.awt.Graphics;import java.awt.Image;import java.awt.event.ActionEvent;import java.awt.event.ActionListener;import java.awt.event.ComponentAdapter;import java.awt.event.ComponentEvent;import java.awt.event.ItemEvent;import java.awt.event.ItemListener;import java.awt.event.KeyEvent;import java.awt.event.KeyListener;import java.awt.event.MouseAdapter;import java.awt.event.MouseEvent;import java.awt.event.MouseMotionAdapter;import java.awt.event.WindowAdapter;import java.awt.event.WindowEvent;import java.io.File;import java.io.FileInputStream;import java.io.FileOutputStream;import java.io.IOException;import java.io.InputStream;import java.util.ArrayList;import java.util.Arrays;import java.util.Locale;import java.util.ResourceBundle;import javax.imageio.ImageIO;import javax.sound.midi.Instrument;import javax.sound.midi.InvalidMidiDataException;import javax.sound.midi.MetaEventListener;import javax.sound.midi.MetaMessage;import javax.sound.midi.MidiChannel;import javax.sound.midi.MidiDevice;import javax.sound.midi.MidiEvent;import javax.sound.midi.MidiSystem;import javax.sound.midi.MidiUnavailableException;import javax.sound.midi.Receiver;import javax.sound.midi.Sequence;import javax.sound.midi.Sequencer;import javax.sound.midi.ShortMessage;import javax.sound.midi.Soundbank;import javax.sound.midi.Synthesizer;import javax.sound.midi.Track;import javax.sound.midi.Transmitter;import javax.swing.ImageIcon;import javax.swing.JButton;import javax.swing.JFrame;import javax.swing.JMenu;import javax.swing.JMenuItem;import javax.swing.JOptionPane;import javax.swing.plaf.ColorUIResource;import javax.xml.parsers.ParserConfigurationException;import javax.xml.parsers.SAXParser;import javax.xml.parsers.SAXParserFactory;import org.xml.sax.SAXException;

public class Jalmus extends JFrame implements KeyListener, ActionListener, ItemListener {

  //----------------------------------------------------------------
  // Translation variables

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  String language = "en";

  Font MusiSync; // font used to render scores 

  //----------------------------------------------------------------
  // Main variables

  int selectedGame; // FIRSTSCREEN, NOTEREADING, RHYTHMREADING, SCOREREADING
  static int FIRSTSCREEN = 0;
  static int NOTEREADING = 1;
  static int RHYTHMREADING = 2;
  static int SCOREREADING = 3;

  //----------------------------------------------------------------
  // Lesson variables
  Lessons currentlesson = new Lessons();
  boolean isLessonMode;

  //----------------------------------------------------------------
  // Note reading variables

  // Midi Resources

  private MidiDevice inputDevice;
  MidiDevice outputDevice = null;
  private Synthesizer syn;
  Instrument[] instruments;
  private int noteDuration = 2000;
  ChannelData currentChannel; // current channel
  boolean open;

  Piano piano;
  //  private int transpose = 0;  // number of 1/2tons for MIDI transposition -24 +24

  // Animation Resources

  RenderingThread renderingThread = new RenderingThread(this);
  Anim panelanim = new Anim(this);
  Image jbackground;  JalmusUi ui = new JalmusUi(this);

  Note ncourante = new Note(0, 25, 0);
  Chord acourant = new Chord(ncourante, ncourante, ncourante, "", 0);
  Interval icourant = new Interval(ncourante, ncourante, "");

  /*
   *  ************************************* SCORE LAYOUT ***************************************
   * 
   | window |keyWidth|alteration|timeSign|  noteDistance  
   | Margin |        |  Width   | Width  |    /------\    
   |        |        |          |        |    |      |    
   |         ---GG------#---------------------|------|---------------------------------------
   |         ----G----------#-------4---------|-----O----------------------------------------
   |         --GG---------#---------4--------O-----------------------------------------------
   |         --G-G---------------------------------------------------------------------------
   |         ---G----------------------------------------------------------------------------
   *
   *  ******************************************************************************************
   * 
   */

  int windowMargin = 50; // margin from the window border
  int keyWidth = 30; // width of score keys
  private int alterationWidth = 0; // width of alterations symbols. None by default
  private int timeSignWidth = 30; // width of current score time signature symbol. This includes also the first note margin

  int scoreYpos = 110; // Y coordinate of the first row of the score
  int rowsDistance = 100; // distance in pixel between staff rows
  private int numberOfMeasures = 2; // number of measures in a single row
  int numberOfRows = 4; // number of score rows
  int notesShift = 10; // space in pixel to align notes to the score layout
  int noteDistance = 72; // distance in pixel between 1/4 notes


  private int posnote = 1; // current position of the note within a chor or an interval

  private boolean alterationOk;

  int noteMargin = 220; // margin for note reading
  int firstNoteXPos = windowMargin + keyWidth + alterationWidth + timeSignWidth + notesShift;

  Score currentScore = new Score();

  NoteLevel noteLevel = new NoteLevel();

  // Learning Game

  private int notecounter = 1;

  // Line Game
  Note[] line = new Note[40]; // array of notes
  Chord[] lineacc = new Chord[40]; // array of chords
  Interval[] lineint = new Interval[40];
  private int position; // position of the current note in the list
  private int precedente; // position of the previous note to avoid repetitions

  boolean gameStarted; // whether the game has started or not.
  boolean paused;

  boolean midierror;

  //----------------------------------------------------------------
  // Rhythm reading variables

  RhythmLevel rhythmLevel = new RhythmLevel();

  private ArrayList<Rhythm> rhythms = new ArrayList<Rhythm>(); 
  private int rhythmIndex = -1; // index of the current note in the list
  private ArrayList<RhythmAnswer> answers = new ArrayList<RhythmAnswer>();
  int rhythmAnswerScoreYpos = 100; //distance to paint answer
  float rhythmCursorXpos = firstNoteXPos - noteDistance; // X position of the cursor on the score during rhythm game
  int rhythmCursorXStartPos = firstNoteXPos - noteDistance;
  int rhythmCursorXlimit = firstNoteXPos + (4 * numberOfMeasures * noteDistance);
  private int precision = 10; //precision on control between note and answer
  private boolean samerhythms = true;
  boolean muterhythms = false;
  boolean paintrhythms = false;
  boolean cursorstart = false;
  long timestart; // timestamp of cursor at the beginning of a line
  private long latency; // synthesizer latency

  int rhythmgame = 0;

  int tempo = 40; // sequencer time - rhythmGameSpeedComboBox button
  //private double nbtemps = 4; // nombre de temps par mesure
  //private int nbmesures = 8;
  private int metronomeCount = 0;
  private int metronomeYPos = 100;

  private Track track;
  private Track mutetrack;
  private Track metronome;
  private static final int ppq = 1000;
  private Sequence sequence;
  private Sequencer sm_sequencer;

  ScoreLevel scoreLevel = new ScoreLevel();

  private int[] savePrefs = new int[30]; // for cancel button

  private int[] sauvmidi = new int[16]; // for save midi options when cancel

  //################################################################
  // Initialization methods

  private void init(String paramlanguage) {
    ui.init(paramlanguage);
    
    if (!initializeMidi()) {
      return;
    }

    try {
      jbackground = ImageIO.read(getClass().getClassLoader().getResource("images/bg1.png"));
    } catch (Exception e) {
      System.out.println("Cannot load background image");
    }

    try {
      InputStream fInput = this.getClass().getResourceAsStream("/images/MusiSync.ttf");
      MusiSync = Font.createFont (Font.PLAIN, fInput);
    } catch (Exception e) {
      System.out.println("Cannot load MusiSync font !!");
      System.exit(1);
    }

    
    System.out.println(new Locale(language));

    piano = new Piano(73, 40);

    Image icone;

    try {
      icone = ImageIO.read(getClass().getClassLoader().getResource("images/icon.png"));
      setIconImage(icone);
    } catch (Exception e) {
      System.out.println("Cannot load Jalmus icon");
    }

    addKeyListener(this);
    addMouseMotionListener(new MouseMotionAdapter() {      @Override
      public void mouseMoved(MouseEvent e) {

        if (selectedGame == NOTEREADING) {
          Key key = piano.getKey(e.getPoint());

          if (piano.Getprevkey() != null && piano.Getprevkey()!=key) {
            piano.Getprevkey().off(currentChannel, ui.soundOnCheckBox.isSelected() && !midierror);
          }
          if (key != null && piano.Getprevkey()!=key) {
            key.on(currentChannel, false);
          }
          piano.Setprevkey(key);
          repaint();
        }
      }
    });

    addComponentListener(new ComponentAdapter() {      @Override
      public void componentResized(ComponentEvent e) {
        System.out.println("Jalmus has been resized !");
        if (selectedGame == RHYTHMREADING || selectedGame==SCOREREADING) {
          handleNewButtonClicked();
        }
      }
    });

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        requestFocus();

        Dimension d = getSize();

        if (selectedGame == NOTEREADING) {

          if (piano.rightbuttonpressed(e.getPoint(),d.width))  noteLevel.basenotetoRight(piano);
          if (piano.leftbuttonpressed(e.getPoint(),d.width))  noteLevel.basenotetoLeft(piano);

          repaint();

          // System.out.println (e.getPoint());
          Key key = piano.getKey(e.getPoint());
          piano.Setprevkey(key);
          if (!midierror)  key.on(currentChannel,  !midierror);
          if (key != null) {
            if (key.Getknum() == 60 && !gameStarted) {
              requestFocus();
              startNoteGame();
              if (!renderingThread.isAlive()) {
                renderingThread.start();
              }
            } else if (key != null && gameStarted &&!paused) {
              key.on(currentChannel, ui.soundOnCheckBox.isSelected() && !midierror);
              repaint();

              if (key.Getknum() == ncourante.getPitch()) {
                rightAnswer();
              } else {
                wrongAnswer();
              }
            }
          }
        }
      }
      @Override
      public void mouseReleased(MouseEvent e) {
        if (selectedGame == NOTEREADING) {
          if (piano.Getprevkey() != null) {
            piano.Getprevkey().off(currentChannel, ui.soundOnCheckBox.isSelected() && !midierror);
            repaint();
          }
        }
      }
      @Override
      public void mouseExited(MouseEvent e) {
        if (selectedGame == NOTEREADING) {
          if (piano.Getprevkey() != null) {
            piano.Getprevkey().off(currentChannel, ui.soundOnCheckBox.isSelected() && !midierror);
            repaint();
            piano.Setprevkey(null);
          }
        }
      }
    });

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent evt) {
        ui.savesettings();
        dispose();
        System.exit(0);
      }
      @Override
      public void windowClosing(WindowEvent evt) {
        ui.savesettings();
        dispose();
        System.exit(0);
      }
    });

    ui.changeLanguage();

    // load user preferences from settings file
    try {
      ui.settings.load(new FileInputStream("settings.properties"));
      if ("on".equals(ui.settings.getProperty("sound"))) {    	  ui.soundOnCheckBox.setSelected(true);      } else if ("off".equals(ui.settings.getProperty("sound"))) {    	  ui.soundOnCheckBox.setSelected(false);      }
      if ("on".equals(ui.settings.getProperty("keyboardsound"))) {    	  ui.keyboardsoundCheckBox.setSelected(true);      } else if ("off".equals(ui.settings.getProperty("keyboardsound"))) {    	  ui.keyboardsoundCheckBox.setSelected(false);      }
      int ins = Integer.parseInt(ui.settings.getProperty("instrument"));
      if (ins >= 0 & ins < 20) {
        ui.instrumentsComboBox.setSelectedIndex(ins);
      }
      int k = Integer.parseInt(ui.settings.getProperty("keyboard"));
      if (k> 0 & k < ui.midiInComboBox.getItemCount()) {
        ui.midiInComboBox.setSelectedIndex(k);
        ui.midiInComboBoxModel.setSelectedItem(ui.midiInComboBoxModel.getElementAt(k));
        System.out.println(ui.midiInComboBox.getSelectedItem());
      }
      int ko = Integer.parseInt(ui.settings.getProperty("midiout"));
      if (ko> 0 & ko < ui.midiOutComboBox.getItemCount()) {
        ui.midiOutComboBox.setSelectedIndex(ko);
        ui.midiOutComboBoxModel.setSelectedItem(ui.midiOutComboBoxModel.getElementAt(ko));
        System.out.println(ui.midiOutComboBox.getSelectedItem());
      }
      int kl = Integer.parseInt(ui.settings.getProperty("keyboardlength"));
      if (kl == 61) {    	  ui.keyboardLengthComboBox.setSelectedIndex(1);      } else if (kl == 73) {    	  ui.keyboardLengthComboBox.setSelectedIndex(0);      }

      int kt = Integer.parseInt(ui.settings.getProperty("transposition"));
      if (kt >= -24 & kt < 24) {
        ui.transpositionSpinner.setValue(kt);
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  //----------------------------------------------------------------
  private boolean initializeMidi() {
    try {
      if (syn == null) {
        if ((syn = MidiSystem.getSynthesizer())==null) {
          System.out.println("getSynthesizer() failed!");

          return false;
        }
      }
      syn.open();
    } catch (MidiUnavailableException e) {
      System.out.println("Midiunavailable : sortie MIDI occupee - fermez toutes les autres applications pour avoir du son. "+e);
      midierror = true;
    }

    if (!midierror) {
      Soundbank sb = syn.getDefaultSoundbank();
      if (sb != null) {
        instruments = syn.getDefaultSoundbank().getInstruments();

        if (instruments != null) {
          syn.loadInstrument(instruments[0]);
        } else {
          midierror = true;
          System.out.println("Soundbank null");
        }
      }

      MidiChannel[] mc = syn.getChannels();

      ChannelData[] channels = new ChannelData[mc.length];
      for (int i = 0; i < channels.length; i++) {
        channels[i] = new ChannelData(mc[i], i);
      }
      currentChannel = channels[0];
    }
    return true;
  }

  //################################################################
  // METHODES D'ACTION DES BOUTONS ET CHOICES

  /** Initialize note reading game if there is modification in
   * parameters and game restart. */
  private void changeScreen() {
    if (isLessonMode) { 		
      if (currentlesson.isNoteLevel()) {	
        ui.startButton.setVisible(false);
        ui.preferencesButton.setVisible(false);
        ui.newButton.setVisible(false);
        ui.listenButton.setVisible(false);
        ui.menuPrefs.setEnabled(false);
      } else if (currentlesson.isRhythmLevel() || currentlesson.isScoreLevel()) {	
        ui.pgamebutton.add(ui.newButton);
        ui.pgamebutton.add(ui.listenButton);
        ui.pgamebutton.add(ui.startButton);
        ui.pgamebutton.add(ui.preferencesButton);
        scoreYpos = 110;
        if (currentlesson.isScoreLevel()) {
          alterationWidth = scoreLevel.getCurrentTonality().getAlterationsNumber() * 12;
          firstNoteXPos = windowMargin + keyWidth + alterationWidth + timeSignWidth + notesShift;
          Dimension size = getSize();
          int scoreLineWidth = keyWidth + alterationWidth + timeSignWidth;
//          numberOfMeasures = (size.width - (windowMargin * 2) - scoreLineWidth) / (scoreLevel.getTimeSignNumerator() * noteDistance);         	  
        } else if ( currentlesson.isRhythmLevel()) {
          Dimension size = getSize();
          int scoreLineWidth = keyWidth + alterationWidth + timeSignWidth;
          numberOfMeasures = (size.width - (windowMargin * 2) - scoreLineWidth) / (rhythmLevel.getTimeSignNumerator() * noteDistance);         	  
        }
        repaint();
        ui.pgamebutton.setVisible(true);

        ui.menuPrefs.setEnabled(false);
      }
    } else {
      ui.startButton.setVisible(true);
      ui.preferencesButton.setVisible(true);
      ui.menuPrefs.setEnabled(true);
    }

    if (selectedGame == NOTEREADING) {
      ui.pgamebutton.setVisible(true);
      ui.pnotes.setVisible(true);
      ui.principal.setVisible(true);
      System.out.println(noteLevel.getNbnotes());
      if (noteLevel.isNotesgame() && noteLevel.getCurrentTonality().getAlterationsNumber() == 0) {
        ui.bdiese.setVisible(false);
        ui.bdiese2.setVisible(false);
        ui.bbemol.setVisible(false);
        ui.bbemol2.setVisible(false);
        ui.pnotes.validate();
      } else {
        ui.bdiese.setVisible(true);
        ui.bdiese2.setVisible(true);
        ui.bbemol.setVisible(true);
        ui.bbemol2.setVisible(true);
        ui.pnotes.validate();
      }
    } else if (selectedGame == RHYTHMREADING || selectedGame==SCOREREADING) {
      ui.pgamebutton.setVisible(true);
      ui.pnotes.setVisible(false);
      ui.newButton.setVisible(true);
      ui.listenButton.setVisible(true);
      ui.principal.setVisible(true);
    }
  }

  private void updateTonality() {
    String stmp;

    if ((selectedGame == NOTEREADING && noteLevel.getRandomtonality())
        || (selectedGame == SCOREREADING && scoreLevel.getRandomtonality())) { // to change tonality when randomly
      int i = (int)Math.round((Math.random()*7));
      double tmp = Math.random();
      if (tmp<0.1) {
        stmp = "";
      } else if (tmp >= 0.1 && tmp<0.6) {
        stmp = "#";
      } else {
        stmp = "b";
      }

      if (selectedGame == NOTEREADING) noteLevel.getCurrentTonality().init(i, stmp);
      else if (selectedGame == SCOREREADING) scoreLevel.getCurrentTonality().init(i, stmp);
    } else
      if (!isLessonMode && noteLevel.getCurrentTonality().getAlterationsNumber() == 0) {
        // Do Major when tonality is no sharp no flat
        double tmp = Math.random();
        if (tmp<0.5) {
          stmp = "#";
        } else {
          stmp = "b";
        }
        noteLevel.getCurrentTonality().init(0, stmp);
      }

  }

  private void initNoteGame() {

    gameStarted = false;

    currentScore.initScore();

    precedente = 0;
    notecounter = 1;

    scoreYpos = 110;
    paused = false;
    // stopSound();

    ColorUIResource def = new ColorUIResource(238, 238, 238);
    ui.bdo.setBackground(def);
    ui.bre.setBackground(def);
    ui.bmi.setBackground(def);
    ui.bfa.setBackground(def);
    ui.bsol.setBackground(def);
    ui.bla.setBackground(def);
    ui.bsi.setBackground(def);
    ui.bdiese.setBackground(def);
    ui.bbemol2.setBackground(def);

    if (noteLevel.isNormalgame() || noteLevel.isLearninggame()) {
      noteMargin = 220;
      repaint();
    } else if (noteLevel.isInlinegame()) {
      noteMargin = 30;
      repaint();
    }
  }

  /** Initialize rhythm reading game if there is modification in
   * parameters and game restart. */
  void stopRhythmGame() {

    int tmpdiv = 1;

    if (selectedGame == RHYTHMREADING) {
      tmpdiv = rhythmLevel.getTimeDivision();     } else {
      tmpdiv = scoreLevel.getTimeDivision();     }

    ui.startButton.setText(ui.bundle.getString("_start"));

    rhythmIndex = -1;
    scoreYpos = 110;
    rhythmCursorXpos = firstNoteXPos - (noteDistance * tmpdiv);
    rhythmCursorXStartPos = firstNoteXPos - (noteDistance * tmpdiv);
    rhythmAnswerScoreYpos = 100;
    cursorstart = false;
    metronomeCount = 0;
    metronomeYPos = 100;

    if (sm_sequencer != null) {
      sm_sequencer.close();
    }
    //repaint();
  }

  /** Stops all games. */

  private void stopGames() {
    if (selectedGame == NOTEREADING) stopNoteGame();
    else stopRhythmGame();
  }

  private void stopNoteGame() {

    gameStarted = false;
    ui.startButton.setText(ui.bundle.getString("_start"));

    ncourante = new Note(0, 25, 0);
    acourant = new Chord(ncourante, ncourante, ncourante, "", 0);
    icourant = new Interval(ncourante, ncourante, "");
    resetButtonColor();

    stopSound();
    //     if (sm_sequencer != null) {
    //          sm_sequencer.stop();
    //    }
  }

  private void initRhythmGame() {

    System.out.println("[initRhythmGame] latency: " + latency);

    initializeMidi(); 
    if (!renderingThread.isAlive()) {
      renderingThread.start();
    }  
    stopRhythmGame(); // stop previous game

    if (!samerhythms) {      createSequence();    }

    try {
      sm_sequencer = MidiSystem.getSequencer();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
      System.exit(1);
    }
    if (sm_sequencer == null) {
      System.out.println("Can't get a Sequencer");
      System.exit(1);
    }

    try {
      sm_sequencer.open();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
      System.exit(1);
    }

    try {
      sm_sequencer.setSequence(sequence);
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
      System.exit(1);
    }

    if (!(sm_sequencer instanceof Synthesizer)) {

      try {
        Synthesizer sm_synthesizer = MidiSystem.getSynthesizer();
        sm_synthesizer.open();
        Receiver synthReceiver = sm_synthesizer.getReceiver();
        Transmitter seqTransmitter = sm_sequencer.getTransmitter();
        seqTransmitter.setReceiver(synthReceiver);
        //latency = sm_synthesizer.getLatency()/1000;  
        latency = ui.latencySlider.getValue();
        System.out.println("MIDI latency " + latency);
      } catch (MidiUnavailableException e) {
        e.printStackTrace();
      }
    }

    sm_sequencer.addMetaEventListener(new MetaEventListener() {
      public void meta(MetaMessage meta) {
        byte[] abData = meta.getData();
        String strText = new String(abData);

        int tmpnum = 4;         int tmpdiv = 1;
        if (selectedGame == RHYTHMREADING ) {
          tmpnum = rhythmLevel.getTimeSignNumerator();
          tmpdiv = rhythmLevel.getTimeDivision();
        } else if (selectedGame == SCOREREADING ) {
          tmpnum = scoreLevel.getTimeSignNumerator();
          tmpdiv = scoreLevel.getTimeDivision();
        }

    if ("departthread".equals(strText)) {
      System.out.println("Cursor started");
      rhythmCursorXlimit = firstNoteXPos + (tmpnum * numberOfMeasures * noteDistance);
      cursorstart = true;
      timestart = System.currentTimeMillis();
    } 

    if ("depart".equals(strText)) {
      System.out.println("Game start");
      rhythmIndex = 0;
      repaint();
    } else if ("beat".equals(strText)) {
      // show metronome beats
      //System.out.println("Added metronome beat");
      answers.add(new RhythmAnswer(firstNoteXPos + (metronomeCount%((tmpnum/tmpdiv) * numberOfMeasures)) * (noteDistance * tmpdiv), metronomeYPos - 30, true, 3 ));
      metronomeCount++;
      //System.out.println("Metronome beat: " + metronomeCount + ", metronomeYPos: " + metronomeYPos);
      if (metronomeCount == ((tmpnum/tmpdiv) * numberOfMeasures) && 
          metronomeYPos < scoreYpos + (numberOfRows * rowsDistance)) {
        metronomeYPos += rowsDistance;
        metronomeCount = 0;
      }
    } else {
      nextRythm();
      repaint();
    }
      }
    });
    if (selectedGame == RHYTHMREADING) {
      tempo = rhythmLevel.getspeed();     } else {
      tempo = scoreLevel.getspeed();     }

    sm_sequencer.setTempoInBPM(tempo);
    System.out.println("[initRhythmGame] tempo : " + tempo);

    //init line answers
    answers.clear();
  }

  private void startRhythmGame() {
    sm_sequencer.start();
    int tmpdiv = 1;

    if (selectedGame == RHYTHMREADING)
      tmpdiv = rhythmLevel.getTimeDivision(); 
    else
      tmpdiv = scoreLevel.getTimeDivision(); 

    //   Track[] tracks = sequence.getTracks();

    if (muterhythms) {
      sm_sequencer.setTrackMute(1, true); 
      sm_sequencer.setTrackMute(0, false); 
    }
    else {
      sm_sequencer.setTrackMute(1, false); 
      sm_sequencer.setTrackMute(0, true); 
    }


    gameStarted = true; // start game
    ui.startButton.setText(ui.bundle.getString("_stop"));
    rhythmCursorXpos = firstNoteXPos - (noteDistance * tmpdiv);

    cursorstart = false;
  }

  void startNoteGame() {
    initNoteGame();     // to stop last game
    updateTonality(); //when selected random tonality

    if (noteLevel.isNormalgame() || noteLevel.isLearninggame()) {
      if (noteLevel.isNotesgame() || noteLevel.isAccidentalsgame() || noteLevel.isCustomNotesgame()) {
        newnote();
      } else if (noteLevel.isChordsgame()) {
        newChord();
      } else if (noteLevel.isIntervalsgame()) {
        newinterval();
      }
    } else if (noteLevel.isInlinegame()) {
      createLine();
    }

    gameStarted = true;        // start of game
    ui.startButton.setText(ui.bundle.getString("_stop"));
  }

  void rightAnswer() {
    if (noteLevel.isLearninggame()) {

      if (noteLevel.isChordsgame() || noteLevel.isIntervalsgame()) {
        /* if (isLessonMode & notecounter < noteLevel.getLearningduration()){
           parti = false;
           nextlevel();
        }

        else*/
        nextnote();

      } else if (isLessonMode && notecounter == noteLevel.getLearningduration()) {
        gameStarted = false;
        ui.startButton.setText(ui.bundle.getString("_start"));
        nextLevel();
      } else {
        newnote();
      }

      resetButtonColor();
    } else {
      currentScore.addNbtrue(1);

      if (noteLevel.isNotesgame() || noteLevel.isAccidentalsgame() || noteLevel.isCustomNotesgame()) {
        currentScore.addPoints(10);
      } else if (noteLevel.isChordsgame() || noteLevel.isIntervalsgame()) {
        currentScore.addPoints(5);
      }

      if (currentScore.isWin()) {
        gameStarted = false;
        ui.startButton.setText(ui.bundle.getString("_start"));
        showResult();
      }

      if (noteLevel.isInlinegame() && position == line.length-1) { // derniÃ¨re note trouvÃ©e
        currentScore.setWin();
        gameStarted = false;
        ui.startButton.setText(ui.bundle.getString("_start"));
        showResult();
      }
      if (noteLevel.isChordsgame() || noteLevel.isIntervalsgame()) {
        nextnote();
      } else {
        newnote();
      }
    }
  }

  private void startLevel() {
    if (currentlesson.isNoteLevel()) {        
      if (!noteLevel.isMessageEmpty()) {
        ui.textlevelMessage.setText("  "+noteLevel.getMessage()+"  ");        ui.levelMessage.setTitle(ui.bundle.getString("_information"));
        ui.levelMessage.pack();
        ui.levelMessage.setLocationRelativeTo(this);
        ui.levelMessage.setVisible(true);
      } else {
        ui.startButton.doClick();
      }        
    } else if (currentlesson.isRhythmLevel()) {        
      if (!rhythmLevel.isMessageEmpty()) {
        ui.textlevelMessage.setText("  "+rhythmLevel.getMessage()+"  ");        ui.levelMessage.setTitle(ui.bundle.getString("_information"));
        ui.levelMessage.pack();
        ui.levelMessage.setLocationRelativeTo(this);
        ui.levelMessage.setVisible(true);
      } else {
        ui.startButton.doClick();
      }        
    } else if (currentlesson.isScoreLevel()) {        
      if (!scoreLevel.isMessageEmpty()) {
        ui.textlevelMessage.setText("  "+scoreLevel.getMessage()+"  ");        ui.levelMessage.setTitle(ui.bundle.getString("_information"));
        ui.levelMessage.pack();
        ui.levelMessage.setLocationRelativeTo(this);
        ui.levelMessage.setVisible(true);
      } else {
        ui.startButton.doClick();
      }        
    }
  }

  private void nextLevel() {
    if (!currentlesson.lastexercice()) {
      stopNoteGame();
      currentlesson.nextLevel();
      if (currentlesson.isNoteLevel()) { 
        noteLevel.copy((NoteLevel)currentlesson.getLevel());
        noteLevel.updatenbnotes(piano);

        selectedGame = NOTEREADING ;
        initNoteGame();
        changeScreen();
        noteLevel.printtest();

        startLevel();
      } else if (currentlesson.isRhythmLevel()) { 
        rhythmLevel.copy((RhythmLevel)currentlesson.getLevel());

        selectedGame = RHYTHMREADING;
        initRhythmGame();

        changeScreen();
        rhythmLevel.printtest();
        ui.newButton.doClick();
        startLevel();
      } else if (currentlesson.isScoreLevel()) { 
        scoreLevel.copy((ScoreLevel)currentlesson.getLevel());

        selectedGame = SCOREREADING;
        initRhythmGame();

        changeScreen();
        scoreLevel.printtest();
        ui.newButton.doClick();
        startLevel();
      }
    } else {
      System.out.println("End level");
      JOptionPane.showMessageDialog(this, ui.bundle.getString("_lessonfinished"),
          ui.bundle.getString("_congratulations"),
          JOptionPane.INFORMATION_MESSAGE);

      isLessonMode = false;
      stopGames();
      handleNoteReadingMenuItem();

      repaint();
    }
  }

  void wrongAnswer() {
    alterationOk = false;

    if (!noteLevel.isLearninggame()) {
      currentScore.addNbfalse(1);
      // if (soundOnCheckBox.getState()) sonerreur.play();

      if (noteLevel.isNotesgame() || noteLevel.isAccidentalsgame()  || noteLevel.isCustomNotesgame()) {
        currentScore.addPoints(-20);
      } else if (noteLevel.isChordsgame() || noteLevel.isIntervalsgame()) {
        currentScore.addPoints(-10);
      }

      if (currentScore.isLost()) {
        gameStarted = false;
        ui.startButton.setText(ui.bundle.getString("_start"));
        showResult();
      }
    }
  }

  /** FONCTIONS POUR SAISIE AU CLAVIER */
  public void keyTyped(KeyEvent evt) {
    char ch = evt.getKeyChar();  // The character typed.

    if (selectedGame == NOTEREADING && gameStarted) {
      if (ch == 'P' || ch == 'p') {
        if (!paused) {
          paused = true;
        }

        int n = JOptionPane.showConfirmDialog(this, "",
            ui.bundle.getString("_gamepaused"),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (n == 0) {
          paused = false;
        }
      }
    }

    if (selectedGame == NOTEREADING && gameStarted && !paused && noteLevel.isNotesgame()) {
      if (ch == 'Q' || ch=='q' || ch=='A' || ch=='a' || ch=='S' || ch=='s' ||
          ch == 'D' || ch=='d' || ch=='F' || ch=='f' || ch=='G' || ch=='g' ||
          ch == 'H' || ch=='h' || ch=='J' || ch=='j' || ch=='K' || ch=='k') {

        if (((language == "fr" && (ch=='Q' || ch=='q'))
              || ((language == "en" || language=="es" || language=="de") && (ch=='A' || ch=='a')))
            && ncourante.getNom() == ui.DO)
        {
          rightAnswer();
        } else if ((ch == 'S' || ch=='s') && ncourante.getNom().equals(ui.RE)) {
          rightAnswer();
        } else if ((ch == 'D' || ch=='d') && ncourante.getNom().equals(ui.MI)) {
          rightAnswer();
        } else if ((ch == 'F' || ch=='f') && ncourante.getNom().equals(ui.FA)) {
          rightAnswer();
        } else if ((ch == 'G' || ch=='g') && ncourante.getNom().equals(ui.SOL)) {
          rightAnswer();
        } else if ((ch == 'H' || ch=='h') && ncourante.getNom().equals(ui.LA)) {
          rightAnswer();
        } else if ((ch == 'J' || ch=='j') && ncourante.getNom().equals(ui.SI)) {
          rightAnswer();
        } else if ((ch == 'K' || ch=='k') && ncourante.getNom().equals(ui.DO)) {
          rightAnswer();
        } else {
          wrongAnswer();
        }
          }
    }
  }  // end keyTyped()

  public void keyPressed(KeyEvent evt) {

    // Called when the user has pressed a key, which can be
    // a special key such as an arrow key.
    int key = evt.getKeyCode(); // keyboard code for the key that was pressed

    if (isLessonMode && gameStarted && key == KeyEvent.VK_ESCAPE) {
      gameStarted = false;
      nextLevel();
    }

    if (selectedGame == NOTEREADING     	&& !isLessonMode        && !gameStarted         && (noteLevel.isNotesgame()             || noteLevel.isAccidentalsgame()             || noteLevel.isCustomNotesgame())         && !noteLevel.isAllnotesgame()) {
      if (key == KeyEvent.VK_LEFT) {
        noteLevel.basenotetoLeft(piano);
      } else if (key == KeyEvent.VK_RIGHT) {
        noteLevel.basenotetoRight(piano);
      }
    } else if (selectedGame == RHYTHMREADING && rhythmgame == 0 && muterhythms && gameStarted) {
      if (key == KeyEvent.VK_SPACE) {
        rhythmKeyPressed(71);
      }
    }
    repaint();
  } // end keyPressed()

  public void keyReleased(KeyEvent evt) {
    // empty method, required by the KeyListener Interface
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == ui.rblanguageen) {
      language = "en";
      ui.changeLanguage();
      //To do : recharge lessons with new lang
    }

    if (e.getSource() == ui.rblanguagede) {
      language = "de";
      ui.changeLanguage();
    }

    if (e.getSource() == ui.rblanguagees) {
      language = "es";
      ui.changeLanguage();
    }

    if (e.getSource() == ui.rblanguagefr) {
      language = "fr";
      ui.changeLanguage();
    }

    if (e.getSource() == ui.rblanguageit) {
      language = "it";
      ui.changeLanguage();
    }

    if (e.getSource() == ui.rblanguageda) {
      language = "da";
      ui.changeLanguage();
    }

    if (e.getSource() == ui.rblanguagetr) {
      language = "tr";
      ui.changeLanguage();
    }

    if (e.getSource() == ui.rblanguagefi) {
      language = "fi";
      ui.changeLanguage();
    } 

    if (e.getSource() == ui.rblanguageko) {
      language = "ko";
      ui.changeLanguage();
    }

    if (e.getSource() == ui.rblanguagepl) {
      language = "pl";
      ui.changeLanguage();
    }   

    if (e.getSource() == ui.rblanguageiw) {
      language = "iw";
      ui.changeLanguage();
    }   

    if (e.getSource() == ui.rblanguageeo) {
      language = "eo";
      ui.changeLanguage();
    } 

    if (e.getSource() == ui.rblanguagegr) {
      language = "gr";
      ui.changeLanguage();
    } 

    for (int i0 = 0; i0 < ui.lessonsMenuItem.length; i0++) {
      for (int i = 0; i < ui.lessonsMenuItem[0].length; i++) {
        if (e.getSource() == ui.lessonsMenuItem[i0][i]) {
          handleLessonMenuItem(ui.lessonsMenuItem[i0][i].getText(),i0);
          System.out.println("lesson " + i0 + i + ui.lessonsMenuItem[i0][i].getText());
        }
      }
    }

    if (e.getSource() == ui.menuPrefs) {
      stopGames();
      backupPreferences();

      ui.preferencesDialog.setLocationRelativeTo(this);
      ui.preferencesDialog.setVisible(true);
    } else if (e.getSource() == ui.helpSummary) {
      stopGames();
      Object[] options = {ui.bundle.getString("_yes"), ui.bundle.getString("_no")};
      int n = JOptionPane.showOptionDialog(this,
          ui.bundle.getString("_wikidoc"),
          "Information",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE,
          null,     //don't use a custom Icon
          options,  //the titles of buttons
          options[0]); //default button title
      if (n == 0) {
        OpenURI("http://www.jalmus.net/pmwiki/pmwiki.php/"+language);
      }
    } else if (e.getSource() == ui.siteinternet) {
      stopGames();
      OpenURI("http://jalmus.net?lang = "+language);
    } else if (e.getSource() == ui.okscoreMessage) {
      ui.scoreMessage.dispose();
      if (isLessonMode) {
        if ((currentlesson.isNoteLevel() & currentScore.isWin())              || currentlesson.isRhythmLevel()              || currentlesson.isScoreLevel()) {
          nextLevel();
        } else {
          startLevel();
        }
      }
    } else if (e.getSource() == ui.menuMidi) {
      if (gameStarted) {
        paused = true;
      }
      backupMidiOptions();
      ui.midiOptionsDialog.setLocationRelativeTo(this);
      ui.midiOptionsDialog.setVisible(true);
    } else if (e.getSource() == ui.bfermer) {
      ui.aboutDialog.setVisible(false);
    } else if (e.getSource() == ui.aboutMenuItem) {
      stopGames();
      ui.aboutDialog.setContentPane(ui.aboutPanel);
      ui.aboutPanelTextArea.setText(ui.tcredits);
      ui.aboutDialog.setSize(400, 330);
      ui.aboutDialog.setLocationRelativeTo(this);
      ui.aboutDialog.setVisible(true);
    } else if (e.getSource() == ui.blicence) {
      ui.aboutPanelTextArea.setText(ui.tlicence);
    } else if (e.getSource() == ui.bcredits) {
      ui.aboutPanelTextArea.setText(ui.tcredits);
    } 

    //  SI LE LABEL DU BOUTON SELECTIONNE EST EGAL A LA NOTE COURANTE   ----> GAGNE

    if ((gameStarted && selectedGame == NOTEREADING && !paused)        && (e.getSource() == ui.bdo || e.getSource() == ui.bre || e.getSource() == ui.bmi || e.getSource() == ui.bfa         || e.getSource() == ui.bsol || e.getSource() == ui.bla || e.getSource() == ui.bsi || e.getSource() == ui.bdo2
        || e.getSource() == ui.bdiese || e.getSource() == ui.bdiese2 || e.getSource() == ui.bbemol         || e.getSource() == ui.bbemol2)) {
      if (!ncourante.getAlteration().equals("")) {  // NOTES AVEC ALTERATION
        if (((JButton)e.getSource()).getText().equals(ncourante.getAlteration())) {
          alterationOk = true;
        } else if (alterationOk && ((JButton)e.getSource()).getText().equals(ncourante.getNom())) {
          rightAnswer();
        } else {
          wrongAnswer();
        }
      } else if (ncourante.getAlteration().equals("")) { // NOTE SANS ALTERATION
        if (((JButton)e.getSource()).getText() == ncourante.getNom()) {
            rightAnswer();
        } else {
          wrongAnswer();
        }
      }
    }
    repaint();
  }

  void handleExitMenuItem() {
    stopGames();
    dispose();
  }

  private void handleLessonMenuItem(String lesson, Integer i) {
    String parseError;

    stopGames();
    isLessonMode = true;

    try {
      // crÃ©ation d'une fabrique de parseurs SAX
      SAXParserFactory fabrique = SAXParserFactory.newInstance();

      // crÃ©ation d'un parseur SAX
      SAXParser parseur = fabrique.newSAXParser();
      currentlesson = new Lessons();
      // lecture d'un fichier XML avec un DefaultHandler

      File lessonFile = new File(ui.pathsubdir[i]+File.separator+lesson+".xml");
      parseur.parse(lessonFile, currentlesson);

      if (currentlesson.isNoteLevel()) { 
        noteLevel.copy((NoteLevel)currentlesson.getLevel());
        noteLevel.updatenbnotes(piano);

        selectedGame = NOTEREADING;
        initNoteGame();

        changeScreen();
        noteLevel.printtest();
        startLevel();
      } else if (currentlesson.isRhythmLevel()) { 
        rhythmLevel.copy((RhythmLevel)currentlesson.getLevel());
        selectedGame = RHYTHMREADING;       
        changeScreen();
        initRhythmGame();
        rhythmLevel.printtest();
        ui.newButton.doClick();
        startLevel();
      } else if (currentlesson.isScoreLevel()) { 
        scoreLevel.copy((ScoreLevel)currentlesson.getLevel());

        scoreLevel.printtest();
        selectedGame = SCOREREADING;
        changeScreen();
        initRhythmGame();
        ui.newButton.doClick();
        startLevel();
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
    //  lessonsDialog.setVisible(false);
  }

  void handleRhythmReadingMenuItem() {
    stopGames();
    /*
       if (latencySlider.getValue() == 0)
       JOptionPane.showMessageDialog(this, ui.bundle.getString("_setlatency"),
       "", JOptionPane.INFORMATION_MESSAGE);
       */
    //   pgamebutton.removeAll();

    ui.pgamebutton.add(ui.newButton);
    ui.pgamebutton.add(ui.listenButton);
    ui.pgamebutton.add(ui.startButton);
    ui.pgamebutton.add(ui.preferencesButton);
    scoreYpos = 110;
    repaint();

    selectedGame = RHYTHMREADING;
    ui.newButton.doClick();
    if (isLessonMode) {
      noteLevel.init();
    }
    isLessonMode = false;
    changeScreen();
  }

  void handleScoreReadingMenuItem() {
    stopGames();
    //   ui.pgamebutton.removeAll();

    /*
       if (latencySlider.getValue() == 0)
       JOptionPane.showMessageDialog(this, ui.bundle.getString("_setlatency"),
       "", JOptionPane.INFORMATION_MESSAGE);
       */
    ui.pgamebutton.add(ui.newButton);
    ui.pgamebutton.add(ui.listenButton);
    ui.pgamebutton.add(ui.startButton);
    ui.pgamebutton.add(ui.preferencesButton);
    scoreYpos = 110;
    repaint();

    selectedGame = SCOREREADING;
    ui.newButton.doClick();
    if (isLessonMode) {
      noteLevel.init();
    }
    isLessonMode = false;
    changeScreen();
  }

  void handleNoteReadingMenuItem() {
    stopGames();
    ui.pgamebutton.removeAll();
    ui.pgamebutton.add(ui.startButton);
    ui.pgamebutton.add(ui.pnotes);
    ui.pgamebutton.add(ui.preferencesButton);

    initNoteGame();
    if (isLessonMode) {
      noteLevel.init();
    }
    selectedGame = NOTEREADING;
    isLessonMode = false;
    changeScreen();
  }

  void handleMidiOptionsCancelClicked() {
    restoreMidiOptions();
    ui.midiOptionsDialog.setVisible(false);
    if (paused) {
      paused = false;
    }
  }

  void handleMidiOptionsOkClicked() {
    ui.midiOptionsDialog.setVisible(false);
    if (paused) {
      paused = false;
    }
  }

  void handleStartButtonClicked() {
    stopSound();
    if (selectedGame == NOTEREADING) {
      if (gameStarted) {
        stopNoteGame();
        initNoteGame(); //stop the game before restart
      } else {
        requestFocus();
        startNoteGame();
        if (!renderingThread.isAlive()) {
          renderingThread.start();
        }
      }
    } else if (selectedGame == RHYTHMREADING || selectedGame==SCOREREADING) {
      if (gameStarted) {
        stopRhythmGame();
        gameStarted = false;
      } else if (paintrhythms) {
        samerhythms = true;
        muterhythms = true;
        initRhythmGame();
        startRhythmGame();
      }
    }
  }

  void handleListenButtonClicked() {
    samerhythms = true;
    muterhythms = false;
    initRhythmGame();
    startRhythmGame();
  }

  void handleNewButtonClicked() {
    samerhythms = false;
    muterhythms = false;
    initRhythmGame();
    paintrhythms = true; 
    repaint(); //only to paint exercise
    gameStarted = false;
  }

  void handlePreferencesClicked() {
    if (selectedGame == NOTEREADING) {
      ui.preferencesTabbedPane.setSelectedIndex(ui.NOTE_READING_TAB);
    } else if (selectedGame == RHYTHMREADING) {
      ui.preferencesTabbedPane.setSelectedIndex(ui.RHYTHM_READING_TAB);     
    }
    else if (selectedGame == SCOREREADING) {
      ui.preferencesTabbedPane.setSelectedIndex(ui.SCORE_READING_TAB);     
    }
    ui.menuPrefs.doClick();
  }

  void handleLevelOkClicked() {
    ui.levelMessage.dispose();
    if (isLessonMode) {
      ui.startButton.doClick();
    }
  }

  void handlePreferencesCancelClicked() {
    restorePreferences();
    ui.preferencesDialog.setVisible(false);
  }

  void handlePreferencesSaveClicked() {
    ui.saveDialog.setTitle(ui.bundle.getString("_buttonsave"));
    //	ui.saveDialog.setLayout(new GridLayout(3, 1));    
    ui.saveDialog.pack();
    ui.saveDialog.setLocationRelativeTo(this);
    ui.saveDialog.setVisible(true);
  }

  void handleOKSave() {
    try {
      if (ui.lessonName.getText().length() != 0) {
        if (ui.preferencesTabbedPane.getSelectedIndex() == 0) {          noteLevel.save(currentlesson,ui.lessonName.getText()+".xml", ui.lessonMessage.getText(), language);        } else if (ui.preferencesTabbedPane.getSelectedIndex() == 1) {
          rhythmLevel.printtest();
          rhythmLevel.save(currentlesson,ui.lessonName.getText()+".xml", ui.lessonMessage.getText(), language);
        } else if (ui.preferencesTabbedPane.getSelectedIndex() == 2) {
          scoreLevel.printtest();
          scoreLevel.save(currentlesson,ui.lessonName.getText()+".xml", ui.lessonMessage.getText(), language);
        }

        ui.saveDialog.setVisible(false);
        ui.maBarre.remove(ui.lessonsMenu);
        ui.lessonsMenu = ui.buildLessonsMenu();        ui.maBarre.add(ui.lessonsMenu, 1);
      } else {        JOptionPane.showMessageDialog(null, "Give the name of the lesson", "Warning", JOptionPane.ERROR_MESSAGE);       }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void handlePreferencesOkClicked() {

    if (selectedGame == NOTEREADING) {
      // update current level for note reading
      noteLevel.inibasenote();
      initNoteGame();
      noteLevel.updatenbnotes(piano);
    } else if (selectedGame == RHYTHMREADING) {

      // update parameters for rhythm reading NO MORE NEEDED now on Itemstatechanged
      /*  if (! wholeCheckBox.isSelected() && !halfCheckBox.isSelected() && 
          !quarterCheckBox.isSelected() && !eighthCheckBox.isSelected()) {

          JOptionPane.showMessageDialog(this,
          ui.bundle.getString("_leastrythm"),
          "Warning",
          JOptionPane.WARNING_MESSAGE);

          } else {
          rhythmLevel.adjustLevel(wholeCheckBox.isSelected(), halfCheckBox.isSelected(), quarterCheckBox.isSelected(),
          eighthCheckBox.isSelected(), restCheckBox.isSelected(), tripletCheckBox.isSelected());
          }*/
      ui.newButton.doClick();
    } else if (selectedGame == SCOREREADING) {

      scoreLevel.printtest();
      // update parameters for rhythm reading
      /*  if (!scorewholeCheckBox.isSelected() && !scorehalfCheckBox.isSelected() && !scorequarterCheckBox.isSelected() && !scoreeighthCheckBox.isSelected()) {

          JOptionPane.showMessageDialog(this,
          ui.bundle.getString("_leastrythm"),
          "Warning",
          JOptionPane.WARNING_MESSAGE);

          } else {
          scoreLevel.updateRhythm(scorewholeCheckBox.isSelected(), scorehalfCheckBox.isSelected(), scorequarterCheckBox.isSelected(),
          scoreeighthCheckBox.isSelected(), scorerestCheckBox.isSelected(), scoreTripletCheckBox.isSelected());
          }*/
      ui.newButton.doClick();
    }

    // update screen
    changeScreen();
    ui.preferencesDialog.setVisible(false);
    repaint();
  }

  private void backupPreferences() {
    savePrefs[0] = ui.noteGameTypeComboBox.getSelectedIndex();
    savePrefs[1] = ui.noteGameSpeedComboBox.getSelectedIndex();
    savePrefs[2] = ui.keyComboBox.getSelectedIndex();
    savePrefs[4] = ui.keySignatureCheckBox.getSelectedIndex();
    savePrefs[5] = ui.noteGameSpeedComboBox.getSelectedIndex();
    savePrefs[6] = ui.noteGroupComboBox.getSelectedIndex();
    if (ui.noteGroupComboBox.getSelectedIndex() == 0     	|| ui.noteGroupComboBox.getSelectedIndex() == 1) {
      savePrefs[7] = ui.noteCountComboBox.getSelectedIndex();
    } else if (ui.noteGroupComboBox.getSelectedIndex() == 2) {
      savePrefs[7] = ui.intervalComboBox.getSelectedIndex();
    } else if (ui.noteGroupComboBox.getSelectedIndex() == 3) {
      savePrefs[7] = ui.chordTypeComboBox.getSelectedIndex();
    }
    savePrefs[8] = ui.rhythmGameTypeComboBox.getSelectedIndex();
    savePrefs[9] = ui.rhythmGameSpeedComboBox.getSelectedIndex();
    if (ui.wholeCheckBox.isSelected()) {
      savePrefs[10] = 1;
    } else {
      savePrefs[10] = 0;
    }
    if (ui.halfCheckBox.isSelected()) {
      savePrefs[11] = 1;
    } else {
      savePrefs[11] = 0;
    }
    if (ui.dottedhalfCheckBox.isSelected()) {
      savePrefs[28] = 1;
    } else {
      savePrefs[28] = 0;
    }
    if (ui.quarterCheckBox.isSelected()) {
      savePrefs[12] = 1;
    } else {
      savePrefs[12] = 0;
    }
    if (ui.eighthCheckBox.isSelected()) {
      savePrefs[13] = 1;
    } else {
      savePrefs[13] = 0;
    }
    if (ui.restCheckBox.isSelected()) {
      savePrefs[14] = 1;
    } else {
      savePrefs[14] = 0;
    }
    if (ui.metronomeCheckBox.isSelected()) {
      savePrefs[15] = 1;
    } else {
      savePrefs[15] = 0;
    }
    savePrefs[16] = ui.scoreGameTypeComboBox.getSelectedIndex();
    savePrefs[17] = ui.scoreGameSpeedComboBox.getSelectedIndex();
    if (ui.scorewholeCheckBox.isSelected()) {
      savePrefs[18] = 1;
    } else {
      savePrefs[18] = 0;
    }
    if (ui.scorehalfCheckBox.isSelected()) {
      savePrefs[19] = 1;
    } else {
      savePrefs[19] = 0;
    }
    if (ui.scoredottedhalfCheckBox.isSelected()) {
      savePrefs[28] = 1;
    } else {
      savePrefs[28] = 0;
    }
    if (ui.scorequarterCheckBox.isSelected()) {
      savePrefs[20] = 1;
    } else {
      savePrefs[20] = 0;
    }
    if (ui.scoreeighthCheckBox.isSelected()) {
      savePrefs[21] = 1;
    } else {
      savePrefs[21] = 0;
    }
    if (ui.scorerestCheckBox.isSelected()) {
      savePrefs[22] = 1;
    } else {
      savePrefs[22] = 0;
    }
    if (ui.scoreMetronomeCheckBox.isSelected()) {
      savePrefs[23] = 1;
    } else {
      savePrefs[23] = 0;
    }
    savePrefs[24] = ui.scoreKeyComboBox.getSelectedIndex();
    savePrefs[25] = ui.scoreAlterationsComboBox.getSelectedIndex();
    if (ui.tripletCheckBox.isSelected()) {
      savePrefs[26] = 1;
    } else {
      savePrefs[26] = 0;
    }
    if (ui.scoreTripletCheckBox.isSelected()) {
      savePrefs[27] = 1;
    } else {
      savePrefs[27] = 0;
    }
  }

  private void restorePreferences() {
    ui.noteGameTypeComboBox.setSelectedIndex(savePrefs[0]);
    ui.noteGameSpeedComboBox.setSelectedIndex(savePrefs[1]);
    ui.keyComboBox.setSelectedIndex(savePrefs[2]);
    ui.keySignatureCheckBox.setSelectedIndex(savePrefs[4]);
    ui.noteGameSpeedComboBox.setSelectedIndex(savePrefs[5]);
    ui.noteGroupComboBox.setSelectedIndex(savePrefs[6]);
    if (ui.noteGroupComboBox.getSelectedIndex() == 0 || ui.noteGroupComboBox.getSelectedIndex()==1) {
      ui.noteCountComboBox.setSelectedIndex(savePrefs[7]);
    } else if (ui.noteGroupComboBox.getSelectedIndex() == 2) {
      ui.intervalComboBox.setSelectedIndex(savePrefs[7]);
    } else if (ui.noteGroupComboBox.getSelectedIndex() == 3) {
      ui.chordTypeComboBox.setSelectedIndex(savePrefs[7]);
    }
    ui.rhythmGameTypeComboBox.setSelectedIndex(savePrefs[8]);
    ui.rhythmGameSpeedComboBox.setSelectedIndex(savePrefs[9]);
    if (savePrefs[10] == 1) {
      ui.wholeCheckBox.setSelected(true);
    } else {
      ui.wholeCheckBox.setSelected(false);
    }
    if (savePrefs[11] == 1) {
      ui.halfCheckBox.setSelected(true);
    } else {
      ui.halfCheckBox.setSelected(false);
    }
    if (savePrefs[28] == 1) {
      ui.dottedhalfCheckBox.setSelected(true);
    } else {
      ui.dottedhalfCheckBox.setSelected(false);
    }
    if (savePrefs[12] == 1) {
      ui.quarterCheckBox.setSelected(true);
    } else {
      ui.quarterCheckBox.setSelected(false);
    }
    if (savePrefs[13] == 1) {
      ui.eighthCheckBox.setSelected(true);
    } else {
      ui.eighthCheckBox.setSelected(false);
    }
    if (savePrefs[14] == 1) {
      ui.restCheckBox.setSelected(true);
    } else {
      ui.restCheckBox.setSelected(false);
    }
    if (savePrefs[15] == 1) {
      ui.metronomeCheckBox.setSelected(true);
    } else {
      ui.metronomeCheckBox.setSelected(false);
    }

    ui.scoreGameTypeComboBox.setSelectedIndex(savePrefs[16]);
    ui.scoreGameSpeedComboBox.setSelectedIndex(savePrefs[17]);
    if (savePrefs[18] == 1) {
      ui.scorewholeCheckBox.setSelected(true);
    } else {
      ui.scorewholeCheckBox.setSelected(false);
    }
    if (savePrefs[19] == 1) {
      ui.scorehalfCheckBox.setSelected(true);
    } else {
      ui.scorehalfCheckBox.setSelected(false);
    }
    if (savePrefs[28] == 1) {
      ui.scoredottedhalfCheckBox.setSelected(true);
    } else {
      ui.scoredottedhalfCheckBox.setSelected(false);
    }
    if (savePrefs[20] == 1) {
      ui.scorequarterCheckBox.setSelected(true);
    } else {
      ui.scorequarterCheckBox.setSelected(false);
    }
    if (savePrefs[21] == 1) {
      ui.scoreeighthCheckBox.setSelected(true);
    } else {
      ui.scoreeighthCheckBox.setSelected(false);
    }
    if (savePrefs[22] == 1) {
      ui.scorerestCheckBox.setSelected(true);
    } else {
      ui.scorerestCheckBox.setSelected(false);
    }
    if (savePrefs[23] == 1) {
      ui.scoreMetronomeCheckBox.setSelected(true);
    } else {
      ui.scoreMetronomeCheckBox.setSelected(false);
    }
    ui.scoreKeyComboBox.setSelectedIndex(savePrefs[24]);
    ui.scoreAlterationsComboBox.setSelectedIndex(savePrefs[25]);
    if (savePrefs[26] == 1) {
      ui.tripletCheckBox.setSelected(true);
    } else {
      ui.tripletCheckBox.setSelected(false);
    }
    if (savePrefs[27] == 1) {
      ui.scoreTripletCheckBox.setSelected(true);
    } else {
      ui.scoreTripletCheckBox.setSelected(false);
    }
  }

  /**
   * Open uri with default browser
   *
   * @param  uristring uri to open
   * @return      void
   *
   */
  public  void OpenURI(String uristring) {
    if(!java.awt.Desktop.isDesktopSupported()) {
      System.err.println( "Desktop is not supported (fatal)" );
      System.exit(1);
    }

    java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

    if(!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
      System.err.println("Desktop doesn't support the browse action (fatal)");
      System.exit(1);
    }

    try {
      java.net.URI uri = new java.net.URI( uristring );
      desktop.browse( uri );
    } catch (Exception e) {
      System.err.println( e.getMessage() );
    }       
  }

  public  void OpenDirectory(File dir) {
    if(!java.awt.Desktop.isDesktopSupported()) {
      System.err.println("Desktop is not supported (fatal)");
      System.exit(1);
    }

    java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

    if(!desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
      System.err.println("Desktop doesn't support the open action (fatal)");
      System.exit(1);
    }

    try {
      desktop.open(dir);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }       
  } 
  private void backupMidiOptions() {
    //TODO: This approach does not work when midi device order is changed!!
    if (ui.soundOnCheckBox.isSelected()) {
      sauvmidi[0] = 1;
    } else {
      sauvmidi[0] = 0;
    }
    sauvmidi[1] = ui.instrumentsComboBox.getSelectedIndex();
    sauvmidi[2] = ui.midiInComboBox.getSelectedIndex();
    sauvmidi[3] = ((Number)ui.transpositionSpinner.getValue()).intValue();
    if (ui.keyboardsoundCheckBox.isSelected()) {
      sauvmidi[4] = 1;
    } else {
      sauvmidi[4] = 0;
    }
    sauvmidi[5] = ui.midiOutComboBox.getSelectedIndex();
  }

  private void restoreMidiOptions() {
    //TODO: This approach does not work when midi device order is changed!!
    if (sauvmidi[0] == 1) {
      ui.soundOnCheckBox.setSelected(true);
    } else {
      ui.soundOnCheckBox.setSelected(false);
    }
    ui.instrumentsComboBox.setSelectedIndex(sauvmidi[1]);
    ui.midiInComboBox.setSelectedIndex(sauvmidi[2]);
    ui.transpositionSpinner.setValue(sauvmidi[3]);
    if (sauvmidi[4] == 1) {
      ui.keyboardsoundCheckBox.setSelected(true);
    } else {
      ui.keyboardsoundCheckBox.setSelected(false);
    }
    ui.midiOutComboBox.setSelectedIndex(sauvmidi[5]);
  }

  public void itemStateChanged(ItemEvent evt) {
    if (evt.getItemSelectable() == ui.midiInComboBox && !ui.selectmidi_forlang) {
      String smidiin = (String) ui.midiInComboBox.getSelectedItem();
      if (smidiin != ui.pasclavier) {
        if (open) {
          inputDevice.close();
          open = false;
        }

        String midimessage = "Initialisation "+smidiin;

        MidiDevice.Info info = MidiCommon.getMidiDeviceInfo(smidiin, false);
        if (info == null) {
          midimessage = "nodevice";
          System.out.println(midimessage);
        } else {
          try {
            inputDevice = MidiSystem.getMidiDevice(info);
            inputDevice.open();
          } catch (MidiUnavailableException e) {
            midimessage = "nodevice";
            System.out.println(midimessage);
          }

          Receiver r = new DumpReceiver(this);
          try {
            Transmitter t = inputDevice.getTransmitter();
            t.setReceiver(r);
          } catch (MidiUnavailableException e) {
            midimessage = "wasn't able to connect the device's Transmitter to the Receiver:";
            System.out.println(e);
            inputDevice.close();
            System.exit(1);
          }
          midimessage = "End initialisation";
        }
        if (inputDevice.isOpen()) {
          System.out.println("Midi Device open : play a key, if this key don't change his color at screen, verify the MIDI port name");
        }
        open = true;
      }
    }

    if (evt.getItemSelectable() == ui.midiOutComboBox && !ui.selectmidi_forlang) {
      String smidiout = (String) ui.midiOutComboBox.getSelectedItem();
      if (smidiout != ui.pasclavier) {
        String midimessage = "Initialisation " + smidiout;
        MidiDevice.Info info = MidiCommon.getMidiDeviceInfo(smidiout, true);
        if (info == null) {
          midimessage = "nodevice";
          System.out.println(midimessage);
        } else {
          try {
            outputDevice = MidiSystem.getMidiDevice(info);
            outputDevice.open();
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
      else if (evt.getItemSelectable() == ui.wholeCheckBox) {
        if (ui.wholeCheckBox.isSelected()) {          rhythmLevel.setWholeNote(true);        } else {          rhythmLevel.setWholeNote(false);        }
      } else if (evt.getItemSelectable() == ui.halfCheckBox) {
        if (ui.halfCheckBox.isSelected()) {          rhythmLevel.setHalfNote(true);        } else {          rhythmLevel.setHalfNote(false);        }
      } else if (evt.getItemSelectable() == ui.dottedhalfCheckBox) {
        if (ui.dottedhalfCheckBox.isSelected()) {          rhythmLevel.setDottedHalfNote(true);        } else {          rhythmLevel.setDottedHalfNote(false);        }
      } else if (evt.getItemSelectable() == ui.quarterCheckBox) {
        if (ui.quarterCheckBox.isSelected()) {          rhythmLevel.setQuarterNote(true);        } else {          rhythmLevel.setQuarterNote(false);        }
      } else if (evt.getItemSelectable() == ui.eighthCheckBox) {
        if (ui.eighthCheckBox.isSelected()) {          rhythmLevel.setEighthNote(true);        } else {          rhythmLevel.setEighthNote(false);        }
      } else if (evt.getItemSelectable() == ui.restCheckBox) {
        if (ui.restCheckBox.isSelected()) {          rhythmLevel.setSilence(true);        } else {          rhythmLevel.setSilence(false);        }
      } else if (evt.getItemSelectable() == ui.tripletCheckBox) {
        if (ui.tripletCheckBox.isSelected()) {          rhythmLevel.setTriplet(true);        } else {          rhythmLevel.setTriplet(false);        }
      } else if (evt.getItemSelectable() == ui.metronomeCheckBox) {
        if (ui.metronomeCheckBox.isSelected()) {          rhythmLevel.setMetronome(true);        } else {          rhythmLevel.setMetronome(false);        }
      } else if (evt.getItemSelectable() == ui.metronomeShowCheckBox) {
        if (ui.metronomeShowCheckBox.isSelected()) {          rhythmLevel.setMetronomeBeats(true);        } else {          rhythmLevel.setMetronomeBeats(false);        }
      } 
      // For score level update
      else if (evt.getItemSelectable() == ui.scorewholeCheckBox) {
        if (ui.scorewholeCheckBox.isSelected()) {          scoreLevel.setWholeNote(true);        } else {          scoreLevel.setWholeNote(false);        }
      } else if (evt.getItemSelectable() == ui.scorehalfCheckBox) {
        if (ui.scorehalfCheckBox.isSelected()) {          scoreLevel.setHalfNote(true);        } else {          scoreLevel.setHalfNote(false);        }
      } else if (evt.getItemSelectable() == ui.scoredottedhalfCheckBox) {
        if (ui.scoredottedhalfCheckBox.isSelected()) {          scoreLevel.setDottedHalfNote(true);        } else {          scoreLevel.setDottedHalfNote(false);        }
      } else if (evt.getItemSelectable() == ui.scorequarterCheckBox) {
        if (ui.scorequarterCheckBox.isSelected()) {          scoreLevel.setQuarterNote(true);        } else {          scoreLevel.setQuarterNote(false);        }
      } else if (evt.getItemSelectable() == ui.scoreeighthCheckBox) {
        if (ui.scoreeighthCheckBox.isSelected()) {          scoreLevel.setEighthNote(true);        } else {          scoreLevel.setEighthNote(false);        }
      } else if (evt.getItemSelectable() == ui.scorerestCheckBox) {
        if (ui.scorerestCheckBox.isSelected()) {          scoreLevel.setSilence(true);        } else {          scoreLevel.setSilence(false);        }
      } else if (evt.getItemSelectable() == ui.scoreTripletCheckBox) {
        if (ui.scoreTripletCheckBox.isSelected()) {          scoreLevel.setTriplet(true);        } else {          scoreLevel.setTriplet(false);        }
      } else if (evt.getItemSelectable() == ui.scoreMetronomeCheckBox) {
        if (ui.scoreMetronomeCheckBox.isSelected()) {          scoreLevel.setMetronome(true);        } else {          scoreLevel.setMetronome(false);        }
      } else if (evt.getItemSelectable() == ui.scoreMetronomeShowCheckBox) {
        if (ui.scoreMetronomeShowCheckBox.isSelected()) {          scoreLevel.setMetronomeBeats(true);        } else {          scoreLevel.setMetronomeBeats(false);        }
      } else if (evt.getItemSelectable() == ui.instrumentsComboBox) {
        if (!midierror && instruments != null) {
          currentChannel.getchannel().programChange(ui.instrumentsComboBox.getSelectedIndex());
        }
      } else if (evt.getItemSelectable() == ui.keyComboBox) {
        if (ui.keyComboBox.getSelectedIndex() == 0) {
          noteLevel.setCurrentKey("treble");
          noteLevel.resetPitcheslist();
        } else if (ui.keyComboBox.getSelectedIndex() == 1) {
          noteLevel.setCurrentKey("bass");
          noteLevel.resetPitcheslist();
        } else if (ui.keyComboBox.getSelectedIndex() == 2) {
          noteLevel.setCurrentKey("both");
          noteLevel.resetPitcheslist();
        }
      } else if (evt.getItemSelectable() == ui.scoreKeyComboBox) {
        if (ui.scoreKeyComboBox.getSelectedIndex() == 0) {
          scoreLevel.setCurrentKey("treble");
          scoreLevel.initPitcheslist(9);
          if (selectedGame == SCOREREADING) {        	  initRhythmGame();          }
        } else if (ui.scoreKeyComboBox.getSelectedIndex() == 1) {
          scoreLevel.setCurrentKey("bass");
          scoreLevel.initPitcheslist(9);
          if (selectedGame == SCOREREADING) {        	  initRhythmGame();          }
        }
      } else if (evt.getItemSelectable() == ui.keySignatureCheckBox) {
        initNoteGame();

        if (ui.keySignatureCheckBox.getSelectedIndex() == 0) {
          double tmp = Math.random();  // to choice same alteration for alterated notes
          String stmp;
          if (tmp<0.5) {
            stmp = "#";
          } else {
            stmp = "b";
          }
          noteLevel.setRandomtonality(false);
          noteLevel.getCurrentTonality().init(0, stmp);
        } else if (ui.keySignatureCheckBox.getSelectedIndex() == 15) {
          // choix de la tonalite au hasard au lancement du jeu
          noteLevel.setRandomtonality(true);
          noteLevel.getCurrentTonality().init(0, "r");
        } else {
          noteLevel.setRandomtonality(false);
          if (ui.keySignatureCheckBox.getSelectedIndex() == 1) {
            noteLevel.getCurrentTonality().init(1, "#");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 2) {
            noteLevel.getCurrentTonality().init(2, "#");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 3) {
            noteLevel.getCurrentTonality().init(3, "#");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 4) {
            noteLevel.getCurrentTonality().init(4, "#");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 5) {
            noteLevel.getCurrentTonality().init(5, "#");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 6) {
            noteLevel.getCurrentTonality().init(6, "#");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 7) {
            noteLevel.getCurrentTonality().init(7, "#");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 8) {
            noteLevel.getCurrentTonality().init(1, "b");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 9) {
            noteLevel.getCurrentTonality().init(2, "b");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 10) {
            noteLevel.getCurrentTonality().init(3, "b");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 11) {
            noteLevel.getCurrentTonality().init(4, "b");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 12) {
            noteLevel.getCurrentTonality().init(5, "b");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 13) {
            noteLevel.getCurrentTonality().init(6, "b");
          } else if (ui.keySignatureCheckBox.getSelectedIndex() == 14) {
            noteLevel.getCurrentTonality().init(7, "b");
          }
        }
      }
      // Game type choice
      else if (evt.getItemSelectable() == ui.noteGameTypeComboBox) {
        if (ui.noteGameTypeComboBox.getSelectedIndex() == 0) {
          noteLevel.setGametype("normal");
        } else if (ui.noteGameTypeComboBox.getSelectedIndex() == 1) {
          noteLevel.setGametype("inline");
        } else if (ui.noteGameTypeComboBox.getSelectedIndex() == 2) {
          noteLevel.setGametype("learning");
        }
      }

      else if (evt.getItemSelectable() == ui.rhythmGameTypeComboBox) {
        if (ui.rhythmGameTypeComboBox.getSelectedIndex() == 0) {
          rhythmgame = 0; // fix this with creating class level rhythm
        }/* else if (rhythmGameTypeComboBox.getSelectedIndex() == 1) {
        rhythmgame = 1;

        }*/
      }

      // Speed choice note reading
      else if (evt.getItemSelectable() == ui.noteGameSpeedComboBox) {
        if (ui.noteGameSpeedComboBox.getSelectedIndex() == 0) {
          noteLevel.setSpeed(28);
        } else if (ui.noteGameSpeedComboBox.getSelectedIndex() == 1) {
          noteLevel.setSpeed(22);
        } else if (ui.noteGameSpeedComboBox.getSelectedIndex() == 2) {
          noteLevel.setSpeed(16);
        } else if (ui.noteGameSpeedComboBox.getSelectedIndex() == 3) {
          noteLevel.setSpeed(12);
        } else if (ui.noteGameSpeedComboBox.getSelectedIndex() == 4) {
          noteLevel.setSpeed(8);
        }
      }

      // Speed choice rhythm reading
      else if (evt.getItemSelectable() == ui.rhythmGameSpeedComboBox) {
        if (ui.rhythmGameSpeedComboBox.getSelectedIndex() == 0) {
          rhythmLevel.setSpeed(40);
        } else if (ui.rhythmGameSpeedComboBox.getSelectedIndex() == 1) {
          rhythmLevel.setSpeed(60);
        } else if (ui.rhythmGameSpeedComboBox.getSelectedIndex() == 2) {
          rhythmLevel.setSpeed(100);
        } else if (ui.rhythmGameSpeedComboBox.getSelectedIndex() == 3) {
          rhythmLevel.setSpeed(120);
        } else if (ui.rhythmGameSpeedComboBox.getSelectedIndex() == 4) {
          rhythmLevel.setSpeed(160);
        }
      } else if (evt.getItemSelectable() == ui.noteGroupComboBox) {
        if (ui.noteGroupComboBox.getSelectedIndex() == 0) {
          noteLevel.setNotetype("notes");
          ui.noteReadingNotesPanel.removeAll();
          ui.noteReadingNotesPanel.add(ui.noteGroupComboBox);
          ui.noteReadingNotesPanel.add(ui.noteCountComboBox);
          ui.noteReadingNotesPanel.repaint();
          ui.preferencesDialog.repaint();
        }
        if (ui.noteGroupComboBox.getSelectedIndex() == 1) {
          noteLevel.setNotetype("accidentals");
          ui.noteReadingNotesPanel.removeAll();
          ui.noteReadingNotesPanel.add(ui.noteGroupComboBox);
          ui.noteReadingNotesPanel.add(ui.noteCountComboBox);
          ui.noteReadingNotesPanel.repaint();
          ui.preferencesDialog.repaint();
        } else if (ui.noteGroupComboBox.getSelectedIndex() == 2) {
          noteLevel.setNotetype("custom");
          ui.noteReadingNotesPanel.removeAll();
          ui.noteReadingNotesPanel.add(ui.noteGroupComboBox);
          ui.preferencesDialog.repaint();
          ui.chooseNoteP = new ChooseNotePanel(noteLevel.getKey(),NOTEREADING,  ui.bundle);
          ui.chooseNoteP.updateTable(noteLevel.getPitcheslist());
          ui.chooseNoteP.setOpaque(true); //content panes must be opaque 
          ui.chooseNoteP.setVisible(true);
          ui.chooseNoteP.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              //Execute when button is pressed
              if (!ui.chooseNoteP.atLeast3Pitches()) {                JOptionPane.showMessageDialog(null, "Choose at least three notes", "Warning", JOptionPane.ERROR_MESSAGE);               } else {
                ui.notesDialog.setVisible(false);
                noteLevel.setPitcheslist(ui.chooseNoteP.getPitches());
              }
            }
          });   

          ui.notesDialog.setContentPane(ui.chooseNoteP);
          ui.notesDialog.setSize(650, 220);
          ui.notesDialog.setLocationRelativeTo(this);
          ui.notesDialog.setVisible(true);

          ui.chooseNoteP.setVisible(true);

          this.add(ui.notesDialog);

        } else if (ui.noteGroupComboBox.getSelectedIndex() == 3) {
          noteLevel.setNotetype("intervals");

          ui.noteReadingNotesPanel.removeAll();
          ui.noteReadingNotesPanel.add(ui.noteGroupComboBox);
          ui.noteReadingNotesPanel.add(ui.intervalComboBox);
          ui.noteReadingNotesPanel.repaint();
          ui.preferencesDialog.repaint();

        } else if (ui.noteGroupComboBox.getSelectedIndex() == 4) {
          noteLevel.setNotetype("chords");

          ui.noteReadingNotesPanel.removeAll();
          ui.noteReadingNotesPanel.add(ui.noteGroupComboBox);
          ui.noteReadingNotesPanel.add(ui.chordTypeComboBox);
          ui.noteReadingNotesPanel.repaint();
          ui.preferencesDialog.repaint();
        }
      } else if (evt.getItemSelectable() == ui.keyboardLengthComboBox) {
        if (ui.keyboardLengthComboBox.getSelectedIndex() == 0) {
          piano = new Piano(73, 40);
        } else if (ui.keyboardLengthComboBox.getSelectedIndex() == 1) {
          piano = new Piano(61, 90);
        }
      } else if (evt.getItemSelectable() == ui.noteCountComboBox) {
        if (ui.noteCountComboBox.getSelectedIndex() == 0) {
          noteLevel.setNbnotes(3);
        } else if (ui.noteCountComboBox.getSelectedIndex() == 1) {
          noteLevel.setNbnotes(5);
        } else if (ui.noteCountComboBox.getSelectedIndex() == 2) {
          noteLevel.setNbnotes(9);
        } else if (ui.noteCountComboBox.getSelectedIndex() == 3) {
          noteLevel.setNbnotes(15);
        } else if (ui.noteCountComboBox.getSelectedIndex() == 4) {
          noteLevel.setNbnotes(0);
        }
        ;
      } else if (evt.getItemSelectable() == ui.chordTypeComboBox) {
        if (ui.chordTypeComboBox.getSelectedIndex() == 0) {
          noteLevel.setChordtype("root");
        } else if (ui.chordTypeComboBox.getSelectedIndex() == 1) {
          noteLevel.setChordtype("inversion");
        }
      } else if (evt.getItemSelectable() == ui.intervalComboBox) {
        if (ui.intervalComboBox.getSelectedIndex() == 0) {
          noteLevel.setIntervaltype("second");
        } else if (ui.intervalComboBox.getSelectedIndex() == 1) {
          noteLevel.setIntervaltype("third");
        } else if (ui.intervalComboBox.getSelectedIndex() == 2) {
          noteLevel.setIntervaltype("fourth");
        } else if (ui.intervalComboBox.getSelectedIndex() == 3) {
          noteLevel.setIntervaltype("fifth");
        } else if (ui.intervalComboBox.getSelectedIndex() == 4) {
          noteLevel.setIntervaltype("sixth");
        } else if (ui.intervalComboBox.getSelectedIndex() == 5) {
          noteLevel.setIntervaltype("seventh");
        } else if (ui.intervalComboBox.getSelectedIndex() == 6) {
          noteLevel.setIntervaltype("octave");
        } else if (ui.intervalComboBox.getSelectedIndex() == 7) {
          noteLevel.setIntervaltype("random");
        } else if (ui.intervalComboBox.getSelectedIndex() == 8) {
          noteLevel.setIntervaltype("all");
        }
      } else if (evt.getItemSelectable() == ui.scoreGameSpeedComboBox) {
        if (ui.scoreGameSpeedComboBox.getSelectedIndex() == 0) {
          scoreLevel.setSpeed(40);
        } else if (ui.scoreGameSpeedComboBox.getSelectedIndex() == 1) {
          scoreLevel.setSpeed(60);
        } else if (ui.scoreGameSpeedComboBox.getSelectedIndex() == 2) {
          scoreLevel.setSpeed(100);
        } else if (ui.scoreGameSpeedComboBox.getSelectedIndex() == 3) {
          scoreLevel.setSpeed(120);
        } else if (ui.scoreGameSpeedComboBox.getSelectedIndex() == 4) {
          scoreLevel.setSpeed(160);
        }
      } else if (evt.getItemSelectable() == ui.scoreNotesComboBox) {
        if (ui.scoreNotesComboBox.getSelectedIndex() == 0) {
          scoreLevel.setNotetype("notes");
          scoreLevel.setNbnotes(9);
        } 
        if (ui.scoreNotesComboBox.getSelectedIndex() == 1) {
          scoreLevel.setNotetype("notes");
          scoreLevel.setNbnotes(15);
        } 
        if (ui.scoreNotesComboBox.getSelectedIndex() == 2) {
          scoreLevel.setNbnotes(0);
          scoreLevel.setNotetype("custom");

          ui.scoreChooseNoteP = new ChooseNotePanel(scoreLevel.getKey(), SCOREREADING, ui.bundle);
          ui.scoreChooseNoteP.updateTable(scoreLevel.getPitcheslist());
          ui.scoreChooseNoteP.setOpaque(true); //content panes must be opaque 
          ui.scoreChooseNoteP.setVisible(true);
          ui.scoreChooseNoteP.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            //Execute when button is pressed
              if (! ui.scoreChooseNoteP.atLeast3Pitches()) {                JOptionPane.showMessageDialog(null, "Choose at least three notes", "Warning", JOptionPane.ERROR_MESSAGE);               } else {
                ui.scoreNotesDialog.setVisible(false);
                scoreLevel.setPitcheslist(ui.scoreChooseNoteP.getPitches());
              }
            }
          });    

          ui.scoreNotesDialog.setContentPane(ui.scoreChooseNoteP);
          ui.scoreNotesDialog.setSize(650, 220);
          ui.scoreNotesDialog.setLocationRelativeTo(this);
          ui.scoreNotesDialog.setVisible(true);

          ui.scoreChooseNoteP.setVisible(true);

          this.add(ui.scoreNotesDialog);
        } 
      } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 15) {
        // random choice of tonality when game start
        scoreLevel.setRandomTonality(true);
        scoreLevel.getCurrentTonality().init(0, "r");
      } else {
        scoreLevel.setRandomTonality(false);
        if (ui.scoreAlterationsComboBox.getSelectedIndex() == 1) {
          scoreLevel.getCurrentTonality().init(1, "#");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 2) {
          scoreLevel.getCurrentTonality().init(2, "#");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 3) {
          scoreLevel.getCurrentTonality().init(3, "#");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 4) {
          scoreLevel.getCurrentTonality().init(4, "#");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 5) {
          scoreLevel.getCurrentTonality().init(5, "#");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 6) {
          scoreLevel.getCurrentTonality().init(6, "#");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 7) {
          scoreLevel.getCurrentTonality().init(7, "#");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 8) {
          scoreLevel.getCurrentTonality().init(1, "b");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 9) {
          scoreLevel.getCurrentTonality().init(2, "b");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 10) {
          scoreLevel.getCurrentTonality().init(3, "b");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 11) {
          scoreLevel.getCurrentTonality().init(4, "b");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 12) {
          scoreLevel.getCurrentTonality().init(5, "b");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 13) {
          scoreLevel.getCurrentTonality().init(6, "b");
        } else if (ui.scoreAlterationsComboBox.getSelectedIndex() == 14) {
          scoreLevel.getCurrentTonality().init(7, "b");
        }
      }
    }

    // Translating functions

    // DRAW METHODS

    // KEYS

    void drawKeys(Graphics g) {
      if (selectedGame == NOTEREADING) {
        if (noteLevel.isCurrentKeyTreble()) {
          g.setFont(MusiSync.deriveFont(70f));
          g.drawString("G", noteMargin, scoreYpos + 42);
        } else if (noteLevel.isCurrentKeyBass()) {
          g.setFont(MusiSync.deriveFont(60f));
          g.drawString("?", noteMargin, scoreYpos + 40);
        } else if (noteLevel.isCurrentKeyBoth()) {
          g.setFont(MusiSync.deriveFont(70f));
          g.drawString("G", noteMargin, scoreYpos+42);
          g.setFont(MusiSync.deriveFont(60f));
          g.drawString("?", noteMargin, scoreYpos+130);
        }
      } else if (selectedGame == RHYTHMREADING ) {
        for (int rowNum = 0; rowNum < numberOfRows; rowNum++) {
          // g.setFont(MusiSync.deriveFont(70f));
          //g.drawString("G", windowMargin, scoreYpos+42+rowNum*rowsDistance);
        }
      } else if (selectedGame == SCOREREADING ) {
        if (scoreLevel.isCurrentKeyTreble()) {
          for (int rowNum = 0; rowNum < numberOfRows; rowNum++) {
            g.setFont(MusiSync.deriveFont(70f));
            g.drawString("G", windowMargin, scoreYpos+42+rowNum*rowsDistance);
          }        } else if (scoreLevel.isCurrentKeyBass()) {
          for (int rowNum = 0; rowNum < numberOfRows; rowNum++) {
            g.setFont(MusiSync.deriveFont(60f));
            g.drawString("?", windowMargin, scoreYpos+40+rowNum*rowsDistance);
          }        }
      }
    }

    void drawTimeSignature(Graphics g) {
      g.setFont(MusiSync.deriveFont(58f));
      int tmpnum = 4;      int tmpden = 4;      if (selectedGame == RHYTHMREADING ) {
        tmpnum = rhythmLevel.getTimeSignNumerator();
        tmpden = rhythmLevel.getTimeSignDenominator();
      } else if (selectedGame == SCOREREADING ) {
        tmpnum = scoreLevel.getTimeSignNumerator();
        tmpden = scoreLevel.getTimeSignDenominator();
      }

      for (int rowNum = 0; rowNum < numberOfRows; rowNum++) {
        String t = "";
        if (tmpnum == 4 && tmpden == 4) {
          t = "$";        } else if (tmpnum == 3 && tmpden == 4) {
          t = "#";        } else if (tmpnum == 2 && tmpden == 4) {
          t = "@";        } else if (tmpnum == 6 && tmpden == 8) {
          t = "P";        }        g.drawString(t, windowMargin + keyWidth + alterationWidth, scoreYpos+41+rowNum*rowsDistance);
      }
    }

    // ROWS

    void drawInlineGame(Graphics g) {
      Dimension size = getSize();
      g.setColor(Color.black);
      int yd;

      for (yd = scoreYpos; yd<=scoreYpos+40; yd+=10) { //  1ere ligne ï¿½ 144;   derniï¿½re ï¿½ 176
        g.drawLine(noteMargin, yd, size.width-noteMargin, yd);
      }

      if (noteLevel.isCurrentKeyBoth()) {  // dessine la deuxiï¿½me portï¿½e 72 points en dessous
        for (yd = scoreYpos+90; yd<=scoreYpos+130; yd+=10) {  //  1ere ligne ï¿½ 196;   derniï¿½re ï¿½ 228
          g.drawLine(noteMargin, yd, size.width-noteMargin, yd);
        }
      }
      if (noteLevel.isInlinegame()) {
        g.setColor(Color.red);
        g.drawLine(noteMargin+98, scoreYpos-30, noteMargin+98, scoreYpos+70);
        if (noteLevel.isCurrentKeyBoth()) {
          g.drawLine(noteMargin+98, scoreYpos+20, noteMargin+98, scoreYpos+160);
        }
        g.setColor(Color.black);
      }
    }

    void drawScore(Graphics g) {
      Dimension size = getSize();
      g.setColor(Color.black);
      alterationWidth = scoreLevel.getCurrentTonality().getAlterationsNumber() * 12;
      int tmpnum = 4;
      if (selectedGame == RHYTHMREADING ) {        tmpnum = rhythmLevel.getTimeSignNumerator();      } else if (selectedGame == SCOREREADING ) {        tmpnum = scoreLevel.getTimeSignNumerator();      }

      int scoreLineWidth = keyWidth + alterationWidth + timeSignWidth;
      firstNoteXPos = windowMargin + keyWidth + alterationWidth + timeSignWidth + notesShift;
      numberOfMeasures = (size.width - (windowMargin * 2) - scoreLineWidth) / (tmpnum * noteDistance);
      numberOfRows = (size.height - scoreYpos - 50) / rowsDistance; // 50 = window bottom margin
      int yPos = scoreYpos;
      int vXPos = windowMargin + scoreLineWidth + (tmpnum * noteDistance);

      scoreLineWidth += windowMargin + (numberOfMeasures * (tmpnum * noteDistance));

      for (int r = 0; r < numberOfRows; r++) {
        // draw vertical separators first
        for (int v = 0; v < numberOfMeasures; v++)
          g.drawLine(vXPos + v * (tmpnum * noteDistance), yPos, vXPos + v * (tmpnum * noteDistance), yPos+40);
        // draw the score 5 rows 
        if (selectedGame == SCOREREADING ) {
          for (int l = 0;l < 5;l++,yPos+=10) {
            g.drawLine(windowMargin, yPos, scoreLineWidth, yPos);
          }
        } else if (selectedGame == RHYTHMREADING ) { //only one line
          g.drawLine(windowMargin, yPos+20, scoreLineWidth, yPos+20);
          yPos += (rowsDistance - 50);
        }
        yPos += (rowsDistance - 50);
      }
    }

    // NOTE

    boolean isSameNote(int p1, int p2) {
      // compare two pitch when using computer keyboard
      return p1+(((Number) ui.transpositionSpinner.getValue()).intValue()) == p2;
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
          h = (scoreYpos+noteLevel.getBasetreble())-(i*5); // 20 for trebble key
        } else {
          h = (scoreYpos+noteLevel.getBasebass())-(i*5); // 4 far bass key
        }
      } else if (noteLevel.isCurrentKeyBoth()) {
      // SECOND CASE double Key        int belowBase;
        if (nbupper2 < 0) {
          belowBase = nbupper2;
        } else {
          belowBase = 0;
        }
        double tmpcle = Math.random();
        if (tmpcle < 0.5) { // treble key
          tmp = Math.random();
          if (tmp < 0.5) {
            i = (int)Math.round((Math.random()*nbupper1));
          } else {        	// between 0 and upper note 
            i = -(int)Math.round((Math.random()*nbunder1));
          }
          // negative number between under note and 0 
          h = scoreYpos+noteLevel.getBasetreble()-(i*5);
        } else {
          tmp = Math.random();
          if (tmp < 0.5) {
            i = (int)Math.round((Math.random()*nbupper2)+belowBase);
          } else {
            i = -(int)Math.round((Math.random()*nbunder2))+belowBase;
          }
          h = scoreYpos+noteLevel.getBasebass()+90-(i*5);
        }
      }
      return h;
    }

    void rhythmKeyReleased(int pitch) {
      if (ui.keyboardsoundCheckBox.isSelected()) {
        currentChannel.stopnote(true,pitch);
      }

      float rhythmCursorXposcorrected;
      if (cursorstart) {
        rhythmCursorXposcorrected = rhythmCursorXStartPos + ((System.currentTimeMillis()-timestart-latency)*noteDistance)/(60000/tempo);      } else {
        rhythmCursorXposcorrected = rhythmCursorXpos;      }

      System.out.println ("rhythmCursorXpos" + rhythmCursorXposcorrected);
      if (cursorstart) {
        // key should be released at the end of the rhythm
        if ((rhythmIndex >= 0) && (rhythmIndex < rhythms.size()) 
            && (!rhythms.get(rhythmIndex).isSilence()) && (rhythms.get(rhythmIndex).duration != 0)
            && ((int)rhythmCursorXposcorrected < rhythms.get(rhythmIndex).getPosition() + 8/rhythms.get(rhythmIndex).duration * 27 - precision) 
            && ((int)rhythmCursorXposcorrected > rhythms.get(rhythmIndex).getPosition() + precision)) {
          answers.add(new RhythmAnswer((int)rhythmCursorXposcorrected, rhythmAnswerScoreYpos -15 , true, 2 ));
        }
        //key should be released just before a silent  
        if ((rhythmIndex >= 0) && (rhythms.get(rhythmIndex).isSilence()) 
            && (rhythmIndex-1 >= 0)
            && (!rhythms.get(rhythmIndex-1).isSilence())	
            && ((int)rhythmCursorXposcorrected > rhythms.get(rhythmIndex).getPosition() + precision) 
            && ((int)rhythmCursorXposcorrected < rhythms.get(rhythmIndex).getPosition() + 8/rhythms.get(rhythmIndex).duration * 27 - precision)) {
          answers.add(new RhythmAnswer((int)rhythmCursorXposcorrected, rhythmAnswerScoreYpos -15 , true, 2 ));
        }
      }
    }

    void rhythmKeyPressed(int pitch) {
      int result = 0;
      boolean goodnote = false;

      if (ui.keyboardsoundCheckBox.isSelected()){
        //currentChannel.stopnotes();
        currentChannel.playNote(true, pitch, 2000);
      }
      //  System.out.println("time sound" + System.currentTimeMillis());
      float rhythmCursorXposcorrected;

      if (cursorstart) {
        rhythmCursorXposcorrected = rhythmCursorXStartPos + ((System.currentTimeMillis()-timestart-latency)*noteDistance)/(60000/tempo);      } else {
        rhythmCursorXposcorrected = rhythmCursorXpos;      }

      System.out.println ("rhythmCursorXpos" + rhythmCursorXposcorrected);

      if (((rhythmIndex >= 0)            && ((int) rhythmCursorXposcorrected < rhythms.get(rhythmIndex).getPosition() + precision) 
           && ((int) rhythmCursorXposcorrected > rhythms.get(rhythmIndex).getPosition() - precision)            && !rhythms.get(rhythmIndex).isSilence())) {
        if (pitch == rhythms.get(rhythmIndex).getPitch()) {
          result = 0;
          goodnote = true;
        } else {
          result = 0;
          goodnote = false;
        }
      //to resolve problem with eight on fast tempo 
      } else if (((rhythmIndex-1 >= 0)     		      && ((int) rhythmCursorXposcorrected < rhythms.get(rhythmIndex-1).getPosition() + precision) 
                  && ((int) rhythmCursorXposcorrected > rhythms.get(rhythmIndex-1).getPosition() - precision)                   && !rhythms.get(rhythmIndex-1).isSilence())) {
        if (pitch == rhythms.get(rhythmIndex).getPitch()) {
          result = 0;
          goodnote = true;
        } else {
          result = 0;
          goodnote = false;
        }      } else {
        if (rhythmIndex >= 0         	&& pitch== rhythms.get(rhythmIndex).getPitch()) {
          result = 1;
          goodnote = true;
        } else {
          result = 1;
          goodnote = false;
        }      }
      answers.add(new RhythmAnswer((int)rhythmCursorXposcorrected,  rhythmAnswerScoreYpos -15, goodnote, result ));
    }

    private Interval intervalchoice() {
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
        while (h == precedente) {
          h = setNoteHeight(13-i, 5, 6-i, 10);
        }
      } else {
        h = setNoteHeight(13-i, 8, 13-i, 8);
        while (h == precedente) {
          h = setNoteHeight(13-i, 8, 13-i, 8);
        }
      }

      Note n1 = new Note(h, noteMargin+98, 0);
      n1.updateNote(noteLevel, scoreYpos, ui.bundle);
      n1.updateAccidental(noteLevel, ui.bundle);

      Note n2 = new Note(h-i*5, noteMargin+98, 0);
      n2.updateNote(noteLevel, scoreYpos, ui.bundle);
      n2.updateAccidental(noteLevel, ui.bundle);

      String name = "";
      if (n2.getPitch()-n1.getPitch() == 0 && i==1) {
        name = ui.bundle.getString("_seconddim");
      } else if (n2.getPitch()-n1.getPitch() == 1 && i==1) {
        name = ui.bundle.getString("_secondmin");
      } else if (n2.getPitch()-n1.getPitch() == 2 && i==1) {
        name = ui.bundle.getString("_secondmaj");
      } else if (n2.getPitch()-n1.getPitch() == 3 && i==1) {
        name = ui.bundle.getString("_secondaug");
      } else if (n2.getPitch()-n1.getPitch() == 2 && i==2) {
        name = ui.bundle.getString("_thirddim");
      } else if (n2.getPitch()-n1.getPitch() == 3 && i==2) {
        name = ui.bundle.getString("_thirdmin");
      } else if (n2.getPitch()-n1.getPitch() == 4 && i==2) {
        name = ui.bundle.getString("_thirdmaj");
      } else if (n2.getPitch()-n1.getPitch() == 5 && i==2) {
        name = ui.bundle.getString("_thirdaug");
      } else if (n2.getPitch()-n1.getPitch() == 4 && i==3) {
        name = ui.bundle.getString("_fourthdim");
      } else if (n2.getPitch()-n1.getPitch() == 5 && i==3) {
        name = ui.bundle.getString("_fourthper");
      } else if (n2.getPitch()-n1.getPitch() == 6 && i==3) {
        name = ui.bundle.getString("_fourthaug");
      } else if (n2.getPitch()-n1.getPitch() == 6 && i==4) {
        name = ui.bundle.getString("_fifthdim");
      } else if (n2.getPitch()-n1.getPitch() == 7 && i==4) {
        name = ui.bundle.getString("_fifthper");
      } else if (n2.getPitch()-n1.getPitch() == 8 && i==4) {
        name = ui.bundle.getString("_fifthaug");
      } else if (n2.getPitch()-n1.getPitch() == 7 && i==5) {
        name = ui.bundle.getString("_sixthdim");
      } else if (n2.getPitch()-n1.getPitch() == 8 && i==5) {
        name = ui.bundle.getString("_sixthmin");
      } else if (n2.getPitch()-n1.getPitch() == 9 && i==5) {
        name = ui.bundle.getString("_sixthmaj");
      } else if (n2.getPitch()-n1.getPitch() == 10 && i==5) {
        name = ui.bundle.getString("_sixthaug");
      } else if (n2.getPitch()-n1.getPitch() == 9 && i==6) {
        name = ui.bundle.getString("_seventhdim");
      } else if (n2.getPitch()-n1.getPitch() == 10 && i==6) {
        name = ui.bundle.getString("_seventhmin");
      } else if (n2.getPitch()-n1.getPitch() == 11 && i==6) {
        name = ui.bundle.getString("_seventhmaj");
      } else if (n2.getPitch()-n1.getPitch() == 12 && i==6) {
        name = ui.bundle.getString("_seventhaug");//inusitï¿½e
      } else if (n2.getPitch()-n1.getPitch() == 11 && i==7) {
        name = ui.bundle.getString("_octavedim");
      } else if (n2.getPitch()-n1.getPitch() == 12 && i==7) {
        name = ui.bundle.getString("_octaveper");
      } else if (n2.getPitch()-n1.getPitch() == 13 && i==7) {
        name = ui.bundle.getString("_octaveaug");
      }

      Interval inter = new Interval(n1, n2, name);
      precedente = n1.getHeight();

      return inter;
    }

    private void newinterval() {
      stopSound();
      icourant.copy(intervalchoice());
      if (noteLevel.isNormalgame() || noteLevel.isLearninggame()) {
        posnote = 0;
        ncourante = icourant.getNote(posnote);
        if (ui.soundOnCheckBox.isSelected()) {
          synthNote(ncourante.getPitch(), 80, noteDuration);
        }
      } else if (noteLevel.isInlinegame()) {
        if (position<line.length-1) {
          position += 1;
          icourant.copy(lineint[position]);

          posnote = 0;
          //acourant.convertir(clecourante, typeaccord);
          ncourante = icourant.getNote(posnote);
          if (ui.soundOnCheckBox.isSelected()) {
            synthNote(ncourante.getPitch(), 80, noteDuration);
          }
        }
      }
    }

    private Chord chordchoice() {
      int h;
      Note n1 = new Note(0, 0, 0);
      Note n2 = new Note(0, 0, 0);
      Note n3 = new Note(0, 0, 0);

      if (noteLevel.isCurrentKeyBoth()) {
        h = setNoteHeight(6, 5, -2, 10);
        while (h == precedente) {
          h = setNoteHeight(6, 5, -2, 10);
        }

      } else {
        h = setNoteHeight(6, 8, 6, 8);
        while (h == precedente) {
          h = setNoteHeight(6, 8, 6, 8);
        }

      }

      String minmaj = "";
      String salt = "";
      boolean ok = false;
      while (!ok) {

        n1 = new Note(h, noteMargin+98, 0);
        n1.updateNote(noteLevel, scoreYpos, ui.bundle);
        n1.updateAccidental(noteLevel, ui.bundle);

        n2 = new Note(h-2*5, noteMargin+98, 0);
        n2.updateNote(noteLevel, scoreYpos, ui.bundle);
        n2.updateAccidentalInChord(noteLevel.getCurrentTonality(), n1.getPitch(), 2, ui.bundle); //deuxieme note

        n3 = new Note(h-4*5, noteMargin+98, 0);
        n3.updateNote(noteLevel, scoreYpos, ui.bundle);
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
        // if (!ok) System.out.println("faux"+n1.getNom() +salt);
      }
      Chord a = new Chord(n1, n2, n3, n1.getNom()+salt+" "+minmaj, 0);
      precedente = n1.getHeight();
      return a;
    }

    private void resetButtonColor() {
      ColorUIResource def = new ColorUIResource(238, 238, 238);
      ui.bdo.setBackground(def);
      ui.bre.setBackground(def);
      ui.bmi.setBackground(def);
      ui.bfa.setBackground(def);
      ui.bsol.setBackground(def);
      ui.bla.setBackground(def);
      ui.bsi.setBackground(def);
      ui.bdiese.setBackground(def);
      ui.bbemol2.setBackground(def);
    }

    void applyButtonColor() {
      resetButtonColor();

      Color red = new Color(242, 179, 112);
      if (ncourante.getNom().equals(ui.bdo.getText())) {
        ui.bdo.setBackground(red);
      } else if (ncourante.getNom().equals(ui.bre.getText())) {
        ui.bre.setBackground(red);
      } else if (ncourante.getNom().equals(ui.bmi.getText())) {
        ui.bmi.setBackground(red);
      } else if (ncourante.getNom().equals(ui.bfa.getText())) {
        ui.bfa.setBackground(red);
      } else if (ncourante.getNom().equals(ui.bsol.getText())) {
        ui.bsol.setBackground(red);
      } else if (ncourante.getNom().equals(ui.bla.getText())) {
        ui.bla.setBackground(red);
      } else if (ncourante.getNom().equals(ui.bsi.getText())) {
        ui.bsi.setBackground(red);
      }

      if (ncourante.getAlteration().equals(ui.bdiese.getText())) {
        ui.bdiese.setBackground(red);
      } else if (ncourante.getAlteration().equals(ui.bbemol.getText())) {
        ui.bbemol2.setBackground(red);
      }
    }

    void drawChord(Chord a, Graphics g, boolean accordcourant) {
      Dimension d = getSize();

      if (a.getNote(posnote).getX()<d.width-noteMargin &&
          a.getNote(posnote).getX() >= noteMargin+98 && gameStarted) {
        // NOTE DANS LIMITES
        a.paint(posnote, noteLevel, g, MusiSync, accordcourant, this,
            scoreYpos, ui.bundle);
        //g.drawString("Renv" + a.renvst,100,100);
      } else {
        if (noteLevel.isNormalgame()) {
          currentScore.addPoints(-20);

          if (currentScore.isLost()) {
            gameStarted = false;
            ui.startButton.setText(ui.bundle.getString("_start"));
            stopSound();
            showResult();
          }

          if (gameStarted) {            newChord();          }
        } else if (noteLevel.isLearninggame()) {
          newChord();
          resetButtonColor();
        } else if (noteLevel.isInlinegame() &&         		   gameStarted &&        		   noteLevel.isChordsgame() &&         		   lineacc[position].getNote(0).getX()<noteMargin+98) {
          // If the current note (except the last) touch the limit
          currentScore.setPoints(0);
          currentScore.setLost();
          gameStarted = false;
          ui.startButton.setText(ui.bundle.getString("_start"));
          stopSound();
          showResult();
        }
      }
    }

    private void synthNote(int nNoteNumber, int nVelocity, int nDuration) {
      currentChannel.playNote(!midierror, nNoteNumber);
    }

    private void newnote() {
      if ((noteLevel.isNormalgame() || noteLevel.isLearninggame()) & gameStarted) {
        notecounter++;
        if (precedente != 0 & ui.soundOnCheckBox.isSelected()) {
          stopSound();
        }
        ncourante.init();

        if (noteLevel.isNotesgame() || noteLevel.isAccidentalsgame()) {
          //choosing note with height to do change to choose note with pitch
          ncourante.setHeight(setNoteHeight(noteLevel.getNbnotesupper(),        				                    noteLevel.getNbnotesunder(),        				                    noteLevel.getNbnotesupper(),        				                    noteLevel.getNbnotesunder()));
          while (ncourante.getHeight() == precedente) {
            ncourante.setHeight(setNoteHeight(noteLevel.getNbnotesupper(),            		                          noteLevel.getNbnotesunder(),            		                          noteLevel.getNbnotesupper(),            		                          noteLevel.getNbnotesunder()));
          }
          ncourante.updateNote(noteLevel, scoreYpos, ui.bundle);
          ncourante.updateAccidental(noteLevel, ui.bundle);
          precedente = ncourante.getHeight();
        } else if (noteLevel.isCustomNotesgame()) {
          // choosing note with pitch

          ncourante.setPitch(noteLevel.getRandomPitch());
          ncourante.updateNotePitch(noteLevel, scoreYpos, ui.bundle);
          precedente = ncourante.getHeight();
        }

        ncourante.setX(noteMargin+98);
        System.out.println(ncourante.getNom());
        System.out.println(ncourante.getHeight());
        System.out.println(ncourante.getPitch());
        //if (soundOnCheckBox.isSelected()) sons[indiceson(ncourante.getHeight())].play();

        if (ui.soundOnCheckBox.isSelected()) {
          synthNote(ncourante.getPitch(), 80, noteDuration);
        }
      } else if (noteLevel.isInlinegame()) {
        //sons[indiceson(ncourante.getHeight())].stop();
        if (precedente != 0 & ui.soundOnCheckBox.isSelected()) {
          stopSound();
        }
        if (position<line.length-1) {
          position += 1;
          ncourante.copy(line[position]);
        }
        //if (soundOnCheckBox.isSelected()) sons[indiceson(ncourante.getHeight())].play();
        if (ui.soundOnCheckBox.isSelected()) {
          synthNote(ncourante.getPitch(), 80, noteDuration);
        }
      }
    }

    private void stopSound() {
      currentChannel.stopnotes();
    }

    void drawNote(Note note, Graphics g, Font f, Color couleur) {
      Dimension size = getSize();

      g.setColor(couleur);
      if (note.getX()<size.width-noteMargin && note.getX() >= noteMargin+98 && gameStarted) { // NOTE DANS LIMITES
        if (noteLevel.isAccidentalsgame() || noteLevel.isCustomNotesgame()) {
          note.paint(noteLevel, g, f, 9, 0, scoreYpos, this, couleur, ui.bundle);
        } else {
          note.paint(noteLevel, g, f, 0, 0, scoreYpos, this, couleur, ui.bundle);
        }
      } else {
        if (noteLevel.isNormalgame()) {
          currentScore.addPoints(-20);
          if (currentScore.isLost()) {
            gameStarted = false;
            ui.startButton.setText(ui.bundle.getString("_start"));
            showResult();
          }
          newnote();
        } else if (noteLevel.isLearninggame()) {
          newnote();
          resetButtonColor();
        } else if (noteLevel.isInlinegame() && gameStarted) {
          if (line[position].getX() < noteMargin+98) { // Si la note courant (sauf la derniï¿½re)dï¿½passe la limite ici marge +25
            currentScore.setPoints(0);
            currentScore.setLost();
            gameStarted = false;
            ui.startButton.setText(ui.bundle.getString("_start"));
            showResult();
          }
        }
      }
    }

    //################################################################
    // RHYTHMS READING

    private void nextRythm() {
      System.out.println ("rhytm xpos: " + rhythms.get(rhythmIndex).getPosition() + 
          " pitch: " + rhythms.get(rhythmIndex).getPitch() + 
          " index: " + rhythmIndex);

      if (rhythms.get(rhythmIndex).getDuration() != 0) {
        if (rhythmIndex<rhythms.size()-1) {
          rhythmIndex++;
          repaint();
          /* if (soundOnCheckBox.getState() & !ligne[position].silence) Synthnote(71,80,durationrhythme);*/
        }
      }
    }

    private void createMetronome() {
      final int TEXT = 0x01;
      int nbpulse;

      try {
        //ShortMessage sm = new ShortMessage();
        //sm.setMessage(ShortMessage.PROGRAM_CHANGE, 1, 115, 0);
        //metronome.add(new MidiEvent(sm, 0));

        int tmpnum = 4;        int tmpden = 4;        int tmpdiv = 1;
        if (selectedGame == RHYTHMREADING ) {
          tmpnum = rhythmLevel.getTimeSignNumerator();
          tmpden = rhythmLevel.getTimeSignDenominator();
          tmpdiv = rhythmLevel.getTimeDivision();
        } else if (selectedGame == SCOREREADING ) {
          tmpnum = scoreLevel.getTimeSignNumerator();
          tmpden = scoreLevel.getTimeSignDenominator();
          tmpdiv = scoreLevel.getTimeDivision();
        }

        System.out.println("[createMetronome] timeSignNumerator =  " + tmpnum + ", timeSignDenominator = " + tmpden);

        String textd = "depart";
        addEvent(metronome, TEXT, textd.getBytes(), (int)(tmpnum/tmpdiv)*ppq);

        String textdt = "departthread"; //one beat before rhythms
        addEvent(metronome, TEXT, textdt.getBytes(), (int)((tmpnum/tmpdiv)-1)*ppq);

        if ((selectedGame == RHYTHMREADING && rhythmLevel.getMetronome()) || 
            (selectedGame == SCOREREADING && scoreLevel.getMetronome())) {
          nbpulse = (tmpnum * numberOfMeasures * numberOfRows) + tmpnum;
        } else {          nbpulse = tmpnum; //only few first to indicate pulse        }

        nbpulse /= tmpdiv;

        for (int i = 0; i < nbpulse; i++) {
          ShortMessage mess = new ShortMessage();
          ShortMessage mess2 = new ShortMessage();
          mess.setMessage(ShortMessage.NOTE_ON, 9, 76, 40); // can use 37 as well, but it has reverb

          metronome.add(new MidiEvent(mess, i*ppq));
          mess2.setMessage(ShortMessage.NOTE_OFF, 9, 77, 0);
          metronome.add(new MidiEvent(mess2, (i*ppq)+1));

          if ((selectedGame == RHYTHMREADING && rhythmLevel.getMetronomeBeats() && i > ((tmpnum / tmpdiv) - 1)) 
              || (selectedGame == SCOREREADING && scoreLevel.getMetronomeBeats()) && i > ((tmpnum / tmpdiv) - 1)) {
            //System.out.println("adding metronome beat : "+i + "tmpnum : " + tmpnum + "tmpdiv : "+tmpdiv);
            String textb = "beat";
            addEvent(metronome, TEXT, textb.getBytes(), (int)i*ppq);
          }
        }
      } catch (InvalidMidiDataException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }

    private void addEvent(Track track, int type, byte[] data, long tick) {
      MetaMessage message = new MetaMessage();
      try {
        message.setMessage(type, data, data.length);
        MidiEvent event = new MidiEvent(message, tick);
        track.add(event);
      } catch (InvalidMidiDataException e) {
        e.printStackTrace();
      }
    }

    private static MidiEvent createNoteOnEvent(int nKey, int velocity, long lTick) {
      return createNoteEvent(ShortMessage.NOTE_ON, nKey, velocity, lTick);
    }

    private static MidiEvent createNoteOffEvent(int nKey, long lTick) {
      return createNoteEvent(ShortMessage.NOTE_OFF, nKey, 0, lTick);
    }

    private static MidiEvent createNoteEvent(int nCommand, int nKey, int nVelocity, long lTick) {
      ShortMessage message = new ShortMessage();
      try {
        message.setMessage(nCommand, 0, // always on channel 1
                           nKey, nVelocity);
      } catch (InvalidMidiDataException e) {
        e.printStackTrace();
        System.exit(1);
      }
      return new MidiEvent(message, lTick);
    }

    private int addRhythm(double duration, int pitch, boolean stemup, int currentTick, int row, int newXPos) {
      int tick = currentTick;
      int velocity = 71;
      boolean silence = false;

      final int TEXT = 0x01;
      String text = "off";
      int tmpdiv = 1;
      int tmpnum = 4;
      //int tmpden = 4;

      if (selectedGame == RHYTHMREADING) {
        silence = rhythmLevel.getSilence();
        tmpdiv =  rhythmLevel.getTimeDivision();
        tmpnum =  rhythmLevel.getTimeSignNumerator();
        //tmpden =  rhythmLevel.getTimeSignDenominator();
      } else if (selectedGame == SCOREREADING) {
        silence = scoreLevel.getSilence();
        tmpdiv =  scoreLevel.getTimeDivision();
        tmpnum =  scoreLevel.getTimeSignNumerator();
        //tmpden =  scoreLevel.getTimeSignDenominator();
      }

      if (duration == 0.333) { // do not handle pauses into triplets for now 
        silence = false;      }

      System.out.println("[addRhythm] pitch: " + pitch + "duration: " + duration + "stemup " + stemup);

      double tmpsilence = Math.random();
      if (!silence           || (silence && tmpsilence<0.85)           || (duration == 3 && tmpnum !=3)) {
        rhythms.add(new Rhythm(duration, newXPos, pitch,  row, stemup, false, false, 0));
        track.add(createNoteOnEvent(pitch, velocity, tick));
        mutetrack.add(createNoteOnEvent(pitch, 0, tick));
        tick += (int)((duration*tmpdiv)*ppq);
        addEvent(track, TEXT, text.getBytes(), tick);
        addEvent(mutetrack, TEXT, text.getBytes(), tick);
        track.add(createNoteOffEvent(pitch, tick));
        mutetrack.add(createNoteOffEvent(pitch, tick));
      } else { // silence
        rhythms.add(new Rhythm(duration, newXPos, pitch, row, false, false, true, 0));
        track.add(createNoteOffEvent(pitch, tick));
        mutetrack.add(createNoteOffEvent(pitch, tick));
        tick += (int)((duration*tmpdiv)*ppq);
        addEvent(track, TEXT, text.getBytes(), tick);
        addEvent(mutetrack, TEXT, text.getBytes(), tick);
      }
      return tick;
    }

    private void setTripletValue(int val) {
      rhythms.get(rhythms.size() - 1).setTrpletValue(val);
    }

    private boolean isBeginMeasure(int i) {
      double d = 0;
      int id = 0;
      for (int j = 0; j<i; j++) {
        //   d += 4.0/rhythms.get(j).getDuration();
        d += rhythms.get(j).getDuration();
      }
      id = (int) Math.round(d); // we should round because of 0.33 triplet need to be fixed

      int tmpnum = 4;        if (selectedGame == RHYTHMREADING) {
        tmpnum = rhythmLevel.getTimeSignNumerator();
      } else if (selectedGame == SCOREREADING) {
        tmpnum = scoreLevel.getTimeSignNumerator();
      }

      boolean reponse = false;
      for (int k = 1; k<tmpnum * 2; k++) {
        if (id == tmpnum*k) {
          reponse = true;
        }
      }
      return reponse;
    }

    private void createSequence() {
      repaint();
      int tmpnum = 4;    	  // int tmpden = 4;    	  int tmpdiv = 1;
      int currentTick = 0;
      int rowCount = 0; // measures counter
      double tpsmes = 0; // number of quarters 
      int currentXPos = windowMargin + keyWidth + alterationWidth + timeSignWidth + notesShift;
      int pitch;
      boolean wholeNote = false;      boolean halfNote = false;      boolean dottedhalfNote = false;      boolean quarterNote = false;      boolean eighthNote = false;      boolean triplet = false;
      boolean stemup = true;
      //Dimension size = getSize();

      if (selectedGame == RHYTHMREADING) {
        wholeNote = rhythmLevel.getWholeNote();
        halfNote = rhythmLevel.getHalfNote();
        dottedhalfNote = rhythmLevel.getDottedHalfNote();
        quarterNote = rhythmLevel.getQuarterNote();
        eighthNote = rhythmLevel.getEighthNote();
        triplet = rhythmLevel.getTriplet();
        tmpnum = rhythmLevel.getTimeSignNumerator();
        //tmpden = rhythmLevel.getTimeSignDenominator();
        tmpdiv = rhythmLevel.getTimeDivision();
      } else if  (selectedGame == SCOREREADING) {
        wholeNote = scoreLevel.getWholeNote();
        halfNote = scoreLevel.getHalfNote();
        dottedhalfNote = scoreLevel.getDottedHalfNote();
        quarterNote = scoreLevel.getQuarterNote();
        eighthNote = scoreLevel.getEighthNote();
        triplet = scoreLevel.getTriplet();
        tmpnum = scoreLevel.getTimeSignNumerator();
        //tmpden = scoreLevel.getTimeSignDenominator();
        tmpdiv = scoreLevel.getTimeDivision();
      }

      currentTick = (int)((tmpnum/tmpdiv)*ppq);

      // INITIALIZE Sequence and tracks
      try {
        sequence = new Sequence(Sequence.PPQ, ppq);
      } catch (InvalidMidiDataException e) {
        e.printStackTrace();
        System.exit(1);
      }

      mutetrack = sequence.createTrack();
      track = sequence.createTrack();
      metronome = sequence.createTrack();

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

      if (selectedGame == SCOREREADING && !scoreLevel.isCustomNotes()) {
        updateTonality(); //when selected random tonality       
        scoreLevel.initPitcheslist( scoreLevel.getNbnotes());
      }

      for (int r = 1; r <= (numberOfMeasures * numberOfRows); r++) { // creates all the measures
        while (tpsmes != tmpnum) {
          //System.out.println("tpsmes : " + tpsmes);
          double tmp = Math.random();
          if (selectedGame == RHYTHMREADING) {
            pitch = 71;
            stemup = true;
          } else {
            pitch = scoreLevel.getRandomPitch();
            if (scoreLevel.isCurrentKeyTreble() && pitch >= 71) stemup= false; //SI
            else if (scoreLevel.isCurrentKeyTreble() && pitch < 71) stemup = true;
            else if (scoreLevel.isCurrentKeyBass() && pitch >= 50) stemup= false; //RE
            else if (scoreLevel.isCurrentKeyBass() && pitch < 50) stemup = true;
            // it will be better to use noteY than pitch
          }

          if (wholeNote && tpsmes+4 <= tmpnum && tmp<0.2) { // ronde, whole
            tpsmes += 4;
            currentTick = addRhythm(4, pitch, stemup, currentTick, rowCount, currentXPos);
            currentXPos += (noteDistance*4);
          } else if (dottedhalfNote && tpsmes + 3 <= tmpnum && tmp < 0.4) { // blanche pointee, dotted half
            tpsmes += 3;
            currentTick = addRhythm(3, pitch, stemup, currentTick, rowCount, currentXPos);
            currentXPos += (noteDistance*3);
          } else if (halfNote && tpsmes + 2 <= tmpnum && tmp < 0.4) { // blanche, half
            tpsmes += 2;
            currentTick = addRhythm(2, pitch, stemup, currentTick, rowCount, currentXPos);
            currentXPos += (noteDistance*2);
          } else if (quarterNote && tpsmes + 1 <= tmpnum && tmp < 0.6) { // noire, quarter
            tpsmes += 1;
            currentTick = addRhythm(1, pitch, stemup, currentTick, rowCount, currentXPos);
            currentXPos += noteDistance;
          } else if (eighthNote && tpsmes + 0.5 <= tmpnum && tmp < 0.8) { // croche, eighth
            tpsmes += 0.5;
            currentTick = addRhythm(0.5, pitch, stemup, currentTick, rowCount, currentXPos);
            currentXPos += (noteDistance/2);
          } else if (triplet && tpsmes+1 <= tmpnum && tmp<0.9) { // triplet
            int[] tripletPitches = { pitch, 71, 71 };
            int lowestPitch = tripletPitches[0];
            if (selectedGame == SCOREREADING) {
              tripletPitches[1] = scoreLevel.tripletRandomPitch(tripletPitches[0]);
              tripletPitches[2] = scoreLevel.tripletRandomPitch(tripletPitches[0]);
            }
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
            currentXPos += (noteDistance/3);
            currentTick = addRhythm(0.333, tripletPitches[1], stemup, currentTick, rowCount, currentXPos);
            setTripletValue(100 + lowestPitch);
            currentXPos += (noteDistance/3);
            currentTick = addRhythm(0.333, tripletPitches[2], stemup, currentTick, rowCount, currentXPos);
            setTripletValue(100 + lowestPitch);
            tpsmes += 1;
            currentXPos += (noteDistance/3);
          }
        }

        tpsmes = 0;
        if ((r%numberOfMeasures) == 0) {
          currentXPos = windowMargin + keyWidth + alterationWidth + timeSignWidth + notesShift;
          rowCount++;
        }
        /*
           } else {
           rhythms.add(new Rhythm(0, 0, 0, 71, false, false, 0));
           }
           */
    }

    if (selectedGame == RHYTHMREADING) {    	regroupenotes(); //not workin with Scorereading yet    }
  }

  private void regroupenotes() {
    for (int i = 0; i<rhythms.size()-1; i++) {
      if (rhythms.get(i).getDuration() == 0.5 && rhythms.get(i+1).getDuration()==0.5 &&  //TO BE FIX  FOR 8
          !rhythms.get(i+1).isSilence() && !rhythms.get(i).isSilence() &&
          !isBeginMeasure(i+1)  && !rhythms.get(i).isGroupee()) {
        rhythms.get(i).setGroupee(1);
        rhythms.get(i+1).setGroupee(2);
      }
    }
  }

  // LINES OF NOTES

  private void createLine() {
    Dimension size = getSize();
    Chord a = new Chord(ncourante, ncourante, ncourante, "", 0);
    Interval inter = new Interval(ncourante, ncourante, "");

    // System.out.println(type2);

    if (noteLevel.isNotesgame() || noteLevel.isAccidentalsgame()) {
      line[0] = new Note(setNoteHeight(noteLevel.getNbnotesupper(), noteLevel.getNbnotesunder(),    		             noteLevel.getNbnotesupper(), noteLevel.getNbnotesunder()), size.width-noteMargin, 0);
      line[0].updateNote(noteLevel, scoreYpos, ui.bundle);
      line[0].updateAccidental(noteLevel, ui.bundle);

      for (int i = 1; i<line.length; i++) {
        int tmph = setNoteHeight(noteLevel.getNbnotesupper(), noteLevel.getNbnotesunder(),        		                 noteLevel.getNbnotesupper(), noteLevel.getNbnotesunder());
        while (tmph == line[i-1].getHeight()) {
          tmph = setNoteHeight(noteLevel.getNbnotesupper(), noteLevel.getNbnotesunder(), noteLevel.getNbnotesupper(), noteLevel.getNbnotesunder()); // pour �viter les r�p�titions
        }

        line[i] = new Note(tmph, size.width-noteMargin+i*35, 0);
        line[i].updateNote(noteLevel, scoreYpos, ui.bundle);
        line[i].updateAccidental(noteLevel, ui.bundle);
      }
    } else if (noteLevel.isCustomNotesgame()) {
      line[0] = new Note(0, size.width-noteMargin, noteLevel.getRandomPitch() );
      line[0].updateNotePitch(noteLevel, scoreYpos, ui.bundle);

      for (int i = 1; i<line.length; i++) {
        int tmpp = noteLevel.getRandomPitch();
        while (tmpp == line[i-1].getPitch()) {
          tmpp = noteLevel.getRandomPitch(); // to avoid same pitch
        }

        line[i] = new Note(0, size.width-noteMargin+i*35, tmpp);
        line[i].updateNotePitch(noteLevel, scoreYpos, ui.bundle);
      }
    }

    position = 0;
    ncourante = line[position]; // initialisa tion avec la premiï¿½re note
    //if (soundOnCheckBox.isSelected()) sons[indiceson(ncourante.getHeight())].play(); // dï¿½part du son de la premiï¿½re note
    if (ui.soundOnCheckBox.isSelected()) {
      synthNote(ncourante.getPitch(), 80, noteDuration);
    } else if (noteLevel.isChordsgame()) {
      // voir pour precedant
      for (int i = 0; i<line.length; i++) {
        a.copy(chordchoice());
        a.updatex(size.width-noteMargin+i*50);
        lineacc[i] = new Chord(a.getNote(0), a.getNote(1), a.getNote(2),
            a.getName(), a.getInversion());
        lineacc[i].convert(noteLevel);
      }
      position = 0;
      posnote = 0;
      acourant.copy(lineacc[position]);
      // acourant.convertir(clecourante,typeaccord);
      ncourante = acourant.getNote(acourant.realposition(posnote));
      if (ui.soundOnCheckBox.isSelected()) {
        synthNote(ncourante.getPitch(), 80, noteDuration);
      }
    } else if (noteLevel.isIntervalsgame()) {
      // voir pour precedant
      for (int i = 0; i<line.length; i++) {
        inter.copy(intervalchoice());
        //i = nouvelintervalle();
        inter.updatex(size.width-noteMargin+i*65);
        lineint[i] = new Interval(            inter.getNote(0), inter.getNote(1), inter.getName());
      }
      position = 0;
      posnote = 0;

      icourant.copy(lineint[position]);
      ncourante = icourant.getNote(posnote); //0
      if (ui.soundOnCheckBox.isSelected()) {
        synthNote(ncourante.getPitch(), 80, noteDuration);
      }
    }
  }

  void drawInlineNotes(Graphics g, Font f) {
    for (int i = position; i<line.length; i++) {
      // n'affiche que la ligne ï¿½ partir de la position
      if (noteLevel.isNotesgame() || noteLevel.isAccidentalsgame() || noteLevel.isCustomNotesgame()) {
        drawNote(line[i], g, f, Color.black);
      } else if (noteLevel.isChordsgame()) {
        drawChord(lineacc[i], g, i == position);
      } else if (noteLevel.isIntervalsgame()) {
        drawInterval(lineint[i], g, i == position);
      }
    }
  }

  void drawNotesAndAnswers(Graphics g, Font f) {

    // paint answers: red = wrong, green = good
    for (int i = 0; i<answers.size(); i++) {
      if (!answers.get(i).isnull()) answers.get(i).paint(g);
    }

    for (int i = 0; i < rhythms.size(); i++) {
      // System.out.println(i);
      if (rhythms.get(i).getDuration() != 0) {
        if ((rhythmgame == 0) && (i!=rhythmIndex) || (muterhythms)) { //only paint note in learning mode
          rhythms.get(i).paint(g, selectedGame, f, scoreLevel, 9, rowsDistance, false, scoreYpos, this);
        } else {
          rhythms.get(i).paint(g, selectedGame, f, scoreLevel, 9, rowsDistance, true, scoreYpos, this);
        }
      }
    }
  }

  // CHORDS

  private void newChord() {

    if (noteLevel.isNormalgame() || noteLevel.isLearninggame()) {
      posnote = 0;
      acourant.copy(chordchoice());
      acourant.convert(noteLevel);
      ncourante = acourant.getNote(acourant.realposition(posnote));
      if (ui.soundOnCheckBox.isSelected()) {
        synthNote(ncourante.getPitch(), 80, noteDuration);
      }
    } else if (noteLevel.isInlinegame()) {
      if (position<line.length-1) {
        position += 1;
        acourant.copy(lineacc[position]);

        posnote = 0;
        //acourant.convertir(clecourante,typeaccord);
        ncourante = acourant.getNote(acourant.realposition(posnote));
        if (ui.soundOnCheckBox.isSelected()) {
          synthNote(ncourante.getPitch(), 80, noteDuration);
        }
      }
    }
  }

  void drawInterval(Interval inter, Graphics g, boolean Intervallecourant) {
    Dimension size = getSize();

    if (inter.getNote(posnote).getX()<size.width-noteMargin &&
        inter.getNote(posnote).getX() >= noteMargin+98 && gameStarted) {
      // NOTE DANS LIMITES
      inter.paint(posnote, noteLevel, g, MusiSync, scoreYpos,
          ui.bundle, Intervallecourant, this);
      //g.drawString("Renv" + a.renvst,100,100);
    } else {
      if (noteLevel.isNormalgame()) {
        currentScore.addPoints(-20);
        if (currentScore.isLost()) {
          gameStarted = false;
          ui.startButton.setText(ui.bundle.getString("_start"));
          stopSound();
          showResult();
        }

        if (gameStarted) {          newinterval();        }
      } else if (noteLevel.isLearninggame()) {
        newinterval();
        resetButtonColor();
      } else if (noteLevel.isInlinegame()     		     && gameStarted     		     && lineint[position].getNote(0).getX() < noteMargin+98) {
        // Si la note courant dï¿½passe la limite ici marge +25
        currentScore.setPoints(0);
        currentScore.setLost();
        gameStarted = false;
        ui.startButton.setText(ui.bundle.getString("_start"));
        stopSound();
        showResult();
      }
    }
  }

  private void nextnote() {

    if (noteLevel.isChordsgame()) {
      if (posnote < 2) {
        posnote += 1;

        ncourante = acourant.getNote(acourant.realposition(posnote));
        alterationOk = false;
        if (ui.soundOnCheckBox.isSelected()) {
          synthNote(ncourante.getPitch(), 80, noteDuration);
        }
      } else {
        if (isLessonMode && notecounter == noteLevel.getLearningduration()) {
          gameStarted = false;
          ui.startButton.setText(ui.bundle.getString("_start"));
          nextLevel();
        } else {
          newChord();
          notecounter++;
        }
      }
    } else if (noteLevel.isIntervalsgame()) {
      if (posnote == 0) {
        posnote += 1;
        ncourante = icourant.getNote(posnote);
        alterationOk = false;
        if (ui.soundOnCheckBox.isSelected()) {
          synthNote(ncourante.getPitch(), 80, noteDuration);
        }
      } else {
        if (isLessonMode && notecounter == noteLevel.getLearningduration()) {
          gameStarted = false;
          ui.startButton.setText(ui.bundle.getString("_start"));
          nextLevel();
        } else {
          newinterval();
          notecounter++;
        }
      }
    }
  }

  // SCORE

  void showResult() {
    if (selectedGame == NOTEREADING) {
      if (currentScore.isWin()) {
        ui.scoreMessage.setTitle(ui.bundle.getString("_congratulations"));

        ui.textscoreMessage.setText("  "+currentScore.getNbtrue()+" "+ui.bundle.getString("_correct")+
            " / "+currentScore.getNbfalse()+" "+
            ui.bundle.getString("_wrong")+"  ");
        ui.scoreMessage.pack();
        ui.scoreMessage.setLocationRelativeTo(this);

        ui.scoreMessage.setVisible(true);

        stopNoteGame();
      } else if (currentScore.isLost()) {
        ui.scoreMessage.setTitle(ui.bundle.getString("_sorry"));

        ui.textscoreMessage.setText("  "+currentScore.getNbtrue()+" "+ui.bundle.getString("_correct")+
            " / "+currentScore.getNbfalse()+" "+
            ui.bundle.getString("_wrong")+"  ");
        ui.scoreMessage.pack();
        ui.scoreMessage.setLocationRelativeTo(this);
        ui.scoreMessage.setVisible(true);

        stopNoteGame();
      }
    } else if (selectedGame == RHYTHMREADING || selectedGame==SCOREREADING ) {

      int nbgood = 0;
      int nbnotefalse = 0;
      int nbrhythmfalse = 0;
      int nbrhythms = 0;

      for (int i = 0; i<answers.size(); i++) {
        if (answers.get(i).allgood() && !answers.get(i).isnull()) nbgood = nbgood +1;
        if (!answers.get(i).isnull() && answers.get(i).badnote()) nbnotefalse = nbnotefalse +1;
        if (!answers.get(i).isnull() && answers.get(i).badrhythm() ) nbrhythmfalse = nbrhythmfalse +1;
      }

      //Nb rhythms
      for (int i = 0; i<rhythms.size(); i++) {
        if (!rhythms.get(i).isSilence() && !rhythms.get(i).isnull()) nbrhythms =  nbrhythms +1;
      }
      if (nbrhythms ==  nbgood) {        ui.scoreMessage.setTitle(ui.bundle.getString("_congratulations"));      } else {        ui.scoreMessage.setTitle(ui.bundle.getString("_sorry"));      }

      ui.textscoreMessage.setText("  " + nbrhythms + " " + ui.bundle.getString("_menuRythms") +     		                      " : " + nbgood + " " + ui.bundle.getString("_correct") +
                                  " / " + nbnotefalse + " " + ui.bundle.getString("_wrong") +                                  "  " + nbrhythmfalse + " " + ui.bundle.getString("_wrongrhythm") + "  ");
      ui.scoreMessage.pack();
      ui.scoreMessage.setLocationRelativeTo(this);
      ui.scoreMessage.setVisible(true);
    }
  }

  public static void main(String[] arg) {
    // Event pour la gestion des Evenements et principalement le message EXIT
    // Constructions de la frame
    Dimension dim = new Dimension(790, 590);

    Jalmus jalmus = new Jalmus();
    // Initialization
    if (arg.length == 0) {
      jalmus.init("");
    } else {
      jalmus.init(arg[0]);
    }

    // Force the window size
    jalmus.setSize(800, 600);
    jalmus.setMinimumSize(dim);

    // Draw
    jalmus.repaint();

    jalmus.setVisible(true);
    jalmus.setFocusable(true);

    //jalmus.setResizable(false);

    jalmus.setTitle("Jalmus"); // Give the application a title

    jalmus.setLocationRelativeTo(null); // Center the window on the display
    jalmus.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit when frame closed
  }
}

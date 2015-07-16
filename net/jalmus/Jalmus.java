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

import java.awt.Color;import java.awt.Dimension;import java.awt.Font;import java.awt.Graphics;import java.awt.Image;import java.io.File;import java.io.FileInputStream;import java.io.IOException;import java.io.InputStream;import java.util.ArrayList;import java.util.List;import java.util.Locale;import javax.imageio.ImageIO;import javax.sound.midi.Instrument;import javax.sound.midi.InvalidMidiDataException;import javax.sound.midi.MetaEventListener;import javax.sound.midi.MetaMessage;import javax.sound.midi.MidiChannel;import javax.sound.midi.MidiDevice;import javax.sound.midi.MidiEvent;import javax.sound.midi.MidiSystem;import javax.sound.midi.MidiUnavailableException;import javax.sound.midi.Receiver;import javax.sound.midi.Sequence;import javax.sound.midi.Sequencer;import javax.sound.midi.ShortMessage;import javax.sound.midi.Soundbank;import javax.sound.midi.Synthesizer;import javax.sound.midi.Track;import javax.sound.midi.Transmitter;import javax.swing.JOptionPane;import javax.swing.plaf.ColorUIResource;import javax.xml.parsers.ParserConfigurationException;import javax.xml.parsers.SAXParser;import javax.xml.parsers.SAXParserFactory;import org.xml.sax.SAXException;

public class Jalmus {

  //----------------------------------------------------------------
  // Translation variables

  String language = "en";

  Font musiSync; // font used to render scores 

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

  MidiDevice inputDevice;
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
  Image jbackground;    // TODO: Remove all references to UI  JalmusUi ui;

  Note currentNote = new Note(0, 25, 0);
  Chord currentChord = new Chord(currentNote, currentNote, currentNote, "", 0);
  Interval currentInterval = new Interval(currentNote, currentNote, "");

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

  int rowsDistance = 100; // distance in pixel between staff rows
  int numberOfMeasures = 2; // number of measures in a single row
  int numberOfRows = 4; // number of score rows
  List<Game> games = new ArrayList<>();

  private int posnote = 1; // current position of the note within a chor or an interval

  boolean alterationOk;

  Score currentScore = new Score();

  // Learning Game

  private int notecounter = 1;

  // Line Game
  Note[] line = new Note[40]; // array of notes
  Chord[] lineacc = new Chord[40]; // array of chords
  Interval[] lineint = new Interval[40];
  private int position; // position of the current note in the list
  private int prevNote; // position of the previous note to avoid repetitions

  boolean gameStarted; // whether the game has started or not.
  boolean paused;

  boolean midierror;

  //----------------------------------------------------------------
  // Rhythm reading variables

  RhythmLevel rhythmLevel = new RhythmLevel();

  private ArrayList<Rhythm> rhythms = new ArrayList<Rhythm>(); 
  private int rhythmIndex = -1; // index of the current note in the list
  private ArrayList<RhythmAnswer> answers = new ArrayList<RhythmAnswer>();
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

  private int[] sauvmidi = new int[16]; // for save midi options when cancel
  NoteReadingGame game;    public Jalmus(NoteReadingGame game) {    this.game = game;  }  public void setUi(JalmusUi ui) {    this.ui = ui;  }  
  //################################################################
  // Initialization methods

  void init(String paramlanguage) {
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
      musiSync = Font.createFont (Font.PLAIN, fInput);
    } catch (Exception e) {
      System.out.println("Cannot load MusiSync font !!");
      System.exit(1);
    }

    System.out.println(new Locale(language));

    piano = new Piano(73, 40);

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
  
  private void updateTonality() {
    String stmp;

    if ((selectedGame == NOTEREADING && game.noteLevel.getRandomtonality())
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

      if (selectedGame == NOTEREADING) {        game.noteLevel.getCurrentTonality().init(i, stmp);      } else if (selectedGame == SCOREREADING) {        scoreLevel.getCurrentTonality().init(i, stmp);      }
    } else if (!isLessonMode && game.noteLevel.getCurrentTonality().getAlterationsNumber() == 0) {
        // Do Major when tonality is no sharp no flat
        double tmp = Math.random();
        if (tmp<0.5) {
          stmp = "#";
        } else {
          stmp = "b";
        }
        game.noteLevel.getCurrentTonality().init(0, stmp);
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
    ui.scoreYpos = 110;
    ui.rhythmCursorXpos = ui.firstNoteXPos - (ui.noteDistance * tmpdiv);
    ui.rhythmCursorXStartPos = ui.firstNoteXPos - (ui.noteDistance * tmpdiv);
    ui.rhythmAnswerScoreYpos = 100;
    cursorstart = false;
    metronomeCount = 0;
    metronomeYPos = 100;

    if (sm_sequencer != null) {
      sm_sequencer.close();
    }
    //repaint();
  }

  /** Stops all games. */

  void stopGames() {
    if (selectedGame == NOTEREADING) stopNoteGame();
    else stopRhythmGame();
  }

  private void stopNoteGame() {

    gameStarted = false;
    ui.startButton.setText(ui.bundle.getString("_start"));

    currentNote = new Note(0, 25, 0);
    currentChord = new Chord(currentNote, currentNote, currentNote, "", 0);
    currentInterval = new Interval(currentNote, currentNote, "");
    resetButtonColor();

    stopSound();
    //     if (sm_sequencer != null) {
    //          sm_sequencer.stop();
    //    }
  }

  void initRhythmGame() {

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
      ui.rhythmCursorXlimit = ui.firstNoteXPos + (tmpnum * numberOfMeasures * ui.noteDistance);
      cursorstart = true;
      timestart = System.currentTimeMillis();
    } 

    if ("depart".equals(strText)) {
      System.out.println("Game start");
      rhythmIndex = 0;
      ui.repaint();
    } else if ("beat".equals(strText)) {
      // show metronome beats
      //System.out.println("Added metronome beat");
      answers.add(new RhythmAnswer(ui.firstNoteXPos + (metronomeCount%((tmpnum/tmpdiv) * numberOfMeasures)) * (ui.noteDistance * tmpdiv), metronomeYPos - 30, true, 3 ));
      metronomeCount++;
      //System.out.println("Metronome beat: " + metronomeCount + ", metronomeYPos: " + metronomeYPos);
      if (metronomeCount == ((tmpnum/tmpdiv) * numberOfMeasures) && 
          metronomeYPos < ui.scoreYpos + (numberOfRows * rowsDistance)) {
        metronomeYPos += rowsDistance;
        metronomeCount = 0;
      }
    } else {
      nextRhythm();
      ui.repaint();
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
    ui.rhythmCursorXpos = ui.firstNoteXPos - (ui.noteDistance * tmpdiv);

    cursorstart = false;
  }

  void startNoteGame() {
    game.initGame();    // to stop last game
    updateTonality(); //when selected random tonality

    if (game.noteLevel.isNormalgame() || game.noteLevel.isLearninggame()) {
      if (game.noteLevel.isNotesgame() || game.noteLevel.isAccidentalsgame() || game.noteLevel.isCustomNotesgame()) {
        newnote();
      } else if (game.noteLevel.isChordsgame()) {
        newChord();
      } else if (game.noteLevel.isIntervalsgame()) {
        newinterval();
      }
    } else if (game.noteLevel.isInlinegame()) {
      createLine();
    }

    gameStarted = true;        // start of game
    ui.startButton.setText(ui.bundle.getString("_stop"));
  }

  void rightAnswer() {
    if (game.noteLevel.isLearninggame()) {

      if (game.noteLevel.isChordsgame() || game.noteLevel.isIntervalsgame()) {
        /* if (isLessonMode & notecounter < game.noteLevel.getLearningduration()){
           parti = false;
           nextlevel();
        }

        else*/
        nextnote();

      } else if (isLessonMode && notecounter == game.noteLevel.getLearningduration()) {
        gameStarted = false;
        ui.startButton.setText(ui.bundle.getString("_start"));
        nextLevel();
      } else {
        newnote();
      }

      resetButtonColor();
    } else {
      currentScore.addNbtrue(1);

      if (game.noteLevel.isNotesgame() || game.noteLevel.isAccidentalsgame() || game.noteLevel.isCustomNotesgame()) {
        currentScore.addPoints(10);
      } else if (game.noteLevel.isChordsgame() || game.noteLevel.isIntervalsgame()) {
        currentScore.addPoints(5);
      }

      if (currentScore.isWin()) {
        gameStarted = false;
        ui.startButton.setText(ui.bundle.getString("_start"));
        showResult();
      }

      if (game.noteLevel.isInlinegame() && position == line.length-1) { // derniÃ¨re note trouvÃ©e
        currentScore.setWin();
        gameStarted = false;
        ui.startButton.setText(ui.bundle.getString("_start"));
        showResult();
      }
      if (game.noteLevel.isChordsgame() || game.noteLevel.isIntervalsgame()) {
        nextnote();
      } else {
        newnote();
      }
    }
  }

  void startLevel() {
    if (currentlesson.isNoteLevel()) {        
      if (!game.noteLevel.isMessageEmpty()) {
        ui.textlevelMessage.setText("  "+game.noteLevel.getMessage()+"  ");        ui.levelMessage.setTitle(ui.bundle.getString("_information"));
        ui.levelMessage.pack();
        ui.levelMessage.setLocationRelativeTo(ui);
        ui.levelMessage.setVisible(true);
      } else {
        ui.startButton.doClick();
      }        
    } else if (currentlesson.isRhythmLevel()) {        
      if (!rhythmLevel.isMessageEmpty()) {
        ui.textlevelMessage.setText("  "+rhythmLevel.getMessage()+"  ");        ui.levelMessage.setTitle(ui.bundle.getString("_information"));
        ui.levelMessage.pack();
        ui.levelMessage.setLocationRelativeTo(ui);
        ui.levelMessage.setVisible(true);
      } else {
        ui.startButton.doClick();
      }        
    } else if (currentlesson.isScoreLevel()) {        
      if (!scoreLevel.isMessageEmpty()) {
        ui.textlevelMessage.setText("  "+scoreLevel.getMessage()+"  ");        ui.levelMessage.setTitle(ui.bundle.getString("_information"));
        ui.levelMessage.pack();
        ui.levelMessage.setLocationRelativeTo(ui);
        ui.levelMessage.setVisible(true);
      } else {
        ui.startButton.doClick();
      }        
    }
  }

  void nextLevel() {
    if (!currentlesson.lastexercice()) {
      stopNoteGame();
      currentlesson.nextLevel();
      if (currentlesson.isNoteLevel()) { 
        game.noteLevel.copy((NoteLevel)currentlesson.getLevel());
        game.noteLevel.updatenbnotes(piano);

        selectedGame = NOTEREADING ;
        game.initGame();
        ui.changeScreen(isLessonMode, currentlesson, selectedGame);
        game.noteLevel.printtest();

        startLevel();
      } else if (currentlesson.isRhythmLevel()) { 
        rhythmLevel.copy((RhythmLevel)currentlesson.getLevel());

        selectedGame = RHYTHMREADING;
        initRhythmGame();

        ui.changeScreen(isLessonMode, currentlesson, selectedGame);
        rhythmLevel.printtest();
        ui.newButton.doClick();
        startLevel();
      } else if (currentlesson.isScoreLevel()) { 
        scoreLevel.copy((ScoreLevel)currentlesson.getLevel());

        selectedGame = SCOREREADING;
        initRhythmGame();

        ui.changeScreen(isLessonMode, currentlesson, selectedGame);
        scoreLevel.printtest();
        ui.newButton.doClick();
        startLevel();
      }
    } else {
      System.out.println("End level");
      JOptionPane.showMessageDialog(ui, ui.bundle.getString("_lessonfinished"),
          ui.bundle.getString("_congratulations"),
          JOptionPane.INFORMATION_MESSAGE);

      isLessonMode = false;
      stopGames();
      handleNoteReadingMenuItem();

      ui.repaint();
    }
  }

  void wrongAnswer() {
    alterationOk = false;

    if (!game.noteLevel.isLearninggame()) {
      currentScore.addNbfalse(1);
      // if (soundOnCheckBox.getState()) sonerreur.play();

      if (game.noteLevel.isNotesgame() || game.noteLevel.isAccidentalsgame()  || game.noteLevel.isCustomNotesgame()) {
        currentScore.addPoints(-20);
      } else if (game.noteLevel.isChordsgame() || game.noteLevel.isIntervalsgame()) {
        currentScore.addPoints(-10);
      }

      if (currentScore.isLost()) {
        gameStarted = false;
        ui.startButton.setText(ui.bundle.getString("_start"));
        showResult();
      }
    }
  }

  void handleExitMenuItem() {
    stopGames();
    ui.dispose();
  }

  void handleLessonMenuItem(String lesson, Integer i) {
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
        game.noteLevel.copy((NoteLevel)currentlesson.getLevel());
        game.noteLevel.updatenbnotes(piano);

        selectedGame = NOTEREADING;
        game.initGame();

        ui.changeScreen(isLessonMode, currentlesson, selectedGame);
        game.noteLevel.printtest();
        startLevel();
      } else if (currentlesson.isRhythmLevel()) { 
        rhythmLevel.copy((RhythmLevel)currentlesson.getLevel());
        selectedGame = RHYTHMREADING;       
        ui.changeScreen(isLessonMode, currentlesson, selectedGame);
        initRhythmGame();
        rhythmLevel.printtest();
        ui.newButton.doClick();
        startLevel();
      } else if (currentlesson.isScoreLevel()) { 
        scoreLevel.copy((ScoreLevel)currentlesson.getLevel());

        scoreLevel.printtest();
        selectedGame = SCOREREADING;
        ui.changeScreen(isLessonMode, currentlesson, selectedGame);
        initRhythmGame();
        ui.newButton.doClick();
        startLevel();
      }
    } catch (ParserConfigurationException pce) {
      parseError = "Configuration Parser error.";
      JOptionPane.showMessageDialog(ui, parseError, "Warning", JOptionPane.WARNING_MESSAGE);
    } catch (SAXException se) {
      parseError = "Parsing error : "+se.getMessage();
      JOptionPane.showMessageDialog(ui, parseError, "Warning", JOptionPane.WARNING_MESSAGE);
      se.printStackTrace();
    } catch (IOException ioe) {
      parseError = "I/O error : I/O error";
      JOptionPane.showMessageDialog(ui, parseError, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    //  lessonsDialog.setVisible(false);
  }

  void handleRhythmReadingMenuItem() {
    stopGames();
    /*
       if (latencySlider.getValue() == 0)
       JOptionPane.showMessageDialog(ui, ui.bundle.getString("_setlatency"),
       "", JOptionPane.INFORMATION_MESSAGE);
       */
    //   pgamebutton.removeAll();

    ui.gameButtonPanel.add(ui.newButton);
    ui.gameButtonPanel.add(ui.listenButton);
    ui.gameButtonPanel.add(ui.startButton);
    ui.gameButtonPanel.add(ui.preferencesButton);
    ui.scoreYpos = 110;
    ui.repaint();

    selectedGame = RHYTHMREADING;
    ui.newButton.doClick();
    if (isLessonMode) {
      game.noteLevel.init();
    }
    isLessonMode = false;
    ui.changeScreen(isLessonMode, currentlesson, selectedGame);
  }

  void handleScoreReadingMenuItem() {
    stopGames();
    //   ui.pgamebutton.removeAll();

    /*
       if (latencySlider.getValue() == 0)
       JOptionPane.showMessageDialog(ui, ui.bundle.getString("_setlatency"),
       "", JOptionPane.INFORMATION_MESSAGE);
       */
    ui.gameButtonPanel.add(ui.newButton);
    ui.gameButtonPanel.add(ui.listenButton);
    ui.gameButtonPanel.add(ui.startButton);
    ui.gameButtonPanel.add(ui.preferencesButton);
    ui.scoreYpos = 110;
    ui.repaint();

    selectedGame = SCOREREADING;
    ui.newButton.doClick();
    if (isLessonMode) {
      game.noteLevel.init();
    }
    isLessonMode = false;
    ui.changeScreen(isLessonMode, currentlesson, selectedGame);
  }

  void handleNoteReadingMenuItem() {
    stopGames();
    ui.gameButtonPanel.removeAll();
    ui.gameButtonPanel.add(ui.startButton);
    ui.gameButtonPanel.add(ui.noteButtonPanel);
    ui.gameButtonPanel.add(ui.preferencesButton);

    game.initGame();
    if (isLessonMode) {
      game.noteLevel.init();
    }
    selectedGame = NOTEREADING;
    isLessonMode = false;
    ui.changeScreen(isLessonMode, currentlesson, selectedGame);
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
        game.initGame(); //stop the game before restart
      } else {
        ui.requestFocus();
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
    ui.repaint(); //only to paint exercise
    gameStarted = false;
  }

  void handlePreferencesClicked() {
    if (selectedGame == NOTEREADING) {
      ui.preferencesTabbedPane.setSelectedIndex(JalmusUi.NOTE_READING_TAB);
    } else if (selectedGame == RHYTHMREADING) {
      ui.preferencesTabbedPane.setSelectedIndex(JalmusUi.RHYTHM_READING_TAB);     
    }
    else if (selectedGame == SCOREREADING) {
      ui.preferencesTabbedPane.setSelectedIndex(JalmusUi.SCORE_READING_TAB);     
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
    ui.restorePreferences();
    ui.preferencesDialog.setVisible(false);
  }

  void handlePreferencesSaveClicked() {
    ui.saveDialog.setTitle(ui.bundle.getString("_buttonsave"));
    //	ui.saveDialog.setLayout(new GridLayout(3, 1));    
    ui.saveDialog.pack();
    ui.saveDialog.setLocationRelativeTo(ui);
    ui.saveDialog.setVisible(true);
  }

  void handleOKSave() {
    try {
      if (ui.lessonName.getText().length() != 0) {
        if (ui.preferencesTabbedPane.getSelectedIndex() == 0) {          game.noteLevel.save(currentlesson,ui.lessonName.getText()+".xml", ui.lessonMessage.getText(), language);        } else if (ui.preferencesTabbedPane.getSelectedIndex() == 1) {
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
      game.noteLevel.inibasenote();
      game.initGame();
      game.noteLevel.updatenbnotes(piano);
    } else if (selectedGame == RHYTHMREADING) {

      // update parameters for rhythm reading NO MORE NEEDED now on Itemstatechanged
      /*  if (! wholeCheckBox.isSelected() && !halfCheckBox.isSelected() && 
          !quarterCheckBox.isSelected() && !eighthCheckBox.isSelected()) {

          JOptionPane.showMessageDialog(ui,
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

          JOptionPane.showMessageDialog(ui,
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
    ui.changeScreen(isLessonMode, currentlesson, selectedGame);
    ui.preferencesDialog.setVisible(false);
    ui.repaint();
  }
  
  /**
   * Open uri with default browser
   *
   * @param  uristring uri to open
   * @return      void
   *
   */
  public void OpenURI(String uristring) {
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

  public void OpenDirectory(File dir) {
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
  void backupMidiOptions() {
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
    // Translating functions

    // DRAW METHODS

    // KEYS

    void drawKeys(Graphics g) {
      if (selectedGame == NOTEREADING) {
        if (game.noteLevel.isCurrentKeyTreble()) {
          g.setFont(musiSync.deriveFont(70f));
          g.drawString("G", ui.noteMargin, ui.scoreYpos + 42);
        } else if (game.noteLevel.isCurrentKeyBass()) {
          g.setFont(musiSync.deriveFont(60f));
          g.drawString("?", ui.noteMargin, ui.scoreYpos + 40);
        } else if (game.noteLevel.isCurrentKeyBoth()) {
          g.setFont(musiSync.deriveFont(70f));
          g.drawString("G", ui.noteMargin, ui.scoreYpos+42);
          g.setFont(musiSync.deriveFont(60f));
          g.drawString("?", ui.noteMargin, ui.scoreYpos+130);
        }
      } else if (selectedGame == RHYTHMREADING ) {
        for (int rowNum = 0; rowNum < numberOfRows; rowNum++) {
          // g.setFont(MusiSync.deriveFont(70f));
          //g.drawString("G", windowMargin, ui.scoreYpos+42+rowNum*rowsDistance);
        }
      } else if (selectedGame == SCOREREADING ) {
        if (scoreLevel.isCurrentKeyTreble()) {
          for (int rowNum = 0; rowNum < numberOfRows; rowNum++) {
            g.setFont(musiSync.deriveFont(70f));
            g.drawString("G", ui.windowMargin, ui.scoreYpos+42+rowNum*rowsDistance);
          }        } else if (scoreLevel.isCurrentKeyBass()) {
          for (int rowNum = 0; rowNum < numberOfRows; rowNum++) {
            g.setFont(musiSync.deriveFont(60f));
            g.drawString("?", ui.windowMargin, ui.scoreYpos+40+rowNum*rowsDistance);
          }        }
      }
    }

    void drawTimeSignature(Graphics g) {
      g.setFont(musiSync.deriveFont(58f));
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
          t = "P";        }        g.drawString(t, ui.windowMargin + ui.keyWidth + ui.alterationWidth, ui.scoreYpos+41+rowNum*rowsDistance);
      }
    }

    // ROWS

    void drawInlineGame(Graphics g) {
      Dimension size = ui.getSize();
      g.setColor(Color.black);
      int yd;

      for (yd = ui.scoreYpos; yd<=ui.scoreYpos+40; yd+=10) { //  1ere ligne ï¿½ 144;   derniï¿½re ï¿½ 176
        g.drawLine(ui.noteMargin, yd, size.width-ui.noteMargin, yd);
      }

      if (game.noteLevel.isCurrentKeyBoth()) {  // dessine la deuxiï¿½me portï¿½e 72 points en dessous
        for (yd = ui.scoreYpos+90; yd<=ui.scoreYpos+130; yd+=10) {  //  1ere ligne ï¿½ 196;   derniï¿½re ï¿½ 228
          g.drawLine(ui.noteMargin, yd, size.width-ui.noteMargin, yd);
        }
      }
      if (game.noteLevel.isInlinegame()) {
        g.setColor(Color.red);
        g.drawLine(ui.noteMargin+98, ui.scoreYpos-30, ui.noteMargin+98, ui.scoreYpos+70);
        if (game.noteLevel.isCurrentKeyBoth()) {
          g.drawLine(ui.noteMargin+98, ui.scoreYpos+20, ui.noteMargin+98, ui.scoreYpos+160);
        }
        g.setColor(Color.black);
      }
    }

    void drawScore(Graphics g) {
      Dimension size = ui.getSize();
      g.setColor(Color.black);
      ui.alterationWidth = scoreLevel.getCurrentTonality().getAlterationsNumber() * 12;
      int tmpnum = 4;
      if (selectedGame == RHYTHMREADING ) {        tmpnum = rhythmLevel.getTimeSignNumerator();      } else if (selectedGame == SCOREREADING ) {        tmpnum = scoreLevel.getTimeSignNumerator();      }

      int scoreLineWidth = ui.keyWidth + ui.alterationWidth + ui.timeSignWidth;
      ui.firstNoteXPos = ui.windowMargin + ui.keyWidth + ui.alterationWidth + ui.timeSignWidth + ui.notesShift;
      numberOfMeasures = (size.width - (ui.windowMargin * 2) - scoreLineWidth) / (tmpnum * ui.noteDistance);
      numberOfRows = (size.height - ui.scoreYpos - 50) / rowsDistance; // 50 = window bottom margin
      int yPos = ui.scoreYpos;
      int vXPos = ui.windowMargin + scoreLineWidth + (tmpnum * ui.noteDistance);

      scoreLineWidth += ui.windowMargin + (numberOfMeasures * (tmpnum * ui.noteDistance));

      for (int r = 0; r < numberOfRows; r++) {
        // draw vertical separators first
        for (int v = 0; v < numberOfMeasures; v++)
          g.drawLine(vXPos + v * (tmpnum * ui.noteDistance), yPos, vXPos + v * (tmpnum * ui.noteDistance), yPos+40);
        // draw the score 5 rows 
        if (selectedGame == SCOREREADING ) {
          for (int l = 0;l < 5;l++,yPos+=10) {
            g.drawLine(ui.windowMargin, yPos, scoreLineWidth, yPos);
          }
        } else if (selectedGame == RHYTHMREADING ) { //only one line
          g.drawLine(ui.windowMargin, yPos+20, scoreLineWidth, yPos+20);
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

      if (game.noteLevel.isCurrentKeyTreble() || game.noteLevel.isCurrentKeyBass()) {
        tmp = Math.random();
        if (tmp < 0.5) {
          i = (int) Math.round((Math.random()*nbupper1));
        } else {
          i = -(int) Math.round((Math.random()*nbunder1));
        }
        // negative number between under note and 0 
        if (game.noteLevel.isCurrentKeyTreble()) {
          h = (ui.scoreYpos+game.noteLevel.getBasetreble())-(i*5); // 20 for trebble key
        } else {
          h = (ui.scoreYpos+game.noteLevel.getBasebass())-(i*5); // 4 far bass key
        }
      } else if (game.noteLevel.isCurrentKeyBoth()) {
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
          h = ui.scoreYpos+game.noteLevel.getBasetreble()-(i*5);
        } else {
          tmp = Math.random();
          if (tmp < 0.5) {
            i = (int)Math.round((Math.random()*nbupper2)+belowBase);
          } else {
            i = -(int)Math.round((Math.random()*nbunder2))+belowBase;
          }
          h = ui.scoreYpos+game.noteLevel.getBasebass()+90-(i*5);
        }
      }
      return h;
    }

    void rhythmKeyReleased(int pitch) {
      if (ui.keyboardsoundCheckBox.isSelected()) {
        currentChannel.stopNote(true,pitch);
      }

      float rhythmCursorXposcorrected;
      if (cursorstart) {
        rhythmCursorXposcorrected = ui.rhythmCursorXStartPos + ((System.currentTimeMillis()-timestart-latency)*ui.noteDistance)/(60000/tempo);      } else {
        rhythmCursorXposcorrected = ui.rhythmCursorXpos;      }

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
        if ((rhythmIndex >= 0) && (rhythms.get(rhythmIndex).isSilence()) 
            && (rhythmIndex-1 >= 0)
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

      if (ui.keyboardsoundCheckBox.isSelected()){
        //currentChannel.stopnotes();
        currentChannel.playNote(true, pitch, 2000);
      }
      //  System.out.println("time sound" + System.currentTimeMillis());
      float rhythmCursorXposcorrected;

      if (cursorstart) {
        rhythmCursorXposcorrected = ui.rhythmCursorXStartPos + ((System.currentTimeMillis()-timestart-latency)*ui.noteDistance)/(60000/tempo);      } else {
        rhythmCursorXposcorrected = ui.rhythmCursorXpos;      }

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
      answers.add(new RhythmAnswer((int)rhythmCursorXposcorrected, ui.rhythmAnswerScoreYpos - 15, goodnote, result));
    }

    private Interval intervalchoice() {
      int i = 1;
      if (game.noteLevel.isSecondInterval()) {
        i = 1;
      } else if (game.noteLevel.isThirdInterval()) {
        i = 2;
      } else if (game.noteLevel.isFourthInterval()) {
        i = 3;
      } else if (game.noteLevel.isFifthInterval()) {
        i = 4;
      } else if (game.noteLevel.isSixthInterval()) {
        i = 5;
      } else if (game.noteLevel.isSeventhInterval()) {
        i = 6;
      } else if (game.noteLevel.isOctaveInterval()) {
        i = 7;
      } else if (game.noteLevel.isRandomInterval()) {
        i = (int)Math.round((Math.random()*6))+1;
      }

      int h;
      if (game.noteLevel.isCurrentKeyBoth()) {
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
      n1.updateNote(game.noteLevel, ui.scoreYpos, ui.bundle);
      n1.updateAccidental(game.noteLevel, ui.bundle);

      Note n2 = new Note(h-i*5, ui.noteMargin+98, 0);
      n2.updateNote(game.noteLevel, ui.scoreYpos, ui.bundle);
      n2.updateAccidental(game.noteLevel, ui.bundle);

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
      prevNote = n1.getHeight();

      return inter;
    }

    private void newinterval() {
      stopSound();
      currentInterval.copy(intervalchoice());
      if (game.noteLevel.isNormalgame() || game.noteLevel.isLearninggame()) {
        posnote = 0;
        currentNote = currentInterval.getNote(posnote);
        if (ui.soundOnCheckBox.isSelected()) {
          synthNote(currentNote.getPitch(), 80, noteDuration);
        }
      } else if (game.noteLevel.isInlinegame()) {
        if (position<line.length-1) {
          position += 1;
          currentInterval.copy(lineint[position]);

          posnote = 0;
          //acourant.convertir(clecourante, typeaccord);
          currentNote = currentInterval.getNote(posnote);
          if (ui.soundOnCheckBox.isSelected()) {
            synthNote(currentNote.getPitch(), 80, noteDuration);
          }
        }
      }
    }

    private Chord chordchoice() {
      int h;
      Note n1 = new Note(0, 0, 0);
      Note n2 = new Note(0, 0, 0);
      Note n3 = new Note(0, 0, 0);

      if (game.noteLevel.isCurrentKeyBoth()) {
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

        n1 = new Note(h, ui.noteMargin+98, 0);
        n1.updateNote(game.noteLevel, ui.scoreYpos, ui.bundle);
        n1.updateAccidental(game.noteLevel, ui.bundle);

        n2 = new Note(h-2*5, ui.noteMargin+98, 0);
        n2.updateNote(game.noteLevel, ui.scoreYpos, ui.bundle);
        n2.updateAccidentalInChord(game.noteLevel.getCurrentTonality(), n1.getPitch(), 2, ui.bundle); //deuxieme note

        n3 = new Note(h-4*5, ui.noteMargin+98, 0);
        n3.updateNote(game.noteLevel, ui.scoreYpos, ui.bundle);
        n3.updateAccidentalInChord(game.noteLevel.getCurrentTonality(), n1.getPitch(), 3, ui.bundle); //troisieme note

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
      prevNote = n1.getHeight();
      return a;
    }

    private void resetButtonColor() {
      ColorUIResource def = new ColorUIResource(238, 238, 238);
      ui.doButton1.setBackground(def);
      ui.reButton.setBackground(def);
      ui.miButton.setBackground(def);
      ui.faButton.setBackground(def);
      ui.solButton.setBackground(def);
      ui.laButton.setBackground(def);
      ui.siButton.setBackground(def);
      ui.sharpButton1.setBackground(def);
      ui.flatButton2.setBackground(def);
    }

    void applyButtonColor() {
      resetButtonColor();

      Color red = new Color(242, 179, 112);
      if (currentNote.getNom().equals(ui.doButton1.getText())) {
        ui.doButton1.setBackground(red);
      } else if (currentNote.getNom().equals(ui.reButton.getText())) {
        ui.reButton.setBackground(red);
      } else if (currentNote.getNom().equals(ui.miButton.getText())) {
        ui.miButton.setBackground(red);
      } else if (currentNote.getNom().equals(ui.faButton.getText())) {
        ui.faButton.setBackground(red);
      } else if (currentNote.getNom().equals(ui.solButton.getText())) {
        ui.solButton.setBackground(red);
      } else if (currentNote.getNom().equals(ui.laButton.getText())) {
        ui.laButton.setBackground(red);
      } else if (currentNote.getNom().equals(ui.siButton.getText())) {
        ui.siButton.setBackground(red);
      }

      if (currentNote.getAlteration().equals(ui.sharpButton1.getText())) {
        ui.sharpButton1.setBackground(red);
      } else if (currentNote.getAlteration().equals(ui.flatButton1.getText())) {
        ui.flatButton2.setBackground(red);
      }
    }

    void drawChord(Chord a, Graphics g, boolean accordcourant) {
      Dimension d = ui.getSize();

      if (a.getNote(posnote).getX()<d.width-ui.noteMargin &&
          a.getNote(posnote).getX() >= ui.noteMargin+98 && gameStarted) {
        // NOTE DANS LIMITES
        a.paint(posnote, game.noteLevel, g, musiSync, accordcourant, ui,
            ui.scoreYpos, ui.bundle);
        //g.drawString("Renv" + a.renvst,100,100);
      } else {
        if (game.noteLevel.isNormalgame()) {
          currentScore.addPoints(-20);

          if (currentScore.isLost()) {
            gameStarted = false;
            ui.startButton.setText(ui.bundle.getString("_start"));
            stopSound();
            showResult();
          }

          if (gameStarted) {            newChord();          }
        } else if (game.noteLevel.isLearninggame()) {
          newChord();
          resetButtonColor();
        } else if (game.noteLevel.isInlinegame() &&         		   gameStarted &&        		   game.noteLevel.isChordsgame() &&         		   lineacc[position].getNote(0).getX()<ui.noteMargin+98) {
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
      if ((game.noteLevel.isNormalgame() || game.noteLevel.isLearninggame()) & gameStarted) {
        notecounter++;
        if (prevNote != 0 & ui.soundOnCheckBox.isSelected()) {
          stopSound();
        }
        currentNote.init();

        if (game.noteLevel.isNotesgame() || game.noteLevel.isAccidentalsgame()) {
          //choosing note with height to do change to choose note with pitch
          currentNote.setHeight(setNoteHeight(game.noteLevel.getNbnotesupper(),        				                    game.noteLevel.getNbnotesunder(),        				                    game.noteLevel.getNbnotesupper(),        				                    game.noteLevel.getNbnotesunder()));
          while (currentNote.getHeight() == prevNote) {
            currentNote.setHeight(setNoteHeight(game.noteLevel.getNbnotesupper(),            		                          game.noteLevel.getNbnotesunder(),            		                          game.noteLevel.getNbnotesupper(),            		                          game.noteLevel.getNbnotesunder()));
          }
          currentNote.updateNote(game.noteLevel, ui.scoreYpos, ui.bundle);
          currentNote.updateAccidental(game.noteLevel, ui.bundle);
          prevNote = currentNote.getHeight();
        } else if (game.noteLevel.isCustomNotesgame()) {
          // choosing note with pitch

          currentNote.setPitch(game.noteLevel.getRandomPitch());
          currentNote.updateNotePitch(game.noteLevel, ui.scoreYpos, ui.bundle);
          prevNote = currentNote.getHeight();
        }

        currentNote.setX(ui.noteMargin+98);
        System.out.println(currentNote.getNom());
        System.out.println(currentNote.getHeight());
        System.out.println(currentNote.getPitch());
        //if (soundOnCheckBox.isSelected()) sons[indiceson(ncourante.getHeight())].play();

        if (ui.soundOnCheckBox.isSelected()) {
          synthNote(currentNote.getPitch(), 80, noteDuration);
        }
      } else if (game.noteLevel.isInlinegame()) {
        //sons[indiceson(ncourante.getHeight())].stop();
        if (prevNote != 0 & ui.soundOnCheckBox.isSelected()) {
          stopSound();
        }
        if (position < line.length-1) {
          position += 1;
          currentNote.copy(line[position]);
        }
        //if (soundOnCheckBox.isSelected()) sons[indiceson(ncourante.getHeight())].play();
        if (ui.soundOnCheckBox.isSelected()) {
          synthNote(currentNote.getPitch(), 80, noteDuration);
        }
      }
    }

    private void stopSound() {
      currentChannel.stopnotes();
    }

    void drawNote(Note note, Graphics g, Font f, Color couleur) {
      Dimension size = ui.getSize();

      g.setColor(couleur);
      if (note.getX() < size.width-ui.noteMargin && note.getX() >= ui.noteMargin + 98           && gameStarted) { // NOTE DANS LIMITES
        if (game.noteLevel.isAccidentalsgame() || game.noteLevel.isCustomNotesgame()) {
          note.paint(game.noteLevel, g, f, 9, 0, ui.scoreYpos, ui, couleur, ui.bundle);
        } else {
          note.paint(game.noteLevel, g, f, 0, 0, ui.scoreYpos, ui, couleur, ui.bundle);
        }
      } else {
        if (game.noteLevel.isNormalgame()) {
          currentScore.addPoints(-20);
          if (currentScore.isLost()) {
            gameStarted = false;
            ui.startButton.setText(ui.bundle.getString("_start"));
            showResult();
          }
          newnote();
        } else if (game.noteLevel.isLearninggame()) {
          newnote();
          resetButtonColor();
        } else if (game.noteLevel.isInlinegame() && gameStarted) {
          if (line[position].getX() < ui.noteMargin+98) { // Si la note courant (sauf la derniï¿½re)dï¿½passe la limite ici marge +25
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

    private void nextRhythm() {
      System.out.println ("rhytm xpos: " + rhythms.get(rhythmIndex).getPosition() + 
          " pitch: " + rhythms.get(rhythmIndex).getPitch() + 
          " index: " + rhythmIndex);

      if (rhythms.get(rhythmIndex).getDuration() != 0) {
        if (rhythmIndex<rhythms.size()-1) {
          rhythmIndex++;
          ui.repaint();
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
      try {        // always on channel 1
        message.setMessage(nCommand, 0, nKey, nVelocity);
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
      if (!silence           || (silence && tmpsilence < 0.85)           || (duration == 3 && tmpnum != 3)) {
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
      rhythms.get(rhythms.size() - 1).setTripletValue(val);
    }

    private boolean isBeginMeasure(int i) {
      double d = 0;
      int id = 0;
      for (int j = 0; j < i; j++) {
        //   d += 4.0/rhythms.get(j).getDuration();
        d += rhythms.get(j).getDuration();
      }
      id = (int) Math.round(d); // we should round because of 0.33 triplet need to be fixed

      int tmpnum = 4;      if (selectedGame == RHYTHMREADING) {
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
      ui.repaint();
      int tmpnum = 4;    	  // int tmpden = 4;    	int tmpdiv = 1;
      int currentTick = 0;
      int rowCount = 0; // measures counter
      double tpsmes = 0; // number of quarters 
      int currentXPos = ui.windowMargin + ui.keyWidth + ui.alterationWidth + ui.timeSignWidth + ui.notesShift;
      int pitch;
      boolean wholeNote = false;      boolean halfNote = false;      boolean dottedhalfNote = false;      boolean quarterNote = false;      boolean eighthNote = false;      boolean triplet = false;
      boolean stemup = true;
      // Dimension size = getSize();

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
        if ((r % numberOfMeasures) == 0) {
          currentXPos = ui.windowMargin + ui.keyWidth + ui.alterationWidth + ui.timeSignWidth + ui.notesShift;
          rowCount++;
        }
        /*
           } else {
           rhythms.add(new Rhythm(0, 0, 0, 71, false, false, 0));
           }
           */
    }

    if (selectedGame == RHYTHMREADING) {    	regroupNotes(); //not workin with Scorereading yet    }
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

  // LINES OF NOTES

  private void createLine() {
    Dimension size = ui.getSize();
    Chord a = new Chord(currentNote, currentNote, currentNote, "", 0);
    Interval inter = new Interval(currentNote, currentNote, "");

    // System.out.println(type2);

    if (game.noteLevel.isNotesgame() || game.noteLevel.isAccidentalsgame()) {
      line[0] = new Note(setNoteHeight(game.noteLevel.getNbnotesupper(), game.noteLevel.getNbnotesunder(),    		             game.noteLevel.getNbnotesupper(), game.noteLevel.getNbnotesunder()), size.width-ui.noteMargin, 0);
      line[0].updateNote(game.noteLevel, ui.scoreYpos, ui.bundle);
      line[0].updateAccidental(game.noteLevel, ui.bundle);

      for (int i = 1; i < line.length; i++) {
        int tmph = setNoteHeight(game.noteLevel.getNbnotesupper(), game.noteLevel.getNbnotesunder(),        		                 game.noteLevel.getNbnotesupper(), game.noteLevel.getNbnotesunder());
        while (tmph == line[i-1].getHeight()) {
          tmph = setNoteHeight(game.noteLevel.getNbnotesupper(), game.noteLevel.getNbnotesunder(), game.noteLevel.getNbnotesupper(), game.noteLevel.getNbnotesunder()); // pour �viter les r�p�titions
        }

        line[i] = new Note(tmph, size.width-ui.noteMargin+i*35, 0);
        line[i].updateNote(game.noteLevel, ui.scoreYpos, ui.bundle);
        line[i].updateAccidental(game.noteLevel, ui.bundle);
      }
    } else if (game.noteLevel.isCustomNotesgame()) {
      line[0] = new Note(0, size.width-ui.noteMargin, game.noteLevel.getRandomPitch() );
      line[0].updateNotePitch(game.noteLevel, ui.scoreYpos, ui.bundle);

      for (int i = 1; i < line.length; i++) {
        int tmpp = game.noteLevel.getRandomPitch();
        while (tmpp == line[i-1].getPitch()) {
          tmpp = game.noteLevel.getRandomPitch(); // to avoid same pitch
        }

        line[i] = new Note(0, size.width-ui.noteMargin+i*35, tmpp);
        line[i].updateNotePitch(game.noteLevel, ui.scoreYpos, ui.bundle);
      }
    }

    position = 0;
    currentNote = line[position]; // initialisa tion avec la premiï¿½re note
    //if (soundOnCheckBox.isSelected()) sons[indiceson(ncourante.getHeight())].play(); // dï¿½part du son de la premiï¿½re note
    if (ui.soundOnCheckBox.isSelected()) {
      synthNote(currentNote.getPitch(), 80, noteDuration);
    } else if (game.noteLevel.isChordsgame()) {
      // voir pour precedant
      for (int i = 0; i<line.length; i++) {
        a.copy(chordchoice());
        a.updatex(size.width-ui.noteMargin+i*50);
        lineacc[i] = new Chord(a.getNote(0), a.getNote(1), a.getNote(2),
            a.getName(), a.getInversion());
        lineacc[i].convert(game.noteLevel);
      }
      position = 0;
      posnote = 0;
      currentChord.copy(lineacc[position]);
      // acourant.convertir(clecourante,typeaccord);
      currentNote = currentChord.getNote(currentChord.realposition(posnote));
      if (ui.soundOnCheckBox.isSelected()) {
        synthNote(currentNote.getPitch(), 80, noteDuration);
      }
    } else if (game.noteLevel.isIntervalsgame()) {
      // voir pour precedant
      for (int i = 0; i<line.length; i++) {
        inter.copy(intervalchoice());
        //i = nouvelintervalle();
        inter.updatex(size.width-ui.noteMargin+i*65);
        lineint[i] = new Interval(            inter.getNote(0), inter.getNote(1), inter.getName());
      }
      position = 0;
      posnote = 0;

      currentInterval.copy(lineint[position]);
      currentNote = currentInterval.getNote(posnote); //0
      if (ui.soundOnCheckBox.isSelected()) {
        synthNote(currentNote.getPitch(), 80, noteDuration);
      }
    }
  }

  void drawInlineNotes(Graphics g, Font f) {
    for (int i = position; i<line.length; i++) {
      // n'affiche que la ligne ï¿½ partir de la position
      if (game.noteLevel.isNotesgame() || game.noteLevel.isAccidentalsgame() || game.noteLevel.isCustomNotesgame()) {
        drawNote(line[i], g, f, Color.black);
      } else if (game.noteLevel.isChordsgame()) {
        drawChord(lineacc[i], g, i == position);
      } else if (game.noteLevel.isIntervalsgame()) {
        drawInterval(lineint[i], g, i == position);
      }
    }
  }

  void drawNotesAndAnswers(Graphics g, Font f) {

    // paint answers: red = wrong, green = good
    for (int i = 0; i < answers.size(); i++) {
      if (!answers.get(i).isnull()) answers.get(i).paint(g);
    }

    for (int i = 0; i < rhythms.size(); i++) {
      // System.out.println(i);
      if (rhythms.get(i).getDuration() != 0) {
        if ((rhythmgame == 0) && (i!=rhythmIndex) || (muterhythms)) { //only paint note in learning mode
          rhythms.get(i).paint(g, selectedGame, f, scoreLevel, 9, rowsDistance, false, ui.scoreYpos, ui);
        } else {
          rhythms.get(i).paint(g, selectedGame, f, scoreLevel, 9, rowsDistance, true, ui.scoreYpos, ui);
        }
      }
    }
  }

  // CHORDS

  private void newChord() {

    if (game.noteLevel.isNormalgame() || game.noteLevel.isLearninggame()) {
      posnote = 0;
      currentChord.copy(chordchoice());
      currentChord.convert(game.noteLevel);
      currentNote = currentChord.getNote(currentChord.realposition(posnote));
      if (ui.soundOnCheckBox.isSelected()) {
        synthNote(currentNote.getPitch(), 80, noteDuration);
      }
    } else if (game.noteLevel.isInlinegame()) {
      if (position<line.length-1) {
        position += 1;
        currentChord.copy(lineacc[position]);

        posnote = 0;
        //acourant.convertir(clecourante,typeaccord);
        currentNote = currentChord.getNote(currentChord.realposition(posnote));
        if (ui.soundOnCheckBox.isSelected()) {
          synthNote(currentNote.getPitch(), 80, noteDuration);
        }
      }
    }
  }

  void drawInterval(Interval inter, Graphics g, boolean Intervallecourant) {
    Dimension size = ui.getSize();

    if (inter.getNote(posnote).getX() < size.width - ui.noteMargin &&
        inter.getNote(posnote).getX() >= ui.noteMargin + 98 && gameStarted) {
      // NOTE DANS LIMITES
      inter.paint(posnote, game.noteLevel, g, musiSync, ui.scoreYpos,
          ui.bundle, Intervallecourant, ui);
      //g.drawString("Renv" + a.renvst,100,100);
    } else {
      if (game.noteLevel.isNormalgame()) {
        currentScore.addPoints(-20);
        if (currentScore.isLost()) {
          gameStarted = false;
          ui.startButton.setText(ui.bundle.getString("_start"));
          stopSound();
          showResult();
        }

        if (gameStarted) {          newinterval();        }
      } else if (game.noteLevel.isLearninggame()) {
        newinterval();
        resetButtonColor();
      } else if (game.noteLevel.isInlinegame()     		     && gameStarted     		     && lineint[position].getNote(0).getX() < ui.noteMargin+98) {
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

    if (game.noteLevel.isChordsgame()) {
      if (posnote < 2) {
        posnote += 1;

        currentNote = currentChord.getNote(currentChord.realposition(posnote));
        alterationOk = false;
        if (ui.soundOnCheckBox.isSelected()) {
          synthNote(currentNote.getPitch(), 80, noteDuration);
        }
      } else {
        if (isLessonMode && notecounter == game.noteLevel.getLearningduration()) {
          gameStarted = false;
          ui.startButton.setText(ui.bundle.getString("_start"));
          nextLevel();
        } else {
          newChord();
          notecounter++;
        }
      }
    } else if (game.noteLevel.isIntervalsgame()) {
      if (posnote == 0) {
        posnote += 1;
        currentNote = currentInterval.getNote(posnote);
        alterationOk = false;
        if (ui.soundOnCheckBox.isSelected()) {
          synthNote(currentNote.getPitch(), 80, noteDuration);
        }
      } else {
        if (isLessonMode && notecounter == game.noteLevel.getLearningduration()) {
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
        ui.scoreMessage.setLocationRelativeTo(ui);

        ui.scoreMessage.setVisible(true);

        stopNoteGame();
      } else if (currentScore.isLost()) {
        ui.scoreMessage.setTitle(ui.bundle.getString("_sorry"));

        ui.textscoreMessage.setText("  "+currentScore.getNbtrue()+" "+ui.bundle.getString("_correct")+
            " / "+currentScore.getNbfalse()+" "+
            ui.bundle.getString("_wrong")+"  ");
        ui.scoreMessage.pack();
        ui.scoreMessage.setLocationRelativeTo(ui);
        ui.scoreMessage.setVisible(true);

        stopNoteGame();
      }
    } else if (selectedGame == RHYTHMREADING || selectedGame == SCOREREADING ) {

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
      if (nbrhythms ==  nbgood) {        ui.scoreMessage.setTitle(ui.bundle.getString("_congratulations"));      } else {        ui.scoreMessage.setTitle(ui.bundle.getString("_sorry"));      }

      ui.textscoreMessage.setText("  " + nbrhythms + " " + ui.bundle.getString("_menuRythms") +     		                          " : " + nbgood + " " + ui.bundle.getString("_correct") +
                                  " / " + nbnotefalse + " " + ui.bundle.getString("_wrong") +                                  "  " + nbrhythmfalse + " " + ui.bundle.getString("_wrongrhythm") + "  ");
      ui.scoreMessage.pack();
      ui.scoreMessage.setLocationRelativeTo(ui);
      ui.scoreMessage.setVisible(true);
    }
  }
}

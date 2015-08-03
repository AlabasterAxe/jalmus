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

import java.io.File;

public class Jalmus {

  //----------------------------------------------------------------
  // Main variables

  int selectedGame = FIRSTSCREEN; // FIRSTSCREEN, NOTEREADING, RHYTHMREADING, SCOREREADING
  static int FIRSTSCREEN = 0;
  static int NOTEREADING = 1;
  static int RHYTHMREADING = 2;
  static int SCOREREADING = 3;
  //----------------------------------------------------------------
  // Lesson variables
  Lessons currentLesson = new Lessons();
  boolean isLessonMode;

  //----------------------------------------------------------------
  // Note reading variables

  boolean samerhythms = true;  Piano piano;
  //  private int transpose = 0;  // number of 1/2tons for MIDI transposition -24 +24

  // Animation Resources

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

  Score currentScore = new Score();

  boolean gameStarted; // whether the game has started or not.
  boolean paused;

  //----------------------------------------------------------------
  // Rhythm reading variables

  long timestart; // timestamp of cursor at the beginning of a line

  int tempo = 40; // sequencer time - rhythmGameSpeedComboBox button
  //private double nbtemps = 4; // nombre de temps par mesure
  //private int nbmesures = 8;
  int metronomeCount = 0;

  NoteReadingGame noteGame;  RhythmReadingGame rhythmGame;  ScoreReadingGame scoreGame;    public Jalmus(NoteReadingGame noteGame, RhythmReadingGame rhythmGame, ScoreReadingGame scoreGame) {    this.noteGame = noteGame;    this.rhythmGame = rhythmGame;    this.scoreGame = scoreGame;  }  //################################################################
  // Initialization methods

  void init(String paramlanguage) {
    piano = new Piano(73, 40);
  }

  //----------------------------------------------------------------
  
  //################################################################
  // METHODES D'ACTION DES BOUTONS ET CHOICES

  /** Initialize note reading game if there is modification in
   * parameters and game restart. */
  
  /** Initialize rhythm reading game if there is modification in
   * parameters and game restart. */

  /** Stops all games. */

  void stopGames() {
    if (selectedGame == NOTEREADING) {      stopNoteGame();    } else {      stopRhythmGame();    }
  }

  void stopNoteGame() {
    noteGame.stopGame();
  }
  void stopRhythmGame() {    rhythmGame.stopGame();  }
  void initRhythmGame() {
    rhythmGame.initGame();
  }

  void startRhythmGame() {
    rhythmGame.startGame();
  }

  void startNoteGame() {
    noteGame.startGame();
  }

  void startLevel() {
    if (currentLesson.isNoteLevel()) {      noteGame.startLevel();
    } else if (currentLesson.isRhythmLevel()) {
      rhythmGame.startLevel();
    } else if (currentLesson.isScoreLevel()) {      scoreGame.startLevel();
    }
  }

  boolean nextLevel() {
    if (!currentLesson.lastexercice()) {
      stopNoteGame();
      currentLesson.nextLevel();
      if (currentLesson.isNoteLevel()) {
        noteGame.nextGame();
      } else if (currentLesson.isRhythmLevel()) {        rhythmGame.nextGame();
      }      return true;
    } else {
      return false;
    }
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
  }}

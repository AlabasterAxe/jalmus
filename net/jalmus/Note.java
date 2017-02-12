package net.jalmus;

/**
 * <p>Title: Java Lecture Musicale</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 *
 * @author RICHARD Christophe
 * @version 1.0
 */

import java.awt.*;
import java.util.ResourceBundle;

public class Note {
  String alteration;
  String name;
  int height;
  int x; // x position for animation
  int pitch; // midi pitch

  public Note(int h, int x, int p) {
    this.alteration = "";
    this.name = "";
    this.height = h; // pos. de la note dportee+20 = sol cle de sol, dportee+24 = fa (+4)
    this.x = x;
    this.pitch = p;
  }

  public static boolean samePitch(int pitch1, int pitch2, int offset) {
    return pitch2 == pitch1 + offset;
  }

  public void init() {
    this.alteration = "";
    this.name = "";
    this.height = 0; // pos. de la note dportee+20 = sol cle de sol, dportee+24 = fa (+4)
    this.x = 0;
    this.pitch = 0;
  }

  public void copy(Note n) {
    this.alteration = n.alteration;
    this.name = n.name;
    this.height = n.height; // pos. de la note dportee+20 = sol cle de sol, dportee+24 = fa (+4)
    this.x = n.x;
    this.pitch = n.pitch;
  }

  public String getName() {
    return this.name;
  }

  public String getAlteration() {
    return this.alteration;
  }

  public int getPitch() {
    return this.pitch;
  }

  public void setPitch(int i) {
    this.pitch = i;
  }

  public int getX() {
    return this.x;
  }

  public void setX(int i) {
    this.x = i;
  }

  public int getHeight() {
    return this.height;
  }

  public void setHeight(int h) {
    this.height = h;
  }

  public void paint(NoteLevel nrlevel, Graphics g, Font f, int offsetA, int offsetN,
                    int dportee, Color color, ResourceBundle b) {
    g.setFont(f.deriveFont(50f));
    g.setColor(color);

    g.drawString("w", this.x + offsetN, this.height + 11);
    if ((!this.alteration.equals("") && !this.accidentalInTonality(nrlevel.getCurrentTonality(), b))
        || (this.alteration.equals("n"))) {
      if (this.alteration.equals("#")) {
        g.drawString("B", this.x - offsetA, this.height + 10);
      } else if (this.alteration.equals("b")) {
        g.drawString("b", this.x - offsetA, this.height + 10);
      } else if (this.alteration.equals("n")) {
        String bq = "" + (char) 0xBD;
        g.drawString(bq, this.x - offsetA, this.height + 10);
      }
    }

    if (nrlevel.isCurrentKeyTreble() || nrlevel.isCurrentKeyBass()) {
      if (this.height >= dportee + 45) { // <DO en dessous de la port�e en cl� de sol
        for (int i = dportee + 50; i <= this.height + 5; i += 10) {
          if (i != this.height + 5) {
            g.setColor(Color.black); //!= this.height+4
          } else {
            g.setColor(color);
          }
          g.drawLine(this.x - 2 + offsetN, i, this.x + 18 + offsetN, i); // dessine la port�e en dessous de la port�e normale
        }
      } else if (this.height <= dportee - 15) {  // <LA au dessus de la port�e en cl� de sol
        for (int i = dportee - 10; i >= this.height + 5; i = i - 10) {
          if (i != this.height + 5) {
            g.setColor(Color.black);
          } else {
            g.setColor(color);
          }
          // dessine la portee en dessus de la port�e normale
          g.drawLine(this.x - 2 + offsetN, i, this.x + 18 + offsetN, i);
        }
      }
      ;
    } else if (nrlevel.isCurrentKeyBoth()) {
      if (this.height >= dportee + 45 & this.height <= dportee + 55) { // du DO jusqu'au LA en dessous de la port�e
        for (int i = dportee + 50; i <= this.height + 5; i += 10) {
          g.drawLine(this.x - 2 + offsetN, i, this.x + 18 + offsetN, i);
        }       // dessine la port�e en dessous de la port�e normale
      } else if (this.height <= dportee - 15) {  // <LA au dessus de la port�e en cl� de sol
        for (int i = dportee - 10; i >= this.height + 5; i -= 10) {
          g.drawLine(this.x - 2 + offsetN, i, this.x + 18 + offsetN, i);
        }       // dessine la portee en dessus de la port�e normale
      } else if (this.height >= dportee + 135) {  // � partie du MI en dessous de la port�e
        // cas de la cl� de fa
        for (int i = dportee + 140; i <= this.height + 5; i = i + 10) {
          g.drawLine(this.x - 2 + offsetN, i, this.x + 18 + offsetN, i);
        }       // dessine la port�e en dessous de la port�e normale
      } else if (this.height <= dportee + 75 & this.height >= dportee + 60) {
        for (int i = dportee + 80; i >= this.height + 5; i -= 10) {
          g.drawLine(this.x - 2 + offsetN, i, this.x + 18 + offsetN, i);
        }      // dessine la portee en dessus de la port�e normale
      }
      ;
    }
  }

  public boolean sameNoteHeight(int base) {
    for (int x = 0; x <= 3; x++) { //28 = 8 notes entre 2 notes identiques * 4 entre chaque note
      if ((this.height + x * 35 == base) || (this.height - x * 35 == base)) {
        return true;
      }
    }
    return false;
  }

  public boolean sameNotePitch(int pitchbase) {
    for (int x = 0; x <= 9; x++) { //28 = 8 notes entre 2 notes identiques * 4 entre chaque note
      if ((this.pitch + x * 12 == pitchbase) || (this.pitch - x * 12 == pitchbase)) {
        return true;
      }
    }
    return false;
  }

  public boolean accidentalInTonality(Tonality t, ResourceBundle bundle) {

    String DO = bundle.getString("_do");
    String RE = bundle.getString("_re");
    String MI = bundle.getString("_mi");
    String FA = bundle.getString("_fa");
    String SOL = bundle.getString("_sol");
    String LA = bundle.getString("_la");
    String SI = bundle.getString("_si");

    if (t.Alteration.equalsIgnoreCase("#")) {
      if (this.name.equals(FA) && t.getAlterationsNumber() >= 1) {
        return true;
      } else if (this.name.equals(DO) && t.getAlterationsNumber() >= 2) {
        return true;
      } else if (this.name.equals(SOL) && t.getAlterationsNumber() >= 3) {
        return true;
      } else if (this.name.equals(RE) && t.getAlterationsNumber() >= 4) {
        return true;
      } else if (this.name.equals(LA) && t.getAlterationsNumber() >= 5) {
        return true;
      } else if (this.name.equals(MI) && t.getAlterationsNumber() >= 6) {
        return true;
      } else if (this.name.equals(SI) && t.getAlterationsNumber() >= 7) {
        return true;
      } else {
        return false;
      }
    } else if (t.Alteration.equalsIgnoreCase("b")) {
      if (this.name.equals(SI) && t.getAlterationsNumber() >= 1) {
        return true;
      } else if (this.name.equals(MI) && t.getAlterationsNumber() >= 2) {
        return true;
      } else if (this.name.equals(LA) && t.getAlterationsNumber() >= 3) {
        return true;
      } else if (this.name.equals(RE) && t.getAlterationsNumber() >= 4) {
        return true;
      } else if (this.name.equals(SOL) && t.getAlterationsNumber() >= 5) {
        return true;
      } else if (this.name.equals(DO) && t.getAlterationsNumber() >= 6) {
        return true;
      } else if (this.name.equals(FA) && t.getAlterationsNumber() >= 7) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public void updateAccidental(NoteLevel nrlevel, ResourceBundle b) {
    if (!nrlevel.isNotesgame()) {
      double tmp = Math.random();

      if (this.accidentalInTonality(nrlevel.getCurrentTonality(), b)) {
        if (tmp < 0.9) {
          this.alteration = nrlevel.getCurrentTonality().Alteration;
        } else {
          this.alteration = "n";
        }
      } else {
        if (tmp < 0.9) {
          this.alteration = "";
        } else {
          this.alteration = nrlevel.getCurrentTonality().Alteration;
        }
      }
    } else if (this.accidentalInTonality(nrlevel.getCurrentTonality(), b)) {
      this.alteration = nrlevel.getCurrentTonality().Alteration;
    } else {
      this.alteration = "";
    }

    // MODIFY PITCH ACCORDING TO ACCIDENTAL
    int alt = 0;
    if (this.alteration.equals("#")) {
      alt = 1;
    } else if (this.alteration.equals("b")) {
      alt = -1;
    }
    this.pitch += alt;
  }


  public void updateAccidentalInChord(Tonality t, int pitch0, int nnote, ResourceBundle b) { //pour les accords

    double tmp = Math.random();

    if (nnote == 2) { //deuxieme note de l'accord tierce majeure ou mineure
      if (this.pitch - pitch0 == 2) {
        if (t.Alteration.equals("#")) {
          this.alteration = "#";
        }
      } else if (this.pitch - pitch0 == 3) {
        if (tmp < 0.4 || t.Alteration.equals("b")) {// laisser tierce mineure
          if (this.accidentalInTonality(t, b)) {
            this.alteration = "n";
          }
        } else {//passer a tierce majeure
          this.alteration = "#";
        }
      } else if (this.pitch - pitch0 == 4) {
        if (tmp < 0.4 || t.Alteration.equals("#")) { // laisser tierce majeure
          if (this.accidentalInTonality(t, b)) {
            this.alteration = "n";
          }
        } else { // passer a tierce mineure
          this.alteration = "b";
        }
      } else if (this.pitch - pitch0 == 5) {
        if (t.Alteration.equals("b")) {
          this.alteration = "b";
        }
      }
    } else if (nnote == 3) { // troisieme note de l'accord quinte juste
      if (this.pitch - pitch0 == 6) {
        if (tmp < 0.4 || t.Alteration.equals("b")) { // laisser quinte diminuee
          if (this.accidentalInTonality(t, b)) {
            this.alteration = "n";
          }
        } else {
          this.alteration = "#";
        }
      } else if (this.pitch - pitch0 == 7) {
        if (tmp < 0.1 && t.Alteration.equals("b")) {
          this.alteration = "b"; // quinte diminuee
        } else if (tmp < 0.2 && t.Alteration.equals("#")) {
          this.alteration = "#"; // quinte augmentee
        } else if (this.accidentalInTonality(t, b)) {
          this.alteration = "n";
        }
      } else if (this.pitch - pitch0 == 8) {
        if (tmp < 0.4 || t.Alteration.equals("#")) { // laisser quinte augmentee
          if (this.accidentalInTonality(t, b)) {
            this.alteration = "n";
          }
        } else {
          this.alteration = "b";
        }
      }
    }

    // MODIFY PITCH ACCORDING TO ACCIDENTAL
    int alt = 0;
    if (this.alteration.equals("#")) {
      alt = 1;
    } else if (this.alteration.equals("b")) {
      alt = -1;
    }

    this.pitch += alt;
  }

  public void updateNote(NoteLevel nrlevel, int dportee, ResourceBundle bundle) {

    String DO = bundle.getString("_do");
    String RE = bundle.getString("_re");
    String MI = bundle.getString("_mi");
    String FA = bundle.getString("_fa");
    String SOL = bundle.getString("_sol");
    String LA = bundle.getString("_la");
    String SI = bundle.getString("_si");

    if (nrlevel.isCurrentKeyTreble()) { // Treble key
      if (this.sameNoteHeight(dportee + 10)) {
        this.name = DO;
        this.pitch = 72 - (this.height - (dportee + 10)) / 28 * 12;
      } else if (this.sameNoteHeight(dportee + 15)) {
        this.name = SI;
        this.pitch = 71 - (this.height - (dportee + 15)) / 28 * 12;
      } else if (this.sameNoteHeight(dportee + 20)) {
        this.name = LA;
        this.pitch = 69 - (this.height - (dportee + 20)) / 28 * 12;
      } else if (this.sameNoteHeight(dportee + 25)) {
        this.name = SOL;
        this.pitch = 67 - (this.height - (dportee + 25)) / 28 * 12;
      } else if (this.sameNoteHeight(dportee + 30)) {
        this.name = FA;
        this.pitch = 65 - (this.height - (dportee + 30)) / 28 * 12;
      } else if (this.sameNoteHeight(dportee + 35)) {
        this.name = MI;
        this.pitch = 64 - (this.height - (dportee + 35)) / 28 * 12;
      } else if (this.sameNoteHeight(dportee + 40)) {
        this.name = RE;
        this.pitch = 62 - (this.height - (dportee + 40)) / 28 * 12;
      }
    } else if (nrlevel.isCurrentKeyBass()) { // Bass key
      if (this.sameNoteHeight(dportee + 20)) {
        this.name = DO;
        this.pitch = 48 - (this.height - (dportee + 20)) / 28 * 12;
      } else if (this.sameNoteHeight(dportee + 25)) {
        this.name = SI;
        this.pitch = 47 - (this.height - (dportee + 25)) / 28 * 12;
      } else if (this.sameNoteHeight(dportee + 30)) {
        this.name = LA;
        this.pitch = 45 - (this.height - (dportee + 30)) / 28 * 12;
      } else if (this.sameNoteHeight(dportee + 35)) {
        this.name = SOL;
        this.pitch = 43 - (this.height - (dportee + 35)) / 28 * 12;
      } else if (this.sameNoteHeight(dportee + 5)) {
        this.name = FA;
        this.pitch = 53 - (this.height - (dportee + 5)) / 28 * 12;
      } else if (this.sameNoteHeight(dportee + 10)) {
        this.name = MI;
        this.pitch = 52 - (this.height - (dportee + 10)) / 28 * 12;
      } else if (this.sameNoteHeight(dportee + 15)) {
        this.name = RE;
        this.pitch = 50 - (this.height - (dportee + 15)) / 28 * 12;
      }
      ;
    } else if (nrlevel.isCurrentKeyBoth()) {
      if (this.height <= dportee + 55) {   // Treble key
        if (this.sameNoteHeight(dportee + 10)) {
          this.name = DO;
          this.pitch = 72 - (this.height - (dportee + 10)) / 28 * 12;
        } else if (this.sameNoteHeight(dportee + 15)) {
          this.name = SI;
          this.pitch = 71 - (this.height - (dportee + 15)) / 28 * 12;
        } else if (this.sameNoteHeight(dportee + 20)) {
          this.name = LA;
          this.pitch = 69 - (this.height - (dportee + 20)) / 28 * 12;
        } else if (this.sameNoteHeight(dportee + 25)) {
          this.name = SOL;
          this.pitch = 67 - (this.height - (dportee + 25)) / 28 * 12;
        } else if (this.sameNoteHeight(dportee + 30)) {
          this.name = FA;
          this.pitch = 65 - (this.height - (dportee + 30)) / 28 * 12;
        } else if (this.sameNoteHeight(dportee + 35)) {
          this.name = MI;
          this.pitch = 64 - (this.height - (dportee + 35)) / 28 * 12;
        } else if (this.sameNoteHeight(dportee + 40)) {
          this.name = RE;
          this.pitch = 62 - (this.height - (dportee + 40)) / 28 * 12;
        }
      } else {       //  Bass key
        if (this.sameNoteHeight(dportee + 110)) {
          this.name = DO;
          this.pitch = 48 - (this.height - (dportee + 110)) / 28 * 12;
        } else if (this.sameNoteHeight(dportee + 115)) {
          this.name = SI;
          this.pitch = 47 - (this.height - (dportee + 115)) / 28 * 12;
        } else if (this.sameNoteHeight(dportee + 120)) {
          this.name = LA;
          this.pitch = 45 - (this.height - (dportee + 120)) / 28 * 12;
        } else if (this.sameNoteHeight(dportee + 125)) {
          this.name = SOL;
          this.pitch = 43 - (this.height - (dportee + 125)) / 28 * 12;
        } else if (this.sameNoteHeight(dportee + 95)) {
          this.name = FA;
          this.pitch = 53 - (this.height - (dportee + 95)) / 28 * 12;
        } else if (this.sameNoteHeight(dportee + 100)) {
          this.name = MI;
          this.pitch = 52 - (this.height - (dportee + 100)) / 28 * 12;
        } else if (this.sameNoteHeight(dportee + 105)) {
          this.name = RE;
          this.pitch = 50 - (this.height - (dportee + 105)) / 28 * 12;
        }
        ;
      }
    }
  }

  public void updateNotePitch(NoteLevel nrlevel, int dportee, ResourceBundle bundle) {

    String DO = bundle.getString("_do");
    String RE = bundle.getString("_re");
    String MI = bundle.getString("_mi");
    String FA = bundle.getString("_fa");
    String SOL = bundle.getString("_sol");
    String LA = bundle.getString("_la");
    String SI = bundle.getString("_si");

    Integer doubleStaffHeight = 0;
    if (nrlevel.isCurrentKeyBoth()) {
      doubleStaffHeight = 90;
    } else {
      doubleStaffHeight = 0;
    }

    if (nrlevel.isCurrentKeyTreble() || (nrlevel.isCurrentKeyBoth() & this.pitch >= 57)) {//base cl� de sol : SOL = dportee+25
      if (this.sameNotePitch(0)) {
        this.name = DO;
        this.height = (60 - this.pitch) * 35 / 12 + dportee + 45;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      } else if (this.sameNotePitch(1)) {
        if (nrlevel.getCurrentTonality().isflat()) {
          this.name = RE;
          this.height = (61 - this.pitch) * 35 / 12 + dportee + 40;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "b";
          }
        } else {
          this.name = DO;
          this.height = (61 - this.pitch) * 35 / 12 + dportee + 45;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "#";
          }
        }
      } else if (this.sameNotePitch(2)) {
        this.name = RE;
        this.height = (62 - this.pitch) * 35 / 12 + dportee + 40;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      } else if (this.sameNotePitch(3)) {
        if (nrlevel.getCurrentTonality().issharp()) {
          this.name = RE;
          this.height = (63 - this.pitch) * 35 / 12 + dportee + 40;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "#";
          }
        } else {
          this.name = MI;
          this.height = (63 - this.pitch) * 35 / 12 + dportee + 35;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "b";
          }
        }
      } else if (this.sameNotePitch(4)) {
        this.name = MI;
        this.height = (64 - this.pitch) * 35 / 12 + dportee + 35;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      } else if (this.sameNotePitch(5)) {
        this.name = FA;
        this.height = (65 - this.pitch) * 35 / 12 + dportee + 30;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      } else if (this.sameNotePitch(6)) {
        if (nrlevel.getCurrentTonality().issharp()) {
          this.name = FA;
          this.height = (66 - this.pitch) * 35 / 12 + dportee + 30;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "#";
          }
        } else {
          this.name = SOL;
          this.height = (66 - this.pitch) * 35 / 12 + dportee + 25;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "b";
          }
        }
      } else if (this.sameNotePitch(7)) {
        this.name = SOL;
        this.height = (67 - this.pitch) * 35 / 12 + dportee + 25;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      } else if (this.sameNotePitch(8)) {
        if (nrlevel.getCurrentTonality().issharp()) {
          this.name = SOL;
          this.height = (68 - this.pitch) * 35 / 12 + dportee + 25;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "#";
          }
        } else {
          this.name = LA;
          this.height = (68 - this.pitch) * 35 / 12 + dportee + 20;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "b";
          }
        }
      } else if (this.sameNotePitch(9)) {
        this.name = LA;
        this.height = (69 - this.pitch) * 35 / 12 + dportee + 20;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      } else if (this.sameNotePitch(10)) {
        if (nrlevel.getCurrentTonality().isflat()) {
          this.name = SI;
          this.height = (70 - this.pitch) * 35 / 12 + dportee + 15;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "b";
          }
        } else {
          this.name = LA;
          this.height = (70 - this.pitch) * 35 / 12 + dportee + 20;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "#";
          }
        }
      } else if (this.sameNotePitch(11)) {
        this.name = SI;
        this.height = (71 - this.pitch) * 35 / 12 + dportee + 15;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      }
    } else if (nrlevel.isCurrentKeyBass() || (nrlevel.isCurrentKeyBoth() && this.pitch < 57)) {
      if (this.sameNotePitch(0)) {
        this.name = DO;
        this.height = (60 - this.pitch) * 35 / 12 + dportee - 15 + doubleStaffHeight;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      } else if (this.sameNotePitch(1)) {
        if (nrlevel.getCurrentTonality().isflat()) {
          this.name = RE;
          this.height = (61 - this.pitch) * 35 / 12 + dportee - 20 + doubleStaffHeight;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "b";
          }
        } else {
          this.name = DO;
          this.height = (61 - this.pitch) * 35 / 12 + dportee - 15 + doubleStaffHeight;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "#";
          }
        }
      } else if (this.sameNotePitch(2)) {
        this.name = RE;
        this.height = (62 - this.pitch) * 35 / 12 + dportee - 20 + doubleStaffHeight;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      } else if (this.sameNotePitch(3)) {
        if (nrlevel.getCurrentTonality().issharp()) {
          this.name = RE;
          this.height = (63 - this.pitch) * 35 / 12 + dportee - 20 + doubleStaffHeight;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "#";
          }
        } else {
          this.name = MI;
          this.height = (63 - this.pitch) * 35 / 12 + dportee - 25 + doubleStaffHeight;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "b";
          }
        }
      } else if (this.sameNotePitch(4)) {
        this.name = MI;
        this.height = (64 - this.pitch) * 35 / 12 + dportee - 25 + doubleStaffHeight;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      } else if (this.sameNotePitch(5)) {
        this.name = FA;
        this.height = (65 - this.pitch) * 35 / 12 + dportee - 30 + doubleStaffHeight;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      } else if (this.sameNotePitch(6)) {
        if (nrlevel.getCurrentTonality().issharp()) {
          this.name = FA;
          this.height = (66 - this.pitch) * 35 / 12 + dportee - 30 + doubleStaffHeight;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "#";
          }
        } else {
          this.name = SOL;
          this.height = (66 - this.pitch) * 35 / 12 + dportee - 35 + doubleStaffHeight;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "b";
          }
        }
      } else if (this.sameNotePitch(7)) {
        this.name = SOL;
        this.height = (67 - this.pitch) * 35 / 12 + dportee - 35 + doubleStaffHeight;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      } else if (this.sameNotePitch(8)) {
        if (nrlevel.getCurrentTonality().issharp()) {
          this.name = SOL;
          this.height = (68 - this.pitch) * 35 / 12 + dportee - 35 + doubleStaffHeight;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "#";
          }
        } else {
          this.name = LA;
          this.height = (68 - this.pitch) * 35 / 12 + dportee - 40 + doubleStaffHeight;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "b";
          }
        }
      } else if (this.sameNotePitch(9)) {
        this.name = LA;
        this.height = (69 - this.pitch) * 35 / 12 + dportee - 40 + doubleStaffHeight;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      } else if (this.sameNotePitch(10)) {
        if (nrlevel.getCurrentTonality().isflat()) {
          this.name = SI;
          this.height = (70 - this.pitch) * 35 / 12 + dportee - 45 + doubleStaffHeight;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "b";
          }
        } else {
          this.name = LA;
          this.height = (70 - this.pitch) * 35 / 12 + dportee - 40 + doubleStaffHeight;
          if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
            this.alteration = "";
          } else {
            this.alteration = "#";
          }
        }
      } else if (this.sameNotePitch(11)) {
        this.name = SI;
        this.height = (71 - this.pitch) * 35 / 12 + dportee - 45 + doubleStaffHeight;
        if (accidentalInTonality(nrlevel.getCurrentTonality(), bundle)) {
          this.alteration = "n";
        } else {
          this.alteration = "";
        }
      }
    }
  }
}

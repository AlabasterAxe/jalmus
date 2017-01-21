package net.jalmus;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ResourceBundle;

/**
 * <p>Title: Jalmus</p>
 *
 * <p>Description: Free software for sight reading</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author RICHARD Christophe
 * @version 1.0
 */
public class Chord {

    private enum InversionType {
        UNINVERTED(0),
        FIRST_INVERSION(1),
        SECOND_INVERSION(2);
        InversionType(int value) {
            this.value = value;
        }

        private final int value;

        int toInt() {
            return value;
        }

        static InversionType fromInt(int type) {
            switch (type) {
                case 0:
                    return UNINVERTED;
                case 1:
                    return FIRST_INVERSION;
                case 2:
                    return SECOND_INVERSION;
                default:
                    throw new IllegalArgumentException("Invalid inversion value must be one of 0,1,2. You provided: "
                            + type);
            }
        };
    }
    Note tabNotes[] = new Note [3];
    String name;
    InversionType inversion;
    //AudioClip son;

    public Chord (Note note1, Note note2, Note note3, String name, int inversion){
        this.tabNotes[0] = note1;
        this.tabNotes[1] = note2;
        this.tabNotes[2] = note3;
        this.name = name;
        this.inversion = InversionType.fromInt(inversion);
    }

    public Note getNote(int i){
        return this.tabNotes[i];
    }

    public String getName() {
        return this.name;
    }

    public int getInversion() {
        return this.inversion.toInt();
    }


    public void copy (Chord a){
        this.tabNotes[0] = new Note(a.tabNotes[0].getHeight(),a.tabNotes[0].getX(),a.tabNotes[0].getPitch());
        this.tabNotes[1] = new Note(a.tabNotes[1].getHeight(),a.tabNotes[1].getX(),a.tabNotes[1].getPitch());
        this.tabNotes[2] = new Note(a.tabNotes[2].getHeight(),a.tabNotes[2].getX(),a.tabNotes[2].getPitch());
        this.name = a.name;
        this.inversion = a.inversion;
    }

    public int getNotePosition(int pos, Tonality tcourante, ResourceBundle bundle){
        // to modify position of the note in the chord according to alteration
        int nbalt = 0;
        int resultat = 10;


        //pr = this.posreelle(pos);
        if (this.tabNotes[pos].getAlteration() == "") resultat = 0;
        else {
            for (int i=0;i<3;i=i+1){
                if (((this.tabNotes[i].getAlteration() == "#" | this.tabNotes[i].getAlteration() == "b")& !this.tabNotes[i].accidentalInTonality(tcourante,bundle))
                        | this.tabNotes[i].getAlteration() == "n")
                    nbalt++;
            }

            if (nbalt == 0) resultat = 0;
            else if (nbalt == 1) resultat = 10;
            else if (nbalt == 2) {
                switch (this.inversion) {
                    case UNINVERTED:
                        if (pos == 0) resultat = 10;
                        else if (pos == 1 & this.tabNotes[0].getAlteration() != "") resultat = 20;
                        else if (pos == 1 & this.tabNotes[2].getAlteration() != "") resultat = 10;
                        /*else if (pos == 2 & this.acc[0].getAlteration() != "") resultat = 8;*/
                        else resultat = 20;
                        break;
                    case FIRST_INVERSION:
                        if (pos ==0 &  this.tabNotes[2].getAlteration() != "") resultat = 20;
                        else if (pos == 2 &  this.tabNotes[1].getAlteration() != "") resultat = 20;
                        else resultat = 10;
                        break;
                    case SECOND_INVERSION:
                        if (pos == 1 &  this.tabNotes[0].getAlteration() != "") resultat = 20;
                        else if (pos == 0 &  this.tabNotes[2].getAlteration() != "") resultat = 20;
                        else resultat = 10;
                        break;
                    default:
                        throw new AssertionError("I want to punch a baby.");
                }
            }

            else if (nbalt == 3) {
                if (this.inversion.toInt() == 0) {
                    if (pos == 0) resultat = 14;
                    else if (pos == 1) resultat = 24;
                    else if (pos == 2) resultat = 8;
                }
                else if (this.inversion.toInt() == 1) {
                    if (pos == 2) resultat = 24;
                    else if (pos == 0) resultat = 8;
                    else resultat = 14;
                }
                else if (this.inversion.toInt() == 2) {
                    if (pos == 0) resultat = 24;
                    else if (pos == 1) resultat = 8;
                    else resultat = 14;
                }
            }
        }
        return resultat;
    }

    public void printName(Graphics g){
        Color red = new Color(242, 179, 112);
        g.setColor(red);
        g.setFont(new Font("Arial",Font.BOLD,17));
        g.drawString(this.name,380-this.name.length()*4,55);
    }

    public void paint(int position, NoteLevel nrlevel,Graphics g, Font f, boolean accordcourant,
                       Component j, int dportee,  ResourceBundle bundle){
        Color c = new Color(147,22,22);

        for (int i=0;i<3;i=i+1) {
            if (!(i== this.realPosition(position)& accordcourant))
                tabNotes[i].paint(nrlevel, g, f, this.getNotePosition(i,nrlevel.getCurrentTonality(), bundle),0, dportee, j, Color.black,bundle);
        }
        if (accordcourant) {
            tabNotes[this.realPosition(position)].paint(nrlevel,g,f,this.getNotePosition(this.realPosition(position),nrlevel.getCurrentTonality(), bundle),0, dportee, j, c, bundle);
        }

        // we paint the current note at the end to keep the color red
        if (nrlevel.isLearningGame()) {
            this.printName(g);
        }
    }


    public void move(int nb){
        for (int i=0;i<3;i=i+1) {
            this.tabNotes[i].setX(this.tabNotes[i].getX() + nb);
        }
    }

    public void updateX(int newX){
        // il faut mettre � jour la coordonn�e de l'accord pour le jeu en ligne
        for (int i=0; i<3; i=i+1) {
            this.tabNotes[i].setX(newX);
        }
    }

    public int realPosition(int pos){
        return (pos + this.inversion.toInt()) % 3;
    }

    public void convert(NoteLevel nrlevel){
        // convertit a chord to his inversion

        double tmp;

        if (nrlevel.isChordTypeInversion()) {
            tmp = Math.random();
            this.inversion = InversionType.UNINVERTED;
            if (tmp < 0.33) { // first inversion
                this.inversion = InversionType.FIRST_INVERSION;
                this.tabNotes[0].setHeight(this.tabNotes[0].getHeight() - 35);
                this.tabNotes[0].setPitch(this.tabNotes[0].getPitch() + 12);
            }
            else if (tmp > 0.33 & tmp < 0.66) { // second inversion
                this.inversion = InversionType.SECOND_INVERSION;
                this.tabNotes[0].setHeight(this.tabNotes[0].getHeight() - 35);
                this.tabNotes[0].setPitch(this.tabNotes[0].getPitch() + 12);
                this.tabNotes[1].setHeight(this.tabNotes[1].getHeight() - 35);
                this.tabNotes[1].setPitch(this.tabNotes[1].getPitch() + 12);
            }
        }
    }
}



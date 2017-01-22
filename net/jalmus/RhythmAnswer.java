/**
 * 
 */
package net.jalmus;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * @author christophe
 *
 */
public class RhythmAnswer {
  int posX; // position of the point
  int posY;
  boolean goodNote;
  int result; // 0 good rhythm, 1 false key pressed, 2 false key released, 3 show rythm
  /**
   *
   */
  public RhythmAnswer(int x, int y, boolean b, int result) {
    this.posX = x;
    this.posY = y;
    this.goodNote = b;
    this.result = result;

  }

  public void init() {
    this.posX = -1;
    this.posY = -1;
    this.result = 0;

  }

  public boolean isnull(){
    return this.posX == -1;
  }

  public boolean allGood(){
    return (this.goodNote && this.result == 0);
  }

  public boolean badNote(){
    return !this.goodNote;
  }

  public boolean badRhythm(){
    return ( this.result == 1);
  }

  public void setPosX(int x) {
    this.posX = x;
  }

  public int getPosX() {
    return this.posX;
  }

  public void setPosY(int y) {
    this.posX = y;
  }

  public int getPosY() {
    return this.posY;
  }

  public boolean isgoodnote() {
    return this.result == 0;
  }

  public void paint(Graphics g){
    Color cr = new Color(238, 0, 0);
    Color co = new Color(238, 153, 0);
    Color cg = new Color(152, 251, 152);
    Color ct = new Color(0, 0, 0);
    if (result == 0) g.setColor(cg);
    else if (result == 1) g.setColor(cr);
    else if (result == 2) g.setColor(co);
    else if (result == 3) g.setColor(ct);
    if (this.goodNote) g.fillOval(this.posX, this.posY -5, 10,10);
    else {
      // g.drawRect(this.posX-5, this.posY-5, 8, 4);
      Font f = new Font ("Sanserif", Font.BOLD, 16);
      g.setFont (f);
      g.drawString("X", this.posX, this.posY +10);
    }
  }
}

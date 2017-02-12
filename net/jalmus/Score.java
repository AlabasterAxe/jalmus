package net.jalmus;

import java.awt.*;


/**
 * <p>Title: Jalmus</p>
 * <p>
 * <p>Description: Free software for sight reading</p>
 * <p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>
 * <p>Company: </p>
 *
 * @author RICHARD Christophe
 * @version 1.0
 */
public class Score {

  static int maxpoints = 500;
  int nbtrue;
  int nbfalse;
  int points;
  Status status;
  public Score() {
    this.nbtrue = 0;
    this.nbfalse = 0;
    this.points = 100;
    status = Status.NO_RESULT;
  }

  public int getNbtrue() {
    return this.nbtrue;
  }

  public void setNbtrue(int i) {
    this.nbtrue = i;
  }

  public void addNbtrue(int i) {
    this.nbtrue = this.nbtrue + i;
  }

  public int getNbfalse() {
    return this.nbfalse;
  }

  public void setNbfalse(int i) {
    this.nbfalse = i;
  }

  public void addNbfalse(int i) {
    this.nbfalse = this.nbfalse + i;
  }

  public void setPoints(int i) {
    this.points = i;
  }

  public void setWin() {
    this.status = Status.WON;
  }

  public void setLost() {
    this.status = Status.LOST;

  }

  public boolean isWin() {
    return this.status == Status.WON;
  }

  public boolean isLost() {
    return this.status == Status.LOST;
  }

  public boolean isUnknown() {
    return this.status == Status.NO_RESULT;
  }

  public void initScore() {
    this.nbtrue = 0;
    this.nbfalse = 0;
    this.points = 100;
    status = Status.NO_RESULT;
  }

  /**
   * To add or substract points
   *
   * @param i for the number of points to add (if i>0) or substract (if i<0)
   *          <p>
   *          If new number of points reach the max points or 0 update the status parameter
   */

  public void addPoints(int i) {
    if (i > 0) {
      if (this.points + i < maxpoints) {
        this.points = this.points + i;
      } else {
        this.points = maxpoints;
        this.setWin();
      }
    } else { // i < 0 substract points
      if (this.points + i > 0) {
        this.points = this.points + i;
      } else {
        this.points = 0;
        this.setLost();
      }
    }
  }

  public void paint(Graphics g, int width) {
    int xPos = (width - 251) / 2;
    g.setColor(Color.black);
    g.draw3DRect(xPos, 420, 251, 20, true);
    for (int tmp = 0; tmp < this.points; tmp = tmp + 10) {
      if (tmp < 100) {
        g.setColor(new Color(60 + (tmp + 10) / 2, 26, 26));
      } else {
        g.setColor(new Color(110, 26 + (tmp - 90) / 2, 26));
      }
      g.fillRect(xPos + 1 + tmp / 2, 421, 5, 19);
    }
  }

  private enum Status {NO_RESULT, WON, LOST}
}

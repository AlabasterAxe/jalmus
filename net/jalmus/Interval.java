package net.jalmus;

import java.awt.*;
import java.util.ResourceBundle;

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
public class Interval {

  private SwingNote interval[] = new SwingNote[2];
  private String name;

  public Interval(SwingNote n1, SwingNote n2, String name) {
    this.interval[0] = n1;
    this.interval[1] = n2;
    this.name = name;
  }

  public SwingNote getNote(int i) {
    return this.interval[i];
  }

  public String getName() {
    return this.name;
  }

  public void copy(Interval a) {
    this.interval[0] = new SwingNote(a.interval[0].getHeight(),
        a.interval[0].getX(), a.interval[0].getPitch());
    this.interval[1] = new SwingNote(a.interval[1].getHeight(),
        a.interval[1].getX(), a.interval[1].getPitch());
    this.name = a.name;
  }

  public void move(int nb) {
    for (int i = 0; i < 2; i = i + 1) {
      this.interval[i].setX(this.interval[i].getX() + nb);
    }
  }

  public void printName(Graphics g) {
    // This is quite un-red, is this supposed to be red? is it just improperly named?
    Color red = new Color(242, 179, 112);
    g.setColor(red);
    g.setFont(new Font("Arial", Font.BOLD, 16));
    g.drawString(this.name, 380 - this.name.length() * 4, 55);
  }

  public void paint(int position, NoteLevel nrlevel, Graphics g, Font f, int dportee,
                    ResourceBundle bundle, boolean intervcourant, Component j) {

    Color c = new Color(147, 22, 22);

    if (position == 0 & intervcourant) {
      interval[0].paint(nrlevel, g, f, 9, 0, dportee, c, bundle);
    } else {
      interval[0].paint(nrlevel, g, f, 9, 0, dportee, Color.black, bundle);
    }

    if (position == 1 & intervcourant) {
      interval[1].paint(nrlevel, g, f, -19, 28, dportee, c, bundle);
    } else {
      interval[1].paint(nrlevel, g, f, -19, 28, dportee, Color.black, bundle);
    }

    if (nrlevel.isLearningGame()) { // name only for learning exercise
      this.printName(g);
    }
  }

  // to update the abscissa  of interval for the game in line
  public void updateX(int newX) {
    for (int i = 0; i < 2; i = i + 1) {
      this.interval[i].setX(newX);
    }
  }
}

package net.jalmus;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ChooseNotePanel extends JPanel {
  static final long serialVersionUID = 1L;
  private static int NOTEREADING = 1;
  private static int SCOREREADING = 3;
  JButton okButton;
  private boolean DEBUG = false;
  private JTable table;

  public ChooseNotePanel(String key, int leveltype, ResourceBundle bundle) {

    System.out.println(key + leveltype);

    if (leveltype == NOTEREADING && key == "treble") {
      table = new JTable(new TableKeyTreble());
    } else if (leveltype == NOTEREADING && key == "bass") {
      table = new JTable(new TableKeyBass());
    } else if (leveltype == NOTEREADING && key == "both") {
      table = new JTable(new NotesTableModel());
    } else if (leveltype == SCOREREADING && key == "treble") {
      table = new JTable(new TableKeyScoreTreble());
    } else if (leveltype == SCOREREADING && key == "bass") {
      table = new JTable(new TableKeyScoreBass());
    } else {
      table = new JTable(new NotesTableModel());
    }

    table.setPreferredScrollableViewportSize(new Dimension(610, 115));
    table.setFillsViewportHeight(false);


    for (int vColIndex = 1; vColIndex < 13; vColIndex++) {
      TableColumn col = table.getColumnModel().getColumn(vColIndex);
      col.setCellRenderer(new CheckBoxTableCellRenderer());
    }

    TableColumn column = table.getColumnModel().getColumn(0);
    column.setPreferredWidth(110);

    //Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);

    okButton = new JButton();
    okButton.setText(bundle.getString("_buttonok"));
    okButton.setIcon(new ImageIcon(getClass().getResource("/images/ok.png")));

    JButton clearButton = new JButton();
    clearButton.setText(bundle.getString("_buttonclear"));
    clearButton.setIcon(new ImageIcon(getClass().getResource("/images/eraser.png")));
    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        resettable();
      }
    });


    // Add the scroll pane to this panel
    // setLayout(new BorderLayout());
    add(scrollPane);
    add(okButton);
    add(clearButton);
  }

  public JTable getTable() {
    return this.table;
  }

  public JButton getButton() {
    return this.okButton;
  }

  public ArrayList<Integer> getPitches() {
    ArrayList<Integer> pitchselected = new ArrayList<Integer>();

    int numRows = table.getRowCount();
    int numCols = table.getColumnCount();
    for (int i = 0; i < numRows; i++) {
      for (int j = 1; j < numCols; j++) {
        if ((Boolean) table.getValueAt(i, j))
          pitchselected.add(24 + 12 * i + (j - 1)); // first note Octave -3 C pitch 24
      }
    }
    return pitchselected;
  }


  public void updateTable(ArrayList<Integer> pitcheslist) {
    int tmp = 0;
    for (Integer pitch : pitcheslist) {
      //to do
      int numRows = table.getRowCount();
      int numCols = table.getColumnCount();
      for (int i = 0; i < numRows; i++) {
        for (int j = 1; j < numCols; j++) {
          tmp = 24 + 12 * i + (j - 1);
          if (tmp == pitch) {
            table.setValueAt(true, i, j);
            //System.out.println("i: "+i+" j: "+j );
          }
        }
      }
    }
  }

  public boolean atLeast3Pitches() {
    int nbpitches = 0;
    int numRows = table.getRowCount();
    int numCols = table.getColumnCount();
    for (int i = 0; i < numRows; i++) {
      for (int j = 1; j < numCols; j++) {
        if ((Boolean) table.getValueAt(i, j)) {
          nbpitches++;
        }
      }
    }
    return (nbpitches >= 3);
  }

  public void resettable() {
    int numRows = table.getRowCount();
    int numCols = table.getColumnCount();
    for (int i = 0; i < numRows; i++) {
      for (int j = 1; j < numCols; j++) {
        table.setValueAt(false, i, j);
      }
    }
  }

  //To modify checkbox background when not editable

  class CheckBoxTableCellRenderer extends JCheckBox
      implements TableCellRenderer {

    static final long serialVersionUID = 1L;

    Border noFocusBorder;
    Border focusBorder;

    public CheckBoxTableCellRenderer() {
      super();
      //setOpaque(true);
      setContentAreaFilled(true);  // use this instead of setOpaque()
      setBorderPainted(true);
      setHorizontalAlignment(SwingConstants.CENTER);
      setVerticalAlignment(SwingConstants.CENTER);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row, int column) {

      if (table == null) {
        // ???
      } else {
        if (isSelected) {
          setForeground(table.getSelectionForeground());
          setBackground(table.getSelectionBackground());
          if (!table.isCellEditable(row, column)) {
            this.setBackground(Color.BLACK);
          }
        } else {
          setForeground(table.getForeground());
          setBackground(table.getBackground());
          if (!table.isCellEditable(row, column)) {
            this.setBackground(Color.BLACK);
          }
        }
        setEnabled(table.isEnabled());
        setComponentOrientation(table.getComponentOrientation());

        if (hasFocus) {
          if (focusBorder == null) {
            focusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
          }
          setBorder(focusBorder);
        } else {
          if (noFocusBorder == null) {
            if (focusBorder == null) {
              focusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
            }
            if (focusBorder != null) {
              Insets n = focusBorder.getBorderInsets(this);
              noFocusBorder = new EmptyBorder(n);
            }
          }
          setBorder(noFocusBorder);
        }

        setSelected(Boolean.TRUE.equals(value));
      }
      return this;
    }
  }

  // Table Model generic
  class NotesTableModel extends AbstractTableModel {

    static final long serialVersionUID = 1L;

    private String[] columnNames = {"Octave", "C", "C#/Db", "D", "D#/Eb", "E", "F", "F#/Gb", "G", "G#/Ab", "A", "A#/Bb", "B"};
    private Object[][] data = {
        {"Octave -3", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE},
        {"Octave -2", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE},
        {"Octave -1", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE},
        {"Octave 0", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE},
        {"Octave 1", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE},
        {"Octave 2", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE},
        {"Octave 3", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE}
    };

    public int getColumnCount() {
      return columnNames.length;
    }

    public int getRowCount() {
      return data.length;
    }

    public String getColumnName(int col) {
      return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
      return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class<?> getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }


    public boolean isCellEditable(int row, int col) {
      // Note that the data/cell address is constant,
      // no matter where the cell appears onscreen.
      // pitch 26 D-3 to 96 C+3
      return !((row == 0 && col < 3) || (row == 6 && col > 1));
    }

    public void setValueAt(Object value, int row, int col) {
      if (DEBUG) {
        System.out.println("Setting value at " + row + "," + col
            + " to " + value
            + " (an instance of "
            + value.getClass() + ")");
      }

      data[row][col] = value;
      fireTableCellUpdated(row, col);

      if (DEBUG) {
        System.out.println("New value of data:");
        printDebugData();
      }

    }

    private void printDebugData() {
      int numRows = getRowCount();
      int numCols = getColumnCount();

      for (int i = 0; i < numRows; i++) {
        System.out.print("    row " + i + ":");
        for (int j = 0; j < numCols; j++) {
          System.out.print("  " + data[i][j]);
        }
        System.out.println();
      }
      System.out.println("--------------------------");
    }
  }


  class TableKeyTreble extends NotesTableModel {

    static final long serialVersionUID = 1L;

    public boolean isCellEditable(int row, int col) {

      int numCols = getColumnCount();

      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.
      // pitch 47 b-2 to 96 c+3
      if ((col < 1)  //col indicate the octava
          || (row == 0 & col < numCols)
          || (row == 1 & col < numCols - 1)
          || (row == 6 & col > 1)) {
        return false;
      } else {
        return true;
      }
    }
  }

  class TableKeyBass extends NotesTableModel {
    static final long serialVersionUID = 1L;

    public boolean isCellEditable(int row, int col) {
      int numCols = getColumnCount();
      // Note that the data/cell address is constant,
      // no matter where the cell appears onscreen.
      // pitch 26 D-3 to 74 D+1
      if ((col < 1)  //col indicate the octava
          || (row == 0 & col < 3)
          || (row == 4 & col > 3)
          || (row == 5 & col < numCols)
          || (row == 6 & col < numCols)) { //pitch 24 25 not supported
        return false;
      } else {
        return true;
      }
    }
  }

  class TableKeyScoreTreble extends NotesTableModel {
    static final long serialVersionUID = 1L;

    public boolean isCellEditable(int row, int col) {

      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.
      // pitch c0 60 c2 84
      if ((col < 1) //col indicate the octava
          || (row == 0) || (row == 1)
          || (row == 2 & col < 8)
          || (row == 5 & col > 1)
          || (row == 6)) {
        return false;
      } else {
        return true;
      }
    }
  }

  class TableKeyScoreBass extends NotesTableModel {
    static final long serialVersionUID = 1L;

    public boolean isCellEditable(int row, int col) {
      int numCols = getColumnCount();
      // Note that the data/cell address is constant,
      // no matter where the cell appears onscreen.
      // pitch 26 D-3 to 74 D+1
      if ((col < 1)  //col indicate the octava
          || (row == 0 & col < 12)
          || (row == 3 & col > 5)
          || (row == 4 & col < numCols)
          || (row == 5 & col < numCols)
          || (row == 6 & col < numCols)) { //pitch 24 25 not supported
        return false;
      } else {
        return true;
      }
    }
  }


}

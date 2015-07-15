package net.jalmus;

import java.util.ResourceBundle;

import javax.swing.JPanel;

public interface SwingGame {
  
  JPanel getPreferencesPanel();

  String getPreferencesIconResource();
  
  String getPreferencesLocalizable();

  void updateLanguage(ResourceBundle bundle);
}
package net.jalmus;

import javax.swing.*;
import java.util.ResourceBundle;

public interface SwingGame {

  JPanel getPreferencesPanel();

  String getPreferencesIconResource();

  String getPreferencesLocalizable();

  void updateLanguage(ResourceBundle bundle);

  void changeScreen();

  int[] serializePrefs();

  void deserializePrefs(int[] prefs);

  void showResult();
}

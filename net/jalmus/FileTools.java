package net.jalmus;

import java.io.File;

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
public class FileTools {

  /**
   * Pour connaitre l'extension du fichier.
   * <p>
   * Renvoi la chaine de charactères après le dernier '.' du nom de fichier.
   *
   * @param file Le fichier à analyser
   * @return Renvoi l'extension du fichier.
   */
  public static String getFileExtension(File file) {
    String extension = "";
    String s = file.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 && i < s.length() - 1) {
      extension = s.substring(i + 1);
    }
    return extension;
  }

  /**
   * Pour avoir le nom du fichier sans l'extension.
   * <p>
   * Renvoi la chaine de charactère du nom avant le dernier '.';
   *
   * @param file Le fichier à analyser.
   * @return Le nom du fichier sans l'extension.
   */
  public static String getFileNameWithoutExtension(File file) {
    String name = file.getName();
    String s = file.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 && i < s.length() - 1) {
      name = s.substring(0, i);
    }
    return name;
  }


}

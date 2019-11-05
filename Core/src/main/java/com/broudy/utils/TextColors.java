package com.broudy.utils;

/**
 * <p>
 * Created on the 3rd of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public enum TextColors {
  RED("\u001B[31m"), GREEN("\u001B[32m"), YELLOW("\u001B[33m"), BLUE("\u001B[34m"), PURPLE(
      "\u001B[35m"), CYAN("\u001B[36m"), WHITE("\u001B[37m");

  private String startColoring;
  private String stopColoring;

  TextColors(final String startColoring) {
    this.startColoring = startColoring;
    this.stopColoring = "\u001B[0m";
  }

  public String colorThis(String text) {
    return startColoring + text + stopColoring;
  }

  public String colorThis(Object object) {
    return colorThis(object.toString());
  }
}

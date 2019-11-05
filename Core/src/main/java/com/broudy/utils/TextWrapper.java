package com.broudy.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * TODO provide a summary to TextWrapper class!!!!!
 * <p>
 * Created on the 14th of August, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class TextWrapper {

  public static void main(String[] args) {

    String myText = "Hey This4 works! [asd23] asdr32 sdlkasf asdk adf adkfj akdj naksd kjasd ";
    ArrayList<String> list = new ArrayList<>();
    list.add("hi");
    list.add("hi24");
    list.add("hi34");
    System.out.println("\t");
    System.out.println("1234567891123456789");
    System.out.println(leftIndent(myText, 5, 21, true));
    System.out.println(leftIndent(list.toString(), 0, 10, false));
  }

  public static String leftIndent(String text, int numberOfLeftSpaces, int maxLength,
      boolean indentFirstLine) {
    return leftIndent(text, numberOfLeftSpaces, maxLength, indentFirstLine, null);
  }

  public static String leftIndent(String text, int numberOfLeftSpaces, int maxLength,
      TextColors color) {
    return leftIndent(text, numberOfLeftSpaces, maxLength, false, color);
  }

  public static String leftIndent(Object object, int numberOfLeftSpaces, int maxLength,
      boolean indentFirstLine) {
    return leftIndent(object != null ? object.toString() : "NULL ", numberOfLeftSpaces, maxLength,
        indentFirstLine, null);
  }

  public static String leftIndent(Object object, int numberOfLeftSpaces, int maxLength,
      boolean indentFirstLine, TextColors color) {
    return leftIndent(object != null ? object.toString() : "NULL ", numberOfLeftSpaces, maxLength,
        indentFirstLine, color);
  }

  public static String leftIndent(String text, int numberOfLeftSpaces, int maxLength,
      boolean indentFirstLine, TextColors color) {

    if (text == null) {
      return "null";
    }

    // ArrayList<String> words = new ArrayList(Arrays.asList(text.split(" ")));
    ArrayList<String> words = new ArrayList(Arrays.asList((text.replace("\n", "\n ")).split(" ")));

    if (words.get(0).length() > maxLength) {
      throw new StringIndexOutOfBoundsException();
    }

    StringBuilder leftIndentation = new StringBuilder("\n");
    leftIndentation.append(new String(new char[numberOfLeftSpaces]).replace("\0", " "));
    //     leftIndentation.append(new String(new char[numberOfLeftSpaces / 2]).replace("\0", "\t"));
    // if (numberOfLeftSpaces % 2 == 1) {
    //   leftIndentation.append(" ");
    // }

    if (leftIndentation.length() + 1 + words.get(0).length() > maxLength) {
      throw new StringIndexOutOfBoundsException();
    }

    int lengthOfFillableSpace = maxLength - numberOfLeftSpaces;

    StringBuilder finalString;
    if (indentFirstLine) {
      finalString = new StringBuilder(leftIndentation.toString());
    } else {
      finalString = new StringBuilder();
    }

    finalString.append(words.get(0));
    int currentRowLength = words.get(0).length();
    words.remove(0);

    //TODO: check if some word doesn't fit in 1 line & decide what to do about it.
    for (String word : words) {
      /* 1 -> space between to be concatenated words */
      if (currentRowLength + 1 + word.length() > lengthOfFillableSpace) {
        finalString.append(leftIndentation.toString());
        currentRowLength = 0;
      } else {
        finalString.append(" ");
        currentRowLength++;
      }
      finalString.append(word);
      currentRowLength += word.length();
    }

    if (color != null) {
      return color.colorThis(finalString.toString());
    }
    return finalString.toString();
  }

//  public static String leftIndent(String text, int numberOfLeftSpaces, int maxLength, boolean indentFirstLine) {
//
//     if (text == null) {
//       return "null";
//     }
//
//     ArrayList<String> words = new ArrayList(Arrays.asList(text.split(" ")));
//
//     if (words.get(0).length() > maxLength) {
//       throw new StringIndexOutOfBoundsException();
//     }
//
//     StringBuilder leftIndentation = new StringBuilder();
//     leftIndentation.append(new String(new char[numberOfLeftSpaces / 2]).replace("\0", "\t"));
//
//
//     if (numberOfLeftSpaces % 2 == 1) {
//       leftIndentation.append(" ");
//     }
//
//     if (leftIndentation.length() + 1 + words.get(0).length() > maxLength) {
//       throw new StringIndexOutOfBoundsException();
//     }
//
//     int lengthOfFillableSpace = maxLength - numberOfLeftSpaces;
//
//     StringBuilder finalString;
//     if(indentFirstLine){
//       finalString  = new StringBuilder();
//     }
//     else{
//        finalString = new StringBuilder(leftIndentation.toString());
//     }
//
//     finalString.append(words.get(0));
//     int currentRowLength = words.get(0).length();
//     words.remove(0);
//
//     //TODO: check if some word doesn't fit in 1 line & decide what to do about it.
//     for (String word : words) {
//       /* 1 -> space between to be concatenated words */
//       if (currentRowLength + 1 + word.length() > lengthOfFillableSpace) {
//         finalString.append("\n").append(leftIndentation.toString());
//         currentRowLength = 0;
//       } else {
//         finalString.append(" ");
//         currentRowLength++;
//       }
//       finalString.append(word);
//       currentRowLength += word.length();
//     }
//
//     return finalString.toString();
//   }
}

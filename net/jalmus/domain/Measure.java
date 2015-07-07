package net.jalmus.domain;

import java.util.SortedMap;
import java.util.TreeMap;

public class Measure {

  SortedMap<Double, Schedulable> notes = new TreeMap<>();
  
  private double tempo;
  private int timeSignatureNumerator;
  private int timeSignatureDenominator;
  
  
  
}

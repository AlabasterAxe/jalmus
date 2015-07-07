package net.jalmus.util;

import java.util.ArrayList;

public class ImmutableList<T> {
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1;
  
  private final ArrayList<T> data;
  
  private ImmutableList(T t1) {
    this.data = new ArrayList<T>();
  }
  
  public boolean contains(T t) {
    return data.contains(t);
  }
  
  public T get(int index) {
    return data.get(index);
  }
}

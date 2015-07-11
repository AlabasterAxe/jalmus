package net.jalmus.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This is a data structure for using cyclicly repeating
 * elements. It has two iterators, one forward, one backward.
 * The iterators can be initialized at a particular point but
 * they never end.
 */
public class Cycle<E> {
  
  private final List<E> elements;
  
  public Cycle() {
    elements = new ArrayList<E>();
  }
  
  public Cycle(Collection<E> list) {
    elements = new ArrayList<E>(list);
  }
  
  public void add(E e) {
    elements.add(e);
  }

  public E get(int i) {
    return elements.get(i);
  }
  
  public Iterator<E> iterator() {
    return iterator(0);
  }
  
  public Iterator<E> iterator(final int i) {
    return new Iterator<E>() {
      
      private int offset = i;

      @Override
      public boolean hasNext() {
        return (elements.size() > 0);
      }

      @Override
      public E next() {
        offset %= elements.size();
        E e = elements.get(offset);
        offset++;
        return e;
      }

      @Override
      public void remove() {
        elements.remove(offset);
      }
    };
  }
  
  public Iterator<E> reverseIterator() {
    return reverseIterator(0);
  }
  
  public Iterator<E> reverseIterator(final int i) {
    return new Iterator<E>() {
      
      private int offset = i;

      @Override
      public boolean hasNext() {
        return (elements.size() > 0);
      }

      @Override
      public E next() {
        if (elements.size() == 0) {
          return null;
        }

        offset %= elements.size();
        E e = elements.get(offset);
        offset--;
        return e;
      }

      @Override
      public void remove() {
        elements.remove(offset);
      }
    };
  }
}

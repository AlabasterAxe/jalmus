package net.jalmus.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This is a data structure for using cyclicly repeating
 * elements. It has two iterators, one forward, one backward.
 * The iterators can be initialized at a particular point but
 * they never end.
 */
public class ListCycle<E> implements List<E> {
  
  private abstract class AbstractCyclicIterator implements ListIterator<E> {

    private int offset;
    private Integer previouslyReturned = null;
    
    protected AbstractCyclicIterator(int offset) {
      this.offset = offset;
    }

    @Override
    public boolean hasNext() {
      return (elements.size() > 0);
    }

    @Override
    public E next() {
      if (elements.size() == 0) {
        return null;
      }

      E e = elements.get(getRealOffset(offset));
      previouslyReturned = getRealOffset(offset);
      offset++;
      return e;
    }

    @Override
    public void remove() {
      elements.remove(offset);
      previouslyReturned = null;
    }

    @Override
    public void add(E e) {
      elements.add(getRealOffset(offset), e);
      previouslyReturned = null;
    }

    @Override
    public boolean hasPrevious() {
      return (elements.size() > 0);
    }

    @Override
    public int nextIndex() {
      return offset;
    }

    @Override
    public E previous() {
      if (elements.size() == 0) {
        return null;
      }

      E e = elements.get(getRealOffset(offset-1));
      previouslyReturned = getRealOffset(offset-1);
      offset--;
      return e;
    }

    @Override
    public int previousIndex() {
      return offset-1;
    }

    @Override
    public void set(E e) {
      if (previouslyReturned == null) {
        throw new IllegalStateException();
      }
      elements.set(previouslyReturned, e);
    }

    abstract protected int getRealOffset(int cyclicOffset);
  }
  
  private class ForwardCyclicIterator extends AbstractCyclicIterator {
    
    ForwardCyclicIterator() {
      this(0);
    }
    
    ForwardCyclicIterator(int offset) {
      super(offset);
    }
    
    protected int getRealOffset(int cyclicOffset) {
      return cyclicOffset % elements.size();
    }
  }
  
  private class ReverseCyclicIterator extends AbstractCyclicIterator {

    ReverseCyclicIterator(int offset) {
      super(offset);
    }
    
    @Override
    protected int getRealOffset(int cyclicOffset) {
      int forwardIndex = cyclicOffset % elements.size();
      return elements.size() - forwardIndex;
    }
  }
  
  private final List<E> elements;
  
  public ListCycle() {
    elements = new ArrayList<E>();
  }
  
  public ListCycle(Collection<E> list) {
    elements = new ArrayList<E>(list);
  }
  
  @Override
  public boolean add(E e) {
    return elements.add(e);
  }

  @Override
  public E get(int i) {
    i %= elements.size();
    return elements.get(i);
  }

  @Override
  public boolean remove(Object o) {
    return elements.remove(o);
  }

  public void clear() {
    elements.clear();
  }
  
  public Iterator<E> iterator() {
    return iterator(0);
  }
  
  public Iterator<E> iterator(final int i) {
    return new ForwardCyclicIterator(i);
  }
  
  public Iterator<E> reverseIterator() {
    return reverseIterator(0);
  }
  
  public Iterator<E> reverseIterator(final int i) {
    return new ReverseCyclicIterator(i);
  }

  @Override
  public void add(int index, E element) {
    elements.add(getRealOffset(index), element);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    return elements.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    return elements.addAll(getRealOffset(index), c);
  }

  @Override
  public boolean contains(Object o) {
    return elements.contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return elements.containsAll(c);
  }

  @Override
  public int indexOf(Object o) {
    return elements.indexOf(o);
  }

  @Override
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  /**
   * lastIndexOf doesn't really make sense
   * in the context of a cycle.
   */
  @Override
  public int lastIndexOf(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ListIterator<E> listIterator() {
    return new ForwardCyclicIterator();
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return new ForwardCyclicIterator(index);
  }

  @Override
  public E remove(int index) {
    return elements.remove(getRealOffset(index));
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return elements.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return elements.retainAll(c);
  }

  @Override
  public E set(int index, E element) {
    return elements.set(getRealOffset(index), element);
  }

  /**
   * This gives the size of the elements that are repeating.
   */
  @Override
  public int size() {
    return elements.size();
  }

  /**
   * This subList method allows consumers of it to generate a list
   * larger than the size of the list of repeating elements. If the
   * fromIndex is less than 0, we continue the cycle.
   * 
   * If the fromIndex is greater than the toIndex, we build the list
   * backwards.
   * 
   * @param fromIndex
   * @param toIndex
   * @return 
   */
  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    if (elements.size() == 0) {
      throw new IllegalStateException("Out of Bounds");
    }

    Iterator<E> iter;
    if(toIndex < fromIndex) {
      iter = iterator(fromIndex);
    } else {
      iter = reverseIterator(toIndex);
    }
    
    ArrayList<E> result = new ArrayList<>();
    int end = Math.abs(toIndex - fromIndex);
    for(int i = 1; i < end; i++) {
      result.add(iter.next());
    }
    
    return result;
  }

  @Override
  public Object[] toArray() {
    return elements.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return elements.toArray(a);
  }

  private int getRealOffset(int cyclicOffset) {
    return cyclicOffset % elements.size();
  }
}

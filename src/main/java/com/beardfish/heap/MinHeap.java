package com.beardfish.heap;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * MinHeap that stores the minimum value at the top according to the natural ordering
 * This heap can also be ordered by providing a comparator!
 * @author Christian
 *
 * @param <E> - the element stored in the heap
 */

public class MinHeap<E> extends AbstractQueue<E> {
  
  // static variable
  private static final int DEFAULT_INITIAL_CAPACITY = 12;
  // instance variables
  private final Comparator<? super E> comparator;
  private transient Object [] queue;
  private int size = 0;
  
  public MinHeap() {
    this(DEFAULT_INITIAL_CAPACITY,null);
  }
  
  public MinHeap(Collection<? extends E> c) {
    initFromCollection(c);
    if(c instanceof MinHeap) {
      this.comparator = (Comparator<? super E>)((MinHeap<? extends E>)c).comparator();
    } else {
      this.comparator = null;
    }
  }
  
  public MinHeap(int initialCapacity) {
    this(initialCapacity,null);
  }
  
  public MinHeap(int initialCapacity, Comparator<? super E> comparator) {
    if(initialCapacity<1) {
      throw new IllegalArgumentException();
    }
    this.queue = new Object[initialCapacity];
    this.comparator = comparator;
  }
  
  public MinHeap(MinHeap<? extends E> c) {
    this.comparator = (Comparator<? super E>)c.comparator();
    initFromCollection(c);
  }
  
  private void initFromCollection(Collection<? extends E> c) {
    Object[] cArray = c.toArray();
    if(cArray.getClass()!=Object[].class) {
      cArray = Arrays.copyOf(cArray,cArray.length,Object[].class);
      this.queue = cArray;
      this.size = cArray.length;
    }
  }
  


  @Override
  public boolean offer(E arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public E peek() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public E poll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterator<E> iterator() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int size() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  public Comparator<? super E> comparator() {
    return this.comparator;
  }

}

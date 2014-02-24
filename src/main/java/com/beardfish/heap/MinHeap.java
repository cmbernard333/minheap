package com.beardfish.heap;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * MinHeap that stores the minimum value at the top according to the natural
 * ordering This heap can also be ordered by providing a comparator!
 * 
 * @author Christian
 * 
 * @param <E>
 *            - the element stored in the heap
 */

public class MinHeap<E> extends AbstractQueue<E> {

	// static variable
	private static final int DEFAULT_INITIAL_CAPACITY = 12;
	// instance variables
	private final Comparator<? super E> comparator;
	private transient Object[] queue;
	private int size = 0;
	// modification count for the iterator
	private transient int modCount = 0;

	public MinHeap() {
		this(DEFAULT_INITIAL_CAPACITY, null);
	}

	public MinHeap(Collection<? extends E> c) {
		initFromCollection(c);
		if (c instanceof MinHeap) {
			this.comparator = (Comparator<? super E>) ((MinHeap<? extends E>) c).comparator();
		} else {
			this.comparator = null;
		}
	}

	public MinHeap(int initialCapacity) {
		this(initialCapacity, null);
	}

	public MinHeap(int initialCapacity, Comparator<? super E> comparator) {
		if (initialCapacity < 1) {
			throw new IllegalArgumentException();
		}
		this.queue = new Object[initialCapacity];
		this.comparator = comparator;
	}

	public MinHeap(MinHeap<? extends E> c) {
		this.comparator = (Comparator<? super E>) c.comparator();
		initFromCollection(c);
	}

	private void initFromCollection(Collection<? extends E> c) {
		Object[] cArray = c.toArray();
		if (cArray.getClass() != Object[].class) {
			cArray = Arrays.copyOf(cArray, cArray.length, Object[].class);
			this.queue = cArray;
			this.size = cArray.length;
		}
	}

	@Override
	public boolean offer(E object) {
		// TODO Auto-generated method stub
		if(object==null) {
			throw new NullPointerException();
		}
		/* update the modCount to make sure you invalidate the iterator */
		this.modCount++;
		
		int i = this.size;
		
		/* check the capacity of the array */
		if(i>=this.queue.length) {
			grow(i+1);
		}
		
		this.size = i + 1;
		
		if(i==0) {
			this.queue[0] = object;
		} else {
			percolateUp(i,object);
		}
		
		return true;
	}

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		if (this.size == 0) {
			return null;
		}
		return (E) this.queue[0];
	}

	@Override
	public E poll() {
		// TODO Auto-generated method stub
		if(this.size==0) {
			return null;
		}
		
		int newSize = --size;
		this.modCount++;
		E head = (E) this.queue[0];
		
		return head;
		
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.size;
	}

	public Comparator<? super E> comparator() {
		return this.comparator;
	}
	
	/**
	 * Grow the heap by the integer specified
	 * @param i
	 */
	public void grow(int minCapacity) {
		if(minCapacity<0) {
			throw new OutOfMemoryError();
		}
		int oldCapacity = this.queue.length;
		int newCapacity = ((oldCapacity<64)) ?
				((oldCapacity+1) * 2) : 
				((oldCapacity/2) * 3);
		if(newCapacity<0) {
			newCapacity = Integer.MAX_VALUE;
		}
		if(newCapacity < minCapacity) {
			newCapacity = minCapacity;
		}
		this.queue = Arrays.copyOf(this.queue,newCapacity);
	}
	
	/**
	 * Percolate the element up until you find one that is less than or equal to
	 * the element you are trying to add
	 */
	private void percolateUp(int index, E element) {
		if(this.comparator!=null) {
			percolateUpUsingComparator(index,element);
		} else {
			percolateUpComparable(index,element);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void percolateUpUsingComparator(int index, E element) {
		if(index==0) {
			return;
		}
		int p = this.getParent(index);
		E parent = (E) this.queue[p];
		if(this.comparator.compare(element,parent)<0) {
			this.queue[index] = parent;
			this.queue[p] = element;
			if(p!=0) {
				this.percolateUpUsingComparator(p, element);
			}
		} else {
			this.queue[index] = element;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void percolateUpComparable(int index, E element) {
		if(index==0) {
			return;
		}
		Comparable<? super E> key = (Comparable<? super E>) element;
		int p = this.getParent(index);
		E parent = (E) this.queue[p];
		if(key.compareTo(parent)<0) {
			this.queue[index] = parent;
			this.queue[p] = element;
			if(p!=0) {
				this.percolateUpComparable(p,element);
			}
		} else {
			this.queue[index] = element;
		}
	}

	
	/**
	 * Find the object in the queue 
	 * @param o
	 * @return 0 if found; -1 otherwise
	 */
	private int indexOf(Object o) {
		if(o!=null) {
			for(int i = 0;i<this.size;i++) {
				if(o.equals(this.queue[i])) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Remove the particular object if it exists
	 * @param o - the object to remove
	 * @return true if it exists; false otherwise
	 */
	public boolean remove(Object o) {
		int index = indexOf(o);
		if(index!=-1) {
			removeAt(index);
			return true;
		}
		return false;
	}
	
	/**
	 * Remove the object at the specified index
	 * @param index
	 * @return the object removed
	 */
	public E removeAt(int index) {
		assert index >= 0 && index < size;
		this.modCount++;
		int s = --size;
		E element = (E) this.queue[index];
		/* last element */
		if(s==index) {
			this.queue[index] = null;
		} else {
			/* place the last element at the index of the removed element */
			this.queue[index] = this.queue[s];
			this.queue[s] = null;
			this.percolateDown(index,(E) this.queue[index]);
		}
		return element;
		
	}

	
	/**
	 * percolateDown until you find that both children are less than the element
	 * while percolating you should replace with the smaller of the children to maintain
	 * the heap property
	 * @param index
	 * @param element
	 */
	private void percolateDown(int index, E element) {
		if(this.comparator!=null) {
			this.percolateDownWithComparator(index, element);
		} else {
			this.percolateDownComparable(index, element);
		}
	}
	
	/**
	 * Percolate down using the comparator was provided at construction time
	 * @param index
	 * @param element
	 */
	private void percolateDownWithComparator(int index, E element) {
		if(index==this.size) {
			return;
		}
		
		
	}
	
	/**
	 * Percolate down by casting an element to the Comparable of the appropriate type
	 * @param index
	 * @param element
	 */
	private void percolateDownComparable(int index, E element) {
		if(index==this.size) {
			return;
		}
		Comparable<? super E> key = (Comparable<? super E>) element;
	}
	
	/**
	 * Retrieve the parent index of a given item
	 * @param index
	 * @return the parent index
	 */
	private int getParent(int index) {
		// even index
		int p = 0;
		if(index%2==0) {
			p = (index-2)/2;
		} else {
			p = (index-1)/2;
		}
		return p;
	}
	
	/**
	 * Calculates the children for a given index
	 * Checks to see which one is smaller and returns the index
	 * It will return the index of a null item if its at the end
	 * @param index
	 * @return index of smaller child
	 */
	private int getSmallerChildComparator(int index) {
		int c = 0;
		int leftChild = (2*index)+1;
		int rightChild = (2*index)+2;
		if(this.queue[rightChild]==null) {
			c = leftChild;
		} else {
			if(this.comparator.compare((E)this.queue[leftChild],(E)this.queue[rightChild])<0) {
				c = leftChild;
			} else {
				c = rightChild;
			}
		}
		return c;
	}
	
	private int getSmallerChildComparable(int index) {
		int c = 0;
		int leftChild = (2*index)+1;
		int rightChild = (2*index)+2;
		if(this.queue[rightChild]==null) {
			c = leftChild;
		} else {
			Comparable<? super E> key = (Comparable<? super E>) this.queue[leftChild];
			if(key.compareTo((E)this.queue[rightChild])<0) {
				c = leftChild;
			} else {
				c = rightChild;
			}
		}
		return c;
	}
	
	
	@Override
	public String toString() {
		return Arrays.toString(this.queue);
	}
	
	public void clear() {
		modCount++;
		for(int i = 0; i< this.size; i++) {
			this.queue[i] = null;
		}
		this.size=0;
	}
	
	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return new Itr();
	}
	
	private final class Itr implements Iterator<E> {
		
		private int cursor = 0;
		
		private int lastRet = -1;
		
		private E lastRetElt = null;

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return this.cursor < size();
		}

		@Override
		public E next() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}

}

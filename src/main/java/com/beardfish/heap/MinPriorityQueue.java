package com.beardfish.heap;

import java.util.*;

/**
 * MinPriorityQueue that stores the minimum value at the top according to the natural
 * ordering This heap can also be ordered by providing a comparator!
 *
 * @author Christian
 *
 * @param <E>
 *            - the element stored in the heap
 */

public class MinPriorityQueue<E> extends AbstractQueue<E> {

    // static variable
    private static final int DEFAULT_INITIAL_CAPACITY = 12;
    // instance variables
    private final Comparator<? super E> comparator;
    private transient Object[] queue;
    private int size = 0;
    // modification count for the iterator
    private transient int modCount = 0;

    // lookup table to have efficient decrease key operation
    private final Map<E,Integer> valueLookup;

    public MinPriorityQueue() {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }

    public MinPriorityQueue(Collection<? extends E> c) {
        initFromCollection(c);
        if (c instanceof MinPriorityQueue) {
            this.comparator = (Comparator<? super E>) ((MinPriorityQueue<? extends E>) c).comparator();
        } else {
            this.comparator = null;
        }
        this.valueLookup = new HashMap<E,Integer>();
    }

    public MinPriorityQueue(int initialCapacity) {
        this(initialCapacity, null);
    }

    public MinPriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException();
        }
        this.queue = new Object[initialCapacity];
        this.comparator = comparator;
        this.valueLookup = new HashMap<E,Integer>();
    }

    public MinPriorityQueue(MinPriorityQueue<? extends E> c) {
        this.comparator = (Comparator<? super E>) c.comparator();
        initFromCollection(c);
        this.valueLookup = new HashMap<E,Integer>();
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
    public boolean add(E object) {
        return this.offer(object);
    }

    @Override
    public boolean offer(E object) {
        if(object==null) {
            throw new NullPointerException();
        }
		/* update the modCount to make sure you invalidate the iterator */
        this.modCount++;

        int i = this.size();
		
		/* check the capacity of the array */
        if(i>=this.queue.length) {
            grow(i+1);
        }

        /* update the size */
        this.size = i + 1;

        if(i==0) {
            this.queue[0] = object;
        } else {
            percolateUp(i,object);
        }

        return true;
    }

    /**
     * Retrieves the head of the queue
     * @return the head of the queue
     */
    @Override
    public E peek() {
        if (this.size() == 0) {
            return null;
        }
        return (E) this.queue[0];
    }

    /**
     * Retrieves the removes the head of the heap
     * @return the removed head or null
     */
    @Override
    public E poll() {
        if(this.size==0) {
            return null;
        }

        E head = this.removeAt(0);

        return head;

    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    public Comparator<? super E> comparator() {
        return this.comparator;
    }

    /**
     * Grow the heap by the integer specified
     * @param minCapacity the minimum capacity to increase by
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
            // NEW
            this.valueLookup.put(parent,index);
            this.valueLookup.put(element,p);
            this.percolateUpUsingComparator(p, element);
        } else {
            this.queue[index] = element;
            // NEW
            this.valueLookup.put(element,index);
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
            // NEW
            this.valueLookup.put(parent,index);
            this.valueLookup.put(element,p);
            this.percolateUpComparable(p,element);
        } else {
            this.queue[index] = element;
            // NEW
            this.valueLookup.put(element,index);
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
    private E removeAt(int index) {
        assert index >= 0 && index < this.size();
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
        /* set the new size */
        this.size = s;
        return element;

    }

    private boolean removeEq(Object o) {
        for(int i = 0;i<this.size();i++) {
            if(o==queue[i]) {
                removeAt(i);
                return true;
            }
        }
        return false;
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
    @SuppressWarnings("unchecked")
    private void percolateDownWithComparator(int index, E element) {
        if(index==this.size) {
            return;
        }
        int childIndex = this.getSmallerChildComparator(index);
        if(childIndex!=-1) {
            E child = (E) this.queue[childIndex];
            if(this.comparator.compare(element,child)>0) {
                this.queue[childIndex] = element;
                this.queue[index] = child;
                // NEW
                this.valueLookup.put(element,childIndex);
                this.valueLookup.put(child,index);
                this.percolateDownWithComparator(childIndex, element);
            }
        }

    }

    /**
     * Percolate down by casting an element to the Comparable of the appropriate type
     * @param index
     * @param element
     */
    @SuppressWarnings("unchecked")
    private void percolateDownComparable(int index, E element) {
        if(index==this.size) {
            return;
        }
        Comparable<? super E> key = (Comparable<? super E>) element;
        int childIndex = this.getSmallerChildComparable(index);
        if(childIndex!=-1) {
            E child = (E) this.queue[childIndex];
            if(key.compareTo(child)>0) {
                this.queue[childIndex] = element;
                this.queue[index] = child;
                // NEW
                this.valueLookup.put(element,childIndex);
                this.valueLookup.put(child,index);
                this.percolateDownComparable(childIndex, element);
            }
        }
    }

    /**
     * Retrieve the parent index of a given item
     * Subtracting 1 and doing an unsigned right shift is the same as dividing by 2 and doing the floor operation
     * @param index
     * @return the parent index
     */
    private int getParent(int index) {
        // even index
        return (index-1) >>> 1;
    }

    /**
     * Calculates the children for a given index
     * Checks to see which one is smaller and returns the index
     * It will return the index of a null item if its at the end
     * @param index
     * @return index of smaller child of -1 if no children
     */
    @SuppressWarnings("unchecked")
    private int getSmallerChildComparator(int index) {
        int c = 0;
        int leftChild = (2*index)+1;
        int rightChild = (2*index)+2;
        if (this.queue[leftChild]==null) {
            c = -1;
        } else if(this.queue[rightChild]==null) {
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

    /**
     * Calculates the children for a given index
     * Checks to see which one is smaller and returns the index
     * It will return the index of a null item if its at the end
     * @param index
     * @return index of a smaller child or -1 if no children
     */
    @SuppressWarnings("unchecked")
    private int getSmallerChildComparable(int index) {
        int c = 0;
        int leftChild = (2*index)+1;
        int rightChild = (2*index)+2;
        if (this.queue[leftChild]==null) {
            c = -1;
        } else if(this.queue[rightChild]==null) {
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
        this.modCount++;
        for(int i = 0; i< this.size; i++) {
            this.queue[i] = null;
        }
        this.size=0;
    }

    @Override
    public <T extends Object> T[] toArray(T[] a) {
        if(a.length<size) {
            return (T[]) Arrays.copyOf(this.queue, this.size);
        } else {
            System.arraycopy(this.queue,0,a,0,this.size);
            return a;
        }
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(this.queue,this.size);
    };


    /**
     * Decrease the key at the specified index
     * @param element
     * @return true if key decreased; false otherwise
     */
    public boolean decreaseKey(E element) {
        int index = this.valueLookup.get(element);
        /* check out of bounds */
        if(index<0 || index>this.queue.length) {
            return false;
        }
        /* check NullPointerException */
        if(element==null) {
            throw new NullPointerException();
        }
        this.queue[index]=element;
        this.percolateDown(index,element);
        return true;
    }


    /* iterator provides no guarantees of the order of iteration */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    private final class Itr implements Iterator<E> {

        private int cursor = 0;

        /* keeps track of the index of the cursor before a call to next() */
        private int lastRet = -1;

        /* keeps track of the last element returned by the forget me not */
        private E lastRetElt = null;

        private ArrayDeque<E> forgetMeNot = null;

        private int expectedModCount = MinPriorityQueue.this.modCount;

        @Override
        public boolean hasNext() {
            return this.cursor < size() || (forgetMeNot!=null && !forgetMeNot.isEmpty());
        }

        @Override
        public E next() {
            if(this.expectedModCount!=MinPriorityQueue.this.modCount) {
                throw new ConcurrentModificationException();
            }
            if(this.cursor< MinPriorityQueue.this.size()) {
                return (E) queue[lastRet = this.cursor++];
            }
            /* need to make sure that if there are removals that elements are not forgotten */
            if(forgetMeNot !=null) {
                this.lastRet = -1;
                this.lastRetElt = this.forgetMeNot.poll();
                if(this.lastRetElt!=null) {
                    return this.lastRetElt;
                }
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            if(this.expectedModCount!=MinPriorityQueue.this.modCount) {
                throw new ConcurrentModificationException();
            }
            if(this.lastRet!=-1) {
                E moved = MinPriorityQueue.this.removeAt(this.lastRet);
                this.lastRet=-1;
                if(moved==null) {
                    this.cursor--;
                } else {
                    /* need to add the element to the forgetMetNot because iteration should cover all elements */
                    if(forgetMeNot==null) {
                        forgetMeNot = new ArrayDeque<E>();
                    }
                    /* save for later iteration */
                    forgetMeNot.add(moved);
                }
            } else if (lastRetElt!=null) {
                MinPriorityQueue.this.removeEq(lastRetElt);
                lastRetElt=null;
            } else {
                throw new IllegalStateException();
            }
            this.expectedModCount=modCount;

        }

    }

}

package util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A heap back priority queue. User has the option
 * to provide a custom comparator for sorting. Defaults
 * to the underlying objects natural ordering.
 * @author Chris Chun, Ayush.
 * @version 1.1
 * @param <T> The object type stored in the PQ.
 */
public final class BinaryHeapPQ<T extends Comparable<? super T>> implements Heap<T>, DataContainer<T> {
    // notes: T extends Comparable<? super T> means the object must be comparable to itself or any of its parent types
    // this is called a lower bounded wild card.

    private final ArrayStore<T> myArray;
    private Comparator<T> myComparator;

    /**
     * Initializes a new priority queue backed by a binary heap.
     * @param theClass The class type of elements to be stored (used for array instantiation).
     * @param theComparator The comparator defining the priority (ordering) of elements.
     */
    public BinaryHeapPQ(Class<T> theClass, Comparator<T> theComparator){
        myArray = new ArrayStore<>(theClass,16);
        myComparator = theComparator;
    }

    /**
     * Initializes a new priority queue backed by a binary heap sorting elements
     * by their natural order as defined by how the objects implement Comparable<T>
     * @param theClass The class type of elements to be stored (used for array instantiation).
     */
    public BinaryHeapPQ(Class<T> theClass) {
        this(theClass, Comparator.naturalOrder());
    }


    /**
     * Inserts a new item into the priority queue.
     * The item is initially placed at the end of the heap and then moved up
     * to maintain the heap property.
     * @param item The element to be added.
     */
    @Override
    public void insert(T item) {
        myArray.add(item);
        heapifyUp();
    }

    /**
     * Removes and returns the highest priority element (the root) from the heap.
     * The last element is moved to the root position and sifted down to
     * restore heap order.
     * @return The element with the highest priority.
     * @throws NoSuchElementException If the priority queue is empty.
     */
    @Override
    public T extract() {
        if (myArray.isEmpty()) throw new NoSuchElementException("Cannot extract because Priority Queue is empty");
        swap(0, myArray.size()-1);
        T removed = myArray.remove();
        heapifyDown();
        return removed;
    }

    /**
     * Retrieves, but does not remove, the highest priority element of this queue.
     * @return The element at the root of the heap.
     * @throws IndexOutOfBoundsException if the underlying storage is empty.
     */
    @Override
    public T peek() {
        if (myArray.isEmpty()) throw new NoSuchElementException("Heap is empty");
        return myArray.get(0);
    }

    // ================= util methods ======================

    /**
     * Allows user to reorder the heap using a new comparator.
     * Assumes the heap is already built.
     * @param theNewComparator the new comparator for sorting.
     */
    public void reorder(Comparator<T> theNewComparator) {
        myComparator = theNewComparator;
        buildHeap();
    }

    /**
     * builds a heap in O(n) efficiency. Assumes
     * the current heap is already filled.
     */
    public void buildHeap() {
        if (myArray.isEmpty()) return;

        int lastLeaf = myArray.size()-1;
        int parentIdx = (lastLeaf -1)/2;

        for (int i = parentIdx; i >=  0; i--) {
            heapifyDown(i);
        }
    }

    /**
     * builds a heap from an array in O(n) efficiency maintaining
     * the heap property.
     * @param theArray the array to build the heap from.
     */
    public void buildHeap(ArrayStore<T> theArray) {
        if (theArray.isEmpty()) return;
        for (T item: theArray) {
            myArray.add(item);
        }
        buildHeap();

    }

    // ================ private helper methods ===================

    /**
     * Restores the heap property by moving the last element up the tree
     * until it is in its correct priority position relative to its parent.
     */
    private void heapifyUp() {

        // the element we just added
        int childIdx = myArray.size()-1;

        while (childIdx > 0) {

            int parentIdx = (childIdx -1) / 2;

            T child = myArray.get(childIdx);
            T parent = myArray.get(parentIdx);

            int priority = myComparator.compare(parent, child);

            // if positive then child comes before parent -> swap
            if (priority > 0) {
                swap(childIdx, parentIdx);
                childIdx = parentIdx;
            }else {
                break;
            }
        }
    }

    private void heapifyDown() {
        heapifyDown(0);
    }

    /**
     * Restores the heap property by moving the root element down the tree
     * until it is in its correct priority position relative to its children.
     */
    private void heapifyDown(int theStartIdx) {

        // no need to heapify down if empty;
        if (myArray.isEmpty()) return;

        int parentIdx = theStartIdx;

        while (true) {

            int left = 2 * (parentIdx +1) - 1;
            int right = 2 * (parentIdx +1);

            // 1) reached the bottom of the heap
            if (left >= myArray.size()) break;

            int highestPriorityIdx = getHighestPriorityIndex(left, right);

            T bestChild = myArray.get(highestPriorityIdx);
            T parent = myArray.get(parentIdx);

            int priority = myComparator.compare(parent, bestChild);

            // if the bestChild should come before the current parent
            if (priority > 0) {
                swap(parentIdx, highestPriorityIdx);
                parentIdx = highestPriorityIdx;

            }else {
                break;
            }
        }
    }

    /**
     * Returns the index of the child that has higher priority based on the comparator.
     * @param left The left child index.
     * @param right The right child index.
     * @return The index of the child that should come before the other.
     */
    private int getHighestPriorityIndex(int left, int right) {

        // no right child
        if (right >= myArray.size()) {
            return left;
        }
        // if the left should come before right
        int priority = myComparator.compare(myArray.get(left), myArray.get(right));
        if(priority < 0) {
            return left;
        }

        // otherwise the right should come
        return right;
    }

    /**
     * Swaps the elements at two specified indices in the underlying storage.
     * @param a The first index.
     * @param b The second index.
     */
    private void swap(int a, int b) {
        T temp = myArray.get(a);
        myArray.set(a, myArray.get(b));
        myArray.set(b, temp);
    }

    // ================= DataContainer Methods required by implementation of interface ========================

    /**
     *
     * @param theIndex Only supports theIndex = 0.
     * @return the element with the highest priority.
     * @throws IllegalArgumentException if the theIndex != 0.
     */
    @Override
    public T get(int theIndex) throws IndexOutOfBoundsException {
        if (theIndex != 0) {
            throw new IllegalArgumentException("Does not support indexed based access");
        }
        return peek();
    }

    /**
     *
     * @return true if empty and false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return myArray.isEmpty();
    }

    /**
     * @return the total number of elements in the priority queue.
     */
    @Override
    public int size() {
        return myArray.size();
    }

    /**
     * Flag used by managers to determine suitability of a priority queue
     * as a DataContainer to manage their data.
     * @return false, Priority Queues do not support indexed based access.
     */
    @Override
    public boolean supportsIndexedAccess() {
        return false;
    }

    /**
     * Appends element to end of list then performs
     * heapify up to maintain the heap property.
     * @param val the object to add to the list.
     */
    @Override
    public void add(T val) {
        insert(val);
    }

    /**
     * Only allows adding at the end of queue. Then performs heapify up.
     * @param theIndex adds an element to the specified index
     * @param theVal the data object you wish to add to
     *               the collection at the specified index
     * @throws IllegalArgumentException if the index != priorityQueue.size();
     */
    @Override
    public void add(int theIndex, T theVal) {
        if (theIndex != myArray.size()) {
            throw new IllegalArgumentException("Priority Queues do not support adding anywhere except the end of the queue");
        }
        insert(theVal);
    }

    /**
     * Compares the  priority element with the requested obj to be removed.
     * If the priority element and the object being requested to remove are considered
     * equal it removes the element otherwise it throws an illegal argument exception.
     * @param theObj the value of the data object to be deleted
     * @return the priority element.
     * @throws NoSuchElementException if PQ is empty.
     * @throws IllegalArgumentException if the priority element is not the element
     * being requested to removed.
     */
    @Override
    public T remove(T theObj) throws NoSuchElementException, IllegalArgumentException {
        if (myArray.isEmpty()) {
            throw new NoSuchElementException("Priority Queue is Empty");
        }
        if (!Objects.equals(theObj, peek())) {
            throw new IllegalArgumentException("Cannot remove the object because the specified " +
                    "object is not the priority element");
        }
        return extract();
    }

    /**
     * Uses O(log(n)) time complexity to remove the priority element
     * maintaining the heap property.
     * @return the element removed.
     * @throws NoSuchElementException if the heap is empty.
     */
    @Override
    public T remove() throws NoSuchElementException {
        return extract();
    }

    @Override
    public T removeAt(int theIndex) throws IndexOutOfBoundsException, IllegalArgumentException {
        if (myArray.isEmpty()) {
            throw new NoSuchElementException("Priority Queue is empty");
        }
        if (theIndex != 0) {
            throw new IllegalArgumentException("Cannot remove anywhere except the front of the Priority Queue");
        }
        return extract();
    }

    /**
     *
     * @param theIndex the index of the data object you wish to update
     * @param theData the data you wish to update the current list with at
     *                the specified index.
     * @throws IllegalArgumentException Always! PQ do not support indexed based access.
     */
    @Override
    public void set(int theIndex, T theData) throws IllegalArgumentException, IndexOutOfBoundsException {
        throw new IllegalArgumentException("Priority Queues do not support indexed based access");
    }

    /**
     * Searches for an element matching the provided predicate.
     * Because a Priority Queue only provides meaningful access to the element
     * with the highest priority, this method only tests the root element (index 0).
     * @param thePredicate The condition used to evaluate the priority element.
     * @return 0 if the priority element matches the predicate;
     * -1 if the queue is empty or the priority element does not match.
     */
    @Override
    public int findBy(Predicate<T> thePredicate) {
        if (myArray.isEmpty()) {
            return -1;
        }
        if (thePredicate.test(peek())) {
            return 0;
        }

        // Element not found at the only accessible priority position.
        return -1;
    }

    /**
     * Removes all elements from the priority queue.
     */
    @Override
    public void clear() {
        myArray.clear();
    }

    /**
     * Returns an iterator over the elements in the heap.
     * Note: The iterator does not guarantee any specific order.
     * @return An iterator for the underlying array.
     */
    @Override
    public Iterator<T> iterator() {
        return myArray.iterator();
    }
    // =============== util ===================

    /**
     * Returns a string representation of the underlying array storage.
     * @return A string in the format [e1, e2, ...].
     */
    @Override
    public String toString() {
        if (myArray.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < myArray.size()-1; i++) {
            sb.append((myArray.get(i)));
            sb.append(", ");
        }
        sb.append(myArray.get(myArray.size()-1));
        sb.append("]");

        return sb.toString();
    }


}



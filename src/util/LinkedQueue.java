package util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

public final class LinkedQueue<T> implements DataContainer<T>, Queue<T> {

    public SinglyLinkedList<T> myQueue = new SinglyLinkedList<>();

    private final String ILLEGAL_ARG_ERR = "Queues do not support indexed based access beyond the front";

    public LinkedQueue(){
        super();
    }

    // =========================  flags ============================
    @Override
    public boolean supportsIndexedAccess() {
        return false;
    }

    private boolean isFront(int theIndex){
        return theIndex == 0;
    }

    private boolean isRear(int theIndex) {
        return theIndex == myQueue.size();
    }

    // =========================  getting/ setting ============================
    @Override
    public T front() {
        if (myQueue.isEmpty()) {
            throw new NoSuchElementException("Queue is empty, nothing to retrieve at front");
        }
        return myQueue.get(0);
    }

    @Override
    public T get(int theIndex) throws IndexOutOfBoundsException {
        if (!isFront(theIndex)){
            throw new IllegalArgumentException(ILLEGAL_ARG_ERR);
        }
        return front();
    }


    @Override
    public int size() {
        return myQueue.size();
    }

    @Override
    public void set(int theIndex, T theData) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (myQueue.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        if (!isFront(theIndex)) {
            throw new IllegalArgumentException((ILLEGAL_ARG_ERR));
        }
        myQueue.set(0,theData);
    }

    // =========================  adding   ============================

    @Override
    public void enqueue(T val) {
        myQueue.add(val);
    }

    @Override
    public void add(T val) {
        myQueue.add(val);
    }

    @Override
    public void add(int theIndex, T theVal) {
        if (!isRear(theIndex)){
            throw new IllegalArgumentException(ILLEGAL_ARG_ERR);
        }
        enqueue(theVal);
    }
    // =========================  removing  ============================

    @Override
    public T dequeue() {
        return myQueue.remove();
    }

    @Override
    public T remove(T theVal) throws NoSuchElementException, IllegalArgumentException {
        if (!Objects.equals(front(),theVal)){
            throw new IllegalArgumentException(ILLEGAL_ARG_ERR);
        }
        return dequeue();
    }

    @Override
    public T remove() throws NoSuchElementException {
        return myQueue.remove();
    }

    @Override
    public T removeAt(int theIndex) throws IndexOutOfBoundsException, IllegalArgumentException {
        if (!isFront(theIndex)) {
            throw new IllegalArgumentException(ILLEGAL_ARG_ERR);
        }
        return dequeue();
    }
    // =========================  searching ============================

    @Override
    public int findBy(Predicate<T> thePredicate) throws IllegalArgumentException {
        throw new IllegalArgumentException(ILLEGAL_ARG_ERR);
    }
    // =========================  utility ============================

    @Override
    public boolean isEmpty() {
        return myQueue.isEmpty();
    }

    @Override
    public void clear() {
        myQueue.clear();
    }
    // =========================  iterating ============================

    @Override
    public Iterator<T> iterator() {
        return myQueue.iterator();
    }
}

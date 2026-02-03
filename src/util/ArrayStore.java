package util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * ArrayStore is a low-level array based implementation of an ArrayList.
 * @param <T> Defines the type of Objects this ArrayStore will contain.
 */
public final class ArrayStore<T> implements DataContainer<T> {

    private T[] myData;
    final private Class<T> dataClass;
    private int size;

    @SuppressWarnings("unchecked")
    public ArrayStore(Class<T> theClass, int theCapacity){
        if (theCapacity <= 0) {
            throw new IllegalArgumentException("initial capacity must be positive");
        }
        this.dataClass = theClass;
        this.size = 0;
        this.myData = (T[]) java.lang.reflect.Array.newInstance(theClass,theCapacity);
    }

    // ================== getting & setting ======================
    @Override
    public void set(int theIndex, T theData) {
        myData[theIndex] = theData;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean supportsIndexedAccess() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    // ================== adding ========================

    public void add(T theData) {
        if (size == myData.length){
            resize();
        }
        myData[size++] = theData;
    }

    @Override
    public void add(int theIndex, T theVal) {
        // 1. Bounds check
        if (theIndex < 0 || theIndex > size) {
            throw new IndexOutOfBoundsException(
                    "Cannot insert at index " + theIndex + " for array of size " + size
            );
        }

        // 2. Resize if full
        if (size == myData.length) {
            resize();
        }

        // 3. Shift elements right
        for (int i = size - 1; i >= theIndex; i--) {
            myData[i + 1] = myData[i];
        }

        // 4. Insert the new value
        myData[theIndex] = theVal;

        // 5. Increment size
        size++;
    }


    public T get(int theIndex) {
        if (theIndex >= size || theIndex < 0) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        return myData[theIndex];
    }

    public void append(ArrayStore<T> theArray) {
        while (myData.length - size < theArray.size) {
            resize();
        }
        int index = 0;
        int newSize = size + theArray.size;
        for (int i = size; i < newSize; i++) {
            set(i, theArray.get(index++));
        }
        size = newSize;
    }

    // ================== removing ========================

    @Override
    public T remove() throws NoSuchElementException {
        if (size == 0) {
            throw new NoSuchElementException("No such element");
        }
        T removed = myData[size -1];
        myData[size -1] = null;
        return removed;
    }


    @Override
    public T remove(T theVal) throws NoSuchElementException {
        T removed;
        int index = indexOf(theVal);

        if (index == -1) {
            throw new NoSuchElementException("Not found");
        }
        removed = removeAt(index);
        return removed;
    }

    @Override
    public T removeAt(int theIndex) {

        T theItemRemoved = get(theIndex);

        for (int i = theIndex; i < size-1; i++) {
            myData[i] = myData[i+1];
        }
        myData[size-1] = null;
        size--;

        return theItemRemoved;
    }

    // ================== searching ========================

    @Override
    public int findBy(Predicate<T> thePredicate) {

        if (size == 0) {
            return -1;
        }

        int index = -1;
        int i = 0;
        for (T item:myData) {
            if (thePredicate.test(item)) {
                return i;
            }

            i++;
        }
        return index;
    }

    public int indexOf(T theItem) {
        int idx = -1;

        for (int i = 0; i < size; i++) {
            if (Objects.equals(theItem, myData[i])) {
                idx = i;
            }
        }

        return idx;
    }

    // ================== helper ========================

    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newArray = (T[]) java.lang.reflect.Array.newInstance(dataClass, myData.length*2);
        System.arraycopy(myData, 0, newArray, 0, size);
        myData = newArray;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public T next() {
                i++;
                return myData[i];
            }
        };
    }
}

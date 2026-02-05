package util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * An array based implementation of stack.
 * @author Chris Chun, Ayush
 * @version 1.1
 * @param <T> the data type you want to store in the stack.
 */
public final class ArrayStack<T> implements Stack<T>, DataContainer<T>{
    public ArrayStore<T> myStack;

    public Class<T> myDataClass;

    public ArrayStack(Class<T> theDataClass){
        myStack = new ArrayStore<>(theDataClass, 16);
        myDataClass = theDataClass;
    }
    //======================= flags ==============================

    @Override
    public boolean supportsIndexedAccess() {
        return false;
    }

    //======================= getting/setting ==============================

    // data container interface
    @Override
    public int size() {
        return myStack.size();
    }

    // stack specific
    @Override
    public T peek() {
        if (myStack.isEmpty()) {
            throw new NoSuchElementException("Empty Stack, nothing to peek() at top.");
        }
        return myStack.get(size()-1);
    }

    // data container interface
    @Override
    public T get(int theIndex) throws IndexOutOfBoundsException, IllegalArgumentException {
        if (theIndex != size()-1) {
            throw  new IllegalArgumentException("Stack do not support indexed based access beyond the top");
        }
        return peek();
    }
    // data container interface
    @Override
    public void set(int theIndex, T theData) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (!isTop(theIndex)) {
            throw new IllegalArgumentException("Stacks do not support indexed based access beyond the top");
        }
        myStack.set(theIndex,theData);
    }


    //========================  adding  ==========================

    // stack specific
    @Override
    public void push(T theElement) {
        myStack.add(theElement);
    }

    /**
     * Appends a new element to the stack.
     * @param val the object to add to the list.
     */
    // data container interface
    @Override
    public void add(T val) {
        myStack.add(val);
    }

    // data container interface
    @Override
    public void add(int theIndex, T theVal) {
        if (size() != theIndex) {
            throw new IllegalArgumentException("Can only add to the top/end of the stack");
        }
        push(theVal);
    }

    //====================   removing  ===========================

    // stack specific
    @Override
    public T pop() {

        if (isEmpty()) {
            throw new NoSuchElementException("The stack is empty");
        }
        return myStack.remove();
    }

    /**
     * Removes the last element in the stack. Equivalent to pop();
     * @return the element removed from the stack.
     * @throws NoSuchElementException if the stack is empty.
     */
    // data container interface
    @Override
    public T remove() throws NoSuchElementException {
        return myStack.remove();
    }

    /**
     * Tests equality of the top of the stack with the object desired to remove from the stack.
     * Uses the underlying object's definition of equality to compare. If the two objects are
     * considered equal, it removes the item from the top of the stack.
     * This method is deprecated and should be avoided. Instead, please use pop(), or a
     * different data structure that supports indexed based access.
     * @param theVal the value of the data object to be deleted
     * @return the item removed if it's at the top of the stack
     * @throws NoSuchElementException if the stack is empty
     * @throws IllegalArgumentException if the requested item to remove
     * is not on the top of the stack
     */
    // data container interface
    @Override
    public T remove(T theVal) throws NoSuchElementException, IllegalArgumentException {
        if (myStack.isEmpty()) {
            throw new NoSuchElementException("The stack is empty. Cannot remove from and empty stack");
        }
        if (!Objects.equals(peek(), theVal)) {
            throw new IllegalArgumentException(
                    "Stacks do not support removal from anywhere other than the top of the stack, if " +
                            "you need a DataContainer that supports indexed based access" +
                            "please use an ArrayStore or SinglyLinkedList."
            );
        }
        return pop();
    }

    // data container interface
    @Override
    public T removeAt(int theIndex) throws IndexOutOfBoundsException, IllegalArgumentException {

        if (!isTop(theIndex)) {
            throw new IllegalArgumentException("Stacks only support removal at the top/ end of the stack");
        }
        return pop();
    }

    //==================== searching ============================
    @Override
    public int findBy(Predicate<T> thePredicate) throws IllegalArgumentException {
        throw new IllegalArgumentException("Stacks do not support indexed based access");
    }

    //====================  helper ==============================

    private boolean isTop(int theIndex) {
        return myStack.size()-1 == theIndex;
    }

    @Override
    public boolean isEmpty() {
        return myStack.isEmpty();
    }

    @Override
    public void clear() {
        myStack.clear();
    }

    //=================== iterating =========================

    @Override
    public Iterator<T> iterator() {
        return myStack.iterator();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[bottom] ");
        for (T val: myStack) {
            sb.append(val);
        }
        sb.append("[top]");
        return sb.toString();
    }

}

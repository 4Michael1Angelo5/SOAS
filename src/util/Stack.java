package util;

/**
 * A simple Stack interface, defining common
 * methods to all stacks: push, pop, peek.
 * @param <T> the object type the stack will store.
 * @version 1.1
 * @author Chris Chun, Ayush.
 */
public interface Stack<T> {

    /**
     * Removes an element from the stack.
     * @return the element removed from the stack.
     */
    T pop();

    /**
     * adds an element to the top of the stack.
     */
    void push(T theElement);

    /**
     * retrieves but does not remove the top
     * element from the stack.
     * @return the top of the stack.
     */
    T peek();
}
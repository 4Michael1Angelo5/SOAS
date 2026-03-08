package util;

/**
 * Declares that any Data structure that implements this
 * interface must implement an operation counting strategy
 * using the {@link counter.OperationCounter} for
 * their associated methods and provide a methods for returning the number of
 * operations performed with the associated methods.
 * @version 1.1
 * @author Chris Chun, Ayush.
 */
public interface OperationCountable<T> {

    /**
     *
     * @return the number of swaps
     */
    int getSwaps();

    /**
     * @return the number of comparisons
     */
    int getComparisons();

    /**
     * Resets the {@link counter.OperationCounter}
     */
    void resetCounter();
}

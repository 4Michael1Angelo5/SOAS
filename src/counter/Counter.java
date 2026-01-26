package counter;

/**
 * @author Chris Chun
 * @version 1.1
 */
public interface Counter {

    /**
     *
     * @param theTask the task/ method to execute.
     * @return the number of operations for the given task/ method to run.
     */
    int countOperations(Runnable theTask);
}
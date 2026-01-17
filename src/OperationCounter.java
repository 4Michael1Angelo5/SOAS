import java.util.HashMap;
import java.util.Map;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class OperationCounter implements Counter {

    /**
     * Map containing operation, ie "comparison", or "swap" and their
     * corresponding count.
     */
    final private Map<String, Integer> counts;

    public OperationCounter() {
        counts = new HashMap<>();
    }

    /**
     * Adds 1 to the count for an operation.
     * @param operationName name of the operation like "comparisons" or "swaps"
     */
    public void increment(String operationName) {
        if (counts.containsKey(operationName)) {
            counts.put(operationName, counts.get(operationName) + 1);
        } else {
            counts.put(operationName, 1);
        }
    }

    /**
     * Adds the specified count to the operation.
     * @param operationName name of the operation ie: "comparisons" or "swaps
     * @param count the integer value you wish to increment the operation count by.
     */
    public void increment(String operationName, int count){
        counts.merge(operationName, count, Integer::sum);
    }

    /**
     * Gets how many times an operation was counted.
     * @param operationName name of the operation to check
     * @return number of times counted, or 0 if not found
     */
    public int getCount(String operationName) {
        if (counts.containsKey(operationName)) {
            return counts.get(operationName);
        }
        return 0;
    }

    /**
     * Sets one operation back to 0.
     * @param operationName name of the operation to reset
     */
    public void reset(String operationName) {
        counts.put(operationName, 0);
    }

    /**
     * Clears all counts and starts over.
     */
    public void resetAll() {
        counts.clear();
    }

    /**
     * Prints all operations and their counts.
     */
    public void printReport() {
        System.out.println("\n--- Operation Counts ---");
        for (String op : counts.keySet()) {
            System.out.println(op + ": " + counts.get(op));
        }
        System.out.println("------------------------\n");
    }

    /**
     * Runs a task and counts total operations.
     * @param theTask the task to run
     * @return total count of all operations
     */
    @Override
    public int countOperations(Runnable theTask) {
        resetAll();
        theTask.run();
        // return total of all operations counted
        int total = 0;
        for (int count : counts.values()) {
            total += count;
        }
        return total;
    }
}
package results;


import util.Dictionary;

public class BenchmarkResult {

    private final int myInputSize;
    private final String myMethodName;
    private final double myAvgTime;
    private final OperationCounts myOperationCounts;
    private double myLoadFactor;
    private int myCollisions;

    /**
     * Creates a new result object with core performance metrics.
     * @param inputSize The number of elements processed.
     * @param method The name of the operation (e.g., "Insert", "Search").
     * @param avgTime The average execution time in milliseconds.
     * @param operationCounts The captured comparison and swap counts.
     */
    public BenchmarkResult(  int inputSize,
                             String method,
                             double avgTime,
                             OperationCounts operationCounts){

        myInputSize = inputSize;
        myMethodName = method;
        myAvgTime = avgTime;
        myOperationCounts = operationCounts;
    }


    //========================= Base Experiment Results =========================
    // All Experiment Results will have these, not optional for display.

    public int getInputSize() {
        return myInputSize;
    }

    public double getAvgTime() {
        return myAvgTime;
    }

    public String getMethodName() {
        return myMethodName;
    }

    // =========================  Operation Counting =========================
    // All Experiment Results are guarnteed access to this ,but it is optional
    // to display.

    public int getComparions() {
        return myOperationCounts.comparisons();
    }

    public int getSwaps() {
        return myOperationCounts.swaps();
    }

    // ========================= HashTable Specific =========================
    // Only HashTable/Dictionary data structures can meaningfully interact with
    // this.

    /**
     * This method is specific to testing containers of type: {@link Dictionary}.
     * @return The number of collisions encounterd during a test trial.
     */
    public int getMyCollisions() {
        return myCollisions;
    }

    /**
     * This method is specific to testing containers of type: {@link Dictionary}.
     * @param theCollisions set the number of collisons encounterd in a test trial.
     */
    public void setCollisions(int theCollisions) {
        myCollisions = theCollisions;
    }

    /**
     *
     * @return The load factor result of a test trial experiment for a
     *         {@link Dictionary} container type.
     */
    public double getLoadFactor() {
        return myLoadFactor;
    }

    /**
     *
     * @param theLoadFactor sets the {@link Dictionary}'s current load factor
     *                      after a benchmark test trial experiment.
     */
    public void setLoadFactor(double theLoadFactor) {
        myLoadFactor = theLoadFactor;
    }


}
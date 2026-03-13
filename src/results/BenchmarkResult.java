package results;


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

    public int getMyCollisions() {
        return myCollisions;
    }

    public void setCollisions(int theCollisions) {
        myCollisions = theCollisions;
    }

    public void setLoadFactor(double theLoadFactor) {
        myLoadFactor = theLoadFactor;
    }

    public double getLoadFactor() {
        return myLoadFactor;
    }

    public int getInputSize() {
        return myInputSize;
    }

    public double getAvgTime() {
        return myAvgTime;
    }

    public String getMethodName() {
        return myMethodName;
    }

    public int getComparions() {
        return myOperationCounts.comparisons();
    }

    public int getSwaps() {
        return myOperationCounts.swaps();
    }


}

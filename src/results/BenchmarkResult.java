package results;

public class BenchmarkResult {

    private final int myInputSize;
    private final String myMethodName;
    private final double myAvgTime;
    private final OperationCounts myOperationCounts;
    private double myLoadFactor;

    public BenchmarkResult(  int inputSize,
                             String method,
                             double avgTime,
                             OperationCounts operationCounts){

        myInputSize = inputSize;
        myMethodName = method;
        myAvgTime = avgTime;
        myOperationCounts = operationCounts;
    }

    public void setLoadFactor(double theLoadFactor) {
        myLoadFactor = theLoadFactor;
    }

    public double getMyLoadFactor() {
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

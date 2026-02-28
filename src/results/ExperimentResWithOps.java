package results;

public record ExperimentResWithOps(
        int inputSize,
        String operation,
        double avgTime,
        int comparisons,
        int swaps

) implements ExperimentInterface {


    @Override
    public int getInputSize() {
        return inputSize;
    }

    @Override
    public String getOperation() {
        return operation;
    }

    @Override
    public double getAvgTime() {
        return avgTime;
    }

    public int getComparison() {
        return comparisons;
    }

    public int getSwaps() {
        return swaps; 
    }
}

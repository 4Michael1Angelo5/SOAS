package results;

/**
 *
 * @param inputSize the input size of the experiement
 * @param method the method/operation name being tested
 * @param avgTime the avgerage time to perform the method/operation
 *                for the given input size
 * @param operationCounts the number of operations e.g. swaps and comparisons
 *                        performed for the given input size.
 * @author Chris Chun, Ayush
 * @version 1.2
 */
public record BenchmarkResult(
        int inputSize,
        String method,
        double avgTime,
        OperationCounts operationCounts){
}

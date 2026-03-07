package results;

/**
 * Holds a record of how many swaps and comparisons
 * was performed for a {@link BenchmarkResult}
 * @param swaps the number of swaps
 * @param comparisons the nmumber of comparisons
 */
public record OperationCounts(int swaps,int comparisons){}

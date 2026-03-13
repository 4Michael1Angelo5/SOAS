package results;

/**
 *
 * @param swaps the number of swaps perfomred for a benchmark test
 * @param comparisons the number of comparisons perfomred for a benchmark test
 * @author Chris Chun, Ayush.
 * @version 1.1
 */
public record OperationCounts(
        int swaps,
        int comparisons){
}

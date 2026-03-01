package simulator;

/**
 * A simple Record object to report statistics on Drill wait times.
 * @param waitTime how long the drill waited before being processed
 * @param orderProcessed the order it was processed eg 5'th 6'th
 */
public record DrillReport(
        int waitTime,
        int orderProcessed
) {

    @Override
    public String toString() {
        return "{\n" +
                "   waitTime: " + waitTime + "\n" +
                "   orderProcessed: " + orderProcessed + "\n" +
                "}"
                ;
    }
}


//Notes: Denied Count = Order Processed - Original Order (FIFO)
// example drill 1 was in line at position 1 but wasn't processed until
// drill 500. It was denied 499 times before being processed.

import counter.OperationCounter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OperationCounterTest {

    @Test
    void testOperationCounting() {
        OperationCounter counter = new OperationCounter();
        String operationName = "swaps";
        int curCount = 1;
        counter.increment(operationName);
        assertAll("Test Operation Counting",
                () -> {
                    assertEquals(1,counter.getCount(operationName),operationName + " should equal " + curCount);
                },
                ()-> {
                    counter.resetAll();
                    assertEquals(0,counter.getCount(operationName));
                },
                () -> {
                    int newCount = 5;
                    counter.increment(operationName, newCount);
                    assertEquals(newCount,counter.getCount(operationName));
                },
                () -> {
                    counter.reset(operationName);
                    assertEquals(0, counter.getCount(operationName));
                });
    }

    @Test
    void testMultipleCounters() {
        OperationCounter counter1 = new OperationCounter();
        OperationCounter counter2 = new OperationCounter();

        counter1.increment("swaps", 5);
        counter2.increment("swaps", 3);

        assertEquals(5, counter1.getCount("swaps"));
        assertEquals(3, counter2.getCount("swaps"));
    }
}

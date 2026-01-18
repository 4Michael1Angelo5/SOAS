import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BenchmarkRunnerTest {
    private static int myCount = 0;
    private final static int numberOfTimesToRunTest = 100;
    @Test
    public void testBenchmarkRunner() {
        final BenchmarkRunner myBenchmarkRunner = new BenchmarkRunner();
        myBenchmarkRunner.runSpeedTest(numberOfTimesToRunTest,this::incrementCount);
        assertEquals(numberOfTimesToRunTest, myCount,
                "The BenchmarkRunner should run the task " + numberOfTimesToRunTest);
    }

    private void incrementCount() {
        myCount += 1;
    }
}

package benchmark;


/**
 * Provides an implementation for benchmarking the execution time of tasks.
 * This class allows for simple speed tests as well as tests requiring
 * a setup phase before each iteration to ensure a clean state.
 * @author Chris Chun, Ayush
 * @version 1.3
 */
public class BenchmarkRunner implements Benchmark {

    /**
     * Constructs a new BenchmarkRunner.
     */
    public BenchmarkRunner() {
        super();
    }

    /**
     * Runs a speed test where a setup task is executed before every
     * single iteration of the main task. This is useful for testing
     * operations that modify a data structure (like add or remove)
     * where the state must be reset to maintain consistent timing.
     * @param theTimesToRun the number of iterations to perform.
     * @param theSetupTask the task to run before each iteration (not timed).
     * @param theTask the main task to be benchmarked.
     * @return the average execution time per iteration in milliseconds (ms).
     */
    @Override
    public double
    runSpeedTestWithSetup(
            int theTimesToRun,
            Runnable theSetupTask,
            Runnable theTask) {

        double totalTime = 0.0;

        for (int i = 0; i < theTimesToRun; i++) {

            theSetupTask.run();

            totalTime += runSpeedTest(1, theTask);
        }

        return totalTime/theTimesToRun;
    }

    /**
     * Runs a speed test for a specified number of iterations and
     * calculates the average execution time.
     * @param theTimesToRun the number of iterations to perform.
     * @param theTask the task to be benchmarked.
     * @return the average execution time per iteration in milliseconds (ms).
     */
    @Override
    public double runSpeedTest(int theTimesToRun, Runnable theTask) {
        long startTime = System.nanoTime();

        for (int i = 0; i < theTimesToRun; i++) {
            theTask.run();
        }

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        return (double) totalTime / theTimesToRun / 1_000_000 ;
    }
}

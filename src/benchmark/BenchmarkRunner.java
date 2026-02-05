package benchmark;


import types.DataType;
import util.DataContainer;

import java.util.function.Supplier;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class BenchmarkRunner implements Benchmark {

    public BenchmarkRunner() {
        super();
    }

    /**
     * Runs a speed test on a given task multiple times and reports the results
     * @param theTimesToRun the number of times to repeat the task
     * @param theTask the task/method to be executed
     */
    @Override
    public void runSpeedTest(int theTimesToRun, Runnable theTask) {

        System.out.println("\n=== Running Speed Test ===");
        System.out.println("Number of runs: " + theTimesToRun);

        long startTime = System.currentTimeMillis();

        // run the task multiple times
        for (int i = 0; i < theTimesToRun; i++) {
            theTask.run();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTime = (double) totalTime / theTimesToRun;


        System.out.println("Total time: " + totalTime + " ms");
        System.out.println("Average time per run: " + avgTime + " ms");
        System.out.println("==========================\n");
    }

    /**
     * Runs a speed test with a custom label for better output
     * @param theTimesToRun number of times to run
     * @param theTask the task to execute
     * @param label description of what's being tested
     */
    public void runSpeedTest(int theTimesToRun, Runnable theTask, String label) {
        System.out.println("\n=== Speed Test: " + label + " ===");
        System.out.println("Number of runs: " + theTimesToRun);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < theTimesToRun; i++) {
            theTask.run();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTime = (double) totalTime / theTimesToRun;

        System.out.println("Total time: " + totalTime + " ms");
        System.out.println("Average time per run: " + avgTime + " ms");
        System.out.println("==========================\n");
    }

    public double
    runSpeedTestWithSetup(
            int theTimesToRun,
            Runnable theSetupTask,
            Runnable theTask) {

        double totalTime = 0.0;

        for (int i = 0; i < theTimesToRun; i++) {

            theSetupTask.run();

            totalTime += runSpeedTestAndGetAvg(1, theTask);
        }

        return totalTime/theTimesToRun;
    }

    public double runSpeedTestAndGetAvg(int theTimesToRun, Runnable theTask) {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < theTimesToRun; i++) {
            theTask.run();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        return (double) totalTime / theTimesToRun;
    }
}

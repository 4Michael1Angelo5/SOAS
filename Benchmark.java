/**
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public interface Benchmark {

    /**
     * Runs a speed test on a given method, specific number of times and logs the results to
     * the console.
     * @param theTimesToRun the number of times to repeat the task.
     * @param theTask the task/ method to be executed a specific number of times.
     */
    void runSpeedTest(int theTimesToRun, Runnable theTask);

}

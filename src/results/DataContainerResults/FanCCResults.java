package results.DataContainerResults;

import manager.DataContainer.FanTicketDCManager;
import manager.FanTicketQueue;
import results.BenchmarkResult;
import results.ExperimentFormat;
import types.FanRequest;
import util.DataContainer;
import util.LinkedQueue;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Orchestrates performance experiments for FanRequest processing.
 * Inherits automated benchmarking and state management from DataContainerResults.
 */
public class FanCCResults
        extends DataContainerResults<FanRequest, DataContainer<FanRequest>, FanTicketDCManager> {

    static final String FAN_50 = "data/seahawks_fan_queue_50.csv";
    static final String FAN_500 = "data/seahawks_fan_queue_500.csv";
    static final String FAN_5000 = "data/seahawks_fan_queue_5000.csv";

    public FanCCResults(FanTicketDCManager theManager, ExperimentFormat theFormat) {
        // We no longer need the Supplier!
        // The type bounds ensure M (FanTicketQueue) is a DataContainerManager<FanRequest, C>
        super(FanRequest.class, theManager, theFormat);
    }

    /**
     * Specialized test for Queue behavior.
     * Combines the Enqueue (Add) and Dequeue (Remove) metrics.
     */
    public BenchmarkResult testEnqueueDequeue() {
        // Use the inherited Template Methods to ensure proper setup and timing
        BenchmarkResult enqueue = this.testAddNTimes("Enqueue");
        BenchmarkResult dequeue = this.testRemoveNTimes("Dequeue");

        int inputSize = myTestContainer.size();
        String testTitle = "Enqueue/Dequeue";

        // Calculate combined average time
        double combinedAvg = (enqueue.avgTime() + dequeue.avgTime()) / 2.0;

        // Using enqueue counts as a representative for complexity
        return new BenchmarkResult(inputSize, testTitle, combinedAvg, enqueue.operationCounts());
    }

    public void runAllExperiments() throws IOException {
        String[] dataFiles = {FAN_50, FAN_500, FAN_5000};

        for (String file : dataFiles) {
            // Use the parent's loading logic
            this.loadData(file);

            // Run our combined metric
            addExperimentResult(testEnqueueDequeue());
        }

        printResults();
    }

    public static void main(String[] args) throws IOException {
        // 1. Define the container strategy
        Supplier<DataContainer<FanRequest>> sup = LinkedQueue::new;

        // 2. Initialize the manager with that supplier
        FanTicketDCManager FM = new FanTicketDCManager(sup);

        // 3. Initialize the orchestrator (Notice we don't need the .class param for FanRequest
        // because it's baked into the FanCCResults constructor unlike RosterDCResults)
        FanCCResults res = new FanCCResults(FM, ExperimentFormat.BENCHMARK_W_OPS);

        // 4. Execute
        res.runAllExperiments();
    }
}
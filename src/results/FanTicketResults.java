package results;

import manager.FanTicketQueue;
import types.FanRequest;
import util.DataContainer;

import java.io.IOException;
import java.util.function.Supplier;

public class FanTicketResults extends Results<FanRequest, FanTicketQueue> {

    static final String FAN_50 = "data/seahawks_fan_queue_50.csv";
    static final String FAN_500 = "data/seahawks_fan_queue_500.csv";
    static final String FAN_5000 = "data/seahawks_fan_queue_5000.csv";

    /**
     *
     * @param theFanTicketManger a FanTicketQueue instance.
     * @param theSupplier a DataContainer meant for FanRequest objects.
     */
    public FanTicketResults(
            FanTicketQueue theFanTicketManger,
            Supplier<DataContainer<FanRequest>> theSupplier) {

        super(FanRequest.class, theFanTicketManger, theSupplier);
    }

    /**
     * Returns the Experiment Result of enqueueing and de-queueing
     * Fan Requests. The average time is calculated by taking the
     * average time of each operation individually then reporting
     * their combined average.
     * @return ExperimentResult of Pushing and Popping N times.
     */
    public ExperimentResult testEnqueueDequeue() {
     ExperimentResult enqueue = this.testAdd("Enqueue");
     ExperimentResult dequeue = this.testRemove("Dequeue");

     int inputSize = myManager.getData().size();
     String testTitle = enqueue.operation() + "/" + dequeue.operation();
     double avgTime = (enqueue.avgTime() + dequeue.avgTime()) / 2.0;

     return new ExperimentResult(inputSize, testTitle, avgTime);
    }

    @Override
    public void runAllExperiments() throws IOException {

        // ===========   Fan Requests 50 =============
        myManager.loadCsvData(FAN_50);
        myExperiments.add(testEnqueueDequeue());
        // ===========   Fan Requests 500 =============
        myManager.loadCsvData(FAN_500);
        myExperiments.add(testEnqueueDequeue());
        // ===========   Fan Requests 5000 =============
        myManager.loadCsvData(FAN_5000);
        myExperiments.add(testEnqueueDequeue());
        printResults();

    }
}

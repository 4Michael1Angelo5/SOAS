package results.DataContainerResults;

import manager.DataContainer.DataContainerManager;
import results.BenchmarkResult;
import results.ExperimentFormat;
import results.orchestration.ExperimentOrchestrator;
import types.DataType;
import util.DataContainer;

public abstract class DataContainerResults <
                T extends DataType,
                C extends DataContainer<T>,
                M extends DataContainerManager<T, C>>
    extends ExperimentOrchestrator<T,C,M> {

    public DataContainerResults(
            Class<T> theDataType,
            M theManagerTestSubject,
            ExperimentFormat theExperimentFormat,
            T theTypeToken) {
        super(theDataType, theManagerTestSubject, theExperimentFormat, theTypeToken);
    }

    // ============================ Results Title =================================

    @Override
    public String getContainerName() {
        return myManagerTestSubject.getData().getClass().getSimpleName();
    }

    // ============================ untimed tasks ===============================

    public void setUpForSearch() {
        for (T dataObj : myTestContainer) {
            myManagerTestSubject.addData(dataObj);
        }
        myManagerTestSubject.resetCounter();
        validateStateBeforeSearch();
    }

    // =============================  Timed Tasks/ Experiments ====================================

    @Override
    public BenchmarkResult testAddNTimes(String theOperationName) {

        int inputSize = myTestContainer.size();

        double avgTime =
                myBenchmarkRunner.runSpeedTestWithSetup(
                        TRIAL_RUNS,
                        this::setUpForAdd,
                        this::addNTimes);

        return new BenchmarkResult(inputSize, theOperationName, avgTime, getOpCounts());
    }


    /**
     * Executes the removal benchmark by coordinating setup and timed execution.
     * * @return An {@link BenchmarkResult} capturing size, operation name, and average time.
     */
    @Override
    public BenchmarkResult testRemoveNTimes(String theOperationName) {
        final int inputSize = myManagerTestSubject.size();
        final double avgTime =
                myBenchmarkRunner.runSpeedTestWithSetup(
                        TRIAL_RUNS,
                        this::setUpForRemove,
                        this::removeNTimes);

        return new BenchmarkResult(inputSize, theOperationName, avgTime, getOpCounts());
    }

    /**
     * Timed task that removes every element from the setup container until it is empty.
     * Must be preceded by setUpForRemove to ensure there is data to remove.
     * * @throws RuntimeException If the container for removal is empty.
     */
    // runnable & timed
    @Override
    public void removeNTimes() {
        while (!myManagerTestSubject.getData().isEmpty()) {
            myManagerTestSubject.removeData();
        }
    }

}

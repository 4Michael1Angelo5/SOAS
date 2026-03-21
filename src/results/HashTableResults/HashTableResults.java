package results.HashTableResults;

import manager.HashTableManager.HashTableManager;
import manager.HashableManager;
import results.BenchmarkResult;
import results.ExperimentFormat;
import results.orchestration.ExperimentOrchestrator;
import types.DataType;
import util.Dictionary;
import util.Entry;

/**
 * Abstract child of {@link ExperimentOrchestrator}
 * Abstract parent of base experiement result types for managers
 * that implement the {@link manager.HashableManager} interface
 * @param <T> the {@link DataType} the Manager Manages
 * @param <C> the {@link Dictionary} container type
 * @param <M> the {@link  HashTableManager} type
 */
public abstract class HashTableResults<
        T extends DataType,                 // The data type the manager manages
        C extends Dictionary<Integer, T>,   // a dictionary type (hashtable ds)
        M extends HashTableManager<T, C>>    // A HashTableManager (abstract parent of base)
extends ExperimentOrchestrator<T,C,M> {

    public HashTableResults(
            Class<T> theDataType,
            M theManagerTestSubject,
            ExperimentFormat theExprimentFormatType,
            T theTypeToken) {
        super(theDataType, theManagerTestSubject, theExprimentFormatType, theTypeToken);
    }

    @Override
    public BenchmarkResult testAddNTimes(String theOperationName) {

        int inputSize = myTestContainer.size();

        double avgTime =
                myBenchmarkRunner.runSpeedTestWithSetup(
                        TRIAL_RUNS,
                        this::setUpForAdd,
                        this::addNTimes);

        BenchmarkResult result = new BenchmarkResult(inputSize, theOperationName, avgTime, getOpCounts());

        result.setLoadFactor(myManagerTestSubject.getData().loadFactor());
        result.setCollisions(myManagerTestSubject.getData().getCollisions());
        return result;
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

        BenchmarkResult result = new BenchmarkResult(inputSize, theOperationName, avgTime, getOpCounts());

        result.setLoadFactor(myManagerTestSubject.getData().loadFactor());
        result.setCollisions(myManagerTestSubject.getData().getCollisions());
        return result;
    }

    @Override
    public String getContainerName() {return myManagerTestSubject.getData().getClass().getSimpleName();}

    @Override
    public void removeNTimes() {
        for (T data: myTestContainer) {
            myManagerTestSubject.removeData(data);
        }
    }

}

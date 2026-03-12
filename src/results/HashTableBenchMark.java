package results;

import benchmark.BenchmarkRunner;
import loader.DataLoader;
import manager.HashableManager;
import manager.MapManager;
import manager.RosterManager;
import types.DataType;
import types.Player;
import util.ArrayStore;
import util.DataContainer;
import util.Dictionary;

import java.io.IOException;

/**
 * An abstract orchestration layer for benchmarking {@link MapManager} performance
 * across various {@link Dictionary} implementations.
 * * <p>The {@code HashTableBenchMark} class defines the lifecycle of a performance experiment,
 * ensuring that benchmarks are conducted under controlled conditions. It manages:
 * <ul>
 * <li><b>State Isolation:</b> Resets and prepares containers before each timed run
 * to prevent data pollution.</li>
 * <li><b>Execution:</b> Utilizes {@link BenchmarkRunner} to perform multiple
 * trial runs for statistical averaging.</li>
 * <li><b>Metrics Collection:</b> Captures both execution time and algorithmic
 * complexity metrics (comparisons/swaps).</li>
 * </ul>
 * * Subclasses should implement specific experiment logic (e.g., search or sort)
 * while leveraging this class's validation and reporting infrastructure.
 *
 * @param <T> The {@link DataType} being stored and manipulated.
 * @param <M> The {@link MapManager} implementation under test.
 */
public abstract class HashTableBenchMark<T extends DataType, M extends HashableManager<T>> implements Experiment {

    public DataContainer<T> myTestContainer;
    private final DataLoader<T> myDataLoader;
    protected final HashableManager<T> myManager;
    private final BenchmarkRunner myBenchmarkRunner = new BenchmarkRunner();
    private final ResultsDisplay myResultsDisplay;
    private final ArrayStore<BenchmarkResult> myResults = new ArrayStore<>(BenchmarkResult.class);
    private static final int TRIAL_RUNS = 30;


    public HashTableBenchMark(Class<T> theDataClass, HashableManager<T> theManager, ExperimentFormat theExperimentFormat) {
        super();
        myTestContainer = new ArrayStore<>(theDataClass);
        myDataLoader = new DataLoader<>(theDataClass, ()-> new ArrayStore<>(theDataClass));
        myManager = theManager;
        myResultsDisplay = new ResultsDisplay(theExperimentFormat, getManagerName(), getDataStructureName());
    }

    private RosterResults initResults(ExperimentFormat theExperimentFormat) {
        RosterManager RM = new RosterManager(()-> new ArrayStore<>(Player.class));

        return new RosterResults(RM,
                ()-> new ArrayStore<>(Player.class),
                theExperimentFormat);
    }
    //========================= Data Loading =================================
    /**
     * Delegates the data loading process to the manager.
     * @param theFilePath The path to the CSV source file.
     * @throws IOException If an error occurs during file reading.
     */
    public void loadData(String theFilePath) throws IOException {
        myTestContainer = myDataLoader.loadData(theFilePath);
    }

    //========================= Error handinling/ State Management =================================

    // ===== Error Handling to ensure coordinated state in experiment pipeline ==================

    public void ensureOperationCounterReset() {
        if (myManager.getSwaps() > 0 || myManager.getComparisons() > 0) {
            throw new RuntimeException(
                    """
                            Misconfigured Experiment:
                            The DataManager's OperationCounter
                            should be reset between Every Experiment.
                            """
            );
        }
    }
    public void ensureTestContainerNotEmpty() {
        if (myTestContainer.isEmpty()) {
            throw new RuntimeException(
                    """
                        Misconfigured Experiment:
                        please ensure to loadData before running experiments
                        """
            );
        }
    }

    public void ensureManagerContainerNotEmpty() {
        if (myManager.getData().isEmpty()) {
            throw new RuntimeException(
                    """
                            Misconfigured Experiment:
                            Please ensure to call setUpForRemove
                            before conducting removal experiments.
                            """
            );
        }
    }

    public void ensureManagerContainerEmpty() {
        if (!myManager.getData().isEmpty()) {
            throw new RuntimeException(
                    """
                            Misconfigured Experiment:
                            Please ensure to call setUpForAdd
                            before conducting add experiments.
                            """
            );
        }
    }

    public void validateStateBeforeAddTest() {
        ensureTestContainerNotEmpty();
        ensureManagerContainerEmpty();
        ensureOperationCounterReset();
    }

    public void validateStateBeforeRemoveTest() {
        ensureTestContainerNotEmpty();
        ensureManagerContainerNotEmpty();
        ensureOperationCounterReset();
    }

    public void validateStateBeforeSearch() {
        ensureTestContainerNotEmpty();
        ensureManagerContainerNotEmpty();
        ensureOperationCounterReset();
    }

    // =============================  Setup Tasks (Not Timed) ====================================
    /**
     * Clears the operation counter and the DataManger's
     * DataContainer.
     * This must be called before every Add experiment.
     */
    public void setUpForAdd() {
        myManager.clearData();
        myManager.resetCounter();
        validateStateBeforeAddTest();
    }

    /**
     * Non-timed setup task that populates {@code myManager} with data.
     * This prepares the state for {@link #removeNTimes()} without skewing the benchmark results.
     * This must be called before each Remove test.
     * @throws RuntimeException If the Manager's {@code HashTable} is not empty before starting.
     */
    // not timed
    public void setUpForRemove() {
        for (T dataObj : myTestContainer) {
            myManager.addData(dataObj);
        }
        myManager.resetCounter();
        validateStateBeforeRemoveTest();
    }

    public void setUpForSearch() {
        for (T dataObj : myTestContainer) {
            myManager.addData(dataObj);
        }
        myManager.resetCounter();
        validateStateBeforeSearch();
    }

    // =============================  Timed Tasks/ Experiments ====================================

    /**
     * Timed task that adds data from the test container to the manager.
     * Requires data to be loaded via {@link #loadData(String)} first.
     * Must be preceded by {@link #setUpForAdd}
     */
    // runnable & timed
    public void addNTimes(){

        for(T dataObj: myTestContainer) {
            myManager.addData(dataObj);
        }
    }

    /**
     * Timed task that removes data from the manager
     * Must be preceded by {@link #setUpForRemove} to ensure there is data to remove.
     */
    // runnable & timed
    public void removeNTimes() {
        for (T dataObj : myTestContainer) {
            myManager.removeData(dataObj);
        }
    }

    // =============================  benchmark testing  ====================================

    /**
     * Executes the addition benchmark.
     * @return An {@link BenchmarkResult} capturing size, operation name, and average time.
     */
    public BenchmarkResult testAdd(String theOperationName) {

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
    // timed
    public BenchmarkResult testRemove(String theOperationName) {
        final int inputSize = myManager.getData().size();
        final double avgTime =
                myBenchmarkRunner.runSpeedTestWithSetup(
                        TRIAL_RUNS,
                        this::setUpForRemove,
                        this::removeNTimes);

        return new BenchmarkResult(inputSize, theOperationName, avgTime, getOpCounts());
    }

    public OperationCounts getOpCounts() {
        return new OperationCounts(
                myManager.getSwaps(),
                myManager.getComparisons(),
                myManager.getLoadFactor(),
                myManager.getCollisions()
        );
    }

    //========================= Displaying Results =================================

    /**
     * Responsible for getting part of the title of the experiement.
     * @return The name of manager being tested
     */
    public String getManagerName() {
        return myManager.getClass().getSimpleName();
    }

    /**
     * Responsible for getting part of the title of the experiement.
     * @return The name of the DataStructure being tested.
     */
    public String getDataStructureName() {
        return myManager.getData().getClass().getSimpleName();
    }

    public void addExperimentResult(BenchmarkResult result) {
        myResults.add(result);
    }

    public void printResults() {
        myResultsDisplay.printResults(myResults);
    }
}
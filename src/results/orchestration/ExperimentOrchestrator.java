package results.orchestration;

import benchmark.BenchmarkRunner;
import loader.DataLoader2;
import manager.DataContainer.DataContainerManager;
import manager.HashTableManager.HashTableManager;
import manager.telemetry.OperationsManager;
import results.*;
import types.DataType;
import util.ArrayStore;
import util.DataContainer;
import util.OperationCountable;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * An abstract orchestration layer for benchmarking {@link DataContainerManager}
 * and {@link HashTableManager} performance
 * across various {@link OperationCountable} container implementations.
 * <p>
 *     The {@code ExperimentOrchestrator} class defines the lifecycle of a performance experiment,
 *      ensuring that benchmarks are conducted under controlled conditions. It manages:
 * <ul>
 *      <li>
 *          <b>State Isolation:</b> Resets and prepares containers before each timed run
 *           to prevent data pollution.
 *      </li>
 *      <li>
 *          <b>Execution:</b> Utilizes {@link BenchmarkRunner} to perform multiple
 *          trial runs for statistical averaging.
 *       </li>
 *      <li>
 *          <b>Metrics Collection:</b> Captures both execution time and algorithmic
 *             complexity metrics (comparisons/swaps).
 *      </li>
 * </ul>
 *
 * Subclasses should implement specific experiment logic (e.g., search or sort)
 * while leveraging this class's validation and reporting infrastructure.
 *
 * @param <T> The {@link DataType} being stored and manipulated.
 * @param <M> The {@link OperationCountable} container under test.
 */
public abstract class ExperimentOrchestrator
        <       T extends DataType,
                M extends OperationsManager<T,?>>
        implements OperationCountable<T>, Orchestraction{

    // logger color formatting
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    // logger
    private static final Logger logger = Logger.getLogger(ExperimentOrchestrator.class.getName());

    //Boilerplate to get logger to look good.
    static {
        // attempt to use logging properties file
        try (var is = ExperimentOrchestrator.class.getClassLoader().getResourceAsStream("logging.properties")) {
            if (is != null) {
                java.util.logging.LogManager.getLogManager().readConfiguration(is);
            } else {
                // Fallback: Manually set the format if file isn't found
                System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
            }
        } catch (Exception e) {
            System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
        }
    }

    /**
     * The number of trial runs to perform for each experiment
     * to calculate an average time.
     */
    public static final int TRIAL_RUNS  = 30;

    /**
     * The class type of the data being stored.
     */
    Class<T> myDataClass;

    /**
     * The utility runner that handles precise
     * timing and execution of benchmarks.
     */
    public BenchmarkRunner myBenchmarkRunner = new BenchmarkRunner();

    /**
     * DataLoader to load CSV data for testing.
     */
    private final DataLoader2<T, ArrayStore<T>> myDataLoader;

    /**
     * Results to Display to the console.
     */
    private final ResultsDisplay myResults;

    /**
     * A test container that holds the CSV data from the data loader.
     * It serves as the container for holding the current data sample
     * for testing.
     */
    public DataContainer<T> myTestContainer;

    protected final M myManagerTestSubject;

    private final ArrayStore<BenchmarkResult> myExperiments = new ArrayStore<>(BenchmarkResult.class);

    public ExperimentOrchestrator(
            Class<T> theDataType,
            M theManagerTestSubject,
            ExperimentFormat theExperimentFormat) {
        myDataClass = theDataType;
        myManagerTestSubject = theManagerTestSubject;

        myTestContainer = new ArrayStore<>(theDataType);
        myDataLoader = new DataLoader2<>(theDataType, ()-> new ArrayStore<>(theDataType));
        myResults = new ResultsDisplay(theExperimentFormat,getContainerName(), getMangerName());

    }

    public abstract void runAllExperiments() throws IOException;

    // =================== Data Loading ===================

    /**
     * Delegates the data loading process to the manager.
     * @param theFilePath The path to the CSV source file.
     * @throws IOException If an error occurs during file reading.
     */
    public void loadData(String theFilePath) throws IOException {
        myTestContainer = myDataLoader.loadData(theFilePath);
    }


    // ===================== Experiment Tite Formatting  =====================

    private String getMangerName() {
        return myManagerTestSubject.getClass().getSimpleName();
    }


    public abstract String getContainerName();


    // ===== Error Handling to ensure coordinated state in experiment pipeline ==================

    public void ensureOperationCounterReset() {
        if (myManagerTestSubject.getSwaps() > 0 || myManagerTestSubject.getComparisons() > 0) {
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
                    "Misconfigured ExperimentResult, please ensure "
                            + "to loadData before running running experiments"
            );
        }
    }

    public void ensureManagerContainerNotEmpty() {
        if (myManagerTestSubject.isEmpty()) {
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

        if (!myManagerTestSubject.isEmpty()) {
            throw new RuntimeException(
                    """
                            Misconfigured Experiment:
                            Please ensure to call setUpForAdd
                            before conducting add experiments.
                            """
            );

        }

    }

    // =============== State Validation before Experiments run ======================

    /**
     * Ensures proper intial conditions before performing
     * add tests.
     */
    @Override
    public void validateStateBeforeAddTest() {
        ensureTestContainerNotEmpty();
        ensureManagerContainerEmpty();
        ensureOperationCounterReset();
    }

    /**
     * Ensure proper intial conditions before performing
     * remove tests.
     */
    @Override
    public void validateStateBeforeRemoveTest() {
        ensureTestContainerNotEmpty();
        ensureManagerContainerNotEmpty();
        ensureOperationCounterReset();
    }

    //=========== set up tasks to run before each experiment (Not Timed) ===========

    @Override
    public void setUpForAdd() {
        myManagerTestSubject.clearData();
        myManagerTestSubject.resetCounter();
        validateStateBeforeAddTest();
    }

    /**
     * must be implemented by subclasses.
     */
    public abstract void setUpForRemove();

    // =================== timed tasks =======================

    public abstract void addNTimes();

    public abstract void removeNTimes();

    // =======================================================

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
    // =============== Recording Results =================
    /**
     * Adds an experiment result to the internal collection for final reporting.
     * @param theResult The result object to store.
     */
    public void addExperimentResult(BenchmarkResult theResult) {
        myExperiments.add(theResult);
    }

    // =============== Operation Counting =================

    public OperationCounts getOpCounts() {
        return new OperationCounts(getSwaps(),getComparisons());
    }
    @Override
    public int getSwaps() {
        return myManagerTestSubject.getSwaps();
    }

    @Override
    public int getComparisons() {
        return myManagerTestSubject.getComparisons();
    }

    @Override
    public void resetCounter() {
        myManagerTestSubject.resetCounter();
    }

    // ============================== Displaying Results =============================

    /**
     * Prints the final summary table of all stored experiments to the console.
     * Includes a header, data rows, and a footer.
     */
    public void printResults() {
        myResults.printResults(myExperiments);
    }


}

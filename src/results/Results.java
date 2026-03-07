package results;

import benchmark.BenchmarkRunner;
import loader.DataLoader;
import manager.DataManager;
import manager.Manager;
import types.DataType;
import util.ArrayStore;
import util.DataContainer;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

import exceptions.ResultsConfigException;

/**
 * An abstract orchestration layer for benchmarking {@link DataManager} performance
 * across various {@link DataContainer} implementations.
 * * <p>The {@code Results} class defines the lifecycle of a performance experiment,
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
 * @param <M> The {@link Manager} implementation under test.
 */
public abstract class Results<T extends DataType, M extends Manager<T>>
        implements Experiment{

    // logger color formatting
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    // logger
    private static final Logger logger = Logger.getLogger(Results.class.getName());

    //Boilerplate to get logger to look good.
    static {
        // attempt to use logging properties file
        try (var is = Results.class.getClassLoader().getResourceAsStream("logging.properties")) {
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
    static final int TRIAL_RUNS  = 30;

    /**
     * The class type of the data being stored.
     */
    Class<T> myDataClass;

    /**
     * The manager instance responsible for data operations.
     */
    M myManager;

    /**
     * The utility runner that handles precise
     * timing and execution of benchmarks.
     */
    public BenchmarkRunner myBenchmarkRunner = new BenchmarkRunner();

    /**
     * Internal storage for the results
     * of various experiments.
     */
    public ArrayStore<BenchmarkResult> myExperiments = new ArrayStore<>(BenchmarkResult.class);

    /**
     * A functional supplier
     * that provides fresh instances
     * of the target DataContainer.
     */
    public Supplier<DataContainer<T>> mySupplier;

    /**
     * Experiment format -> determines if we should include operation counting or not.
     */
    private final ExperimentFormat myExperimentFormat;

    /**
     * A test container that holds the CSV data from the data loader.
     * It serves as the container for holding the current data sample
     * for testing.
     */
    public DataContainer<T> myTestContainer;

    /**
     * DataLoader to load CSV data for testing.
     */
    public DataLoader<T> myDataLoader;

    /**
     * Results Formater
     */
    private final ResultsDisplay resultsDisplay;

    /**
     * Constructs a Results controller to manage benchmarks for a specific data type.
     * @param theDataClass can be Player, Drills, or Transaction, Actions, or FanRequests.
     * @param theManager The data manager instance that performs the actual operations.
     * @param theContainerSupplier A factory to create fresh instances of the DataContainer being tested.
     * @param theExperimentFormat Enum declaring if the experiment result should include operation counting.
     */
    public Results(
            Class<T> theDataClass,
            M theManager,
            Supplier<DataContainer<T>> theContainerSupplier,
            ExperimentFormat theExperimentFormat) {

        myDataClass = theDataClass;
        myManager = theManager;
        mySupplier = theContainerSupplier;
        myExperimentFormat = theExperimentFormat;
        myTestContainer = new ArrayStore<>(theDataClass);
        myDataLoader = new DataLoader<>(theDataClass, ()-> new ArrayStore<>(theDataClass));
        resultsDisplay = new ResultsDisplay(theExperimentFormat, getManagerTitle(), getTestResultsTitle());
        verifyConfiguration();
    }

    // error handling
    private void verifyConfiguration() {
        String managerContainer = myManager.getData().getClass().getSimpleName();
        String  containerProvided = mySupplier.get().getClass().getSimpleName();

        // Ex: if the DataManger was configured to use an Array but this class was provided a different
        // data container supplier to do benchmark.
        // The manager's DataContainer and the Results class's should match.
        if (!managerContainer.equals(containerProvided)) {
            throw new ResultsConfigException(myManager.getData().getClass(), containerProvided.getClass());
        }

        // don't allow the user to pass null to the Experiment format type.
        if (Objects.isNull(myExperimentFormat)) {
            throw new IllegalArgumentException("The experiment format type must not be null");
        }
    }

    /**
     * Delegates the data loading process to the manager.
     * @param theFilePath The path to the CSV source file.
     * @throws IOException If an error occurs during file reading.
     */
    public void loadData(String theFilePath) throws IOException {
        myTestContainer = myDataLoader.loadData(theFilePath);
    }

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
                    "Misconfigured ExperimentResult, please ensure "
                    + "to loadData before running running experiments"
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

    /**
     * Ensures proper intial conditions before performing
     * add tests.
     */
    public void validateStateBeforeAddTest() {
        ensureTestContainerNotEmpty();
        ensureManagerContainerEmpty();
        ensureOperationCounterReset();
    }

    /**
     * Ensure proper intial conditions before performing
     * remove tests.
     */
    public void validateStateBeforeRemoveTest() {
        ensureTestContainerNotEmpty();
        ensureManagerContainerNotEmpty();
        ensureOperationCounterReset();
    }

    /**
     * Ensures proper initial conditions before performing
     * search testing.
     */
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
     * @throws RuntimeException If the Manager's {@code DataContainer} is not empty before starting.
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
     * Timed task that populates a new container with all items from the manager.
     * Requires data to be loaded via {@link #loadData(String)} first.
     * * @throws RuntimeException If the manager's data source is empty.
     */
    // runnable & timed
    public void addNTimes(){

        for(T dataObj: myTestContainer) {
            myManager.addData(dataObj);
        }
    }

    /**
     * Timed task that removes every element from the setup container until it is empty.
     * Must be preceded by setUpForRemove to ensure there is data to remove.
     * * @throws RuntimeException If the container for removal is empty.
     */
    // runnable & timed
    public void removeNTimes() {
        while (!myManager.getData().isEmpty()) {
            myManager.removeData();
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

    // =============================  utlility  ====================================
    public OperationCounts getOpCounts() {
        return new OperationCounts(myManager.getSwaps(), myManager.getComparisons());
    }

    /**
     * Adds an experiment result to the internal collection for final reporting.
     * @param theResult The result object to store.
     */
    public void addExperimentResult(BenchmarkResult theResult) {
        myExperiments.add(theResult);
    }

    private String getTestResultsTitle(){
        return mySupplier.get().getClass().getSimpleName();
    }

    private String getManagerTitle() {
        return myManager.getClass().getSimpleName();
    }

    // ============================== Displaying Results =============================

    /**
     * Prints the final summary table of all stored experiments to the console.
     * Includes a header, data rows, and a footer.
     */
    public void printResults() {
        resultsDisplay.printResults(myExperiments);
    }

}

package results;

import benchmark.BenchmarkRunner;
import loader.DataLoader;
import manager.Manager;
import types.DataType;
import types.UndoRecord;
import util.ArrayStore;
import util.DataContainer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

import exceptions.ResultsConfigException;

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
    public DataContainer<BenchmarkResult> myExperiments = new ArrayStore<>(BenchmarkResult.class);

    /**
     * A temporary container
     * used specifically for destructive removal benchmarks.
     */
//    public DataContainer<T> containerForRemove;

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

//    public HashMap<String,Integer> operationCounts = new HashMap<>();

//    public DataContainer<T> containerForAdd;

    public DataContainer<T> myTestContainer;
    public DataLoader<T> myDataLoader;

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
//        containerForRemove = theContainerSupplier.get();
//        containerForAdd = theContainerSupplier.get();
        myTestContainer = new ArrayStore<>(theDataClass);
        myDataLoader = new DataLoader<>(theDataClass, ()-> new ArrayStore<>(theDataClass));
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

    // =============================  Displaying Results  ====================================

    /**
     * Helper method to format and print a single row of the results table.
     * * @param theResult The result to display.
     */
    public void logExperiment(BenchmarkResult theResult) {

        String row;
        int inputSize = theResult.inputSize();
        String operationName = theResult.method();
        double avgTime = theResult.avgTime();
        switch (myExperimentFormat) {

            case BENCHMARK_W_OPS ->
                    row = String.format("%-10s %-15s %-15.6f %-15s %-10s",
                    inputSize,
                    operationName,
                    avgTime,
                    theResult.operationCounts().comparisons(),
                    theResult.operationCounts().swaps()
                    );

            case BENCHMARK_NO_OPS ->
                    row = String.format("%-10s %-15s %-15.6f",
                    inputSize,
                    operationName,
                    avgTime);
            case null, default -> throw new RuntimeException("Experiment Format type cannot be null");
        }

        logger.info(ANSI_GREEN + row + ANSI_RESET);
    }

    private String getExperimentResultHeader() {
        String columnHeader;
        switch (myExperimentFormat) {
            case BENCHMARK_W_OPS -> columnHeader =
                    String.format("%-10s %-15s %-15s %-15s %-10s",
                            "Size",
                            "Operation",
                            "Avg Time (ms)",
                            "comparisons", "swaps");
            case BENCHMARK_NO_OPS -> columnHeader =
                    String.format("%-10s %-15s %-15s%n",
                            "Size",
                            "Operation",
                            "Avg Time (ms)");
            case null, default -> throw new IllegalArgumentException("FormateType Cannot be null");
        }
        return columnHeader;
    }

    /**
     * Prints the final summary table of all stored experiments to the console.
     * Includes a header, data rows, and a footer.
     */
    public void printResults() {
        logger.info(ANSI_GREEN + getTestResultsTitle() + " " + getManagerTitle() + ANSI_RESET);
        logger.info(ANSI_GREEN + "========== Benchmark Results ==========" + ANSI_RESET);

        logger.info(ANSI_GREEN + getExperimentResultHeader() + ANSI_RESET);

        logger.info(ANSI_GREEN + "----------------------------------------" + ANSI_RESET);
        for (BenchmarkResult result: myExperiments) {
            logExperiment(result);
        }
        logger.info(ANSI_GREEN + "========================================\n" + ANSI_RESET);
    }

}

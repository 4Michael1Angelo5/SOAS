package results;

import benchmark.BenchmarkRunner;
import manager.Manager;
import types.DataType;
import util.ArrayStore;
import util.DataContainer;

import java.io.IOException;
import java.util.function.Supplier;
import java.util.logging.Logger;

import util.ResultsConfigException;


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
    DataContainer<ExperimentResult> myExperiments = new ArrayStore<>(ExperimentResult.class, 16);

    /**
     * A temporary container
     * used specifically for destructive removal benchmarks.
     */
    DataContainer<T> containerForRemove;

    /**
     * A functional supplier
     * that provides fresh instances
     * of the target DataContainer.
     */
    Supplier<DataContainer<T>> mySupplier;

    /**
     * Constructs a Results controller to manage benchmarks for a specific data type.
     * * @param theDataClass can be Player, Drills, or Transaction.
     * @param theManager The data manager instance that performs the actual operations.
     * @param theContainerSupplier A factory to create fresh instances of the DataContainer being tested.
     */
    public Results(
            Class<T> theDataClass,
            M theManager,
            Supplier<DataContainer<T>> theContainerSupplier) {
        myDataClass = theDataClass;
        myManager = theManager;
        containerForRemove = theContainerSupplier.get();
        mySupplier = theContainerSupplier;
        verifyConfiguration();
    }

    // error handling
    private void verifyConfiguration() {
        String managerContainer = myManager.getData().getClass().getSimpleName();
        String containerProved = containerForRemove.getClass().getSimpleName();

        if (!managerContainer.equals(containerProved)) {
            throw new ResultsConfigException(myManager.getData().getClass(), containerForRemove.getClass());
        }
    }

    /**
     * Delegates the data loading process to the manager.
     * * @param theFilePath The path to the CSV source file.
     * @throws IOException If an error occurs during file reading.
     */
    public void loadData(String theFilePath) throws IOException {
        myManager.loadCsvData(theFilePath);
    }

    /**
     * Timed task that populates a new container with all items from the manager.
     * Requires data to be loaded via {@link #loadData(String)} first.
     * * @throws RuntimeException If the manager's data source is empty.
     */
    // runnable & timed
    public void addNTimes(){
        if (myManager.getData().isEmpty()) {
            throw new RuntimeException(
                    "Misconfigured ExperimentResult, please ensure " +
                            "to loadData before running this experiment"
            );
        }
        DataContainer<T> temp = mySupplier.get();
        int inputSize = myManager.getData().size();
        for (T dataObj : myManager.getData()) {
            temp.add(dataObj);
        }
    }

    /**
     * Timed task that removes every element from the setup container until it is empty.
     * Must be preceded by {@link #setUpForRemove()} to ensure there is data to remove.
     * * @throws RuntimeException If the container for removal is empty.
     */
    // runnable & timed
    public void removeNTimes() {
        if (containerForRemove.isEmpty()) {
            throw new RuntimeException(
                    "Misconfigured ExperimentResult, please insure "
                            + "setpUpForRemove() is called each time "
                            + "before running removeNTimes() "
            );
        }
        while (!containerForRemove.isEmpty()) {
            containerForRemove.remove();
        }
    }

    /**
     * Non-timed setup task that populates {@code containerForRemove} with data.
     * This prepares the state for {@link #removeNTimes()} without skewing the benchmark results.
     * * @throws RuntimeException If the removal container is not empty before starting.
     */
    // not timed
    public void setUpForRemove() {
        if (!containerForRemove.isEmpty()) {
            throw new RuntimeException(
                    "Programmer Error: Misconfigured ExperimentResult "
                            + " containerForRemove should be empty "
                            + " before this method is called."
            );
        }
        for (T dataObj : myManager.getData()) {
            containerForRemove.add(dataObj);
        }
    }

    /**
     * Executes the removal benchmark by coordinating setup and timed execution.
     * * @return An {@link ExperimentResult} capturing size, operation name, and average time.
     */
    // timed
    public ExperimentResult testRemove() {
        if (myManager.getData().isEmpty()) {
            throw new RuntimeException(
                    "Misconfigured ExperimentResult, please ensure " +
                            "to loadData before running this experiment"
            );
        }

        final int inputSize = myManager.getData().size();
        final String operation = "remove";
        final double avgTime =
                myBenchmarkRunner.runSpeedTestWithSetup(TRIAL_RUNS, this::setUpForRemove, this::removeNTimes);

        return new ExperimentResult(inputSize, operation, avgTime);
    }

    /**
     * Executes the addition benchmark.
     * * @return An {@link ExperimentResult} capturing size, operation name, and average time.
     */
    public ExperimentResult testAdd() {

        int inputSize = myManager.getData().size();
        String operation = "add";
        double avgTime = myBenchmarkRunner.runSpeedTestAndGetAvg(TRIAL_RUNS, this::addNTimes);

        return new ExperimentResult(inputSize, operation, avgTime);
    }


    /**
     * Adds an experiment result to the internal collection for final reporting.
     * * @param theResult The result object to store.
     */
    public void addExperimentResult(ExperimentResult theResult) {
        myExperiments.add(theResult);
    }

    /**
     * Helper method to format and print a single row of the results table.
     * * @param theResult The result to display.
     */
    public void logExperiment(ExperimentResult theResult) {
        String row = String.format("%-10s %-15s %-15.1f", theResult.inputSize(), theResult.operation(), theResult.avgTime());
        logger.info(ANSI_GREEN + row + ANSI_RESET);
    }

    private String getTestResultsTitle(){
        return mySupplier.get().getClass().getSimpleName();
    }

    private String getManagerTitle() {
        return myManager.getClass().getSimpleName();
    }

    /**
     * Prints the final summary table of all stored experiments to the console.
     * Includes a header, data rows, and a footer.
     */
    public void printResults() {
        logger.info(ANSI_GREEN + getTestResultsTitle() + " " + getManagerTitle() + ANSI_RESET);
        logger.info(ANSI_GREEN + "========== Benchmark Results ==========" + ANSI_RESET);
        String divider = String.format("%-10s %-15s %-15s%n", "Size", "Operation", "Avg Time (ms)");
        logger.info(ANSI_GREEN + divider + ANSI_RESET);
        logger.info(ANSI_GREEN + "----------------------------------------" + ANSI_RESET);
        for (ExperimentResult result: myExperiments) {
            logExperiment(result);
        }
        logger.info(ANSI_GREEN + "========================================\n" + ANSI_RESET);
    }

}

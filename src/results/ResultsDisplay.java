package results;

import util.ArrayStore;
import util.DataContainer;

import java.util.logging.Logger;

/**
 * The ResultsDisplay class is resposnbile for
 * formating experiment results and providng a template
 * for how all results should be displayed to the console.
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class ResultsDisplay {

    // logger color formatting
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    String myManagerName;
    String myDataStructureName;

    // logger
    private static final Logger logger = Logger.getLogger(ResultsDisplay.class.getName());

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

    public final ExperimentFormat myFormat;

    public ResultsDisplay(
                    ExperimentFormat theExperiementFormat,
                    String managerName,
                    String theDataStructureName) {

        myFormat = theExperiementFormat;
        myManagerName = managerName;
        myDataStructureName = theDataStructureName;
    }

    /**
     * Helper method to format and print a single row of the results table.
     * * @param theResult The result to display.
     */
    public void logExperiment(BenchmarkResult theResult) {

        String row;
        int inputSize = theResult.inputSize();
        String operationName = theResult.method();
        double avgTime = theResult.avgTime();
        switch (myFormat) {

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
        switch (myFormat) {
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

    private String getTableHeaderDivider() {
        switch (myFormat) {
            case BENCHMARK_NO_OPS -> {
                return "========== Benchmark Results ==========";
            }
            case BENCHMARK_W_OPS -> {
                return "====================== Benchmark Results ======================";
            }
            case null, default -> {
                throw new RuntimeException("Encounterred Runtime error: Experiment format type cannot be null.");
            }
        }
    }

    private String getTableFooterDivider() {
        switch (myFormat) {

            case BENCHMARK_NO_OPS -> {
                return "========================================\n";
            }
            case BENCHMARK_W_OPS -> {
                return "===============================================================";
            }
            case null, default -> {
                throw new RuntimeException("Encounterred Runtime error: Experiment format type cannot be null.");
            }
        }
    }

    private String getTableDivider() {

        switch (myFormat) {
            case BENCHMARK_NO_OPS -> {
                return "----------------------------------------";
            }
            case BENCHMARK_W_OPS -> {
                return "---------------------------------------------------------------";
            }
            case null, default -> {
                throw new RuntimeException("Encounterred Runtime error: Experiment format type cannot be null.");
            }
        }
    }

    /**
     * Prints the final summary table of all stored experiments to the console.
     * Includes a header, data rows, and a footer.
     */
    public void printResults(ArrayStore<BenchmarkResult> theExprirementResults) {
        logger.info(ANSI_GREEN + "\n"+ myDataStructureName + " " + myManagerName + ANSI_RESET);

        logger.info(ANSI_GREEN + getTableHeaderDivider() + ANSI_RESET);

        logger.info(ANSI_GREEN + getExperimentResultHeader() + ANSI_RESET);

        logger.info(ANSI_GREEN + getTableDivider() + ANSI_RESET);
        for (BenchmarkResult result: theExprirementResults) {
            logExperiment(result);
        }
        logger.info(ANSI_GREEN + getTableFooterDivider() + ANSI_RESET);
    }

}

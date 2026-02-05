package util;

/**
 * An Error that occurs when a DataManager is
 * instantiated with a different DataContainer then
 * the Results class.
 * @author Chris Chun
 * @version 1.1
 */
public class ResultsConfigException extends RuntimeException {
    public ResultsConfigException(String message) {
        super(message);
    }

    public ResultsConfigException(Class<?> expected, Class<?> actual) {
        super(String.format(
                "Manager Configuration Error: Type Mismatch!\n" +
                        "Expected: [%s]\n" +
                        "Actual:   [%s]\n\n" +
                        "The DataManager was instantiated using [%s], but the benchmark " +
                        "results were configured to test [%s].\n" +
                        "Please ensure the Supplier passed to the Manager and the Results match!",
                expected.getSimpleName(), actual.getSimpleName(),
                actual.getSimpleName(), expected.getSimpleName()));
    }
}

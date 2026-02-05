package util;

/**
 * Thrown when the data loader returns
 * a different type of DataContainer
 * than the one the Manager was
 * instantiated with.
 */
public class MismatchedContainerFromLoaderException extends RuntimeException {

    public MismatchedContainerFromLoaderException(String message) {
        super();
    }

    public MismatchedContainerFromLoaderException(Class<?> expected, Class<?> actual) {
        super(String.format(
                """
                        
                        [FATAL CONFIGURATION ERROR]
                        Expected Container: %s
                        Actual Container:   %s
                        Reason: The DataLoader returned a %s container, but the Manager was configured to use %s.
                        Please make sure the DataLoader returns the same container the Manager was instantiated with.""",
                expected.getSimpleName(),
                actual.getSimpleName(),
                actual.getSimpleName(),
                expected.getSimpleName()
        ));
    }
}
package manager;

import types.DataType;
import util.Dictionary;

import java.io.IOException;

public interface HashableManager<T extends DataType> {
    // =====================  getting =======================

    /**
     * Gets the data from the DataContainer.
     * @return A DataContainer with the Objects the Manager manages.
     */
    Dictionary<Integer, T> getData();

    /**
     *
     * @return the child concrete child class that instantiated
     * the abstract parent DataManger.
     */
    Class<?> getManagerClass();

    // =====================  adding =======================

    /**
     * adds data objects to the DataManagers DataContainers
     * using the most efficient underlying implementation for their
     * respective DataContainer.
     *
     */
    void addData(T theData);


    // =====================  removing =======================

    /**
     * Removes data from the DataContainer using the
     * most efficient method.
     * @return the object removed.
     */
    T removeData(T theData);


    // =====================  util =======================

    /**
     * loads data from the CSV loader.
     * @param theFilePath the file path to the csv data.
     * @throws IOException if encountering an error parsing csv data.
     */
    void loadCsvData(String theFilePath) throws IOException;

    /**
     * Prints the data.
     */
    void printData();

    /**
     * Clears the current managers DataContainer.
     */
    void clearData();

    // =====================  operation counting =======================

    /**
     * @return the number of swaps performed over a period of method calls
     * manipulating data.
     */
    int getSwaps();

    /**
     * @return the number of comparisons performed over a period of method calls
     * manipulating data.
     */
    int getComparisons();

    /**
     * Resets the {@link counter.OperationCounter}
     */
    void resetCounter();
}

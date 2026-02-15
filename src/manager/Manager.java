package manager;

import types.DataType;
import util.DataContainer;

import java.io.IOException;

public interface Manager<T extends DataType> {

    // ===================== flags =========================
    /**
     * Each concrete manager class must specify upfront
     * if they require indexed based access to efficiently
     * manage the contents of their containers. This flag
     * ensures each concrete child class is instantiated with
     * an appropriate data container.
     * @return true if the DataManager needs indexed access to
     * effectively manage their data.
     */
    boolean needsIndexedAccess();

    // =====================  getting =======================

    /**
     * Gets the data from the DataContainer.
     * @return A DataContainer with the Objects the Manager manages.
     */
    DataContainer<T> getData();

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
    T removeData();


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


}

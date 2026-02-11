package manager;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

import loader.DataLoader;
import types.Player;
import util.DataContainer;
import types.DataType;
import util.ManagerConfigException;
import util.MismatchedContainerFromLoaderException;

/**
 * DataManager provides a high-level abstraction for managing collections of {@link DataType}
 * objects.
 * * <p>This class is container-agnostic; it does not dictate how data is stored
 * (e.g., Array, Linked List, Stack, or Queue). Instead, the storage strategy is
 * injected via a {@link java.util.function.Supplier} during instantiation,
 * allowing for runtime flexibility and performance optimization based on the
 * specific needs of the data type.</p>
 *
 * <p>Shared functionality provided by this manager includes:</p>
 * <ul>
 * <li>Generic CSV loading via {@link DataLoader}</li>
 * <li>Identity-based searching and removal</li>
 * <li>Iterable data access for reporting and logging</li>
 * </ul>
 * * @author Chris Chun, Ayush
 * @version 1.2
 * @param <T> The specific {@link DataType} managed (e.g., Player, Transaction, or Drill).
 */
public abstract class DataManager <T extends DataType> implements Manager<T>{

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static final Logger logger = Logger.getLogger(DataManager.class.getName());

    private final DataLoader<T> myDataLoader;

    /**
     * An array based data structure holding data:
     * ie Players, Transactions, or Drills
     */
    protected DataContainer<T> myData;

    private final Class<T> myDataClass;

    public DataManager(Class<T> theDataClass, Supplier<DataContainer<T>> theSupplier){
        myData = theSupplier.get();
        myDataClass = theDataClass;
        myDataLoader = new DataLoader<>(theDataClass, theSupplier);
        validateManagerConfig();
    }

    // ======================= error handling and validation ==================

    public boolean isValidContainer(DataContainer<?> theOtherContainer) {
        if (theOtherContainer == null) {
            return false;
        }
        return myData.getClass().equals(theOtherContainer.getClass());
    }

    public void validateManagerConfig() {
        if (this.needsIndexedAccess() && !myData.supportsIndexedAccess()) {
            throw new ManagerConfigException(getManagerClass(), myData.getClass());

        };
    }

    // =======================  getting and setting ===========================
    /**
     * gets the data array.
     * @return the data array
     */
    @Override
    public DataContainer<T> getData() {return myData;}

    public Class<T> getDataClass() {return myDataClass;}

    // =======================  loading ===========================

    /**
     * Resets data to the loaded csv.
     * @param theFilePath the file path to the data you want to load.
     * @throws IOException if file not found.
     */
    @Override
    public void loadCsvData(String theFilePath) throws IOException {
        DataContainer<T> loaderResults = myDataLoader.loadData(theFilePath);

        if (!isValidContainer(loaderResults)) {
            // error example:
            // if the DataLoader returns an ArrayStore
            // but the manager was configured to use a SinglyLinkedList
            throw new MismatchedContainerFromLoaderException(myData.getClass(), loaderResults.getClass());
        }
        myData = loaderResults;
    }

    // =======================  adding ===========================

    /**
     * Add data to the array.
     * @param theData the data to add to the array.
     */
    @Override
    public void addData(T theData) {
        myData.add(theData);
    }

    // ======================= removing  ===========================
    @Override
    public T remove(){
        return myData.remove();
    }

    /**
     * remove element by index
     * This operation can only be performed by roster managers that require indexed access.
     * @param theIndex the index of the element to remove.
     * @return the removed element.
     */
    public T removeAt(int theIndex) {
        if (!this.needsIndexedAccess()) {
            throw new IllegalArgumentException("Stacks and Queues do not support indexed access");
        }
        return myData.removeAt(theIndex);
    }

    /**
     *
     * @param theId the ID of the data entry (Player, Drills, Transactions)
     *              you wish to delete.
     * @return the removed data.
     */
    public T removeById(int theId) {

        int index = myData.findBy((T theDataObject) -> theDataObject.id() == theId);

        // throw exception if not found
        if (index == -1) {
            throw new NoSuchElementException("id not found");
        }

        // store it
        T theRemovedData =  myData.get(index);

      // remove it, and shift everything
        myData.removeAt(index);

        return theRemovedData;
    }


    // =======================  updating ===========================

    protected void setData(int theIndex, T theData) {

        myData.set(theIndex,theData);
    }


    // =======================  searching ===========================


    /**
     *
     * @param theId theId of the data entry (Player, Drills, Transaction)
     * @return the index of the data if present, -1 otherwise.
     */
    @Override
    public int findById(int theId) {

        return myData.findBy( (T theDataObject) -> theDataObject.id() == theId);
    }

    // =======================  utility ===========================


    /**
     * Prints data parsed from a csv file.
     */
    public void printData(){
        for (T data: myData) {
            logger.info(ANSI_GREEN + data.toString() + ANSI_RESET);
        }
    }

    @Override
    public void clearData() {
        myData.clear();
    }

}

package manager;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.logging.Logger;

import counter.OperationCounter;
import loader.DataLoader;
import util.*;
import types.DataType;
import exceptions.ManagerConfigException;
import exceptions.MismatchedContainerFromLoaderException;

import static java.util.Objects.isNull;

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
 * <li>Adding and removing data</li>
 * <li>Iterable data access for reporting and logging</li>
 * </ul>
 * @author Chris Chun, Ayush
 * @version 2
 * @param <T> The specific {@link DataType} managed (e.g., Player, Transaction, or Drill).
 */
public abstract class DataManager <T extends DataType> implements Manager<T>{

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static final Logger logger = Logger.getLogger(DataManager.class.getName());

    private DataLoader<T> myDataLoader;

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

        }
    }

    // =======================  getting and setting ===========================
    /**
     * gets the data array.
     * @return the data array
     */
    @Override
    public DataContainer<T> getData() {return myData;}

    public Class<T> getDataClass() {return myDataClass;}

    public void setDataLoader(Class<T> theDataClass, Supplier<DataContainer<T>>theSupplier) {

        DataContainer<T> providedContainer = theSupplier.get();

        if (providedContainer.getClass().equals(myData.getClass())){
            myDataLoader = new DataLoader<>(theDataClass, theSupplier);

        } else {
            throw new IllegalArgumentException("Cannot update the DataLoader " +
                    "because the Manager was instantiated with a different container type");
        }

    }

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
     * <p>
     * Adds an element to the {@link DataContainer} using the <u>most efficient
     * method</u> for the underlying data structure.
     * </P>
     * <ul>
     * <li>{@link ArrayStore}  -> adds at end</li>
     * <li>{@link SinglyLinkedList} ->  adds at tail (end)</li>
     * <li>{@link ArrayStack} ->  adds at end (push)</li>
     * <li>{@link LinkedQueue} -> adds at end (enqueue)</li>
     * <li>{@link BinaryHeapPQ} -> heapifyUp procedure</li>
     * </ul>
     * @param theData the object to add to the DataContainer.
     */
    @Override
    public void addData(T theData) {

        if (isNull(theData)) {
            throw new IllegalArgumentException("The Data Cannot be null");
        }
        myData.add(theData);
    }


    /**
     * <p>
     * Removes an element from the {@link DataContainer} using the <u>most efficient
     * method</u> for the underlying data structure.
     * </P>
     * <ul>
     * <li>{@link ArrayStore} -> removes from end</li>
     * <li>{@link SinglyLinkedList} -> removes head (front)</li>
     * <li>{@link ArrayStack} ->  removes from end (pop)</li>
     * <li>{@link LinkedQueue} -> removes from front (dequeue)</li>
     * <li>{@link BinaryHeapPQ} -> heapifyDown procedure</li>
     * </ul>
     * @return the removed object from the Data Storage Container.
     * @throws NoSuchElementException if the DataContainer is empty.
     */
    // ======================= removing  ===========================
    @Override
    public T removeData(){
        return myData.remove();
    }


    // =======================  utility ===========================

    /**
     * Prints the {@link DataType} objects contained in the
     * {@link DataContainer}
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

    // =======================  operation counting ===========================

    /**
     *
     * @return the number of swaps performed
     * since initiation of a sequence of commands.
     */
    @Override
    public int getSwaps() {
        return myData.getSwaps();
    }

    /**
     *
     * @return the number of comparisons performed
     * since initiation of a sequence of commands.
     */
    @Override
    public int getComparisons() {
        return myData.getComparisons();
    }

    /**
     * resets the {@link OperationCounter}
     */
    @Override
    public void resetCounter(){myData.resetCounter();}


}

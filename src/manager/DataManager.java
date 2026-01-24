package manager;

import java.io.IOException;
import java.util.logging.Logger;

import loader.DataLoader;
import types.DataType;
import util.ArrayStore;

/**
 *
 * @author Chris Chun, Ayush
 * @version 1.1
 * The Data Manager abstracts how data is managed in an array based
 * utility class, DataStore<T>. The data manager implements shared
 * functionality between the concrete child classes, RoasterManager,
 * DrillsManager, and TransactionManager.
 * @param <T> permits Player, Transaction, and Drill classes
 */
public abstract class DataManager <T extends DataType> {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static final Logger logger = Logger.getLogger(DataManager.class.getName());

    private final DataLoader myDataLoader = new DataLoader();

    /**
     * An array based data structure holding data:
     * ie Players, Transactions, or Drills
     */
    private final ArrayStore<T> myData;

    private final Class<T> myDataClass;

    public DataManager(Class<T> theDataClass){
        myData = new ArrayStore<>(theDataClass,16);
        myDataClass = theDataClass;
    }

    public void addCsvData(String theFilePath) throws IOException {
        myData.append(myDataLoader.loadData(myDataClass,theFilePath));
    }

    /**
     * Add data to the array.
     * @param theData the data to add to the array.
     */
    public void addData(T theData) {
        myData.add(theData);
    }

    /**
     *
     * @param theId theId of the data entry (Player, Drills, Transaction)
     * @return the index of the data if present, -1 otherwise.
     */
    public int findById(int theId) {
        int index = -1;

        for (int i = 0; i < myData.size(); i++) {
            if (myData.get(i).id() == theId){
                index = i;
                break;
            }
        }

        return index;
    }

    /**
     * gets the data array.
     * @return the data array
     */
    protected ArrayStore<T> getData() {
        return myData;
    }

    /**
     *
     * @param theId the ID of the data entry (Player, Drills, Transactions)
     *              you wish to delete.
     * @return the removed data.
     */
    public T removeById(int theId) {

        // find the index;
        int index = findById(theId);

        // throw exception if not found
        if (index == -1) {
            throw new RuntimeException("id not found");
        }

        // store it
        T theRemovedData =  myData.get(index);

        // remove it, and shift everything
        myData.removeAtIndex(index);

        return theRemovedData;
    }

    /**
     * Prints data parsed from a csv file.
     * @param theData An ArrayStore of DataType objects,
     *                either Players, Transactions, or Drills
     */
    public void printData(ArrayStore<T> theData){

        for (int i = 0; i < theData.size(); i++) {
            logger.info(ANSI_GREEN + theData.get(i).toString() + ANSI_RESET);
        }
    }

}

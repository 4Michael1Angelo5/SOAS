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

    /**
     * An array based data structure holding data:
     * ie Players, Transactions, or Drills
     */
    private final ArrayStore<T> myData;

    /**
     * The type of data the DataManager is managing.
     */
    final private Class<T> dataClass;

    public DataManager(Class<T> theDataClass){
        dataClass = theDataClass;
        myData = new ArrayStore<>(theDataClass,16);
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
     * @return the number of data items in the array.
     */
    public int size() {
        return myData.size();
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
    protected T removeById(int theId) {

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
}

package manager;

import loader.DataLoader;
import types.DataType;
import util.*;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * This Manager uses a HashTable to manage {@link DataType} objects.
 * @param <T> the DataType this manager manages.
 */
public abstract class MapManager <T extends DataType>  {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static final Logger LOGGER = Logger.getLogger(MapManager.class.getName());


    private final HashTable<Integer, T> myMap;
    private final DataLoader<T> myDataLoader;


    public MapManager(Class<T> theDataType) {
        myMap = new HashTable<>(Integer.class, theDataType);
        myDataLoader = new DataLoader<>(theDataType, () -> new ArrayStore<>(theDataType));
    }

    /**
     * Resets data to the loaded csv.
     * @param theFilePath the file path to the data you want to load.
     * @throws IOException if file not found.
     */
    public void loadCsvData(String theFilePath) throws IOException {
        DataContainer<T> loaderResults = myDataLoader.loadData(theFilePath);
        myMap.clear();
        for (T dataObject : loaderResults) {
            myMap.put(dataObject.id(), dataObject);
        }
    }

    public void addData(T dataObj) {
        myMap.put(dataObj.id(), dataObj);
    }

    public T removeData(T dataObj) {
        return myMap.delete(dataObj.id());
    }

    public int searchById(int theId) {
        if (containsRecord(theId)) {
            return theId;
        }
        return -1;
    }

    public void updateRecord(T theNewRecord) {
        if (containsRecord(theNewRecord.id())) {
            myMap.put(theNewRecord.id(), theNewRecord);
        }
    }

    public boolean containsRecord(int theId) {
        return myMap.containsKey(theId);
    }

    public HashTable<Integer, T> getData() {
        return myMap;
    }

    public T get(int theId) {
        return myMap.get(theId);
    }

    public void printData() {
        for (Entry<Integer, T> entry: myMap) {
            LOGGER.info(
                    ANSI_GREEN
                    + "{\n"
                    + " id: " + entry.key() + ",\n"
                    + " value: " + entry.value() + "\n"
                    + "},\n"
                    + ANSI_RESET
            );
        }
    }


    public int getSwaps() {
        return myMap.getSwaps();
    }


    public int getComparisons() {
        return myMap.getComparisons();
    }

    public void resetCounter() {
        myMap.resetCounter();
    }
}

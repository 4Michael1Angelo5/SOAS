package manager.HashTableManager;

import manager.HashableManager;
import manager.telemetry.OperationsManager;
import types.DataType;
import util.Entry;
import util.Dictionary;

import java.util.logging.Logger;

public abstract class HashTableManager
        <T extends DataType, M extends Dictionary<Integer,T>>
        extends OperationsManager<T,M>
        implements HashableManager<T> {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static final Logger LOGGER = Logger.getLogger(HashTableManager.class.getName());

    public HashTableManager(Class<T> theDataType, M theContainer) {
        super(theDataType, theContainer);
    }

    @Override
    public void addData(T dataObj) {
        myData.put(dataObj.id(), dataObj);
    }

    @Override
    public T removeData(T dataObj) {
        return myData.delete(dataObj.id());
    }

    public int searchById(int theId) {
        if (containsRecord(theId)) {
            return theId;
        }
        return -1;
    }

    public void updateRecord(T theNewRecord) {
        if (containsRecord(theNewRecord.id())) {
            myData.put(theNewRecord.id(), theNewRecord);
        }
    }

    public boolean containsRecord(int theId) {
        return myData.containsKey(theId);
    }

    @Override
    public M getData() {
        return myData;
    }

    @Override
    public void loadCsvData(String theFilePath) {
        return;
    }


    public T get(int theId) {
        return myData.get(theId);
    }

    @Override
    public void printData() {
        for (Entry<Integer, T> entry: myData) {
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

    @Override
    public void clearData() {}

}

package manager;

import types.DataType;
import util.DataContainer;
import util.ManagerConfigException;

import java.io.IOException;

public interface Manager<T extends DataType> {

    void addData(T theData) throws ManagerConfigException;

    DataContainer<T> getData();

    T removeById(int theId);

    T remove();

    int findById( int theId);

    void printData();

    void loadCsvData(String theFilePath) throws IOException;

    void clearData();

    boolean needsIndexedAccess();

    Class<?> getManagerClass();
}

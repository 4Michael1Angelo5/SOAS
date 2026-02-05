package manager;

import types.DataType;
import util.DataContainer;

import java.io.IOException;

public interface Manager<T extends DataType> {

    public void addData(T theData);

    public DataContainer<T> getData();

    public T removeById(int theId);

    public int findById( int theId);

    public void printData();

    public void loadCsvData(String theFilePath) throws IOException;

    public void clearData();
}

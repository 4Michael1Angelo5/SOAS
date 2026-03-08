package manager.telemetry;

import types.DataType;
import util.OperationCountable;

public abstract class OperationsManager<
        T extends DataType,
        C extends OperationCountable<T>>
        implements OperationCountable<T>{

    protected C myData;
    public OperationsManager(Class<T> theDataType, C theContainer) {
        myData = theContainer;
    }

    public C getData() {
        return myData;
    }

    public abstract boolean isEmpty();

    public abstract void addData(T theData);

    public abstract void clearData();

    public abstract int size();

    @Override
    public int getSwaps() {
        return myData.getSwaps();
    }

    @Override
    public int getComparisons() {
        return myData.getComparisons();
    }

    @Override
    public void resetCounter() {
        myData.resetCounter();
    }
}

package manager.telemetry;

import types.DataType;
import util.OperationCountable;

public abstract class OperationsManager<T extends DataType, M extends OperationCountable<T>>
        implements OperationCountable<T>{

    protected M myData;
    public OperationsManager(Class<T> theDataType, M theContainer) {
        myData = theContainer;
    }

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

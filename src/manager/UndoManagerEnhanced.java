package manager;

import types.Undoable;
import util.DataContainer;

import java.util.function.Supplier;

public class UndoManagerEnhanced<T extends Undoable> extends DataManager<T>{

    public UndoManagerEnhanced(Class<T> theDataClass, Supplier<DataContainer<T>> theSupplier) {
        super(theDataClass, theSupplier);
    }

    @Override
    public boolean needsIndexedAccess() {
        return false;
    }

    @Override
    public Class<?> getManagerClass() {
        return null;
    }
}

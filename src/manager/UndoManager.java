package manager;

import types.Action;
import types.Undoable;
import util.DataContainer;

import java.util.function.Supplier;

public class UndoManager extends  DataManager<Action>{

    public UndoManager( Supplier<DataContainer<Action>> theSupplier){
        super(Action.class, theSupplier);

    }

    // ================================ flags =================================

    @Override
    public boolean needsIndexedAccess() {
        return false;
    }

    @Override
    public Class<UndoManager> getManagerClass() {
        return UndoManager.class;
    }
}

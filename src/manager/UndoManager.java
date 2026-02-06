package manager;

import types.Player;
import types.UndoAction;
import util.DataContainer;

import java.util.function.Supplier;

public class UndoManager extends DataManager<UndoAction> {

    public UndoManager(Supplier<DataContainer<UndoAction>> theSupplier){
        super(UndoAction.class, theSupplier);
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

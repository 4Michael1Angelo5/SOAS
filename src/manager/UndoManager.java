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

    /*
 * UndoManager TODO - Required for Full Credit:
 *
 * 1. Add peek() method - assignment explicitly requires viewing most recent action without undoing
 *    public Action peek() { ... }
 *
 * 2. Add semantic wrapper methods for better API clarity:
 *    - public void recordAction(Action action) { addData(action); }
 *    - public Action undo() { return removeData(); }
 *    - public boolean canUndo() { return !myData.isEmpty(); }
 *
 * 3. Add validation to ensure Stack is used (not Queue/List):
 *    - Check myData instanceof Stack in constructor or validation method
 *
 * 5. Create demonstration method showing LIFO behavior:
 *    - Record 3 actions (Add Smith, Update Brown, Remove Jones)
 *    - Undo in reverse order
 *    - Print to show LIFO
 *
 * Note: peek() is the critical missing piece - without it, you lose points.
 * The s

     */
}

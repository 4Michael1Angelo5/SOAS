package manager;

import types.Action;
import types.ActionType;
import types.Undoable;
import util.DataContainer;
import util.Stack;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class UndoManager extends  DataManager<Action>{

    private static final Logger LOGGER = Logger.getLogger(UndoManager.class.getName());

    public UndoManager( Supplier<DataContainer<Action>> theSupplier){
        super(Action.class, theSupplier);
        validateStack();
    }

    /**
     * Checks that the data container is a Stack.
     * UndoManager only works with Stack implementations.
     * @throws IllegalArgumentException if myData is not a Stack
     */
    private void validateStack() {
        if (!(myData instanceof Stack)) {
            throw new IllegalArgumentException("UndoManager needs a Stack");
        }
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

    // ========= stack operations =========

    /**
     * Look at the most recent action without undoing it.
     * @return the action on top of the stack
     */
    public Action peek() {
        return myData.get(myData.size() - 1);
    }

    /**
     * Remove and return the most recent action from the stack.
     * @return the action that was removed
     */
    public Action pop() {
        return myData.remove();
    }

    /**
     * Adding an action to the top of the stack.
     * @param theAction the action to add
     */
    public void push(Action theAction) {
        myData.add(theAction);
    }

    /**
     * Save an action to the undo history.
     * @param action the action to record
     */
    public void recordAction(Action action) {
        push(action);
    }

    /**
     * Undo the most recent action and return it.
     * @return the action that was undone
     */
    public Action undo() {
        return pop();
    }

    /**
     * Check if there are any actions to undo.
     * @return true if we can undo, false if the stack is empty
     */
    public boolean canUndo() {
        return !myData.isEmpty();
    }

    /**
     * Show how undo works with last-in-first-out order.
     * Records 3 actions then undoes them in reverse.
     */
    public void demonstrateLIFO() {
        LOGGER.info("=== LIFO Demo ===");

        Action a1 = new Action(1, ActionType.ADD_PLAYER, "Add Smith", "t1");
        Action a2 = new Action(2, ActionType.UPDATE_STATS, "Update Brown", "t2");
        Action a3 = new Action(3, ActionType.REMOVE_PLAYER, "Remove Jones", "t3");

        recordAction(a1);
        LOGGER.info("Recorded: " + a1.target());

        recordAction(a2);
        LOGGER.info("Recorded: " + a2.target());

        recordAction(a3);
        LOGGER.info("Recorded: " + a3.target());

        LOGGER.info("Top action is: " + peek().target());

        LOGGER.info("Undoing in reverse order:");

        while (canUndo()) {
            Action undone = undo();
            LOGGER.info("Undid: " + undone.target());
        }

        LOGGER.info("=== Demo Complete ===");
    }
}

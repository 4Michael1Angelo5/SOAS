package results;

import manager.Manager;
import manager.UndoManager;
import types.Action;
import util.ArrayStack;
import util.DataContainer;

import java.io.IOException;
import java.util.function.Supplier;

public class UndoResults extends Results<Action, UndoManager> {


    final static String UNDO_50 = "data/seahawks_undo_actions_50.csv";
    final static String UNDO_500 = "data/seahawks_undo_actions_500.csv";
    final static String UNDO_5000 = "data/seahawks_undo_actions_5000.csv";

    public UndoResults(
            UndoManager theManager,
            Supplier<DataContainer<Action>> theSupplier) {
        super(Action.class, theManager, theSupplier);
    }

    // =======================   loading ================================

    public void loadActions(String theFilePath) throws IOException{
        this.loadData(theFilePath);
    }

    @Override
    public void runAllExperiments() throws IOException {

        // test with 50 undo actions
        // ***************************************************************************
        loadActions(UNDO_50);
        addExperimentResult(testAdd());
        addExperimentResult(testRemove());

        // test with 500 undo actions
        // ***************************************************************************

        loadActions(UNDO_500);
        // test with 5000 undo actions
        // ***************************************************************************
        addExperimentResult(testAdd());
        addExperimentResult(testRemove());

        loadActions(UNDO_5000);
        addExperimentResult(testAdd());
        addExperimentResult(testRemove());

        printResults();
    }
}

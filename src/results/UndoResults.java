package results;

import manager.UndoManager;
import types.Action;
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

    //======================== push/pop ================================

    /**
     * Returns the Experiment Result of Pushing and Popping
     * Actions. The average time is calculated by taking the
     * average time of each operation individually then reporting
     * their combined average.
     * @return ExperimentResult of Pushing and Popping N times.
     */
    public ExperimentResult testPushPop() {

        ExperimentResult push = this.testAdd("push");
        ExperimentResult pop = this.testRemove("pop");

        String title = pop.operation() + "/" + push.operation();
        double avgTime = (push.avgTime() + pop.avgTime() )/ 2.0;
        int inputSize = myManager.getData().size();

        return new ExperimentResult(inputSize, title, avgTime);
    }

    @Override
    public void runAllExperiments() throws IOException {

        // test with 50 undo actions
        // ***************************************************************************
        loadActions(UNDO_50);
        addExperimentResult(testPushPop());

        // test with 500 undo actions
        // ***************************************************************************

        loadActions(UNDO_500);
        // test with 5000 undo actions
        // ***************************************************************************
        addExperimentResult(testPushPop());

        loadActions(UNDO_5000);
        addExperimentResult(testPushPop());

        printResults();
    }
}

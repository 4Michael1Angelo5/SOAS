package results;

import manager.DrillManager;
import manager.Manager;
import manager.UndoManager;
import types.Action;
import types.Drill;
import util.BinaryHeapPQ;
import util.DataContainer;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.function.Supplier;

public class DrillResults extends Results<Drill, DrillManager> {
    final static String DRILL_50 = "data/seahawks_drills_50.csv";
    final static  String DRILL_500 = "data/seahawks_drills_500.csv";
    final static String DRILL_5000 = "data/seahawks_drills_5000.csv";

    public DrillResults(
            DrillManager theManager,
            Supplier<DataContainer<Drill>> theSupplier) {
        super(Drill.class, theManager, theSupplier);
    }

    @Override
    public void runAllExperiments() throws IOException {

        // Drill 50;
        myManager.loadCsvData(DRILL_50);
        addExperimentResult(testAdd("add/enqueue"));
        addExperimentResult(testRemove("remove/dequeue"));
        // Drill 500;
        myManager.loadCsvData(DRILL_500);
        addExperimentResult(testAdd("add/enqueue"));
        addExperimentResult(testRemove("remove/dequeue"));
        // Drill 5000;
        myManager.loadCsvData(DRILL_5000);
        addExperimentResult(testAdd("add/enqueue"));
        addExperimentResult(testRemove("remove/dequeue"));

        printResults();

    }

    public static void main(String[] args) throws IOException {
        Supplier<DataContainer<Drill>> supPq = () -> new BinaryHeapPQ<>(Drill.class);
        DrillResults dr = new DrillResults(new DrillManager(supPq), supPq) ;
        dr.runAllExperiments();

    }
}

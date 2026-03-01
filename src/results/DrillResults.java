package results;

import manager.DrillManager;
import types.Drill;
import util.BinaryHeapPQ;
import util.DataContainer;
import util.LinkedQueue;

import java.io.IOException;
import java.util.function.Supplier;

public class DrillResults extends Results<Drill, DrillManager> {
    final static String DRILL_50 = "data/seahawks_drills_50.csv";
    final static  String DRILL_500 = "data/seahawks_drills_500.csv";
    final static String DRILL_5000 = "data/seahawks_drills_5000.csv";

    public DrillResults(
            DrillManager theManager,
            Supplier<DataContainer<Drill>> theSupplier, ExperimentFormat theExperimentFormatType) {
        super(Drill.class, theManager, theSupplier, theExperimentFormatType);
    }


    @Override
    public void runAllExperiments() throws IOException {

        // Drill 50;
        loadData(DRILL_50);
        myManager.printData();
        addExperimentResult(testAdd("enqueue"));
        addExperimentResult(testRemove("dequeue"));
        // Drill 500;
        loadData(DRILL_500);
        addExperimentResult(testAdd("enqueue"));
        addExperimentResult(testRemove("dequeue"));
        // Drill 5000;
        loadData(DRILL_5000);
        addExperimentResult(testAdd("enqueue"));
        addExperimentResult(testRemove("dequeue"));

        printResults();
    }

    public static void main(String[] args) throws IOException {

        Supplier<DataContainer<Drill>> supPq =
                () -> new BinaryHeapPQ<>(Drill.class,
                        (a,b)->Integer.compare(b.urgency(), a.urgency()));

        DrillResults dr = new DrillResults(new DrillManager(supPq),
                supPq,
                ExperimentFormat.BENCHMARK_W_OPS) ;
        dr.runAllExperiments();

        Supplier<DataContainer<Drill>> supQ = LinkedQueue::new;
        DrillResults drQ = new DrillResults(new DrillManager(supQ),
                supQ, ExperimentFormat.BENCHMARK_W_OPS);
        drQ.runAllExperiments();

    }
}

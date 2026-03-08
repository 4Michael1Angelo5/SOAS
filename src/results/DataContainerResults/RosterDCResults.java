package results.DataContainerResults;

import manager.DataContainer.RosterDCManager;
import results.BenchmarkResult;
import results.ExperimentFormat;
import types.Player;
import util.DataContainer;
import util.SinglyLinkedList;

import java.io.IOException;
import java.util.function.Supplier;

public class RosterDCResults
        extends DataContainerResults<Player, DataContainer<Player>, RosterDCManager> {

    final static String roster50 = "data/seahawks_roster_50.csv";
    final static String roster500 = "data/seahawks_roster_500.csv";
    final static String roster5000 = "data/seahawks_roster_5000.csv";

    public RosterDCResults(
            Class<Player> theDataType,
            RosterDCManager theManagerTestSubject,
            ExperimentFormat theExperimentFormat) {
        super(theDataType, theManagerTestSubject, theExperimentFormat);
    }
    // =======================   loading ================================

    public void loadRoster(String theFilePath) throws IOException {
        this.loadData(theFilePath);
    }

    // =======================   removing ================================

    // runnable
    private void removeFromFrontNTimes() {
        while (!myManagerTestSubject.getData().isEmpty()) {
            myManagerTestSubject.removeAt(0);
        }
    }

    // =======================   searching ================================

    // runnable
    private void searchByNameNTimes() {

        int N = myTestContainer.size();

        for (int i = 0; i < N; i++) {

            myManagerTestSubject.findByName("NOT FINDABLE");

        }
    }

    // =======================   Benchmark Testing ================================

    // gather results
    private BenchmarkResult testRemoveFromFrontNTimes() {
        String operation = "remove front";
        int inputSize = myManagerTestSubject.getPlayerData().size();
        double avgTime = myBenchmarkRunner.runSpeedTestWithSetup(
                TRIAL_RUNS,
                this::setUpForRemove,
                this::removeFromFrontNTimes);

        return new BenchmarkResult(inputSize, operation, avgTime, getOpCounts());
    }

    private BenchmarkResult testSearchByNameNTimes() {

        setUpForSearch();

        final String operation = "Search";

        final int inputSize = myManagerTestSubject.getPlayerData().size();

        final double avgTime = myBenchmarkRunner.runSpeedTest(TRIAL_RUNS, this::searchByNameNTimes);

        return new BenchmarkResult(inputSize, operation, avgTime, getOpCounts());
    }

    public void runAllExperiments() throws IOException {

        // Test with 50 players
        loadRoster(roster50);
        addExperimentResult(testAddNTimes("add"));
        addExperimentResult(testRemoveFromFrontNTimes());
        addExperimentResult(testSearchByNameNTimes());

        //****************************************************************************************************

        // Test with 500 players
        loadRoster(roster500);
        addExperimentResult(testAddNTimes("add"));
        addExperimentResult(testRemoveFromFrontNTimes());
        addExperimentResult(testSearchByNameNTimes());


        //****************************************************************************************************

        // Test with 5000 players
        loadRoster(roster5000);
        addExperimentResult(testAddNTimes("add"));
        addExperimentResult(testRemoveFromFrontNTimes());
        addExperimentResult(testSearchByNameNTimes());

        printResults();
    }

    public static void main() throws IOException {
        Supplier<DataContainer<Player>> sup =SinglyLinkedList::new;
        RosterDCManager RM = new RosterDCManager(sup);
        RosterDCResults res = new RosterDCResults(Player.class, RM, ExperimentFormat.BENCHMARK_W_OPS);
        res.runAllExperiments();
    }
}

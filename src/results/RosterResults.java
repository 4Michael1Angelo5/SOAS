package results;

import benchmark.BenchmarkRunner;
import manager.RosterManager;
import manager.TransactionFeed;
import manager.UndoManager;
import types.Action;
import types.Transaction;
import types.UndoRecord;
import util.ArrayStack;
import util.DataContainer;
import types.Player;
import util.ArrayStore;
import util.SinglyLinkedList;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * @author Chris Chun, Ayush
 * @version 1.2
 */
public class RosterResults extends Results<Player, RosterManager> {

    final static String roster50 = "data/seahawks_roster_50.csv";
    final static String roster500 = "data/seahawks_roster_500.csv";
    final static String roster5000 = "data/seahawks_roster_5000.csv";

    BenchmarkRunner benchmarkRunner = new BenchmarkRunner();

    public RosterResults(
            RosterManager theManager,
            Supplier<DataContainer<Player>> theSupplier){
        super(Player.class, theManager, theSupplier);
    }

    // =======================   loading ================================

    public void loadRoster(String theFilePath) throws IOException{
        this.loadData(theFilePath);
    }

    // =======================   removing ================================

    // runnable
    private void removeFromFrontNTimes() {
        if (myManager.getPlayerData().isEmpty()) {
            throw new RuntimeException(
                    "Misconfigured ExperimentResult, please ensure " +
                            "to loadData before running this experiment"
            );
        }
        while (!containerForRemove.isEmpty()) {
            containerForRemove.removeAt(0);
        }
    }
    // gather results
    private ExperimentResult testRemoveFromFrontNTimes() {
        double avgTime = benchmarkRunner.runSpeedTestWithSetup(
                TRIAL_RUNS,
                this::setUpForRemove,
                this::removeFromFrontNTimes);

        String operation = "remove front";
        int inputSize = myManager.getPlayerData().size();

        return new ExperimentResult(inputSize, operation, avgTime);
    }

    // =======================   searching ================================

    // runnable
    private void searchByNameNTimes() {
        int inputSize = myManager.getData().size();

        // O(n^2)
        for (int i = 0; i < inputSize; i++){

            myManager.findByName("NOT FINDABLE");

        }
    }

    // gather results
    private ExperimentResult testSearchByNameNTimes() {

        final String operation = "Search";

        final int inputSize = myManager.getPlayerData().size();

        final double avgTime = benchmarkRunner.runSpeedTestAndGetAvg(TRIAL_RUNS, this::searchByNameNTimes);

        return new ExperimentResult(inputSize, operation, avgTime);
    }



    public void runAllExperiments() throws IOException {

        // Test with 50 players
        loadRoster(roster50);
        addExperimentResult(testAdd("add"));
        addExperimentResult(testRemoveFromFrontNTimes());
        addExperimentResult(testSearchByNameNTimes());

        //****************************************************************************************************

        // Test with 500 players
        loadRoster(roster500);
        addExperimentResult(testAdd("add"));
        addExperimentResult(testRemoveFromFrontNTimes());
        addExperimentResult(testSearchByNameNTimes());


        //****************************************************************************************************

        // Test with 5000 players
        loadRoster(roster5000);
        addExperimentResult(testAdd("add"));
        addExperimentResult(testRemoveFromFrontNTimes());
        addExperimentResult(testSearchByNameNTimes());

        printResults();
    }

    public static void main(String[] args) throws IOException{

        // container suppliers (array - player/transaction)
        Supplier<DataContainer<Player>> arraySupplierPlayer = ()-> new ArrayStore<>(Player.class, 16);
        Supplier<DataContainer<Transaction>> arraySupplierTransaction = ()-> new ArrayStore<>(Transaction.class, 16);

        // container suppliers (SLL - player/transaction)
        Supplier<DataContainer<Player>> sllSupplierPlayer = SinglyLinkedList::new;
        Supplier<DataContainer<Transaction>> sllListSupplierTransaction = SinglyLinkedList::new;

        // roster mangers (array/sll)
        RosterManager rosterMangerArray = new RosterManager(arraySupplierPlayer);
        RosterManager rosterMangerSLL = new RosterManager(sllSupplierPlayer);

        // transaction mangers (array/sll)
        TransactionFeed transactionFeedArray = new TransactionFeed(arraySupplierTransaction);
        TransactionFeed transactionFeedSLL = new TransactionFeed(sllListSupplierTransaction);

        // roster manager results (array/sll)
        RosterResults results = new RosterResults(rosterMangerArray, arraySupplierPlayer);
        RosterResults resultsSLL = new RosterResults(rosterMangerSLL, sllSupplierPlayer);

        // transaction results (array/sll)
        TransactionResults trResults = new TransactionResults(transactionFeedArray, arraySupplierTransaction);
        TransactionResults trResultsSLL = new TransactionResults(transactionFeedSLL, sllListSupplierTransaction);

        // roster results (array/sll)
        results.runAllExperiments();
        resultsSLL.runAllExperiments();

        // transaction results (array/sll)
        trResults.runAllExperiments();
        trResultsSLL.runAllExperiments();

        Supplier<DataContainer<Action>> undoStack = ()-> new ArrayStack<>(Action.class);
        UndoManager undoManager = new UndoManager(undoStack);

        UndoResults undoResultsStack = new UndoResults(undoManager, undoStack);
        undoResultsStack.runAllExperiments();


    }

}

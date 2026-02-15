package results;

import manager.TransactionFeed;
import types.Transaction;
import util.DataContainer;
import util.SinglyLinkedList;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Benchmark experiments for Transaction operations
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class TransactionResults extends Results<Transaction, TransactionFeed> {

    // file paths to csv data
    final static String trans50 = "data/seahawks_transactions_50.csv";
    final static String trans500 = "data/seahawks_transactions_500.csv";
    final static String trans5000 = "data/seahawks_transactions_5000.csv";


    public TransactionResults(
            TransactionFeed theManger,
            Supplier<DataContainer<Transaction>> theSupplier) {

        super(Transaction.class, theManger, theSupplier);
    }

    // =======================   loading ================================

    /**
     Loads transaction data from a CSV file into the transaction manager
     * and local list for benchmarking.
     * @param theFilePath The path to the source CSV file.
     * @throws IOException If the file cannot be found or read.
     */
    public void loadTransactions(String theFilePath) throws IOException {
        this.loadData(theFilePath);
    }

    // =======================   adding ================================

    /**
     * Benchmarks the addition of transactions to the front of a new list.
     * This method utilizes an iterator to maintain O(n) efficiency.
     */
    // runnable
    public void addFrontNTimes() {
        if (myManager.getData().isEmpty()) {
            throw new RuntimeException(
                    "Misconfigured ExperimentResult, please ensure " +
                            "to loadData before running this experiment"
            );
        }
        DataContainer<Transaction> temp = mySupplier.get();
        for (Transaction theTransction: myManager.getData()) {
            temp.add(0, theTransction);
        }
    }

    // gather results
    public ExperimentResult testAddFrontNTimes() {
        double avgTime = myBenchmarkRunner.runSpeedTest(
                TRIAL_RUNS,
                this::addFrontNTimes);
        String operationName = "add front";
        int inputSize = myManager.getTransactionData().size();

        return new ExperimentResult(inputSize, operationName, avgTime);
    }

    /**
     * Benchmarks the addition of transactions to the rear of a new list.
     * This method utilizes an iterator to maintain O(n) efficiency.
     */
    // runnable
    public void addRearNTimes() {
        if (myManager.getData().isEmpty()) {
            throw new RuntimeException(
                    "Misconfigured ExperimentResult, please ensure " +
                            "to loadData before running this experiment"
            );
        }
        DataContainer<Transaction> temp = mySupplier.get();
        for (Transaction transaction:myManager.getTransactionData()) {
            temp.add(temp.size(),transaction);
        }
    }

    // gather results
    public ExperimentResult testAddRearNTimes() {
        int inputSize = myManager.getTransactionData().size();
        String operation = "add rear";
        double avgTime =
                myBenchmarkRunner.runSpeedTest(TRIAL_RUNS, this::addRearNTimes);

        return new ExperimentResult(inputSize, operation, avgTime);

    }

    // =======================   removing  ================================

    /**
     * Benchmarks the removal of all elements from the front of the list.
     * This is a destructive operation that empties the target list.
     */
    // runnable
    public void removeFromFrontNTimes() {
        if (myManager.getData().isEmpty()) {
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
    public ExperimentResult testRemoveFrontNTimes() {
        final int inputSize = myManager.getTransactionData().size();
        final String operationName = "remove front";
        final double avgTime =
                myBenchmarkRunner.runSpeedTestWithSetup(
                        TRIAL_RUNS,
                        this::setUpForRemove,
                        this::removeFromFrontNTimes);
        return new ExperimentResult(inputSize, operationName, avgTime);
    }

    // =======================   searching ================================

    public void searchListNTimes() {
        int N = myManager.getTransactionData().size();
        for (int i = 0; i < N; i++) {
            myManager.findByTimestamp("NOT FINDABLE");
        }
    }

    // gather results
    public ExperimentResult testSearchNTimes() {
        int inputSize = myManager.getTransactionData().size();
        String operationName = "search";
        final double avgTime =
                myBenchmarkRunner.runSpeedTest(
                        TRIAL_RUNS,
                        this::searchListNTimes);
        return new ExperimentResult(inputSize, operationName, avgTime);
    }


    public void runAllExperiments() throws IOException {

        //=================  Test with 50 transactions ====================
        loadTransactions(trans50);
        addExperimentResult(testAddFrontNTimes());
        addExperimentResult(testRemoveFrontNTimes());
        addExperimentResult(testSearchNTimes());

        //=================  Test with 500 transactions ===================
        loadTransactions(trans500);
        addExperimentResult(testAddFrontNTimes());
        addExperimentResult(testRemoveFrontNTimes());
        addExperimentResult(testSearchNTimes());

        // ================ Test with 5000 transactions ====================
        loadTransactions(trans5000);
        addExperimentResult(testAddFrontNTimes());
        addExperimentResult(testRemoveFrontNTimes());
        addExperimentResult(testSearchNTimes());

        printResults();

    }

    public static void main(String[] args) throws IOException {
        Supplier<DataContainer<Transaction>> supplier = SinglyLinkedList::new;
        TransactionFeed transactionManager = new TransactionFeed(supplier);
        TransactionResults results = new TransactionResults(transactionManager, supplier);
        results.runAllExperiments();
    }
}
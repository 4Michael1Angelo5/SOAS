package results;

import benchmark.BenchmarkRunner;
import manager.TransactionFeed;
import types.Transaction;
import util.DataContainer;
import util.SinglyLinkedList;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Benchmark experiments for Transaction operations
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class TransactionResults {

    final private static int runTrials = 30;

    /**
     * The file path for the small transaction dataset (50 records).
     */
    final static String trans50 = "data/seahawks_transactions_50.csv";

    /**
     * The file path for the medium transaction dataset (500 records).
     */
    final static String trans500 = "data/seahawks_transactions_500.csv";

    /**
     * The file path for the large transaction dataset (5000 records).
     */
    final static String trans5000 = "data/seahawks_transactions_5000.csv";

    /**
     * The timing engine used to execute speed tests and calculate averages.
     */
    BenchmarkRunner benchmarkRunner = new BenchmarkRunner();

    Supplier<DataContainer<Transaction>> supplier = SinglyLinkedList::new;

    /**
     * The manager responsible for high-level transaction logic and search operations.
     */
    TransactionFeed transactionManager = new TransactionFeed(supplier);

    /**
     * The primary list used to store loaded transactions for the "Add" and "Search" experiments.
     */
    DataContainer<Transaction> myTransactions = new SinglyLinkedList<>();

    /**
     * A temporary list used specifically for the "Remove" experiment to prevent
     * the destruction of the primary dataset.
     */
    SinglyLinkedList<Transaction> transactionsForRemove = new SinglyLinkedList<>();

    public TransactionResults() {
        super();
    }

    /**
     Loads transaction data from a CSV file into the transaction manager
     * and local list for benchmarking.
     * @param theFilePath The path to the source CSV file.
     * @throws IOException If the file cannot be found or read.
     */
    public void loadTransactions(String theFilePath) throws IOException {
        transactionManager.loadTransactionData(theFilePath);
        myTransactions = transactionManager.getTransactionData();
    }

    /**
     * Benchmarks the addition of transactions to the front of a new list.
     * This method utilizes an iterator to maintain O(n) efficiency.
     */
    public void addFrontNTimes() {
        SinglyLinkedList<Transaction> temp = new SinglyLinkedList<>();
        for (Transaction transaction:myTransactions) {
            temp.addFront(transaction);
        }
    }

    public void searchListNTime() {
        int N = transactionManager.getTransactionData().size();
        for (int i = 0; i < N; i++) {
            transactionManager.findByPlayer("NOT FINDABLE");
        }
    }

    /**
     * Benchmarks the addition of transactions to the rear of a new list.
     * This method utilizes an iterator to maintain O(n) efficiency.
     */
    public void addRearNTimes() {
        SinglyLinkedList<Transaction> temp = new SinglyLinkedList<>();
        for (Transaction transaction:myTransactions) {
            temp.addRear(transaction);
        }
    }

    /**
     * Prepares a temporary list for the removal benchmark by copying
     * the current transaction data. This setup is not included in the timed test.
     */
    public void setupRemoveTest() {
        transactionsForRemove = new SinglyLinkedList<>();
        for (Transaction transaction:myTransactions) {
            transactionsForRemove.addRear(transaction);
        }
    }

    /**
     * Benchmarks the removal of all elements from the front of the list.
     * This is a destructive operation that empties the target list.
     */
    public void removeFromFrontNTimes() {
        while (!transactionsForRemove.isEmpty()) {
            transactionsForRemove.remove();
        }
    }

    /**
     * Clears the current transaction list to prepare for the next
     * experimental data set.
     */
    public void resetTransactions() {
        myTransactions = new SinglyLinkedList<>();
    }

    /**
     * Orchestrates the full benchmarking suite across multiple data sizes (50, 500, 5000).
     * Measures performance for adding to front/rear, removal, and searching,
     * then outputs results in a formatted table.
     * * @implNote For the removal test, the list is re-populated via
     * {@link #setupRemoveTest()} before each trial to ensure accurate measurement.
     * * @throws IOException If the data files cannot be loaded during experimentation.
     */
    public void runAllExperiments() throws IOException {
        System.out.println("\n============= My Transaction Feed =============\n");
        System.out.printf("%-10s %-15s %-20s%n", "Size", "Operation", "LinkedList Time (ms)");
        System.out.println("-----------------------------------------------");

        // Test with 50 transactions
        loadTransactions(trans50);

        double addFront50 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, this::addFrontNTimes);
        System.out.printf("%-10s %-15s %-20.1f%n", "50", "Add Front", addFront50);

        double addRear50 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, this::addRearNTimes);
        System.out.printf("%-10s %-15s %-20.1f%n", "50", "Add Rear", addRear50);

        double remove50 = 0;
        for (int i = 0; i < runTrials; i++) {
            setupRemoveTest();
            remove50 += benchmarkRunner.runSpeedTestAndGetAvg(1, this::removeFromFrontNTimes);
        }
        double remove50avg = remove50 / runTrials;
        System.out.printf("%-10s %-15s %-20.1f%n", "50", "Remove", remove50avg);

        double search50 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, this::searchListNTime);
        System.out.printf("%-10s %-15s %-20.1f%n", "50", "Search", search50);

        resetTransactions();

        // Test with 500 transactions
        loadTransactions(trans500);

        double addFront500 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, this::addFrontNTimes);
        System.out.printf("%-10s %-15s %-20.1f%n", "500", "Add Front", addFront500);

        double addRear500 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, this::addRearNTimes);
        System.out.printf("%-10s %-15s %-20.1f%n", "500", "Add Rear", addRear500);

        double remove500 = 0;
        for (int i = 0; i < runTrials; i++) {
            setupRemoveTest();
            remove500 += benchmarkRunner.runSpeedTestAndGetAvg(1, this::removeFromFrontNTimes);
        }
        double remove500avg = remove500 / runTrials;
        System.out.printf("%-10s %-15s %-20.1f%n", "500", "Remove", remove500avg);

        double search500 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials,this::searchListNTime);
        System.out.printf("%-10s %-15s %-20.1f%n", "500", "Search", search500);

        resetTransactions();

        // Test with 5000 transactions
        loadTransactions(trans5000);

        double addFront5000 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, this::addFrontNTimes);
        System.out.printf("%-10s %-15s %-20.1f%n", "5000", "Add Front", addFront5000);

        double addRear5000 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, this::addRearNTimes);
        System.out.printf("%-10s %-15s %-20.1f%n", "5000", "Add Rear", addRear5000);

        double remove5000 = 0;
        for (int i = 0; i < runTrials; i++) {
            setupRemoveTest();
            remove5000 += benchmarkRunner.runSpeedTestAndGetAvg(1, this::removeFromFrontNTimes);
        }
        double remove5000avg = remove5000 / runTrials;
        System.out.printf("%-10s %-15s %-20.1f%n", "5000", "Remove", remove5000avg);

        double search5000 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, this::searchListNTime);

        System.out.printf("%-10s %-15s %-20.1f%n", "5000", "Search", search5000);

        System.out.println("===============================================\n");

        resetTransactions();
    }

    public static void main(String[] args) throws IOException {
        TransactionResults results = new TransactionResults();
        results.runAllExperiments();
    }
}
package results;

import benchmark.BenchmarkRunner;
import manager.TransactionFeed;
import types.Transaction;
import util.SinglyLinkedList;

import java.io.IOException;

/**
 * Benchmark experiments for Transaction operations
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class TransactionFeedresult {

    final private static int runTrials = 30;

    final static String trans50 = "data/seahawks_transactions_50.csv";
    final static String trans500 = "data/seahawks_transactions_500.csv";
    final static String trans5000 = "data/seahawks_transactions_5000.csv";

    BenchmarkRunner benchmarkRunner = new BenchmarkRunner();
    TransactionFeed transactionManager = new TransactionFeed();

    SinglyLinkedList<Transaction> transactions = new SinglyLinkedList<>();
    SinglyLinkedList<Transaction> transactionsForRemove = new SinglyLinkedList<>();

    public TransactionFeedresult() {
        super();
    }

    public void loadTransactions(String theFilePath) throws IOException {
        transactionManager.loadTransactionData(theFilePath);
        transactions = transactionManager.getTransactionData();
    }

    public void addFrontNTimes() {
        SinglyLinkedList<Transaction> temp = new SinglyLinkedList<>();
        for (int i = 0; i < transactions.size(); i++) {
            temp.addFront(transactions.get(i));
        }
    }

    public void addRearNTimes() {
        SinglyLinkedList<Transaction> temp = new SinglyLinkedList<>();
        for (int i = 0; i < transactions.size(); i++) {
            temp.addRear(transactions.get(i));
        }
    }

    // Setup method - NOT timed
    public void setupRemoveTest() {
        transactionsForRemove = new SinglyLinkedList<>();
        for (int i = 0; i < transactions.size(); i++) {
            transactionsForRemove.addRear(transactions.get(i));
        }
    }

    // Only the remove operation - this is timed
    public void removeFromFrontNTimes() {
        while (transactionsForRemove.size() > 0) {
            transactionsForRemove.remove();
        }
    }

    public void resetTransactions() {
        transactions = new SinglyLinkedList<>();
    }

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

        double search50 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, () -> transactionManager.findByPlayer("NOT FOUND"));
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

        double search500 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, () -> transactionManager.findByPlayer("NOT FOUND"));
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

        double search5000 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, () -> transactionManager.findByPlayer("NOT FOUND"));
        System.out.printf("%-10s %-15s %-20.1f%n", "5000", "Search", search5000);

        System.out.println("===============================================\n");

        resetTransactions();
    }

    public static void main(String[] args) throws IOException {
        TransactionFeedresult results = new TransactionFeedresult();
        results.runAllExperiments();
    }
}
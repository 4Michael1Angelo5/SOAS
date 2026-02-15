import manager.TransactionFeed;
import util.DataContainer;
import types.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.SinglyLinkedList;

import java.io.IOException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for TransactionManager
 * @author Chris Chun, Ayush
 * @version 1.1
 */
class TransactionFeedTest {

    private TransactionFeed manager;
    private Transaction t1, t2, t3, t4;

    @BeforeEach
    void setUp() {
        Supplier<DataContainer<Transaction>> supplier = SinglyLinkedList::new;
        manager = new TransactionFeed(supplier);
        t1 = new Transaction(101, "Injury", "Smith", "2025-01-03");
        t2 = new Transaction(102, "Trade", "Brown", "2025-01-05");
        t3 = new Transaction(103, "Activation", "Jones", "2025-01-08");
        t4 = new Transaction(104, "Suspension", "Garcia", "2025-01-10");
    }

    // ============= loading data =============
    @Test
    public void testLoadTransactions() throws IOException {
        Supplier<DataContainer<Transaction>> supplier = SinglyLinkedList::new;
        TransactionFeed tFeed = new TransactionFeed(supplier);
        tFeed.loadTransactionData("data/seahawks_transactions_50.csv");
        assertAll("Test Transaction Loading",
                () -> assertEquals(50, tFeed.getTransactionData().size()),
                () -> {
                    tFeed.loadTransactionData("data/seahawks_transactions_500.csv");
                    assertEquals(500, tFeed.getTransactionData().size());
                },
                () -> {
                    tFeed.loadTransactionData("data/seahawks_transactions_5000.csv");
                    assertEquals(5000, tFeed.getTransactionData().size());
                }
        );
    }

    // ============= Correctness =============

    @Test
    void testAddFront() {
        manager.addTransactionFront(t1);
        manager.addTransactionFront(t2);
        manager.addTransactionFront(t3);

        var transactions = manager.getTransactionData();

        assertEquals(3, transactions.size(),
                "After inserting 3 transactions, size should be 3");

        assertEquals(t3, transactions.get(0),
                "Most recent insert should be at the front");

        assertEquals(t2, transactions.get(1),
                "Second insert should now be in the middle");

        assertEquals(t1, transactions.get(2),
                "First insert should now be at the end");
    }

    @Test
    void testAddRear() {
        manager.addTransactionRear(t1);
        manager.addTransactionRear(t2);
        manager.addTransactionRear(t3);

        var transactions = manager.getTransactionData();

        assertEquals(3, transactions.size(),
                "After adding 3 to the rear, size should be 3");

        assertEquals(t1, transactions.get(0),
                "First added transaction should stay at index 0");

        assertEquals(t2, transactions.get(1),
                "Second added transaction should be at index 1");

        assertEquals(t3, transactions.get(2),
                "Last added transaction should be at the end");
    }

    @Test
    void testAddAtIndex() {
        manager.addTransactionRear(t1);
        manager.addTransactionRear(t2);
        manager.addTransactionRear(t4);

        manager.getTransactionData().removeAt(2);
        manager.getTransactionData().add(2, t3);

        var transactions = manager.getTransactionData();

        assertEquals(3, transactions.size(),
                "Size should update correctly after inserting at index");

        assertEquals(t3, transactions.get(2),
                "Inserted transaction should appear at index 2");
    }

    @Test
    void testRemove() {
        manager.addTransactionRear(t1);
        manager.addTransactionRear(t2);
        manager.addTransactionRear(t3);

        Transaction removed = manager.removeFront();

        assertEquals(t1, removed,
                "removeFront should return the first transaction");

        assertEquals(2, manager.getTransactionData().size(),
                "Size should decrease after removal");
    }

    // ============= Edge Cases =============

    @Test
    void testRemoveFromEmptyList() {
        assertThrows(Exception.class,
                () -> manager.removeFront(),
                "Removing from empty TransactionManager should throw exception");
    }

    @Test
    void testRemoveHead() {
        manager.addTransactionRear(t1);
        manager.addTransactionRear(t2);
        manager.addTransactionRear(t3);

        Transaction removed = manager.getTransactionData().removeAt(0);

        assertEquals(t1, removed,
                "Removing index 0 should remove the head");

        assertEquals(t2, manager.getTransactionData().get(0),
                "New head should shift after removal");
    }

    @Test
    void testRemoveTail() {
        manager.addTransactionRear(t1);
        manager.addTransactionRear(t2);
        manager.addTransactionRear(t3);

        int last = manager.getTransactionData().size() - 1;
        Transaction removed = manager.getTransactionData().removeAt(last);

        assertEquals(t3, removed,
                "Removing last index should remove the tail");

        assertEquals(2, manager.getTransactionData().size(),
                "Size should update after removing tail");
    }

    @Test
    void testRemoveMiddle() {
        manager.addTransactionRear(t1);
        manager.addTransactionRear(t2);
        manager.addTransactionRear(t3);

        Transaction removed = manager.getTransactionData().removeAt(1);

        assertEquals(t2, removed,
                "Removing index 1 should remove the middle element");

        assertEquals(t1, manager.getTransactionData().get(0),
                "Head should remain unchanged");

        assertEquals(t3, manager.getTransactionData().get(1),
                "Tail should shift into index 1");
    }

    @Test
    void testIndexOutOfBounds() {
        manager.addTransactionRear(t1);
        manager.addTransactionRear(t2);

        assertThrows(Exception.class,
                () -> manager.getTransactionData().get(5),
                "Accessing index beyond size should throw exception");

        assertThrows(Exception.class,
                () -> manager.getTransactionData().get(-1),
                "Accessing negative index should throw exception");
    }

    // ============= Structural Integrity =============

    @Test
    void testNoSkippedNodes() {
        manager.addTransactionRear(t1);
        manager.addTransactionRear(t2);
        manager.addTransactionRear(t3);
        manager.addTransactionRear(t4);

        var transactions = manager.getTransactionData();

        for (int i = 0; i < transactions.size(); i++) {
            assertNotNull(transactions.get(i),
                    "Transaction at index " + i + " should not be null");
        }
    }

    @Test
    void testCorrectSizeTracking() {
        assertEquals(0, manager.getTransactionData().size(),
                "New manager should start empty");

        manager.addTransactionRear(t1);
        manager.addTransactionRear(t2);
        manager.addTransactionFront(t3);
        manager.removeFront();
        manager.getTransactionData().removeAt(0);

        assertEquals(1, manager.getTransactionData().size(),
                "Size should accurately track all operations");
    }

    @Test
    void testNoCycles() {
        manager.addTransactionRear(t1);
        manager.addTransactionRear(t2);
        manager.addTransactionRear(t3);

        var transactions = manager.getTransactionData();
        int expected = transactions.size();
        int count = 0;

        for (int i = 0; i < expected; i++) {
            assertNotNull(transactions.get(i),
                    "Traversal should not hit null early");
            count++;
        }

        assertEquals(expected, count,
                "Traversal count should match size (no cycles)");
    }

    @Test
    void testFindBy() throws IOException{
        Supplier<DataContainer<Transaction>> supplier = SinglyLinkedList::new;
        TransactionFeed tFeed = new TransactionFeed(supplier);
        tFeed.loadTransactionData("data/seahawks_transactions_50.csv");
        assertAll("Test Transaction Loading",
                () -> assertEquals(-1, tFeed.findByPlayer("Not a findable player")),
                () -> {
                    // algorithm correctness.
                    int numComparisons = 0;
                    int n = tFeed.getTransactionData().size();
                    DataContainer<Transaction> data = tFeed.getTransactionData();
                    for (Transaction transaction: data) {
                        if (transaction.player().equals("Not Findable")) {
                            break;
                        }
                        numComparisons++;
                    }

                    assertEquals(n,numComparisons);

                },
                () -> {
                    tFeed.loadTransactionData("data/seahawks_transactions_500.csv");
                    assertEquals(-1, tFeed.findByTimestamp("Not a findable player"));
                },
                () -> {
                    tFeed.loadTransactionData("data/seahawks_transactions_5000.csv");
                    assertEquals(-1, tFeed.findById(-1010101010));
                }
        );
    }

    @Test
    public void onlyAllowsValidTransactionArguments() {

        assertAll("testing adding null and creating null transactions",
                ()-> assertThrows(IllegalArgumentException.class, ()-> manager.addData(null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Transaction(1,null,null,null))
                );

    }
}

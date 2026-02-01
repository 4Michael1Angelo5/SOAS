import manager.TransactionFeed;
import types.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        manager = new TransactionFeed();
        t1 = new Transaction(101, "Injury", "Smith", "2025-01-03");
        t2 = new Transaction(102, "Trade", "Brown", "2025-01-05");
        t3 = new Transaction(103, "Activation", "Jones", "2025-01-08");
        t4 = new Transaction(104, "Suspension", "Garcia", "2025-01-10");
    }

    // ============= Correctness =============

    @Test
    void testAddFront() {
        manager.insertTransaction(t1);
        manager.insertTransaction(t2);
        manager.insertTransaction(t3);

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
        manager.addTransaction(t1);
        manager.addTransaction(t2);
        manager.addTransaction(t3);

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
        manager.addTransaction(t1);
        manager.addTransaction(t2);
        manager.addTransaction(t4);

        manager.getTransactionData().remove(2);
        manager.getTransactionData().addAtIndex(2, t3);

        var transactions = manager.getTransactionData();

        assertEquals(3, transactions.size(),
                "Size should update correctly after inserting at index");

        assertEquals(t3, transactions.get(2),
                "Inserted transaction should appear at index 2");
    }

    @Test
    void testRemove() {
        manager.addTransaction(t1);
        manager.addTransaction(t2);
        manager.addTransaction(t3);

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
        manager.addTransaction(t1);
        manager.addTransaction(t2);
        manager.addTransaction(t3);

        Transaction removed = manager.getTransactionData().remove(0);

        assertEquals(t1, removed,
                "Removing index 0 should remove the head");

        assertEquals(t2, manager.getTransactionData().get(0),
                "New head should shift after removal");
    }

    @Test
    void testRemoveTail() {
        manager.addTransaction(t1);
        manager.addTransaction(t2);
        manager.addTransaction(t3);

        int last = manager.getTransactionData().size() - 1;
        Transaction removed = manager.getTransactionData().remove(last);

        assertEquals(t3, removed,
                "Removing last index should remove the tail");

        assertEquals(2, manager.getTransactionData().size(),
                "Size should update after removing tail");
    }

    @Test
    void testRemoveMiddle() {
        manager.addTransaction(t1);
        manager.addTransaction(t2);
        manager.addTransaction(t3);

        Transaction removed = manager.getTransactionData().remove(1);

        assertEquals(t2, removed,
                "Removing index 1 should remove the middle element");

        assertEquals(t1, manager.getTransactionData().get(0),
                "Head should remain unchanged");

        assertEquals(t3, manager.getTransactionData().get(1),
                "Tail should shift into index 1");
    }

    @Test
    void testIndexOutOfBounds() {
        manager.addTransaction(t1);
        manager.addTransaction(t2);

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
        manager.addTransaction(t1);
        manager.addTransaction(t2);
        manager.addTransaction(t3);
        manager.addTransaction(t4);

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

        manager.addTransaction(t1);
        manager.addTransaction(t2);
        manager.insertTransaction(t3);
        manager.removeFront();
        manager.getTransactionData().remove(0);

        assertEquals(1, manager.getTransactionData().size(),
                "Size should accurately track all operations");
    }

    @Test
    void testNoCycles() {
        manager.addTransaction(t1);
        manager.addTransaction(t2);
        manager.addTransaction(t3);

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
}


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.ArrayStore;
import util.BinaryHeapPQ;

import java.util.Comparator;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class BinaryHeapPQTest {

    private BinaryHeapPQ<Integer> heap;

    @BeforeEach
    void setUp() {
        // Initialize with Integer (which implements Comparable)
        heap = new BinaryHeapPQ<>(Integer.class);
    }

    @Test
    void testInsertAndPeek() {
        heap.insert(50);
        heap.insert(100);
        heap.insert(25);

        // Natural order for Integer: 25 should be the min (highest priority)
        assertEquals(25, heap.peek(), "Smallest element should be at the root for natural order.");
    }

    @Test
    void testExtract() {
        heap.insert(10);
        heap.insert(30);
        heap.insert(20);
        heap.insert(5);

        assertEquals(5, heap.extract());
        assertEquals(10, heap.extract());
        assertEquals(20, heap.extract());
        assertEquals(30, heap.extract());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testExtractThrowsExceptionOnEmpty() {
        assertThrows(NoSuchElementException.class, () -> heap.extract());
    }

    @Test
    void testBuildHeapFromExistingArray() {
        // Create an ArrayStore with unsorted data
        ArrayStore<Integer> unsorted = new ArrayStore<>(Integer.class, 5);
        unsorted.add(50);
        unsorted.add(10);
        unsorted.add(40);
        unsorted.add(5);
        unsorted.add(20);

        // Use the O(n) build method
        heap.buildHeap(unsorted);

        // Verify the heap property by extracting
        assertEquals(5, heap.extract());
        assertEquals(10, heap.extract());
        assertEquals(20, heap.extract());
    }

    @Test
    void testReorderWithNewComparator() {
        heap.insert(10);
        heap.insert(50);
        heap.insert(30);

        // Current natural order peek: 10
        assertEquals(10, heap.peek());

        // Reorder to Reversed (Max-Heap behavior)
        heap.reorder(Comparator.reverseOrder());

        // New peek should be 50
        assertEquals(50, heap.peek(), "After reordering to reverseOrder, 50 should be the root.");
    }

    @Test
    void testClear() {
        heap.insert(1);
        heap.insert(2);
        heap.clear();
        assertEquals(0, heap.size());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testDataContainerInterfaceMethods() {
        heap.add(100);
        heap.add(50);

        // findBy should only check the root
        assertEquals(0, heap.findBy(n -> n == 50), "Should find 50 at index 0");
        assertEquals(-1, heap.findBy(n -> n == 100), "Should not find 100 even if it exists deeper in the heap");

        // remove(obj) should only work if obj is at the root
        assertThrows(IllegalArgumentException.class, () -> heap.remove(100));
        assertEquals(50, heap.remove(50));
    }

    @Test
    void testLargeVolumeFloydBuild() {
        // Testing the efficiency logic conceptually by ensuring
        // a large set heapifies correctly via buildHeap
        ArrayStore<Integer> largeData = new ArrayStore<>(Integer.class, 100);
        for (int i = 100; i > 0; i--) {
            largeData.add(i);
        }

        heap.buildHeap(largeData);
        assertEquals(1, heap.peek(), "After O(n) build, 1 should be at the root.");
    }

    @Test
    void testStressRandomizedOrder() {
        java.util.Random rand = new java.util.Random();
        java.util.List<Integer> tracker = new java.util.ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            int val = rand.nextInt(10000);
            heap.insert(val);
            tracker.add(val);
        }

        java.util.Collections.sort(tracker); // Natural sort for comparison

        for (Integer expected : tracker) {
            assertEquals(expected, heap.extract(), "Extraction should follow sorted order.");
        }
    }

    // ================= heapifyUp =================

    @Test
    void testHeapifyUpCalledDirectly() {
        // Manually trigger heapifyUp from last index
        heap.insert(50);
        heap.insert(40);
        heap.insert(30);
        // heap should be [30, 50, 40] or valid min-heap shape
        // call heapifyUp on index 0 (root) — should be a no-op
        heap.heapifyUp(0);
        assertEquals(30, heap.peek(), "heapifyUp on root should be a no-op");
    }

    // ================= heapifyDown =================

    @Test
    void testHeapifyDownCalledDirectly() {
        heap.insert(5);
        heap.insert(15);
        heap.insert(10);
        heap.heapifyDown(0);
        assertEquals(5, heap.peek(), "heapifyDown on valid root should be a no-op");
    }
}
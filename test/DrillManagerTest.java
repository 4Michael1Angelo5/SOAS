import manager.DrillManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import types.Drill;
import util.BinaryHeapPQ;

import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DrillManagerTest for heap behavior
 * @author Chris Chun, Ayush
 * @version 2
 */
public class DrillManagerTest {

    private DrillManager drillManager;
    private BinaryHeapPQ<Drill> heap;

    @BeforeEach
    void setup() {
        drillManager = new DrillManager(() -> new BinaryHeapPQ<>(Drill.class));
        heap = (BinaryHeapPQ<Drill>) drillManager.getData();
    }

    private Drill makeDrill(int id, String name, int urgency, int duration, int fatigue, int installDay) {
        return new Drill(id, name, urgency, duration, fatigue, installDay);
    }

    // ================= Heap Correctness =================

    @Test
    void testInsertAndPeek() {
        drillManager.addData(makeDrill(1, "A", 5, 10, 2, 3));
        drillManager.addData(makeDrill(2, "B", 9, 10, 2, 3));
        drillManager.addData(makeDrill(3, "C", 7, 10, 2, 3));

        Drill top = drillManager.peekNextDrill();

        assertAll("Adding drills and peeking at top",
                () -> assertEquals(9, top.urgency(), "Top drill should have urgency 9"),
                () -> assertEquals(2, top.drill_id(), "Top drill should have ID 2"),
                () -> assertEquals(3, heap.size(), "Heap should have 3 drills")
        );
    }

    @Test
    void testExtractInOrder() {
        drillManager.addData(makeDrill(1, "A", 5, 10, 2, 3));
        drillManager.addData(makeDrill(2, "B", 9, 10, 2, 3));
        drillManager.addData(makeDrill(3, "C", 7, 10, 2, 3));

        Drill first = drillManager.removeData();
        Drill second = drillManager.removeData();
        Drill third = drillManager.removeData();

        assertAll("Removing drills in priority order",
                () -> assertEquals(9, first.urgency(), "First removed should have urgency 9"),
                () -> assertEquals(7, second.urgency(), "Second removed should have urgency 7"),
                () -> assertEquals(5, third.urgency(), "Third removed should have urgency 5"),
                () -> assertTrue(heap.isEmpty(), "Heap should be empty after removing all")
        );
    }

    @Test
    void testPeekDoesNotRemove() {
        drillManager.addData(makeDrill(1, "A", 5, 10, 2, 3));
        drillManager.addData(makeDrill(2, "B", 9, 10, 2, 3));

        Drill top1 = drillManager.peekNextDrill();
        Drill top2 = drillManager.peekNextDrill();

        assertAll("Peeking multiple times",
                () -> assertEquals(top1, top2, "Both peeks should return same drill"),
                () -> assertEquals(2, heap.size(), "Size should still be 2 after peeking")
        );
    }

    @Test
    void testExtractFromEmpty() {
        assertAll("Removing from empty heap",
                () -> assertTrue(heap.isEmpty(), "Heap should be empty at start"),
                () -> assertThrows(NoSuchElementException.class,
                        () -> drillManager.removeData(),
                        "Should throw error when removing from empty heap")
        );
    }

    // ================= Edge Cases =================

    @Test
    void testDuplicatePriorities() {
        drillManager.addData(makeDrill(1, "A", 10, 10, 1, 1));
        drillManager.addData(makeDrill(2, "B", 10, 10, 1, 1));
        drillManager.addData(makeDrill(3, "C", 10, 10, 1, 1));

        Drill r1 = drillManager.removeData();
        Drill r2 = drillManager.removeData();
        Drill r3 = drillManager.removeData();

        assertAll("Drills with same urgency",
                () -> assertNotNull(r1, "First drill should not be null"),
                () -> assertNotNull(r2, "Second drill should not be null"),
                () -> assertNotNull(r3, "Third drill should not be null"),
                () -> assertTrue(heap.isEmpty(), "Heap should be empty after removing all")
        );
    }

    @Test
    void testSingleElement() {
        Drill one = makeDrill(7, "Only", 3, 20, 5, 2);
        drillManager.addData(one);

        Drill peeked = drillManager.peekNextDrill();
        Drill removed = drillManager.removeData();

        assertAll("Heap with only one drill",
                () -> assertEquals(one, peeked, "Peek should return the only drill"),
                () -> assertEquals(one, removed, "Remove should return the only drill"),
                () -> assertTrue(heap.isEmpty(), "Heap should be empty after removing")
        );
    }

    @Test
    void testLargeInput() {
        for (int i = 1; i <= 5000; i++) {
            int urgency = i % 100;
            drillManager.addData(makeDrill(i, "D" + i, urgency, 10, 1, 1));
        }

        int prevUrgency = Integer.MAX_VALUE;
        int count = 0;

        while (!heap.isEmpty()) {
            Drill next = drillManager.removeData();
            int currentUrgency = next.urgency();
            assertTrue(currentUrgency <= prevUrgency, "Each drill should have same or lower urgency than previous");
            prevUrgency = currentUrgency;
            count++;
        }

        assertEquals(5000, count, "Should remove all 5000 drills");
        assertTrue(heap.isEmpty(), "Heap should be empty at the end");
    }

    // ================= Comparator Correctness =================

}
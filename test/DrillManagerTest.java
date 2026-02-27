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
        drillManager.addData(makeDrill(17, "A", 23, 45, 8, 12));
        drillManager.addData(makeDrill(42, "B", 87, 30, 5, 7));
        drillManager.addData(makeDrill(9, "C", 56, 90, 3, 15));

        Drill top = drillManager.peekNextDrill();

        assertAll("Adding drills and peeking at top",
                () -> assertEquals(87, top.urgency(), "Top drill should have urgency 87"),
                () -> assertEquals(42, top.drill_id(), "Top drill should have ID 42"),
                () -> assertEquals(3, heap.size(), "Heap should have 3 drills")
        );
    }

    @Test
    void testExtractInOrder() {
        drillManager.addData(makeDrill(51, "A", 14, 60, 2, 9));
        drillManager.addData(makeDrill(73, "B", 92, 25, 7, 4));
        drillManager.addData(makeDrill(28, "C", 68, 80, 1, 11));

        Drill first = drillManager.removeData();
        Drill second = drillManager.removeData();
        Drill third = drillManager.removeData();

        assertAll("Removing drills in priority order",
                () -> assertEquals(92, first.urgency(), "First removed should have urgency 92"),
                () -> assertEquals(68, second.urgency(), "Second removed should have urgency 68"),
                () -> assertEquals(14, third.urgency(), "Third removed should have urgency 14"),
                () -> assertTrue(heap.isEmpty(), "Heap should be empty after removing all")
        );
    }

    @Test
    void testPeekDoesNotRemove() {
        drillManager.addData(makeDrill(64, "A", 33, 55, 9, 6));
        drillManager.addData(makeDrill(19, "B", 77, 40, 4, 13));

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
        Drill d1 = new Drill(1, "A", 65, 45, 8, 12);
        Drill d2 = new Drill(2, "B", 65, 30, 5, 7);
        Drill d3 = new Drill(3, "C", 65, 90, 3, 15);

        drillManager.addData(d1);
        drillManager.addData(d2);
        drillManager.addData(d3);

        assertAll("Drills with same urgency",
                () -> assertEquals(d2, drillManager.removeData(), "First drill should be d2 (urgency 87)"),
                () -> assertEquals(d3, drillManager.removeData(), "Second drill should be d3 (urgency 56)"),
                () -> assertEquals(d1, drillManager.removeData(), "Third drill should be d1 (urgency 23)"),
                () -> assertTrue(heap.isEmpty(), "Heap should be empty after removing all")
        );
    }

    @Test
    void testSingleElement() {
        Drill one = makeDrill(89, "Only", 41, 97, 11, 22);
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
            int urgency = (i * 37) % 100;
            drillManager.addData(makeDrill(i, "D" + i, urgency, (i % 50) + 10, (i % 15) + 1, (i % 30) + 1));
        }

        int prevUrgency = Integer.MAX_VALUE;
        int count = 0;

        while (!heap.isEmpty()) {
            Drill next = drillManager.removeData();
            int currentUrgency = next.urgency();
            assertTrue(currentUrgency <= prevUrgency,
                    "Each drill should have same or lower urgency than previous");
            prevUrgency = currentUrgency;
            count++;
        }

        assertEquals(5000, count, "Should remove all 5000 drills");
        assertTrue(heap.isEmpty(), "Heap should be empty at the end");
    }

    // ================= Comparator Correctness =================

    @Test
    void testFairSortComparator() {
        Comparator<Drill> fair = drillManager.fairSort();
        drillManager.upDateComparator(fair);

        Drill a = makeDrill(101, "A", 85, 120, 18, 25);
        Drill b = makeDrill(226, "B", 85, 120, 18, 17);
        Drill c = makeDrill(323, "C", 85, 120, 7, 17);
        Drill e = makeDrill(454, "E", 85, 45, 7, 17);
        Drill low = makeDrill(565, "Low", 12, 60, 4, 8);

        drillManager.addData(a);
        drillManager.addData(b);
        drillManager.addData(c);
        drillManager.addData(e);
        drillManager.addData(low);

        assertAll("Using fair sort ordering",
                () -> assertEquals(e, drillManager.removeData(), "E should come out first"),
                () -> assertEquals(c, drillManager.removeData(), "C should come out second"),
                () -> assertEquals(b, drillManager.removeData(), "B should come out third"),
                () -> assertEquals(a, drillManager.removeData(), "A should come out fourth"),
                () -> assertEquals(low, drillManager.removeData(), "Low should come out last"),
                () -> assertTrue(heap.isEmpty(), "Heap should be empty at end")
        );
    }

    @Test
    void testSortByIdComparator() {
        drillManager.upDateComparator(drillManager.sortById());

        Drill d10 = makeDrill(763, "Ten", 19, 88, 13, 21);
        Drill d2 = makeDrill(127, "Two", 94, 35, 6, 10);
        Drill d7 = makeDrill(495, "Seven", 52, 110, 20, 16);

        drillManager.addData(d10);
        drillManager.addData(d2);
        drillManager.addData(d7);

        assertAll("Sorting by ID number",
                () -> assertEquals(d2, drillManager.removeData(), "ID 127 should come out first"),
                () -> assertEquals(d7, drillManager.removeData(), "ID 495 should come out second"),
                () -> assertEquals(d10, drillManager.removeData(), "ID 763 should come out third"),
                () -> assertTrue(heap.isEmpty(), "Heap should be empty at end")
        );
    }
}
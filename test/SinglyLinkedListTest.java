import org.junit.jupiter.api.Test;
import util.SinglyLinkedList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tester for the RosterManager
 * @version 1.2
 * @author Chris Chun, Ayush
 */
public class SinglyLinkedListTest {

    private static final int THE_NUM_ADDS = 100;

    private SinglyLinkedList<Integer> createSLL() {
        return new SinglyLinkedList<>();
    }

    private static final Random myRandom = new Random(12345);

    private SinglyLinkedList<Integer> createRandomSLL() {
        SinglyLinkedList<Integer> list = createSLL();
        ArrayList<Integer> randomArray = createRandomArray();
        for (Integer integer : randomArray) {
            list.addRear(integer);
        }

        return list;
    }

    @Test
    public void testConstructor() {
        SinglyLinkedList<Integer> list = createSLL();
        assertAll("Test Constructor",
                () -> assertEquals(0, list.size(), "List should be empty")
        );
    }

    // also test get and size
    @Test
    public void testAddFront() {
        SinglyLinkedList<Integer> list = createSLL();
        ArrayList<Integer> randomArray = createRandomArray();
        for (Integer integer : randomArray) {
            list.addFront(integer);
        }

        int n = randomArray.size();
        assertAll("Test Add Front" ,
                ()-> assertEquals(THE_NUM_ADDS, list.size()," the list size should be " + THE_NUM_ADDS ),
                () -> {
                    // list is now reversed
                    for (int i = 0; i < randomArray.size(); i++) {
                        assertEquals(randomArray.get(n - 1 -  i), list.get(i),
                                "The value in position " + i + " should be " + randomArray.get(i)
                                        + ", but got " + list.get(i));
                    }
                }
        );
    }

    private ArrayList<Integer> createRandomArray() {
        ArrayList<Integer> array = new ArrayList<>();

        for (int i = 0; i < THE_NUM_ADDS; i++) {
            int randomInt = myRandom.nextInt();
            array.add(randomInt);
        }

        return array;
    }
    // Also test's get and size method
    @Test
    public void testAddRear() {
        SinglyLinkedList<Integer> list = createSLL();
        ArrayList<Integer> randomArray = createRandomArray();
        for (Integer integer : randomArray) {
            list.addRear(integer);
        }
        assertAll("Test Add Rear" ,
                ()-> assertEquals(THE_NUM_ADDS, list.size()," the list size should be " + THE_NUM_ADDS ),
                () -> {
                    for (int i = 0; i < randomArray.size(); i++) {
                        assertEquals(randomArray.get(i), list.get(i),
                                "The value in position " + i + " should be " + randomArray.get(i)
                                        + ", but got " + list.get(i));
                    }
                }
        );
    }

    // also tests get and size methods
    @Test void testInsertAtIndex() {
        SinglyLinkedList<Integer> list = createSLL();
        ArrayList<Integer> randomArray = createRandomArray();
        int i = 0;
        for (Integer integer : randomArray) {
            list.add(i,integer);
            i++;
        }

        assertAll("Test Insert At Index",
                () -> assertEquals(THE_NUM_ADDS, list.size()," the list size should be " + THE_NUM_ADDS ),
                () -> assertThrows(Exception.class, ()->list.add(THE_NUM_ADDS + 1, 0),
                        "addAtIndex should only allow adding at valid index positon"),
                () -> assertThrows(Exception.class, () -> list.add(-1, 0),
                        "addAtIndex should only allow adding at valid index positon"),
                () -> {
                    for (int idx = 0; idx < THE_NUM_ADDS; idx++) {
                        assertEquals(randomArray.get(idx), list.get(idx),
                                "The value in position " + idx + " should be " + randomArray.get(idx)
                                        + ", but got " + list.get(idx));
                    }
                }

        );

    }

    @Test void testRemove() {
        SinglyLinkedList<Integer> list = createSLL();
        ArrayList<Integer> randomArray = createRandomArray();
        for (Integer integer : randomArray) {
            list.addRear(integer);
        }

        assertAll("Test Remove",
                () -> {
                    for (int i = 0; i < THE_NUM_ADDS; i++) {
                        int removed = list.remove();
                        int expected = randomArray.get(i);
                        assertEquals(expected, removed);
                        assertEquals(THE_NUM_ADDS - i - 1, list.size());
                    }
                }
        );

    }

    /**
     * Removes a randomly selected value from both lists.
     * Note: This test assumes remove(value) removes the FIRST occurrence
     * in both ArrayList and SinglyLinkedList.
     */
    @Test void testRemoveByItem() {
        final SinglyLinkedList<String> list = new SinglyLinkedList<>();
        final ArrayList<String> array = new ArrayList<>();
        for (int i = 0; i < THE_NUM_ADDS; i++) {
            // randomly push a lower case letter of the alphabet to the lists.
            char randChar = (char) ('a' + myRandom.nextInt(26));
            list.addRear(Character.toString(randChar));
            array.add(Character.toString(randChar));
        }

        assertAll("Test Remove by Value",
                () -> assertThrows(Exception.class, () -> list.remove("A")),
                () -> assertThrows(Exception.class, () -> list.remove("0")),
                () -> {
                    while (!array.isEmpty()) {
                        int randIdx = myRandom.nextInt(array.size());
                        String expected  = array.get(randIdx);
                        array.remove(expected);
                        String actual = list.remove(expected);
                        assertEquals(expected,actual);
                        assertEquals(array.size(), list.size());
                    }
                }
        );

    }

    @Test void testIndexOf() {
        final SinglyLinkedList<String> list = new SinglyLinkedList<>();
        final ArrayList<String> array = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            // create list of the alphabet
            char letter = (char) ('a' + i);
            list.addRear(Character.toString(letter));
            array.add(Character.toString(letter));
        }

        assertAll("Test IndexOf",
                () -> {
                    for (int i = 0; i < 26; i++) {
                        String nextChar = array.get(i);
                        assertEquals(i, list.indexOf(nextChar),
                                "should have returned " + i + ", but returned "
                                        +list.indexOf(nextChar));
                    }
                },
                () -> assertEquals(-1, list.indexOf("NOT IN LIST"),
                        "indexOf should return -1 when the object is not found in the lest"),
                () -> {
                    list.addRear("a");
                    assertEquals(0, list.indexOf("a"),
                            "Should return the first index of the found object");
                }

        );

    }

    @Test void testReset() {
        final SinglyLinkedList<Integer> list = createRandomSLL();
        assertAll("Test Reset",
                () -> assertDoesNotThrow(list::clear),
                () -> assertEquals(0, list.size()),
                () -> assertThrows(Exception.class, () -> list.get(0))
        );
    }

    @Test void testToArrayStore() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        assertEquals(0, list.toArrayStore(String.class).size());

        list.addRear("a");
        list.addRear("b");
        var arr = list.toArrayStore(String.class);
        assertEquals(2, arr.size());
        assertEquals("a", arr.get(0));
        assertEquals("b", arr.get(1));
    }

    @Test void testIteratorThrows() {
        SinglyLinkedList<Integer> list = createSLL();
        Iterator<Integer> it = list.iterator();
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test void testRemoveNull() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.addRear(null);
        list.addRear("a");
        assertNull(list.remove(null));
        assertEquals("a", list.get(0));
    }
}

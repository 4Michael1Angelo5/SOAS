import org.junit.jupiter.api.Test;
import util.SinglyLinkedList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tester for the RosterManager
 * @version 1.1
 * @author Chris Chun, Ayush
 */

public class SinglyLinkedListTest {

    private static final int THE_NUM_ADDS = 100;

    private SinglyLinkedList<Integer> createSLL() {
        return new SinglyLinkedList<>();
    }

    private static Random myRandom = new Random();

    private void add100Times(SinglyLinkedList<Integer> theList,
                           String theAddMethod) {
        for (int i = 0; i < SinglyLinkedListTest.THE_NUM_ADDS; i++) {
            if (Objects.equals(theAddMethod, "add front")) {
                theList.addFront(i);
            } else
            if (Objects.equals(theAddMethod, "add rear")) {
                theList.addRear(i);
            } else {
                throw new IllegalArgumentException("options are 'add front' or 'add rear'.");
            }
        }
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
            list.addAtIndex(i,integer);
            i++;
        }

        assertAll("Test Insert At Index",
                () -> assertEquals(THE_NUM_ADDS, list.size()," the list size should be " + THE_NUM_ADDS ),
                () -> assertThrows(Exception.class, ()->list.addAtIndex(THE_NUM_ADDS + 1, 0),
                        "addAtIndex should only allow adding at valid index positon"),
                () -> assertThrows(Exception.class, () -> list.addAtIndex(-1, 0),
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


}

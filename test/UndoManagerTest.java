import manager.UndoManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import types.Action;
import types.ActionType;
import util.ArrayStack;
import util.ArrayStore;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;



/**
 * JUnit tests for UndoManagerTest
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class UndoManagerTest {
    private UndoManager undoManager;
    private ArrayStack<Action> stack;

    @BeforeEach
    void setup() {
        undoManager = new UndoManager(() -> new ArrayStack<>(Action.class));
        stack = (ArrayStack<Action>) undoManager.getData();
    }

    // ========== push / pop / peek ==========

    @Test
    void pushPopPeek() {

        Action a1 = new Action(1, ActionType.ADD_PLAYER, "A", "t1");
        Action a2 = new Action(2, ActionType.REMOVE_PLAYER, "B", "t2");

        assertAll("push pop peek",

                // push first
                () -> stack.push(a1),

                // peek first
                () -> assertEquals(a1, stack.peek()),
                () -> assertEquals(1, stack.size()),

                // push second
                () -> stack.push(a2),

                // peek second
                () -> assertEquals(a2, stack.peek()),
                () -> assertEquals(2, stack.size()),

                // peek does not remove
                () -> assertEquals(a2, stack.peek()),
                () -> assertEquals(2, stack.size()),

                // pop removes
                () -> assertEquals(a2, stack.pop()),
                () -> assertEquals(1, stack.size()),
                () -> assertEquals(a1, stack.peek())
        );
    }

    // ========== pop on empty stack ==========

    @Test
    void popOnEmptyStack() {
        assertTrue(stack.isEmpty());
        assertThrows(NoSuchElementException.class, () -> stack.pop());
    }

    @Test
    void peekOnEmptyStack() {
        assertTrue(stack.isEmpty());
        assertThrows(NoSuchElementException.class, () -> stack.peek());
    }

    // ========== multiple undos ==========

    @Test
    void multipleUndos() {



        assertAll("multiple undos",
                () -> {
                    Action a1 = new Action(1, ActionType.ADD_PLAYER, "A", "t1");
                    Action a2 = new Action(2, ActionType.REMOVE_PLAYER, "B", "t2");
                    Action a3 = new Action(3, ActionType.ADD_TRANSACTION, "C", "t3");

                    // push
                    stack.push(a1);
                    stack.push(a2);
                    stack.push(a3);
                },

                () -> assertEquals(3, stack.size()),

                // undo all
                () -> assertEquals(new Action(3, ActionType.ADD_TRANSACTION, "C", "t3"), stack.pop()),
                () -> assertEquals(new Action(2, ActionType.REMOVE_PLAYER, "B", "t2"), stack.pop()),
                () -> assertEquals(new Action(1, ActionType.ADD_PLAYER, "A", "t1"), stack.pop()),

                // empty
                () -> assertTrue(stack.isEmpty()),
                () -> assertThrows(NoSuchElementException.class, () -> stack.pop())
        );
    }

    // ========== LIFO behavior ==========

    @Test
    void lifoBehavior() {

        Action first  = new Action(10, ActionType.UPDATE_STATS, "first", "t1");
        Action second = new Action(20, ActionType.REMOVE_TRANSACTION, "second", "t2");

        assertAll("LIFO behavior",
                // push
                () -> stack.push(first),
                () -> stack.push(second),

                // last in, first out
                () -> assertEquals(second, stack.pop()),
                () -> assertEquals(first, stack.pop()),

                // empty
                () -> assertTrue(stack.isEmpty())
        );
    }

    // ========== edge cases ==========

    @Test
    void singleElementStack() {
        Action single = new Action(1, ActionType.ADD_PLAYER, "single", "t1");

        assertAll("single element operations",
                () -> stack.push(single),
                () -> assertEquals(1, stack.size()),
                () -> assertEquals(single, stack.peek()),
                () -> assertEquals(single, stack.pop()),
                () -> assertTrue(stack.isEmpty())
        );
    }

    @Test
    void nullActionType() {
        assertThrows(NullPointerException.class,
                () -> new Action(1, null, "test", "t1"));
    }


    @Test
    void pushPopMultipleTimes() {
        assertAll("push and pop multiple cycles",
                () -> {
                    Action a1 = new Action(1, ActionType.ADD_PLAYER, "A", "t1");
                    Action a2 = new Action(2, ActionType.REMOVE_PLAYER, "B", "t2");

                    stack.push(a1);
                    stack.pop();
                    stack.push(a2);
                },
                () -> assertEquals(1, stack.size()),
                () -> assertEquals(new Action(2, ActionType.REMOVE_PLAYER, "B", "t2"), stack.pop()),
                () -> assertTrue(stack.isEmpty())
        );
    }

    @Test
    void manyActions() {
        assertAll("many actions",
                () -> {
                    for (int i = 0; i < 100; i++) {
                        stack.push(new Action(i, ActionType.UPDATE_STATS, "Action" + i, "t" + i));
                    }
                },
                () -> assertEquals(100, stack.size()),
                () -> {
                    for (int i = 99; i >= 0; i--) {
                        assertEquals(new Action(i, ActionType.UPDATE_STATS, "Action" + i, "t" + i), stack.pop());
                    }
                },
                () -> assertTrue(stack.isEmpty())
        );
    }

    @Test
    void peekAfterClear() {
        Action a1 = new Action(1, ActionType.ADD_PLAYER, "A", "t1");

        assertAll("peek after clear",
                () -> stack.push(a1),
                () -> assertEquals(1, stack.size()),
                () -> stack.clear(),
                () -> assertTrue(stack.isEmpty()),
                () -> assertThrows(NoSuchElementException.class, () -> stack.peek())
        );
    }

    @Test
    void alternatingPushPop() {
        assertAll("alternating push and pop",
                () -> {
                    for (int i = 0; i < 5; i++) {
                        stack.push(new Action(i, ActionType.ADD_TRANSACTION, "Action" + i, "t" + i));
                        if (i % 2 == 1) {
                            stack.pop();
                        }
                    }
                },
                () -> assertEquals(3, stack.size())
        );
    }

    // ========== reverse a stack test ==========

    @Test
    void reverseStack() {
        ArrayStack<String> s = new ArrayStack<>(String.class);
        assertAll("reverse stack",
                () -> {
                    s.push("a");
                    s.push("b");
                    s.push("c");
                    s.push("d");
                },
                () -> {
                    StringBuilder result = new StringBuilder();
                    while (!s.isEmpty()) {
                        result.append(s.pop());
                    }
                    assertEquals("dcba", result.toString());
                }
        );
    }

    @Test
    void testEdgeCase() {
        Action newAction = new Action(1,null, "Brown", "some Time stamp");


        undoManager.addData(null);

        assertAll("test edge case handling",
                ()-> assertThrows(Exception.class, ()-> undoManager.addData(newAction) ),
                () -> assertThrows(Exception.class, ()-> undoManager.addData(null))
            );
    }

}

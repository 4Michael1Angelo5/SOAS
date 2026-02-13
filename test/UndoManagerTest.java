import manager.UndoManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import types.Action;
import util.ArrayStack;

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

        Action a1 = new Action(1, null, "A", "t1");
        Action a2 = new Action(2, null, "B", "t2");

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
                    Action a1 = new Action(1, null, "A", "t1");
                    Action a2 = new Action(2, null, "B", "t2");
                    Action a3 = new Action(3, null, "C", "t3");

                    // push
                    stack.push(a1);
                    stack.push(a2);
                    stack.push(a3);
                },

                () -> assertEquals(3, stack.size()),

                // undo all
                () -> assertEquals(new Action(3, null, "C", "t3"), stack.pop()),
                () -> assertEquals(new Action(2, null, "B", "t2"), stack.pop()),
                () -> assertEquals(new Action(1, null, "A", "t1"), stack.pop()),

                // empty
                () -> assertTrue(stack.isEmpty()),

                // shouldn't allow not null to pass
                () -> assertThrows(NoSuchElementException.class, () -> stack.pop())
        );
    }

    // ========== LIFO behavior ==========

    @Test
    void lifoBehavior() {

        Action first  = new Action(10, null, "first", "t1");
        Action second = new Action(20, null, "second", "t2");

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

    // reverse a stack test

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

}

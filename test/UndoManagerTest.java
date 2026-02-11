import manager.UndoManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import types.UndoAction;
import util.ArrayStack;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class UndoManagerTest {
    private UndoManager undoManager;
    private ArrayStack<UndoAction> stack;

    @BeforeEach
    void setup() {
        undoManager = new UndoManager(() -> new ArrayStack<>(UndoAction.class));
        stack = (ArrayStack<UndoAction>) undoManager.getData();
    }

    // ========== push / pop / peek ==========

    @Test
    void pushPopPeek() {
        UndoAction a1 = new UndoAction(1, null, "A", "t1");
        UndoAction a2 = new UndoAction(2, null, "B", "t2");

        stack.push(a1);
        assertNotNull(stack.peek(), "Peek should not return null after push");
        assertEquals(a1, stack.peek());
        assertEquals(1, stack.size());

        stack.push(a2);
        assertNotNull(stack.peek(), "Peek should not return null after second push");
        assertEquals(a2, stack.peek());
        assertEquals(2, stack.size());

        // Verify peek doesn't remove
        assertEquals(a2, stack.peek());
        assertEquals(2, stack.size());

        // Verify pop removes and returns correct value
        UndoAction popped = stack.pop();
        assertNotNull(popped, "Pop should not return null");
        assertEquals(a2, popped);
        assertEquals(1, stack.size());

        UndoAction peeked = stack.peek();
        assertNotNull(peeked, "Peek should not return null after pop");
        assertEquals(a1, peeked);
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
        UndoAction a1 = new UndoAction(1, null, "A", "t1");
        UndoAction a2 = new UndoAction(2, null, "B", "t2");
        UndoAction a3 = new UndoAction(3, null, "C", "t3");

        stack.push(a1);
        stack.push(a2);
        stack.push(a3);

        assertEquals(3, stack.size());

        UndoAction popped3 = stack.pop();
        assertNotNull(popped3, "First pop should not return null");
        assertEquals(a3, popped3);

        UndoAction popped2 = stack.pop();
        assertNotNull(popped2, "Second pop should not return null");
        assertEquals(a2, popped2);

        UndoAction popped1 = stack.pop();
        assertNotNull(popped1, "Third pop should not return null");
        assertEquals(a1, popped1);

        assertTrue(stack.isEmpty());

        // shouldn't allow not null to pass (undo manager shouldn't let it happen)
    }

    // ========== LIFO behavior ==========

    @Test
    void lifoBehavior() {
        UndoAction first  = new UndoAction(10, null, "first", "t1");
        UndoAction second = new UndoAction(20, null, "second", "t2");

        stack.push(first);
        stack.push(second);

        // Last in, first out
        UndoAction popped1 = stack.pop();
        assertNotNull(popped1, "First pop should not return null");
        assertEquals(second, popped1);

        UndoAction popped2 = stack.pop();
        assertNotNull(popped2, "Second pop should not return null");
        assertEquals(first, popped2);

        assertTrue(stack.isEmpty());
    }

    // reverse a stack test
    @Test
    public void testReverseStack() {
        ArrayStack<String> stack = new ArrayStack<>(String.class);
        ArrayStack<String> stack2 = new ArrayStack<>(String.class);
        String reveresed = "dcba";
        stack.push("a");
        stack.push("b");
        stack.push("c");
        stack.push("d");

         StringBuilder sb = new StringBuilder();

        assertAll("Test Reverse",
                ()-> {
                    while(!stack.isEmpty()){
                        sb.append(stack.pop());
                    }

                    assertEquals(reveresed, sb.toString());

                }
                );


    }
}
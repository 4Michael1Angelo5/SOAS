import manager.FanTicketQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import types.FanRequest;
import util.LinkedQueue;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


/**
 * JUnit tests for FanTicketQueueTest
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class FanTicketQueueTest {
    private FanTicketQueue fanTicketQueue;
    private LinkedQueue<FanRequest> queue;

    @BeforeEach
    void setup() {
        fanTicketQueue = new FanTicketQueue(LinkedQueue::new);
        queue = (LinkedQueue<FanRequest>) fanTicketQueue.getData();
    }
    // ========== enqueue / dequeue ==========

    @Test
    void enqueueDequeue() {
        assertAll("enqueue/dequeue operations",
                () -> {
                    FanRequest f1 = new FanRequest(1, "John", "VIP", "10:00");
                    FanRequest f2 = new FanRequest(2, "Bobby", "General", "10:05");

                    queue.enqueue(f1);
                    queue.enqueue(f2);
                },
                () -> assertEquals(2, queue.size()),
                () -> assertEquals(new FanRequest(1, "John", "VIP", "10:00"), queue.dequeue()),
                () -> assertEquals(1, queue.size()),
                () -> assertEquals(new FanRequest(2, "Bobby", "General", "10:05"), queue.dequeue()),
                () -> assertTrue(queue.isEmpty())
        );
    }

    // ========== dequeue on empty queue ==========

    @Test
    void dequeueOnEmptyQueue() {
        assertAll("dequeue on empty",
                () -> assertTrue(queue.isEmpty()),
                () -> assertThrows(NoSuchElementException.class, () -> queue.dequeue())
        );
    }

    @Test
    void frontOnEmptyQueue() {
        assertAll("front on empty",
                () -> assertTrue(queue.isEmpty()),
                () -> assertThrows(NoSuchElementException.class, () -> queue.front())
        );
    }

    // ========== FIFO behavior ==========

    @Test
    void fifoBehavior() {
        assertAll("FIFO order",
                () -> {
                    FanRequest first = new FanRequest(1, "First", "VIP", "09:00");
                    FanRequest second = new FanRequest(2, "Second", "General", "09:15");
                    FanRequest third = new FanRequest(3, "Third", "Premium", "09:30");

                    queue.enqueue(first);
                    queue.enqueue(second);
                    queue.enqueue(third);
                },
                () -> assertEquals(new FanRequest(1, "First", "VIP", "09:00"), queue.dequeue()),
                () -> assertEquals(new FanRequest(2, "Second", "General", "09:15"), queue.dequeue()),
                () -> assertEquals(new FanRequest(3, "Third", "Premium", "09:30"), queue.dequeue()),
                () -> assertTrue(queue.isEmpty())
        );
    }

    @Test
    void fifoWithFront() {
        assertAll("front maintains FIFO",
                () -> {
                    FanRequest f1 = new FanRequest(1, "Fan1", "VIP", "10:00");
                    FanRequest f2 = new FanRequest(2, "Fan2", "General", "10:30");

                    queue.enqueue(f1);
                    queue.enqueue(f2);
                },
                () -> assertEquals(new FanRequest(1, "Fan1", "VIP", "10:00"), queue.front()),
                () -> assertEquals(new FanRequest(1, "Fan1", "VIP", "10:00"), queue.dequeue()),
                () -> assertEquals(new FanRequest(2, "Fan2", "General", "10:30"), queue.front())
        );
    }

    // ========== wrap-around correctness ==========

    @Test
    void repeatAddRemove() {
        assertAll("repeated add and remove",
                () -> {
                    FanRequest f1 = new FanRequest(1, "Fan1", "VIP", "08:00");
                    FanRequest f2 = new FanRequest(2, "Fan2", "General", "08:30");
                    FanRequest f3 = new FanRequest(3, "Fan3", "Premium", "09:00");
                    FanRequest f4 = new FanRequest(4, "Fan4", "VIP", "09:30");

                    queue.enqueue(f1);
                    queue.enqueue(f2);
                    queue.dequeue();
                    queue.dequeue();

                    queue.enqueue(f3);
                    queue.enqueue(f4);
                },
                () -> assertEquals(new FanRequest(3, "Fan3", "Premium", "09:00"), queue.dequeue()),
                () -> assertEquals(new FanRequest(4, "Fan4", "VIP", "09:30"), queue.dequeue()),
                () -> assertTrue(queue.isEmpty())
        );
    }

    @Test
    void manyOperations() {
        assertAll("many enqueue and dequeue operations",
                () -> {
                    for (int i = 0; i < 10; i++) {
                        queue.enqueue(new FanRequest(i, "Fan" + i, "General", i + ":00"));
                        if (i % 2 == 1) {
                            queue.dequeue();
                        }
                    }
                },
                () -> assertFalse(queue.isEmpty()),
                () -> {
                    while (!queue.isEmpty()) {
                        assertNotNull(queue.dequeue());
                    }
                },
                () -> assertTrue(queue.isEmpty())
        );
    }

    // ========== edge cases ==========

    @Test
    void singleElementQueue() {
        FanRequest single = new FanRequest(1, "Single", "VIP", "10:00");

        assertAll("single element operations",
                () -> queue.enqueue(single),
                () -> assertEquals(1, queue.size()),
                () -> assertEquals(single, queue.front()),
                () -> assertEquals(single, queue.dequeue()),
                () -> assertTrue(queue.isEmpty())
        );
    }

    @Test
    void enqueueDequeueMultipleTimes() {
        assertAll("enqueue and dequeue multiple cycles",
                () -> {
                    FanRequest f1 = new FanRequest(1, "Fan1", "VIP", "08:00");
                    FanRequest f2 = new FanRequest(2, "Fan2", "General", "08:30");

                    queue.enqueue(f1);
                    queue.dequeue();
                    queue.enqueue(f2);
                },
                () -> assertEquals(1, queue.size()),
                () -> assertEquals(new FanRequest(2, "Fan2", "General", "08:30"), queue.dequeue()),
                () -> assertTrue(queue.isEmpty())
        );
    }

    @Test
    void manyRequests() {
        assertAll("many fan requests",
                () -> {
                    for (int i = 0; i < 100; i++) {
                        queue.enqueue(new FanRequest(i, "Fan" + i, "General", i + ":00"));
                    }
                },
                () -> assertEquals(100, queue.size()),
                () -> {
                    for (int i = 0; i < 100; i++) {
                        assertEquals(new FanRequest(i, "Fan" + i, "General", i + ":00"), queue.dequeue());
                    }
                },
                () -> assertTrue(queue.isEmpty())
        );
    }

    @Test
    void frontAfterClear() {
        FanRequest f1 = new FanRequest(1, "Fan1", "VIP", "10:00");

        assertAll("front after clear",
                () -> queue.enqueue(f1),
                () -> assertEquals(1, queue.size()),
                () -> queue.clear(),
                () -> assertTrue(queue.isEmpty()),
                () -> assertThrows(NoSuchElementException.class, () -> queue.front())
        );
    }

    @Test
    void alternatingEnqueueDequeue() {
        assertAll("alternating enqueue and dequeue",
                () -> {
                    for (int i = 0; i < 5; i++) {
                        queue.enqueue(new FanRequest(i, "Fan" + i, "General", i + ":00"));
                        if (i % 2 == 1) {
                            queue.dequeue();
                        }
                    }
                },
                () -> assertEquals(3, queue.size())
        );
    }

    @Test
    void frontDoesNotRemove() {
        FanRequest f1 = new FanRequest(1, "Fan1", "VIP", "10:00");

        assertAll("front does not remove element",
                () -> queue.enqueue(f1),
                () -> assertEquals(f1, queue.front()),
                () -> assertEquals(1, queue.size()),
                () -> assertEquals(f1, queue.front()),
                () -> assertEquals(1, queue.size())
        );

    }
}
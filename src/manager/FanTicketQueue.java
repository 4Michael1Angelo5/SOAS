package manager;

import types.FanRequest;
import util.DataContainer;
import util.Queue;

import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Fan Ticket Queue manages Fan Requests.
 * @author Chris Chun, Ayush.
 * @version 1.1
 */
public class FanTicketQueue extends DataManager<FanRequest>{

    private static final Logger LOGGER = Logger.getLogger(FanTicketQueue.class.getName());

    public FanTicketQueue(Supplier<DataContainer<FanRequest>> theSupplier) {
        super(FanRequest.class, theSupplier);
        validateQueue();
    }

    /**
     * Checks that the data container is a Queue.
     * FanTicketQueue only works with Queue implementations.
     * @throws IllegalArgumentException if myData is not a Queue
     */
    private void validateQueue() {
        if (!(myData instanceof Queue)) {
            throw new IllegalArgumentException("FanTicketQueue needs a Queue");
        }
    }

    @Override
    public boolean needsIndexedAccess() {
        return false;
    }

    @Override
    public Class<FanTicketQueue> getManagerClass() {
        return FanTicketQueue.class;
    }

    /**
     * Semantic helper to clarify removing data.
     * removes a fan request from the DataContainer
     * using the most efficient underlying implementation
     * for that DataContainer.
     * @return A FanRequest that has been processed/ removed from fan ticket line.
     */
    public FanRequest processRequest() {
        return this.removeData();
    }

    /**
     * Semantic helper to clarify adding data.
     * Adds a fan request to chosen data container using
     * the most efficient method for that data container.
     */
    public void addFanRequest(FanRequest theRequest) {
        this.addData(theRequest);
    }

    // ================================ queue operations =================================

    /**
     * Returns the fan at the front of the queue without removing them.
     * @return the first FanRequest in line
     * @throws NoSuchElementException if the queue is empty
     */
    public FanRequest peekFront() {
        if (!hasWaitingFans()) {
            throw new NoSuchElementException("Cannot peek; the queue is empty.");
        }
        // Accesses the front of the LinkedQueue via the DataContainer interface
        return myData.get(0);
    }

    /**
     * Checks if there are any fans currently waiting in the queue.
     * @return true if the queue contains at least one request, false otherwise
     */
    public boolean hasWaitingFans() {
        return myData != null && !myData.isEmpty();
    }

    /**
     * Gets the current number of fans waiting in the queue.
     * @return the size of the underlying data container
     */
    public int getQueueLength() {
        return (myData == null) ? 0 : myData.size();
    }

    /**
     * Show how the queue works with first-in-first-out order.
     * Adds 3 fans then processes them in the same order.
     */
    public void demonstrateFIFO() {
        LOGGER.info("=== FIFO Demo ===");

        FanRequest fan1 = new FanRequest(1, "Fan A", "VIP", "10:00");
        FanRequest fan2 = new FanRequest(2, "Fan B", "General", "10:15");
        FanRequest fan3 = new FanRequest(3, "Fan C", "Premium", "10:30");

        addData(fan1);
        LOGGER.info("Added to queue: " + fan1.name());

        addData(fan2);
        LOGGER.info("Added to queue: " + fan2.name());

        addData(fan3);
        LOGGER.info("Added to queue: " + fan3.name());

        LOGGER.info("Front of line is: " + peekFront().name());
        LOGGER.info("Processing in order:");

        while (hasWaitingFans()) {
            FanRequest processed = removeData();
            LOGGER.info("Processed: " + processed.name());
        }

        LOGGER.info("=== Demo Complete ===");
    }
}

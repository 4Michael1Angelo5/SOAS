package manager;

import types.FanRequest;
import util.DataContainer;

import java.util.function.Supplier;

/**
 * Fan Ticket Queue manages Fan Requests.
 * @author Chris Chun, Ayush.
 * @version 1.1
 */
public class FanTicketQueue extends DataManager<FanRequest>{

    public FanTicketQueue(Supplier<DataContainer<FanRequest>> theSupplier) {
        super(FanRequest.class, theSupplier);
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
    /*
     * FanTicketQueue TODO - Required for Full Credit:
     *
     * 1. Add peekFront() method - assignment explicitly requires peeking front request
     *    public FanRequest peekFront() {
     *        if (myData instanceof Queue<FanRequest> queue) return queue.front();
     *        throw new UnsupportedOperationException("FanTicketQueue requires a Queue");
     *    }
     *
     * 2. Add queue-specific helper methods for clarity:
     *    - public boolean hasWaitingFans() { return !myData.isEmpty(); }
     *    - public int getQueueLength() { return myData.size(); }
     *
     * 3. Add validation to ensure Queue is used (not Stack/List):
     *    - Check myData instanceof Queue in constructor or validation
     *
     * 4. Create demonstration method showing FIFO behavior:
     *    - Add Fan A, B, C
     *    - Process in order: A, B, C
     *    - Print to show FIFO
     *
     * Note: peekFront() is critical - it's explicitly required by assignment.
     * Your semantic wrappers (addFanRequest, processRequest) are already excellent!
     */

}

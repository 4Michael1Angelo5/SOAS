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
}

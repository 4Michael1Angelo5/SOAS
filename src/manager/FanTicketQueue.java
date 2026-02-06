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
}

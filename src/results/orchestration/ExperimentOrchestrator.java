package results.orchestration;

import types.DataType;
import util.ArrayStore;
import util.DataContainer;
import util.OperationCountable;

public class ExperimentOrchestrator <T extends DataType, M extends OperationCountable<T>>{
    public final DataContainer<T> myTestContainer;
    public final M myManagerTestSubject;
    public ExperimentOrchestrator(Class<T> theDataType, M theManagerTestSubject) {
        myTestContainer = new ArrayStore<>(theDataType);
        myManagerTestSubject = theManagerTestSubject;
    }
}

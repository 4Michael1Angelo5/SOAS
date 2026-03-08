package results.DataContainerResults;

import exceptions.ResultsConfigException;
import manager.DataContainer.DataContainerManager;
import manager.telemetry.OperationsManager;
import results.ExperimentFormat;
import results.orchestration.ExperimentOrchestrator;
import types.DataType;
import util.DataContainer;

import java.util.Objects;

public abstract class DataContainerResults <
                T extends DataType,
                C extends DataContainer<T>,
                M extends DataContainerManager<T, C>>
    extends ExperimentOrchestrator<T,M> {

    public DataContainerResults(
            Class<T> theDataType,
            M theManagerTestSubject,
            ExperimentFormat theExperimentFormat) {
        super(theDataType, theManagerTestSubject, theExperimentFormat);
    }

    // ============================ Results Title =================================

    @Override
    public String getContainerName() {
        return myManagerTestSubject.getData().getClass().getSimpleName();
    }

    // =============================

    /**
     * Ensures proper initial conditions before performing
     * search testing.
     */
    public void validateStateBeforeSearch() {
        ensureTestContainerNotEmpty();
        ensureManagerContainerNotEmpty();
        ensureOperationCounterReset();
    }

    // ============================ untimed tasks ===============================

    /**
     * Non-timed setup task that populates {@code myManager} with data.
     * This prepares the state for {@link #removeNTimes()} without skewing the benchmark results.
     * This must be called before each Remove test.
     * @throws RuntimeException If the Manager's {@code DataContainer} is not empty before starting.
     */
    // not timed
    @Override
    public void setUpForRemove() {
        for (T dataObj : myTestContainer) {
            myManagerTestSubject.addData(dataObj);
        }
        myManagerTestSubject.resetCounter();
        validateStateBeforeRemoveTest();
    }

    public void setUpForSearch() {
        for (T dataObj : myTestContainer) {
            myManagerTestSubject.addData(dataObj);
        }
        myManagerTestSubject.resetCounter();
        validateStateBeforeSearch();
    }

    // =============================  Timed Tasks/ Experiments ====================================


    /**
     * Timed task that populates a new container with all items from the manager.
     * Requires data to be loaded via {@link #loadData(String)} first.
     * * @throws RuntimeException If the manager's data source is empty.
     */
    // runnable & timed
    @Override
    public void addNTimes(){

        for(T dataObj: myTestContainer) {
            myManagerTestSubject.addData(dataObj);
        }
    }

    /**
     * Timed task that removes every element from the setup container until it is empty.
     * Must be preceded by setUpForRemove to ensure there is data to remove.
     * * @throws RuntimeException If the container for removal is empty.
     */
    // runnable & timed
    @Override
    public void removeNTimes() {
        while (!myManagerTestSubject.getData().isEmpty()) {
            myManagerTestSubject.removeData();
        }
    }

}

package results;

import benchmark.BenchmarkRunner;
import loader.DataLoader;
import manager.Manager;
import manager.MapManager;
import manager.RosterManager;
import types.DataType;
import types.Player;
import types.PlayerEnhanced;
import util.ArrayStore;
import util.HashTable;
import util.MapTable;

public abstract class HashTableBenchMark<T extends DataType, M extends MapManager<T>> implements Experiment {

    public ArrayStore<T> myTestContainer;
    private final DataLoader<T> myDataLoader;
    private final MapManager<T> myManager;
    private final ExperimentFormat myExperiementFormat;
    private final BenchmarkRunner myBenchmarkRunner = new BenchmarkRunner();
    /**
     * A roster results class used ONLY for displaying test results.
     */
    private final RosterResults myResults;

    private HashTableBenchMark(Class<T> theDataClass, MapManager<T> theManager, ExperimentFormat theExperimentFormat) {
        super();
        myTestContainer = new ArrayStore<>(theDataClass);
        myDataLoader = new DataLoader<>(theDataClass, ()-> new ArrayStore<>(theDataClass));
        myManager = theManager;
        myResults = initResults(theExperimentFormat);
        myExperiementFormat = theExperimentFormat;
    }

    private RosterResults initResults(ExperimentFormat theExperimentFormat) {
        RosterManager RM = new RosterManager(()-> new ArrayStore<>(Player.class));

        return new RosterResults(RM,
                ()-> new ArrayStore<>(Player.class),
                theExperimentFormat);
    }
    //========================= Error handinling/ State Management =================================

    // ===== Error Handling to ensure coordinated state in experiment pipeline ==================

    public void ensureOperationCounterReset() {
        if (myManager.getSwaps() > 0 || myManager.getComparisons() > 0) {
            throw new RuntimeException(
                    """
                            Misconfigured Experiment:
                            The DataManager's OperationCounter
                            should be reset between Every Experiment.
                            """
            );
        }
    }
    public void ensureTestContainerNotEmpty() {
        if (myTestContainer.isEmpty()) {
            throw new RuntimeException(
                    "Misconfigured ExperimentResult, please ensure "
                            + "to loadData before running running experiments"
            );
        }
    }

    public void ensureManagerContainerNotEmpty() {
        if (myManager.getData().isEmpty()) {
            throw new RuntimeException(
                    """
                            Misconfigured Experiment:
                            Please ensure to call setUpForRemove
                            before conducting removal experiments.
                            """
            );
        }
    }

    public void ensureManagerContainerEmpty() {

        if (!myManager.getData().isEmpty()) {
            throw new RuntimeException(
                    """
                            Misconfigured Experiment:
                            Please ensure to call setUpForAdd
                            before conducting add experiments.
                            """
            );

        }

    }

    public void validateStateBeforeAddTest() {
        ensureTestContainerNotEmpty();
        ensureManagerContainerEmpty();
        ensureOperationCounterReset();
    }

    public void validateStateBeforeRemoveTest() {
        ensureTestContainerNotEmpty();
        ensureManagerContainerNotEmpty();
        ensureOperationCounterReset();
    }

    public void validateStateBeforeSearch() {
        ensureTestContainerNotEmpty();
        ensureManagerContainerNotEmpty();
        ensureOperationCounterReset();
    }



    //========================= Displaying Results =================================

    public void printTestHeader(String theDataStructure, String theManagerClass) {
        //@TODO
        // need to expose a method to modify test hearder inside Results class.

    }


    public void addExperimentResults(BenchmarkResult result) {
        myResults.addExperimentResult(result);
    }

    public void printResults() {
        myResults.printResults();
    }


}

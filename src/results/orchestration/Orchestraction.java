package results.orchestration;

import manager.HashTableManager.HashTableManager;
import manager.Manager;
import manager.telemetry.OperationsManager;
import results.BenchmarkResult;

/**
 * Contract defining what testing tasks must be performed in
 * a benchmark testing experiment.
 * <ul>
 *     <li>
 *         Defines what tasks need to be perfomred between
 *         trial runs to ensure data entegrity
 *     </li>
 *     <li>
 *         Provides a bare minimal expectation
 *         that we should be able to benchmark the efficency of adding and removing data
 *         from the data structure of interest to benchmark.
 *     </li>
 * </ul>
 * @author Chris Chun
 * @version 1.2
 */
public interface Orchestraction {

    //  ==============   set tasked between trial runs (NOT TIMED) =============

    void setUpForAdd();

    /**
     * A non-timed setup task to be run between removal test trial experiments.
     * Must be implemented by abstract subclasses of {@link ExperimentOrchestrator}.
     */
    void setUpForRemove();

    // ==========================  Data Integerity ==========================

    /**
     * Performs a validation check that test container for the experiment is not empty
     * and ensures the abstract subclass of the {@link OperationsManager}'s data storage container
     * is empty (ready to add data).
     */
    void validateStateBeforeAddTest();

    /**
     * Perform a validation check that the test containter for the experiment is not empty
     * and ensures the {@link Manager} or {@link HashTableManager} is not empty (ready to remove data).
     */
    void validateStateBeforeRemoveTest();

    // ============= Experiments for adding and removing data (TIMED TASKS) =============

    /**
     *
     * @param theOperationName the opeartion name that will serve as the title for this experiment
     *                         eg: "enqueue", "addFront", "push", etc.
     * @return A {@link BenchmarkResult} that holds the outcome of an experiment test trial.
     */
    BenchmarkResult testAddNTimes(String theOperationName);

    /**
     *
     * @param theOperationName the opeartion name that will serve as the title for this experiment
     *                         eg: "dequeue", "pop", "remove", "removeFront" etc.
     * @return A {@link BenchmarkResult} that holds the outcome of an experiment test trial.
     */
    BenchmarkResult testRemoveNTimes(String theOperationName);


}

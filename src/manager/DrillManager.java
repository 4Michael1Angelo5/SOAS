package manager;

import counter.OperationCounter;
import loader.DataLoader;
import types.Drill;
import util.ArrayStore;
import util.BinaryHeapPQ;
import util.DataContainer;
import util.LinkedQueue;

import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Supplier;

/**
 * DrillManager is in charge of manging Sea Hawks drills.
 * It provides a concrete way of manipulating Drill data.
 * If instantiated with a Priority Queue it allows for
 */
public class DrillManager extends DataManager<Drill> {

    public DrillManager(Supplier<DataContainer<Drill>> theSupplier) {
        super(Drill.class, theSupplier);

    }

    // -------------------------------- getter ------------------------------
    public Drill peekNextDrill() {
        return myData.get(0);
    }

    /**
     * Allows modification of the comparator for a Priority Queue and runs side effects.
     * This method is only allowed when the DrillManager is instantiated with a BinaryHeapPQ<Drill>
     * Side Effects:
     * <ul>
     *     <li>
     *         Modifies the DataLoader to use the same sorting logic when loading drills from a csv
     *         So that subsequent calls to .loadCsv() return a PriorityQueue with the same sorting logic.
     *     </li>
     *     <li>
     *         Reorders the current heap to implement the same sorting logic.
     *     </li>
     *
     * </ul>
     *
     * @param theComparator the new comparator needs
     */
    public void upDateComparator(Comparator<Drill> theComparator){
        if (!(myData instanceof BinaryHeapPQ<Drill>)) {
            throw new UnsupportedOperationException("Cannot update comparator for non Priority Queue Data Structures");
        }

        // create a new supplier for the data loader
        Supplier<DataContainer<Drill>> newSupplier = () -> new BinaryHeapPQ<>(Drill.class, theComparator);

        // update the data loader so that subsequent csv file loads use the new comparator for sorting instead of the
        // one the DrillManager was initially instantiated with.
        setDataLoader(Drill.class, newSupplier);

        // cast safely now because we have narrowed type.
        ((BinaryHeapPQ<Drill>) myData).reorder(theComparator);
    }

    /**
     * Allows a more efficient way of loading CSV data using buildHeap method.
     * @param theFilePath the file path to the CSV data
     * @throws IOException if there are IO errors.
     */
    public void loadDrills(String theFilePath) throws IOException {
        if (!(myData instanceof BinaryHeapPQ<Drill>)) {
            loadCsvData(theFilePath);
        }else {

            // create temp array supplier
            Supplier<DataContainer<Drill>> tempSup = () -> new ArrayStore<>(Drill.class, 16);

            // create temp loader
            DataLoader<Drill> tempLoader = new DataLoader<>(Drill.class, tempSup);

            // create temp array to store csv data
            ArrayStore<Drill> tempArray = (ArrayStore<Drill>) tempLoader.loadData(theFilePath);

            // prevent accumulation.
            myData.clear();

            // build heap from temp array
            ((BinaryHeapPQ<Drill>) myData).buildHeap(tempArray);
        }
    }

    /**
     * Sorts drills by:
      <ol>
        <li>Higher urgency first</li>
        <li>Earlier install_by_day first</li>
        <li>Lower fatigue_cost preferred (tie-breaker)</li>
        <li>Shorter duration preferred (final tie-breaker)</li>
     </ol>
     * @return A comparator with the above sorting logic.
     */
    public Comparator<Drill> fairSort() {
        return (a,b)->{

            // first priority
            // drill with higher urgency comes first
            int urgency = Integer.compare(b.urgency(),a.urgency());
            if (urgency != 0) return urgency; // if negative b comes first.

            // tiebreaker 1.
            // drill with lower/earlier install by day comes first
            int install = Integer.compare(a.install_by_day(), b.install_by_day());
            if (install != 0) return install;

            // tiebreaker 2.
            // drill will lower fatigue should come first.
            int fatigue = Integer.compare(a.fatigue_cost(), b.fatigue_cost());
            if (fatigue != 0) return fatigue;

            // final tiebreaker
            // drill with lower duration comes first.
            return Integer.compare(a.duration_min(), b.duration_min());
        };
    }

    /**
     * Sort by urgency only.
     * @return a comparator placing drills with higher urgency before drills with lower urgency
     */
    public Comparator<Drill> sortByUrgency() {
        return (a,b) -> Integer.compare(b.urgency(),a.urgency());
    }

    /**
     * Sort by id only
     * @return a comparator placing drills with lower id number before drills with lower id number.
     */
    public Comparator<Drill> sortById() {
        return (a,b) -> Integer.compare(a.drill_id(), b.drill_id());
    }
    
    @Override
    public boolean needsIndexedAccess() {
        return false;
    }

    @Override
    public Class<?> getManagerClass() {
        return DrillManager.class;
    }

}

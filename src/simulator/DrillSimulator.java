package simulator;

import manager.DrillManager;
import results.Results;
import types.Drill;
import util.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class DrillSimulator {

    // logger
    private static final Logger logger = Logger.getLogger(Results.class.getName());

    // logger color formatting
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";


    //Boilerplate to get logger to look good.
    static {
        // attempt to use logging properties file
        try (var is = Results.class.getClassLoader().getResourceAsStream("logging.properties")) {
            if (is != null) {
                java.util.logging.LogManager.getLogManager().readConfiguration(is);
            } else {
                // Fallback: Manually set the format if file isn't found
                System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
            }
        } catch (Exception e) {
            System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
        }
    }

    // csv data
    private static final String DRILL_50 = "data/seahawks_drills_50.csv";
    private static final String DRILL_500 = "data/seahawks_drills_500.csv";
    private static final String DRILL_5000 = "data/seahawks_drills_5000.csv";

    // suppliers
    private static final Supplier<DataContainer<Drill>> queueSup = LinkedQueue::new;
    private static final Supplier<DataContainer<Drill>> pQueueSup = () -> new BinaryHeapPQ<>(Drill.class);
    private static final Supplier<DataContainer<Drill>> arraySup = ()-> new ArrayStore<>(Drill.class, 16);

    /**
     * ArrayBacked DrillManager
     */
    private static final DrillManager DM_ARRAY = new DrillManager(arraySup);

    /**
     * Queue backed DrillManager
     */
    private static final DrillManager DM_Q = new DrillManager(queueSup);

    /**
     * Priority Queue backed DrillManager.
     */
    private static final DrillManager DM_PQ = new DrillManager(pQueueSup);

    /**
     * Wait times for each Drill in the Queue
     */
    private final HashMap<Drill, Integer> waitTimesQ = new HashMap<>();

    /**
     * Wait times for each Drill in the Priority Queue.
     */
    private final HashMap<Drill, Integer> waitTimesPQ = new HashMap<>();

    /**
     * Deltas in wait time for the Queue
     */
    private final HashMap<Drill, DrillReport> drillReportQ = new HashMap<>();

    /**
     * Deltas in wait time for the Priority Queue.
     */
    private final HashMap<Drill, DrillReport> drillReportPQ = new HashMap<>();

    /**
     * greatest winners
     */
    private final BinaryHeapPQ<DrillStats> biggestWinners = new BinaryHeapPQ<>(DrillStats.class, (a,b)-> b.deltaPosition() - a.deltaPosition());

    /**
     * greatest losers
     */
    private final BinaryHeapPQ<DrillStats> biggestLosers = new BinaryHeapPQ<>(DrillStats.class, (a,b)-> a.deltaPosition() - b.deltaPosition());


    public DrillSimulator(){super();}

    private double getAverageWaitTime(HashMap<Drill, DrillReport> theDrillReport) {
        AtomicInteger totalTime = new AtomicInteger();
        theDrillReport.forEach(
                (_, report) -> {
                    totalTime.addAndGet(report.waitTime());
                }
        );
        return totalTime.get()/ (double) theDrillReport.size();
    }


    /**
     * Calculates how long it takes to run all the drills.
     * Uses an Array Backed Drill Manager to load CSV data
     * and iterates over every drill summing up the total duration
     * in minutes and returns the total.
     * @return the total time in minutes it take to run all the drills.
     * @throws IOException if an error occurs parsing CSV data from the loader.
     */
    private int getWaitTime() throws IOException {
        DM_ARRAY.loadDrills(DRILL_50);
        AtomicInteger totalWaitTime = new AtomicInteger();
        DM_ARRAY.getData().forEach(
                (drill) -> totalWaitTime.addAndGet(drill.duration_min())
        );
        return totalWaitTime.get();
    }

    /**
     *
     * Processes a drill by removing one drill from the Drill Manager and
     * creates the ability to associate wait time metrics to a specific drill.
     * @param drillManager a drill manager that process/removes a drill.
     * @param drillReportTable a hash table to tie wait time metrics to specific drills
     * @param waitTime how long this drill waited before being processed.
     * @param placeInLine the order this drill was processed.
     * @return the time elapsed to process this drill.
     */
    private int processDrill(DrillManager drillManager,
                              HashMap<Drill, DrillReport> drillReportTable,
                              int waitTime,
                              int placeInLine) {
        // 1) record how long each drill waits in the queue
        //    and its order in line.
        // remove from queue
        Drill removedFromQ = drillManager.removeData();

        // 2)
        // create DrillReport object to report metrics
        drillReportTable.put(removedFromQ, new DrillReport(waitTime, placeInLine));

        // 3)
        // increment how long the next drill waited before being processed.
        waitTime += removedFromQ.duration_min();

        return waitTime;
    }

    private void loadCsvData() throws IOException{
        // load the dril
        DM_Q.loadDrills(DRILL_50);

        // optional for testing to compare how differnt sorting algorithms effect wait time
        // statistics.
        DM_PQ.upDateComparator((DM_PQ.fairSort()));
        DM_PQ.loadDrills(DRILL_50);

        if (DM_Q.getData().size() != DM_PQ.getData().size()) {
            throw new RuntimeException("Drill Managers should have the same size data.");
        }
    }

    /**
     * Records the change in wait time each drill
     * in the priority queue experienced.
     */
    private HashMap<Drill, DrillStats> getDeltas() {
        HashMap<Drill, DrillStats> drillStats = new HashMap<>();
        // fail fast. by the time we get here both hash tables
        // should be the same length
        if (drillReportPQ.size() != drillReportQ.size()) {
            throw new RuntimeException("both drill reports should be the same size");
        }

        for (Map.Entry<Drill,DrillReport> entry: drillReportPQ.entrySet()) {

            Drill drill = entry.getKey();
            DrillReport report = entry.getValue();

            // how many times this drill denied being processed.
            // if negative then this drill had a lower priority.
            // if positive this drill had a higher priority.

            // delta in order processed.
            int deltaPosition =  drillReportQ.get(drill).orderProcessed() - report.orderProcessed();

            // delta in time processed.
            int deltaWaitTime = drillReportQ.get(drill).waitTime() - report.waitTime();

            DrillStats drillStatsObj = new DrillStats(drill, report, deltaPosition, deltaWaitTime);

            biggestLosers.insert(drillStatsObj);
            biggestWinners.insert(drillStatsObj);

            drillStats.put(drill, drillStatsObj);
        }

        return drillStats;
    }

    private double calculateStd(DrillStats drillStats) {

        Drill drill = drillStats.drill();
        return 0.0;
    }


    public void runSimulation() throws IOException {
        // calculate how long it takes to run all the drills.
        final int totalTimeForAllDrills = getWaitTime();

        //-----------------------------------------------
        // Step 1) data loading from CSV files
        loadCsvData();

        // -----------------------------------------------
        // step 2) processing - generate wait time stats.

        // running total of how long each drill waits before being processed in the Queue
        int waitTimeQ = 0;

        // running total of how long each drill waits before being processed in the Priority Queue
        int waitTimePQ = 0;

        int placeInLine = 0;

        while (!DM_PQ.getData().isEmpty() && !DM_Q.getData().isEmpty()) {

            placeInLine++;

            // process drills from the queue.
            waitTimeQ = processDrill(DM_Q, drillReportQ, waitTimeQ, placeInLine);

            // process drills from the priority queue.
            waitTimePQ = processDrill(DM_PQ, drillReportPQ, waitTimePQ, placeInLine);
        }

        // ------------------------------------------------------
        // step 3) do meaningful things with the wait time metrics.
        //         eg, average wait time, starvation, fairness,
        //         probability density function

        //                3a) get average

        // average wait time for a regular queue
        double qAvgWaitTime =  getAverageWaitTime(drillReportQ);

        // average wait time for a priority queue
        double pqAvgWaitTime =  getAverageWaitTime(drillReportPQ);

        //             3b) get deltas in wait time and position between queue and pq.
        HashMap<Drill, DrillStats> drillDeltas = getDeltas();
//        for (Map.Entry<Drill,DrillStats> entry : drillDeltas.entrySet()) {
//
//            Drill drill = entry.getKey();
//            DrillStats deltas = entry.getValue();
//
//            logger.info(ANSI_GREEN +"drill: " + drill.toString() +"\ndeltas: "+ deltas + ANSI_RESET);
//        }

        ArrayStore<DrillStats> arrayWinners = new ArrayStore<>(DrillStats.class, 16);
        ArrayStore<DrillStats> arrayLosers = new ArrayStore<>(DrillStats.class, 16);

        while( !biggestWinners.isEmpty()) {
            arrayWinners.add(biggestWinners.extract());
            arrayLosers.add(biggestLosers.extract());
        }

        logger.info(ANSI_GREEN +"=========================================================================="+ ANSI_RESET);
        logger.info(ANSI_GREEN +"Biggest Winners"+ ANSI_RESET);

        for (DrillStats stats : arrayWinners) {
            logger.info(ANSI_GREEN +
                    stats
                    + ANSI_RESET);
        }

        logger.info(ANSI_GREEN +"=========================================================================="+ ANSI_RESET);
//        logger.info(ANSI_GREEN +"Biggest Losers"+ ANSI_RESET);
//
//        for (DrillStats stats : arrayLosers) {
//            logger.info(ANSI_GREEN + stats.toString() + ANSI_RESET);
//        }






    }

    /**
     * Ideas for measuring starvation
     * Drill X was processed 500th despite being in line first.
     */

    public void main(String[] args) throws IOException {
        runSimulation();
    }



}

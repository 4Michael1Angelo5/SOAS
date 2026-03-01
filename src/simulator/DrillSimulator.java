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

/**
 * A simulation class that compares wait times and positon changes between
 * drills processed in a regular {@link LinkedQueue} and a {@link BinaryHeapPQ}
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class DrillSimulator {

    /**
     * logger for all your logging needs.
     */
    private static final Logger logger = Logger.getLogger(DrillSimulator.class.getName());

    // ========================== logger color formatting ==========================
    /**
     * Lavender color for logger.
     */
    public static final String ANSI_LAVENDER = "\u001B[38;5;147m";

    /**
     * Reset logger color to default.
     */
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

    // ========================== CSV DATA ==========================
    private static final String DRILL_50 = "data/seahawks_drills_50.csv";
    private static final String DRILL_500 = "data/seahawks_drills_500.csv";
    private static final String DRILL_5000 = "data/seahawks_drills_5000.csv";

    // ========================== Suppliers for DrillManagers ==========================
    private static final Supplier<DataContainer<Drill>> queueSup = LinkedQueue::new;
    private static final Supplier<DataContainer<Drill>> pQueueSup = () -> new BinaryHeapPQ<>(Drill.class);

    // ========================== Managers  ==========================

    /**
     * Queue backed DrillManager
     */
    private final DrillManager DM_Q = new DrillManager(queueSup);

    /**
     * Priority Queue backed DrillManager.
     */
    private final DrillManager DM_PQ = new DrillManager(pQueueSup);

    // ========================== Containers for Stats  ==========================

    /**
     * HashMap of Deltas in wait time for the Queue
     */
    private final HashMap<Drill, DrillStats> drillReportQ = new HashMap<>();

    /**
     * HashMap of Deltas in wait time for the Priority Queue.
     */
    private final HashMap<Drill, DrillStats> drillReportPQ = new HashMap<>();


    // ========================== Priority Queue of Stats  ==========================
    /**
     * Priority queue of drills sorted by greatest decrease in
     * change of position/ wait time.
     */
    private final BinaryHeapPQ<DrillStats> biggestWinners =
            new BinaryHeapPQ<>(DrillStats.class,
                    (a, b)-> b.getDeltaPosition() - a.getDeltaPosition());


    // ========================== Sorted Array of Stats  ==========================
    /**
     * Array of drills sorted by greatest decrease in
     * position/ wait time - used to report top percentile deltas
     * in wait time/ position
     */
    private final ArrayStore<DrillStats> arrayWinners = new ArrayStore<>(DrillStats.class, 16);


    public DrillSimulator(){super();}


    // ===========================  Step 1 ======================================

    private void loadCsvData(SampleSizes sampleSize, Comparator<Drill> theComparator) throws IOException{
        // load the dril
        switch (sampleSize.getSize()) {
            case 50 -> {
                DM_Q.loadDrills(DRILL_50);
                DM_PQ.loadDrills(DRILL_50);
            }
            case 500 -> {
                DM_Q.loadDrills(DRILL_500);
                DM_PQ.loadDrills(DRILL_500);
            }
            case 5000 -> {
                DM_Q.loadDrills(DRILL_5000);
                DM_PQ.loadDrills(DRILL_5000);
            }
        }


//        // optional for testing to compare how different sorting algorithms effect wait time
//        // statistics.
        DM_PQ.upDateComparator(theComparator);

        if (DM_Q.getData().size() != DM_PQ.getData().size()) {
            throw new RuntimeException("Drill Managers should have the same size data.");
        }
    }

    // ===========================  Step 2 ======================================

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
                             HashMap<Drill, DrillStats> drillReportTable,
                             int waitTime,
                             int placeInLine) {
        // 1) record how long each drill waits in the queue
        //    and its order in line.
        // remove from queue
        Drill removedFromQ = drillManager.removeData();

        // 2)
        // create DrillReport object to report metrics
        DrillReport report = new DrillReport(waitTime, placeInLine);
        DrillStats stats = new DrillStats(removedFromQ, report);

        drillReportTable.put(removedFromQ,  stats);

        // 3)
        // increment how long the next drill waited before being processed.
        waitTime += removedFromQ.duration_min();

        return waitTime;
    }

    // ===========================  Step 3 ======================================

    private double getAverageWaitTime(HashMap<Drill, DrillStats> theDrillStats) {
        AtomicInteger totalTime = new AtomicInteger();
        theDrillStats.forEach(
                (_, stat) -> totalTime.addAndGet(stat.getReport().waitTime())
        );
        return totalTime.get()/ (double) theDrillStats.size();
    }

    // ===========================  Step 4 ======================================


    /**
     * Records the change in wait time each drill
     * in the priority queue experienced.
     */
    private void generateDeltas() {
        // fail fast. by the time we get here both hash tables
        // should be the same length
        if (drillReportPQ.size() != drillReportQ.size()) {
            throw new RuntimeException("both drill reports should be the same size");
        }

        for (Map.Entry<Drill, DrillStats> entry: drillReportPQ.entrySet()) {

            Drill drill = entry.getKey();
            DrillStats statistic = entry.getValue();
            DrillReport report = entry.getValue().getReport();

            // how many times this drill denied being processed.
            // if negative then this drill had a lower priority.
            // if positive this drill had a higher priority.

            // delta in order processed.
            int deltaPosition =  drillReportQ.get(drill).getReport().orderProcessed() - report.orderProcessed();

            // delta in time processed.
            int deltaWaitTime = drillReportQ.get(drill).getReport().waitTime() - report.waitTime();

            statistic.setDeltaWaitTime(deltaWaitTime);
            statistic.setDeltaPosition(deltaPosition);

            biggestWinners.insert(statistic); // sorts by greatest increase in wait time/posiiton
        }

    }
    // ===========================  Step 5 ======================================
    // get average of deltas

    private double getAverageDeltaTime(HashMap<Drill, DrillStats> map) {

        double sum = 0.0;
        double N = map.size();

        for (DrillStats stat: map.values()) {
            sum += stat.getDeltaWaitTime();
        }

        return sum/N;
    }


    private double getAverageDeltaPosition(HashMap<Drill, DrillStats> map) {

        double sum = 0.0;
        double N = map.size();

        for (DrillStats stat: map.values()) {
            sum += stat.getDeltaPosition();
        }

        return sum/N;
    }

    // ============================== Step 6 =========================================
    // calculate std of deltas

    private double calculateStdDeltaTime(HashMap<Drill, DrillStats> map, double avgDeltaWaitTime) {

        double sum = 0.0;
        double N = map.size();

        for (DrillStats stat: map.values()) {
            sum += Math.pow(stat.getDeltaWaitTime()  - avgDeltaWaitTime, 2);
        }

        return Math.sqrt(sum/N);
    }

    private double calculateStdDeltaPosition(HashMap<Drill, DrillStats> map, double avgDeltaWaitTime) {

        double sum = 0.0;
        double N = map.size();

        for (DrillStats stat: map.values()) {
            sum += Math.pow(stat.getDeltaPosition()  - avgDeltaWaitTime, 2);
        }

        return Math.sqrt(sum/N);
    }
    // ===========================  Step 7 ======================================
    // calculate Z score of deltas

    /**
     *
     * @param x the value of data point entry
     * @param x_bar the average
     * @param std  the standard deviation
     * @return the Z score (how many standard deviations away this entry's value is from the mean)
     */
    private double calculateZScore(int x,
                                   double x_bar,
                                   double std) {
        return (x - x_bar)/std;
    }

    // ===========================  Step 8 ======================================

    /**
     * Convert the Biggest Winners Priority queue to
     * an Array for reporting metrics.
     */
    private void convertPQtoArray() {
        if (biggestWinners.isEmpty()) {
            throw new RuntimeException(""" 
                    Biggest Winner Priority Queue should not be empty.
                    """);
        }
        while( !biggestWinners.isEmpty()) {
            arrayWinners.add(biggestWinners.extract());
        }
    }


    // ===========================  Step 9 ======================================
    // report simulation results

    /**
     * Prints report of the simulation results.
     * @param sampleSizes sample size of the data can be 50, 500, 5000
     * @param qAvgWaitTime average wait time per/drill in the queue
     * @param pqAvgWaitTime average wait time per/drill in the priority queu
     */
    private void printReport(SampleSizes sampleSizes,
                             double qAvgWaitTime,
                             double pqAvgWaitTime ) {
        DrillStats biggestWinner = arrayWinners.get(0);
        DrillStats biggestLoser =  arrayWinners.get(arrayWinners.size()-1);

        logger.info(ANSI_LAVENDER
                + "The simulation compared wait times to process Seahawks drills "
                + "between FIFO behavior queues and Priority Queues  using sample sizes of "
                + sampleSizes.getSize() + "."
                + ANSI_RESET);

        logger.info( ANSI_LAVENDER
                + "The simulation used the following sorting logic to prioritize drills: \n"
                + "   - Higher urgency first\n"
                + "   - Earlier install_by_day first\n"
                + "   - Lower fatigue_cost preferred (tie-breaker)\n"
                + "   - Shorter duration preferred (final tie-breaker)"
                + ANSI_RESET);

        logger.info(ANSI_LAVENDER
                + "The average wait time for the Queue was " + qAvgWaitTime
                + " minutes.\nThe average wait time for "
                + "the Priority Queue was: " + pqAvgWaitTime + " minutes"
                + ANSI_RESET);

        logger.info( ANSI_LAVENDER
                + "On average each drill in the Priority Queue experienced a" +
                (pqAvgWaitTime > qAvgWaitTime ? " longer" : " shorter")
                + " wait time compared to the Queue."
                + ANSI_RESET);

        logger.info( ANSI_LAVENDER
                + "We observed significant starvation - drills being continuously denied to be processed because "
                + "they had a lower priority.\n");
        logger.info(ANSI_LAVENDER + "For example, the biggest loser was:\n" +
                "         {\n"+
                "           drill_id:" + biggestLoser.getDrill().drill_id() + ",\n" +
                "           name:" + biggestLoser.getDrill().name() + ",\n" +
                "           urgency:" + biggestLoser.getDrill().urgency() + ",\n" +
                "           duration_min:" + biggestLoser.getDrill().duration_min() + ",\n" +
                "           fatigue_cost:" + biggestLoser.getDrill().fatigue_cost() + ",\n" +
                "           install_by_day:" + biggestLoser.getDrill().install_by_day() + "\n" +
                "          }\n"
                + ANSI_RESET);
        logger.info(
                ANSI_LAVENDER
                        + "This drill was processed " + Math.abs(biggestLoser.getDeltaWaitTime()) + " minutes later "
                        + "then it would have been processed in a regular queue.\n"
                        + "This drill was pushed back "
                        + Math.abs(biggestLoser.getDeltaPosition()) + " places in line,\n"
                        + "meaning it was originally in line at position "
                        + (biggestLoser.getReport().orderProcessed() + biggestLoser.getDeltaPosition())
                        + " but instead,\n"
                        + Math.abs(biggestLoser.getReport().orderProcessed()-1)
                        + " other drills were processed before this drill.\n"
                        + "It had a Z-time-score of "
                        + String.format("%.3f", biggestLoser.getZScoreTime())
                        + " ,meaning this drill's wait time was "
                        + String.format("%.3f", Math.abs(biggestLoser.getZScoreTime()))
                        + " standard deviations worse than the average change in wait time.\n"
                        + "It had a Z-position-score of "
                        + String.format("%.3f", biggestLoser.getZScorePos())
                        + " meaning this drill's change in position was "
                        + String.format("%.3f", Math.abs(biggestLoser.getZScorePos()))
                        + " standard deviations worse than the average change in position.\n" +
                        ANSI_RESET
        );

        logger.info(ANSI_LAVENDER
                + "However, the biggest winner was:\n"
                + "         {\n"
                + "           drill_id:" + biggestWinner.getDrill().drill_id() + ",\n"
                + "           name:" + biggestWinner.getDrill().name() + ",\n"
                + "           urgency:" + biggestWinner.getDrill().urgency() + ",\n"
                + "           duration_min:" + biggestWinner.getDrill().duration_min() + ",\n"
                + "           fatigue_cost:" + biggestWinner.getDrill().fatigue_cost() + ",\n"
                + "           install_by_day:" + biggestWinner.getDrill().install_by_day() + "\n"
                + "          }\n"
                + ANSI_RESET);
        logger.info(
                ANSI_LAVENDER
                        + "This drill was processed " + biggestWinner.getDeltaWaitTime() + " minutes sooner "
                        + "then it would have been processed in a regular queue.\n"
                        + "This drill was able to skip "
                        + biggestWinner.getDeltaPosition()
                        + " places in line, \n"
                        + "meaning it was originally in line at position "
                        + (biggestWinner.getReport().orderProcessed() + biggestWinner.getDeltaPosition()) + "\n"
                        + "but because it had a high priority, it jumped to position "
                        + biggestWinner.getReport().orderProcessed() + "\n"
                        + "It had a Z-time-score of: "
                        + String.format("%.3f", biggestWinner.getZScoreTime())
                        + " meaning this drill's wait time was "
                        + (String.format("%.3f",biggestWinner.getZScoreTime()))
                        + " standard deviations better than the average change in wait time.\n"
                        + "It had a Z-position-score of "
                        + String.format("%.3f", biggestWinner.getZScorePos())
                        + " meaning this drill's change in position was "
                        + String.format("%.3f",biggestWinner.getZScorePos())
                        + " standard deviations better than the average change in position.\n"
                        +  ANSI_RESET
        );

    }


    /**
     * Runs a simulation comparing drill wait times between
     * a regular queue and a priority queue.
     * @param sampleSizes options are 50, 500, 5000 via an enum
     * @throws IOException if the data loader encounters an error.
     */
    public void runSimulation(SampleSizes sampleSizes, Comparator<Drill> theComparator) throws IOException {

        //-----------------------------------------------
        // step 0) Make Sure all data containers are clear so we run experiments on new data sets
        drillReportQ.clear();
        drillReportPQ.clear();
        biggestWinners.clear();
        arrayWinners.clear();

        //-----------------------------------------------
        // Step 1) data loading from CSV files
        loadCsvData(sampleSizes, theComparator);

        // -----------------------------------------------
        // step 2) processing - generate wait times and record place in line.

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
        // step 3) calculate averages

        // average wait time for a regular queue
        double qAvgWaitTime =  getAverageWaitTime(drillReportQ);

        // average wait time for a priority queue
        double pqAvgWaitTime =  getAverageWaitTime(drillReportPQ);

        // ------------------------------------------------------
        // step 4)
        // track differences in wait times between
        // the queue and the priority queue.

        // get deltas in wait time and position between queue and pq.
        // creates a pq of biggest winners and losers that resulted from the sorting logic.
        generateDeltas();

        // ------------------------------------------------------
        // step 5 & 6
        // calculate average delta in wait time and position and
        // calculate standard deviation in wait time and position.
        //
        double avgT = getAverageDeltaTime(drillReportPQ);
        double stdT = calculateStdDeltaTime(drillReportPQ, avgT);

        double avgP = getAverageDeltaPosition(drillReportPQ);
        double stdP = calculateStdDeltaPosition(drillReportPQ, avgP);

        // ------------------------------------------------------
        // step 7)
        // update Z-score for position and wait time.
        drillReportPQ.values().forEach(stat -> {
            double zTime = calculateZScore(stat.getDeltaWaitTime(), avgT, stdT);
            stat.setZScoreTime(zTime);
            double zPos = calculateZScore(stat.getDeltaPosition(), avgP, stdP);
            stat.setZScorePos(zPos);
        });

        // ------------------------------------------------------
        // step 8)
        // convert the pq of biggest winners and losers to a sorted array.
        convertPQtoArray();

        // ------------------------------------------------------
        // step 9) report the biggest losers and winners
        printReport(sampleSizes, qAvgWaitTime, pqAvgWaitTime);

    }
}

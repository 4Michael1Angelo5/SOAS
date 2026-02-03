package results;

import benchmark.BenchmarkRunner;
import manager.RosterManager;
import util.DataContainer;
import types.Player;
import util.ArrayStore;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class Results {

    public static int initCapacity = 16;
    public static int removeRuns = 1;  // Remove can only run once

    final private static int runTrials = 30;

    final static String roster50 = "data/seahawks_roster_50.csv";
    final static String roster500 = "data/seahawks_roster_500.csv";
    final static String roster5000 = "data/seahawks_roster_5000.csv";

    BenchmarkRunner benchmarkRunner = new BenchmarkRunner();

    final Supplier<DataContainer<Player>> supplier = () -> new ArrayStore<>(Player.class, 16);
    RosterManager rosterManager = new RosterManager(supplier);

    DataContainer<Player> roster = new ArrayStore<>(Player.class,16);
    DataContainer<Player> rosterForRemove = new ArrayStore<>(Player.class, 16);  // Class-level variable

    public Results(){
        super();
    }

    public void loadRoster(String theFilePath) throws IOException{
        rosterManager.loadPlayerData(theFilePath);
        roster = rosterManager.getPlayerData();
    }

    public void addNTimes() {
        ArrayStore<Player> temp = new ArrayStore<>(Player.class, initCapacity);
        for (int i = 0; i < roster.size(); i++ ) {
            temp.add(roster.get(i));
        }
    }


    // Setup method - NOT timed
    public void setupRemoveTest() {
        rosterForRemove = new ArrayStore<>(Player.class, roster.size());
        for (int i = 0; i < roster.size(); i++) {
            rosterForRemove.add(roster.get(i));
        }
    }

    // Only the remove operation - this is timed
    public void removeFromFrontNTimes() {
        while (rosterForRemove.size() > 0) {
            rosterForRemove.removeAt(0);
        }
    }

    public void searchNTimes() {
        for (int i = 0; i < roster.size(); i++) {
            rosterManager.findByName("NOT FINDABLE");
        }
    }

    public void resetRoster() {
        roster = new ArrayStore<>(Player.class, initCapacity);
    }

    /**
     * @implNote Removal is a destructive operation. To ensure accurate averaging,
     * the list is refilled via {@link #setupRemoveTest()} before each trial
     * to prevent timing the removal of elements from an already empty list.
     */
    public void runAllExperiments() throws IOException {
        System.out.println("\n========== Benchmark Results ==========\n");
        System.out.printf("%-10s %-15s %-15s%n", "Size", "Operation", "Avg Time (ms)");
        System.out.println("----------------------------------------");

        // Test with 50 players
        loadRoster(roster50);

        double add50 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, this::addNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "50", "Add", add50);

        double remove50 = 0;

        for (int i =0; i < runTrials ;i++) {
            // add 50 players to roster.
            setupRemoveTest();
            // remove 50 players from roster.
            remove50 += benchmarkRunner.runSpeedTestAndGetAvg(1, this::removeFromFrontNTimes);
        }
        double remove50avg = remove50/runTrials;

        System.out.printf("%-10s %-15s %-15.1f%n", "50", "Remove", remove50avg);

        double search50 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, ()-> rosterManager.findByName("NOT FINDABLE"));
        System.out.printf("%-10s %-15s %-15.1f%n", "50", "Search", search50);

        resetRoster();
        //****************************************************************************************************

        // Test with 500 players
        loadRoster(roster500);
        double add500 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, this::addNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "500", "Add", add500);

        double remove500 = 0;

        for (int i =0; i < runTrials ;i++) {
            setupRemoveTest();
            remove500 += benchmarkRunner.runSpeedTestAndGetAvg(1, this::removeFromFrontNTimes);
        }

        final double remove500avg = remove500/ runTrials;

        System.out.printf("%-10s %-15s %-15.1f%n", "500", "Remove", remove500avg);

        double search500 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, ()-> rosterManager.findByName("NOT FINDABLE"));
        System.out.printf("%-10s %-15s %-15.1f%n", "500", "Search", search500);

        resetRoster();
        //****************************************************************************************************

        // Test with 5000 players
        loadRoster(roster5000);
        double add5000 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, this::addNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "5000", "Add", add5000);

        double remove5000 = 0;

        for (int i =0; i < runTrials ;i++) {
            setupRemoveTest();
            remove5000 += benchmarkRunner.runSpeedTestAndGetAvg(1, this::removeFromFrontNTimes);
        }

        final double remove5000avg = remove5000/runTrials;
        System.out.printf("%-10s %-15s %-15.1f%n", "5000", "Remove", remove5000avg);

        double search5000 = benchmarkRunner.runSpeedTestAndGetAvg(runTrials, ()-> rosterManager.findByName("NOT FINDABLE"));
        System.out.printf("%-10s %-15s %-15.1f%n", "5000", "Search", search5000);

        System.out.println("========================================\n");

        resetRoster();
    }

    public static void main(String[] args) throws IOException {
        Results results = new Results();
        results.runAllExperiments();
    }
}

package results;

import benchmark.BenchmarkRunner;
import manager.RosterManager;
import types.Player;
import util.ArrayStore;

import java.io.IOException;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class Results {

    public static int initCapacity = 16;
    public static int runs = 25;
    public static int removeRuns = 1;  // Remove can only run once

    final static String roster50 = "data/seahawks_roster_50.csv";
    final static String roster500 = "data/seahawks_roster_500.csv";
    final static String roster5000 = "data/seahawks_roster_5000.csv";

    BenchmarkRunner benchmarkRunner = new BenchmarkRunner();
    RosterManager rosterManager = new RosterManager();

    ArrayStore<Player> roster = new ArrayStore<>(Player.class,16);
    ArrayStore<Player> tempForRemove;  // Class-level variable

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
        tempForRemove = new ArrayStore<>(Player.class, initCapacity);
        for (int i = 0; i < roster.size(); i++) {
            tempForRemove.add(roster.get(i));
        }
    }

    // Only the remove operation - this is timed
    public void removeFromFrontNTimes() {
        while (tempForRemove.size() > 0) {
            tempForRemove.removeAtIndex(0);
        }
    }

    public void searchNTimes() {
        for (int i = 0; i < roster.size(); i++) {
            roster.get(i);
        }
    }

    public void resetRoster() {
        roster = new ArrayStore<>(Player.class, initCapacity);
    }

    public void runAllExperiments() throws IOException {
        System.out.println("\n========== Benchmark Results ==========\n");
        System.out.printf("%-10s %-15s %-15s%n", "Size", "Operation", "Avg Time (ms)");
        System.out.println("----------------------------------------");

        // Test with 50 players
        loadRoster(roster50);
        double add50 = benchmarkRunner.runSpeedTestAndGetAvg(1, this::addNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "50", "Add", add50);

        loadRoster(roster50);
        double remove50 = benchmarkRunner.runSpeedTestAndGetAvg(1, this::removeFromFrontNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "50", "Remove", remove50);

        loadRoster(roster50);
        double search50 = benchmarkRunner.runSpeedTestAndGetAvg(1, this::searchNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "50", "Search", search50);

        resetRoster();

        //****************************************************************************************************

        // Test with 500 players
        loadRoster(roster500);
        double add500 = benchmarkRunner.runSpeedTestAndGetAvg(1, this::addNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "500", "Add", add500);

        loadRoster(roster500);
        double remove500 = benchmarkRunner.runSpeedTestAndGetAvg(1, this::removeFromFrontNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "500", "Remove", remove500);

        loadRoster(roster500);
        double search500 = benchmarkRunner.runSpeedTestAndGetAvg(1, this::searchNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "500", "Search", search500);

        resetRoster();

        //****************************************************************************************************

        // Test with 5000 players
        loadRoster(roster5000);
        double add5000 = benchmarkRunner.runSpeedTestAndGetAvg(1, this::addNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "5000", "Add", add5000);

        loadRoster(roster5000);
        double remove5000 = benchmarkRunner.runSpeedTestAndGetAvg(1, this::removeFromFrontNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "5000", "Remove", remove5000);

        loadRoster(roster5000);
        double search5000 = benchmarkRunner.runSpeedTestAndGetAvg(1, this::searchNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "5000", "Search", search5000);

        System.out.println("========================================\n");

        resetRoster();
    }

    public static void main(String[] args) throws IOException {
        Results results = new Results();
        results.runAllExperiments();
    }
}

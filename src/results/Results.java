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

    BenchmarkRunner benchmarkRunner = new BenchmarkRunner();
    ArrayStore<Player> roster = new ArrayStore<>(Player.class,16);
    ArrayStore<Player> tempForRemove;  // Class-level variable

    public Results(){
    }

    public void load50Players() throws IOException {
        RosterManager rosterManager = new RosterManager();
        rosterManager.loadPlayerData("data/seahawks_roster_50.csv");
        roster = rosterManager.getPlayerData();
    }

    public void load500Players() throws IOException {
        RosterManager rosterManager = new RosterManager();
        rosterManager.loadPlayerData("data/seahawks_roster_500.csv");
        roster = rosterManager.getPlayerData();
    }

    public void load5000Players() throws IOException {
        RosterManager rosterManager = new RosterManager();
        rosterManager.loadPlayerData("data/seahawks_roster_5000.csv");
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

    public void runAllExperiments() throws IOException {
        System.out.println("\n========== Benchmark Results ==========\n");
        System.out.printf("%-10s %-15s %-15s%n", "Size", "Operation", "Avg Time (ms)");
        System.out.println("----------------------------------------");

        // Test with 50 players
        load50Players();
        double add50 = benchmarkRunner.runSpeedTestAndGetAvg(runs, this::addNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "50", "Add", add50);

        load50Players();
        setupRemoveTest();
        double remove50 = benchmarkRunner.runSpeedTestAndGetAvg(removeRuns, this::removeFromFrontNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "50", "Remove", remove50);

        load50Players();
        double search50 = benchmarkRunner.runSpeedTestAndGetAvg(runs, this::searchNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "50", "Search", search50);

        // Test with 500 players
        load500Players();
        double add500 = benchmarkRunner.runSpeedTestAndGetAvg(runs, this::addNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "500", "Add", add500);

        load500Players();
        setupRemoveTest();
        double remove500 = benchmarkRunner.runSpeedTestAndGetAvg(removeRuns, this::removeFromFrontNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "500", "Remove", remove500);

        load500Players();
        double search500 = benchmarkRunner.runSpeedTestAndGetAvg(runs, this::searchNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "500", "Search", search500);

        // Test with 5000 players
        load5000Players();
        double add5000 = benchmarkRunner.runSpeedTestAndGetAvg(runs, this::addNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "5000", "Add", add5000);

        load5000Players();
        setupRemoveTest();
        double remove5000 = benchmarkRunner.runSpeedTestAndGetAvg(removeRuns, this::removeFromFrontNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "5000", "Remove", remove5000);

        load5000Players();
        double search5000 = benchmarkRunner.runSpeedTestAndGetAvg(runs, this::searchNTimes);
        System.out.printf("%-10s %-15s %-15.1f%n", "5000", "Search", search5000);

        System.out.println("========================================\n");
    }

    public static void main(String[] args) throws IOException {
        Results results = new Results();
        results.runAllExperiments();
    }
}
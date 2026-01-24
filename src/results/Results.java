package results;

import benchmark.BenchmarkRunner;
import manager.RosterManager;
import types.Player;
import util.ArrayStore;

import java.io.IOException;

public class Results {

    public static int initCapacity = 16;

    BenchmarkRunner benchmarkRunner = new BenchmarkRunner();
    ArrayStore<Player> roster = new ArrayStore<>(Player.class,16);
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

    public void removeFromFrontNTimes(){
        while (roster.size() > 0) {
            roster.removeAtIndex(0);
        }
    }

    public void runSpeedTestWith50Adds() throws IOException{
        load50Players();
        benchmarkRunner.runSpeedTest(1, this::addNTimes);
    }

    public void runSpeedTestWith500Adds() throws IOException{
        load500Players();
        benchmarkRunner.runSpeedTest(1, this::addNTimes);
    }

    public void runSpeedTestWith5000Adds() throws IOException{
        load5000Players();
        benchmarkRunner.runSpeedTest(1, this::addNTimes);
    }

    public void runSpeedTestWith5000Removes() throws IOException{
        load5000Players();
        benchmarkRunner.runSpeedTest(1, this::removeFromFrontNTimes);
    }





}

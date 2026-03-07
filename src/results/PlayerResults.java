package results;

import manager.HashableManager;
import manager.PlayerManager;
import types.PlayerEnhanced;

import java.io.IOException;

public final class PlayerResults extends HashTableBenchMark<PlayerEnhanced, PlayerManager> {

    final static String PLAYER_50 = "data/seahawks_players_50.csv";
    final static  String PLAYER_500 = "data/seahawks_players_500.csv";
    final static String PLAYER_5000 = "data/seahawks_players_5000.csv";

    public PlayerResults(
            PlayerManager theManager,
            ExperimentFormat theExperimentFormat){
        super(PlayerEnhanced.class, theManager,theExperimentFormat);
    }


    @Override
    public void runAllExperiments() throws IOException {

        // Drill 50;
        loadData(PLAYER_50);
        addExperimentResult(testAdd("add"));
        addExperimentResult(testRemove("remove"));
        // Drill 500;
        loadData(PLAYER_500);
        addExperimentResult(testAdd("add"));
        addExperimentResult(testRemove("remove"));
        // Drill 5000;
        loadData(PLAYER_5000);
        addExperimentResult(testAdd("add"));
        addExperimentResult(testRemove("remove"));

        printResults();
    }

    public static void main(String[] args) throws IOException {
        PlayerManager PM = new PlayerManager();
        PlayerResults results = new PlayerResults(PM, ExperimentFormat.BENCHMARK_NO_OPS);
        results.runAllExperiments();
    }

}

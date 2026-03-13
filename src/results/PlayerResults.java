package results;

import manager.HashableManager;
import manager.PlayerManager;
import types.PlayerEnhanced;
import types.Position;
import util.Entry;

import java.io.IOException;

public final class PlayerResults extends HashTableBenchMark<PlayerEnhanced, PlayerManager> {

    final static String PLAYER_50 = "data/seahawks_players_50.csv";
    final static  String PLAYER_500 = "data/seahawks_players_500.csv";
    final static String PLAYER_5000 = "data/seahawks_players_5000.csv";

    private final  PlayerEnhanced notFindable = new PlayerEnhanced(1001,
            "Not findable", Position.QB,
            1,1,false);


    public PlayerResults(
            PlayerManager theManager,
            ExperimentFormat theExperimentFormat){
        super(PlayerEnhanced.class, theManager,theExperimentFormat);
    }

    /**
     * Searches a hash table of size n, n times for an
     * item that does not exist.
     */
    public void searchNTimes() {
        for (int i = 0; i < myTestContainer.size(); i++) {
            myManager.searchById(notFindable);
        }
    }


    @Override
    public void runAllExperiments() throws IOException {

        String[] csvFiles = {PLAYER_50, PLAYER_500, PLAYER_5000};

        for (String csvFile : csvFiles) {
            loadData(csvFile);
            addExperimentResult(testAdd("Insert"));
            addExperimentResult(testSearch("Search", this::searchNTimes));
            addExperimentResult(testRemove("Remove"));
        }

        printResults();
    }

    public static void main(String[] args) throws IOException {
        PlayerManager PM = new PlayerManager();
        PlayerResults results = new PlayerResults(PM, ExperimentFormat.BENCHMARK_MAP);
        results.runAllExperiments();
    }

}
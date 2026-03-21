package results.HashTableResults;

import manager.HashTableManager.HashTableManager;
import manager.HashTableManager.PlayerMapManager;
import results.ExperimentFormat;
import types.PlayerEnhanced;
import types.Position;
import util.Dictionary;
import util.HashTable;

import java.io.IOException;
import java.util.function.Supplier;

public class PlayerMapResults
        extends HashTableResults<
            PlayerEnhanced,
            Dictionary<Integer, PlayerEnhanced>,
            PlayerMapManager> {

    final static String PLAYER_50 = "data/seahawks_players_50.csv";
    final static  String PLAYER_500 = "data/seahawks_players_500.csv";
    final static String PLAYER_5000 = "data/seahawks_players_5000.csv";


    public PlayerMapResults(PlayerMapManager thePlayerMapManagerInstance, ExperimentFormat theExperimentFormat) {
        super(
                PlayerEnhanced.class,
                thePlayerMapManagerInstance,
                theExperimentFormat,
                new PlayerEnhanced(0,"dummy", Position.QB, 0, 0, false)
        );
    }

    @Override
    public void runAllExperiments() throws IOException {

        String[] csvData = {PLAYER_50, PLAYER_500, PLAYER_5000};

        for (String csv : csvData) {
            loadData(csv);
            addExperimentResult(testAddNTimes("add"));
            addExperimentResult(testRemoveNTimes("delete"));
        }

        printResults();
    }

    public static void main(String[] args) throws IOException {
        PlayerMapManager PM = new PlayerMapManager(()-> new HashTable<>(Integer.class,PlayerEnhanced.class));
        PlayerMapResults results = new PlayerMapResults(PM, ExperimentFormat.BENCHMARK_MAP);
        results.runAllExperiments();
    }
}

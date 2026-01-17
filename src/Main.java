import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class Main {
    private static final BufferedReader reader =
            new BufferedReader( new InputStreamReader(System.in));

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    static  void main() throws IOException {
        DataLoader loader = new DataLoader();
        boolean running = true;
        while (running) {
            printMenu();
            String choice = reader.readLine().trim();

            switch (choice) {
                case "1" -> {
                    loader.loadPlayers("data/seahawks_players.csv");
                    loader.printData(loader.playerData);
                }
                case "2" -> {
                    loader.loadDrills("data/seahawks_drills.csv");
                    loader.printData(loader.drillData);
                }
                case "3" -> {
                    loader.loadTransactions("data/seahawks_transactions.csv");
                    loader.printData(loader.transactionData);
                }
                case "4" -> {
                    loader.loadPlayers("data/seahawks_players.csv");
                    loader.loadDrills("data/seahawks_drills.csv");
                    loader.loadTransactions("data/seahawks_transactions.csv");
                    loader.printData(loader.playerData);
                    loader.printData(loader.drillData);
                    loader.printData(loader.transactionData);
                }
                case "5" -> running = false;
                default -> {
                    logger.info("""
                            Unsupported Option
                            Options are:
                            """);
                    printMenu();
                }
            }
        }
    }

    private static void printMenu(){
        logger.info("""
                Seahawks Data Options
                =====================
                1) load players
                2) load drills
                3) load transactions
                4) load all
                5) exit
                """);
    }

}

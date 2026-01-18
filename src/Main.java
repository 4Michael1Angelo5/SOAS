import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * The entry point of the application. Presents a simple CLI menu
 * to interact with different statistics from the Seattle Seahawks.
 * @author Chris Chun
 * @author Ayush
 * @version 1.1
 */
public class Main {


    static {
        // attempt to use logging properties file
        try (var is = Main.class.getClassLoader().getResourceAsStream("logging.properties")) {
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
    private static final BufferedReader reader =
            new BufferedReader( new InputStreamReader(System.in));

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {

        DataLoader loader = new DataLoader();
        BenchmarkRunner benchmark = new BenchmarkRunner();

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
                case "5" -> {
                    // Benchmark - file paths come from Main, not hardcoded in BenchmarkRunner
                    benchmark.runSpeedTest(10, () -> {
                        try {
                            loader.loadPlayers("data/seahawks_players.csv");
                            loader.loadDrills("data/seahawks_drills.csv");
                            loader.loadTransactions("data/seahawks_transactions.csv");
                        } catch (IOException e) {
                            logger.severe("Error: " + e.getMessage());
                        }
                    }, "Loading All Data");
                }
                case "6" -> {
                    // Show operation counter report
                    // @TODO create option to run task and have the report generated at the same time.
                    loader.myCounter.printReport();
                }

                case "7" -> running = false;
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
                5) run benchmark
                6) operation counts
                7) exit
                """);
    }
}

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


    /**
     * Reader to read user inputs from the command line.
     */
    private static final BufferedReader reader =
            new BufferedReader( new InputStreamReader(System.in));


    /**
     * Logger for all your logging needs
     */
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {

        BenchmarkRunner benchmark = new BenchmarkRunner();
        RosterManager rosterManager = new RosterManager();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = reader.readLine().trim();

            switch (choice) {
                case "1" -> {
                    rosterManager.addCsvData("data/seahawks_players.csv");
                }
                case "2" -> {
                    Player newPlayer = new Player(101,
                            "Ayush",
                            "qb",
                            91,
                            199);
                    rosterManager.addPlayer(newPlayer);
                }
                case "3" -> {
                    try {
                        Player p = rosterManager.removeById(10);
                        logger.info("Removed: " + p.toString() + "From the roster.");
                    } catch (RuntimeException e) {
                        logger.warning("could not remove the player with id: " + 10 + ",id not found");
                    }
                }
                case "4" -> {
                    try {

                        rosterManager.updateStats(101, 109);
                        logger.info("Succesfully updated player stats");

                    }catch(RuntimeException e) {
                        logger.warning(
                                "could not update player with id, "
                                + 101
                                + ", because the player is not in the roster");
                    }
                }
                case "5" -> {
                    int index = rosterManager.findByName("Ayush");
                    if (index == -1) {
                        logger.warning("could not find Ayush in roster");
                    } else {
                        logger.info("found Ayush in the roster");
                    }
                }
                case "6" -> {
                    rosterManager.printRoster();
                }
                case "7" -> {
                    //@TODO need to run bench mark tests.
                    running = false;
                }
                case "0" -> running = false;
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
                1. Load roster
                2. Add player
                3. Remove player
                4. Update stats
                5. Search
                6. Print roster
                7. Run benchmark
                0. Exit
                """);
    }
}

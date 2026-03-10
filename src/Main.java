import manager.PlayerManager;
import types.PlayerEnhanced;
import types.Position;
import util.ArrayStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Logger;


/**
 * The entry point of the application. Presents a simple CLI menu
 * to interact with different statistics from the Seattle Seahawks.
 * @author Chris Chun
 * @author Ayush
 * @version 1.4
 */
public class Main {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_LAVENDER = "\u001B[38;5;147m";

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

    public static final String  PLAYERS_50 = "data/seahawks_players_50.csv";

    public static final
    PlayerEnhanced AYUSH = new PlayerEnhanced(
            97,
            "Ayush",
            Position.QB,
            1000,
            110,
            false);

    public static void main(String[] args) throws IOException {

        PlayerManager PM = new PlayerManager();

        boolean running = true;

        while (running) {
            printMenu();
            String choice = reader.readLine().trim();

            switch (choice) {
                case "1" -> {
                    PM.loadCsvData(PLAYERS_50);
                    logger.info("Successfully loaded player data.");
                }
                case "2" -> {
                    PM.addPlayer(AYUSH);
                    logger.info(
                            ANSI_LAVENDER
                            + "Successfully added: " + AYUSH
                            + ANSI_RESET);
                }
                case "3" -> {
                    PlayerEnhanced player = PM.searchById(97);
                    if (!Objects.isNull(player)) {
                        logger.info(
                                ANSI_LAVENDER
                                + "Found: " + player
                                + ANSI_RESET);
                    }else {
                        logger.info("Cound not find player with id: " + 97);
                    }
                }
                case "4" -> {
                    int newYards = AYUSH.yards() + 110;
                    int newTouchDowns = AYUSH.touchdowns() + 3;
                    PlayerEnhanced newPlayerStats = new PlayerEnhanced(
                            97,
                            "Ayush",
                            Position.QB,
                            newYards,
                            newTouchDowns,
                            false);
                    try {
                        PM.updatePlayerStats(newPlayerStats);
                        logger.info(
                                ANSI_LAVENDER
                                    + "Successfully updated " + AYUSH.name()
                                    + "'s stats to new yards: " + newYards
                                    + " and touchdowns to: " + newTouchDowns
                                    + ANSI_RESET);
                    } catch (NoSuchElementException e) {
                        logger.warning(
                                ANSI_LAVENDER
                                + "Was not able to update " + AYUSH.name() + "'s stats."
                                + ANSI_RESET

                        );
                    }
                }
                case "5" -> {
                    PlayerEnhanced removed = PM.removePlayer(AYUSH);
                    if (removed != null) {
                        logger.info(
                                ANSI_LAVENDER
                                + "Successfuly removed: " + removed
                        );
                    }else {
                        logger.warning("Could not remove " + AYUSH);
                    }
                }
                case "6" -> {
                    ArrayStore<PlayerEnhanced> players = PM.listPlayersByPosition(Position.QB);
                    logger.info("Successfully found all the quarterbacks");
                    for (PlayerEnhanced player: players) {
                        logger.info(player.toString());
                    }
                }
                case "7" -> {
                    int injuredCount =  PM.countInjuredPlayers();
                    logger.info(ANSI_LAVENDER + "Successfully counted all injured players:" + ANSI_RESET);
                    logger.info(ANSI_LAVENDER + "Total players injured: " + injuredCount + ANSI_RESET);
                }
                case "8" -> {
                    logger.info(ANSI_LAVENDER + "Successfully counted total yards by position" + ANSI_RESET);
                    logger.info(ANSI_LAVENDER + PM.computeTotalYardsByPosition().toString() + ANSI_RESET);
                    logger.info(
                            ANSI_LAVENDER +
                            "Total yards for Quarterback position was: "
                            + PM.getTotalYardsByPosition(Position.QB)
                            + ANSI_RESET);
                }
                case "0" -> running = false;
                default ->
                    logger.info("""
                            \nUnsupported Option
                            Options are:
                            """);

            }
        }
    }

    private static void printMenu(){
        logger.info( ANSI_GREEN + """ 
                Seahawks Data Options
                =====================
                1. Load Players
                2. Insert Player
                3. Search by Player ID
                4. Update Player Stats
                5. Remove Player
                6. List Players by Position
                7. Count Injured Players
                8. Compute Total Yards by Position
                0. Exit
                """ + ANSI_RESET);
    }
}

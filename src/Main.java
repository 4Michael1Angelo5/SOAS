import manager.DrillManager;
import manager.FanTicketQueue;
import manager.UndoManager;
import results.FanTicketResults;
import results.UndoResults;
import simulator.Simulator;
import types.Action;
import types.Drill;
import types.FanRequest;
import util.ArrayStack;
import util.BinaryHeapPQ;
import util.DataContainer;
import util.LinkedQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Supplier;
import java.util.logging.Logger;


/**
 * The entry point of the application. Presents a simple CLI menu
 * to interact with different statistics from the Seattle Seahawks.
 * @author Chris Chun
 * @author Ayush
 * @version 1.3
 */
public class Main {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static final  Supplier<DataContainer<Action>> ACTION_STACK = () -> new ArrayStack<>(Action.class);
    public static final Supplier<DataContainer<FanRequest>> FAN_QUEUE = LinkedQueue::new;

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

    private static final Simulator mySimulator = new Simulator();

    public static final String ACTIONS50 = "data/seahawks_undo_actions_50.csv";

    public static final String  FAN50 = "data/seahawks_fan_queue_50.csv";

    public static void main(String[] args) throws IOException {

        Supplier<DataContainer<Drill>> drillContSup = ()->new BinaryHeapPQ<>(Drill.class);
        DrillManager DM = new DrillManager(drillContSup);

        boolean running = true;

        while (running) {
            printMenu();
            String choice = reader.readLine().trim();

            switch (choice) {
                case "1" -> {
                    // load actions
                    DM.loadCsvData("data/seahawks_drills_50.csv");
                    DM.printData();

                }
                case "2" -> {
                    // update comparator
                    DM.upDateComparator(DM.fairSort());
                    DM.printData();

                }
                case "0" -> running = false;
                default -> {
                    logger.info("""
                            Unsupported Option
                            Options are:
                            """);
                }
            }
        }
    }

    private static void printMenu(){
        logger.info("""
                Seahawks Data Options
                =====================
                1. Load Drills
                2. Update Comparator
                0. Exit
                """);
    }
}

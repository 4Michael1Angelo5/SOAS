import manager.FanTicketQueue;
import manager.UndoManager;
import results.FanTicketResults;
import results.UndoResults;
import simulator.Simulator;
import types.Action;
import types.FanRequest;
import util.ArrayStack;
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

    public static void main(String[] args) throws IOException {

        UndoManager undoManager = new UndoManager(ACTION_STACK);
        FanTicketQueue fanRequestManager = new FanTicketQueue(FAN_QUEUE);

        UndoResults undoResults = new UndoResults(undoManager,ACTION_STACK);
        FanTicketResults fanTickectResults = new FanTicketResults(fanRequestManager, FAN_QUEUE);

        boolean running = true;

        while (running) {
            printMenu();
            String choice = reader.readLine().trim();

            switch (choice) {
                case "1" -> {
                    // load actions
                    undoManager.loadCsvData("data/seahawks_undo_actions_50.csv");
                    logger.info(ANSI_GREEN+ "Successfully loaded actions" + ANSI_RESET);
                }
                case "2" -> {
                    // load fan requests
                    fanRequestManager.loadCsvData("data/seahawks_fan_queue_5000.csv");
                    logger.info(ANSI_GREEN+ "Successfully loaded fan requests " + ANSI_RESET);
                }
                case "3" -> {
                    //  Undo Action (pop)
                    Action removed = undoManager.remove();

                    logger.info(ANSI_GREEN+ "Successfully popped "
                            +  removed + " from the actions stack" + ANSI_RESET);
                }
                case "4" -> {
                    // Dequeue Request
                    FanRequest removed = fanRequestManager.remove();
                    logger.info(ANSI_GREEN+ "Successfully dequeued "
                            +  removed + " from the fan request queue" + ANSI_RESET);
                }
                case "5" -> {

                    undoManager.printData();
                }
                case "6" -> {
                    fanRequestManager.printData();
                }
                case "7" -> {
                    // run all experiments
                    undoResults.runAllExperiments();
                    fanTickectResults.runAllExperiments();
                }
                case "8" -> mySimulator.runSimulation();
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
                1. Load Actions
                2. Load Fan Request
                3. Undo Action (pop)
                4. Dequeue Request
                5. Print Actions
                6. Print Fan Queue
                7. Run benchmark
                8. Bonus! Run Simulation.
                0. Exit
                """);
    }
}

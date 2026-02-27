import manager.DrillManager;
import simulator.DrillSimulator;
import simulator.UndoSimulator;
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

    public static final String  DRILLS_50 = "data/seahawks_drills_50.csv";

    public static void main(String[] args) throws IOException {

        Supplier<DataContainer<Drill>> drillContSup = ()->new BinaryHeapPQ<>(Drill.class);
        DrillManager DM = new DrillManager(drillContSup);

        boolean running = true;

        while (running) {
            printMenu();
            String choice = reader.readLine().trim();

            switch (choice) {
                case "1" -> {
                    // load csv
                    DM.loadCsvData(DRILLS_50);
                    logger.info(ANSI_GREEN + "Successfully loaded Seahawks data from CSV.\n" + ANSI_RESET);

                }
                case "2" -> {
                    // Add a drill
                    Drill newDrill = new Drill(-1,
                            "Practice Binary Trees",
                            1000,
                            60,
                            100,
                            1);
                    DM.addData(newDrill);
                    logger.info(ANSI_GREEN + "Successfully added new drill: \n" +
                       newDrill.toStringZ() + ANSI_RESET);

                }
                case "3" -> {
                    // peek
                    Drill nextDrill = DM.peekNextDrill();
                    logger.info(ANSI_GREEN + "The next drill to run is" + ANSI_RESET);
                    logger.info(ANSI_GREEN + nextDrill.toString() + ANSI_RESET);

                }
                case "4" -> {
                    // run
                    Drill removed = DM.removeData();
                    logger.info(ANSI_GREEN + "Successfully processed: " + removed.toString() + "\n" + ANSI_RESET);

                }
                case "5" -> {
                    // print
                    DM.printData();
                }
                case "6" -> {
                    // update comparator to sort by shortest drill first.
                    DM.upDateComparator((a,b) -> a.duration_min() - b.duration_min());
                    logger.info(ANSI_GREEN + "Successfully updated comparator\n" + ANSI_RESET);
                }
                case "7" -> {
                    // run simulation:
                    DrillSimulator drillSimulator = new DrillSimulator();
                    drillSimulator.runSimulation();
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
        logger.info( ANSI_GREEN + """ 
                Seahawks Data Options
                =====================
                1. Load drills from a CSV file
                2. Add a drill
                3. Peek next drill (without removing)
                4. Run next drill (remove)
                5. Print the next N scheduled drills (simulate “preview”)
                6. Update Comparator (Shortest Drill First)
                7. Run a simulation and output metrics (wait time/fairness)
                """ + ANSI_RESET);
    }
}

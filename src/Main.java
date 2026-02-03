import manager.TransactionFeed;
import results.TransactionResults;
import types.Transaction;
import util.DataContainer;
import util.SinglyLinkedList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.logging.Logger;


/**
 * The entry point of the application. Presents a simple CLI menu
 * to interact with different statistics from the Seattle Seahawks.
 * @author Chris Chun
 * @author Ayush
 * @version 1.2
 */
public class Main {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

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
        TransactionResults tests = new TransactionResults();
        Supplier<DataContainer<Transaction>> supplier = SinglyLinkedList::new;
        TransactionFeed transactionFeed = new TransactionFeed(supplier);

        boolean running = true;

        while (running) {
            printMenu();
            String choice = reader.readLine().trim();

            switch (choice) {
                case "1" -> {
                    // resets the data back to original csv.
                    transactionFeed.loadTransactionData("data/seahawks_transactions.csv");
                    logger.info(ANSI_GREEN+ "Successfully loaded transactions" + ANSI_RESET);
                }
                case "2" -> {
                    Transaction breakingNews =
                            new Transaction(1271, "Injury", "Chris C.", "2026-01-14");
                    // add transaction to the front
                    transactionFeed.addTransactionFront(breakingNews);
                    logger.info(ANSI_GREEN+ "Successfully added "
                            +  breakingNews + " to the front of the transaction feed" + ANSI_RESET);
                }
                case "3" -> {
                    Transaction olderNews =
                            new Transaction(111, "Trade", "Jerry Rice", "2026-01-10");
                    // add transaction to the end
                    transactionFeed.addTransactionRear(olderNews);
                    logger.info(ANSI_GREEN+ "Successfully added "
                            +  olderNews + " to the end of the transaction feed" + ANSI_RESET);
                }
                case "4" -> {
                    Transaction transaction =
                            new Transaction(89, "Injury", "Ayush", "2025-01-10");
                    try {

                        transactionFeed.insertTransaction(3, transaction);
                        logger.info(ANSI_GREEN+ "Successfully added "
                                +  transaction + " at index 3 to the transaction feed" + ANSI_RESET);

                    } catch(IllegalArgumentException e) {
                        logger.warning(e.toString());
                        logger.warning("Cannot insert at index 3 because the transaction feed only has, "
                                +  transactionFeed.getTransactionData().size() + " transactions");
                    }

                }
                case "5" -> {
                    try {
                        Transaction removed  = transactionFeed.removeFront();
                        logger.info(ANSI_GREEN+ "Successfully removed "
                                +  removed + " to the end of the transaction feed" + ANSI_RESET);
                    } catch (NoSuchElementException e) {
                        logger.warning(e.toString());
                        logger.warning(" Cannot remove transaction from the feed because it is empty");
                    }
                }
                case "6" -> {
                    transactionFeed.printTransactions();
                }
                case "7" -> {
                    tests.runAllExperiments();
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
                1. Load transaction feed
                2. Add breaking news (front)
                3. Add older update (rear)
                4. Insert at position
                5. Remove transaction
                6. Print first N
                7. Run benchmark
                0. Exit
                """);
    }
}

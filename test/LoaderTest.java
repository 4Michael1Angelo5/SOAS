
import loader.DataLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import types.*;
import util.*;

import java.io.IOException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Chris Chun, Ayush
 * @version 1.3
 * Tests for loader class.
 */
public class LoaderTest {

    // remove the time stamp from the logger
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
    }
    // roster
    final static String roster50 = "data/seahawks_roster_50.csv";
    final static String roster500 = "data/seahawks_roster_500.csv";
    final static String roster5000 = "data/seahawks_roster_5000.csv";

    // transaction
    final static String trans50 = "data/seahawks_transactions_50.csv";
    final static String trans500 = "data/seahawks_transactions_500.csv";
    final static String trans5000 = "data/seahawks_transactions_5000.csv";

    //undo actions
    final static String undo50 = "data/seahawks_undo_actions_50.csv";
    final static String undo500 = "data/seahawks_undo_actions_500.csv";
    final static String undo5000 = "data/seahawks_undo_actions_5000.csv";

    // Fan Requests
    final static String fan50 = "data/seahawks_fan_queue_50.csv";
    final static String fan500 = "data/seahawks_fan_queue_500.csv";
    final static String fan5000 = "data/seahawks_fan_queue_5000.csv";

    private <T extends DataType> Supplier<DataContainer<T>>
    getSupplier(Class<T> theDataClass, String theContainerType){
        theContainerType = theContainerType.trim().toLowerCase();
        return switch (theContainerType) {
            case "sll" -> SinglyLinkedList::new;
            case "array" -> ()-> new ArrayStore<>(theDataClass,16);
            case "stack" -> ()-> new ArrayStack<>(theDataClass);
            case "queue" -> LinkedQueue::new;
            default -> throw new RuntimeException("unsported option");
        };
    }

    @Test
    @DisplayName("Player Data Loading Tests")
    void testPlayerLoading() {
        assertAll("Player Integrity",
                () -> {
                    DataLoader<Player> loader = new DataLoader<>(Player.class, getSupplier(Player.class, "array"));
                    assertDoesNotThrow(() -> loader.loadData("data/seahawks_players.csv"));
                    assertNotNull(loader.loadData("data/seahawks_players.csv"), "Player data should not be null");
                },
                () -> {
                    DataLoader<Player> loader = new DataLoader<>(Player.class, getSupplier(Player.class, "sll"));
                    assertDoesNotThrow(() -> loader.loadData("data/seahawks_players.csv"));
                }
        );
    }

    @Test
    @DisplayName("Drill Data Loading Tests")
    void testDrillLoading() {
        assertAll("Drill Integrity",
                () -> {
                    DataLoader<Drill> loader = new DataLoader<>(Drill.class, getSupplier(Drill.class, "array"));
                    assertDoesNotThrow(() -> loader.loadData("data/seahawks_drills_50.csv"));
                },
                () -> {
                    DataLoader<Drill> loader = new DataLoader<>(Drill.class, getSupplier(Drill.class, "sll"));
                    assertDoesNotThrow(() -> loader.loadData("data/seahawks_drills_50.csv"));
                    assertNotNull(loader.loadData("data/seahawks_drills_50.csv"), "Drill data should not be null");
                }
        );
    }

    @Test
    @DisplayName("Transaction Data Loading Tests")
    void testTransactionLoading() {
        assertAll("Transaction Integrity",
                () -> {
                    DataLoader<Transaction> loader = new DataLoader<>(Transaction.class, getSupplier(Transaction.class, "array"));
                    assertDoesNotThrow(() -> loader.loadData("data/seahawks_transactions.csv"));
                },
                () -> {
                    DataLoader<Transaction> loader = new DataLoader<>(Transaction.class, getSupplier(Transaction.class, "sll"));
                    assertDoesNotThrow(() -> loader.loadData("data/seahawks_transactions.csv"));
                },
                () -> {
                    DataLoader<Transaction> loader = new DataLoader<>(Transaction.class, getSupplier(Transaction.class, "queue"));
                    assertNotNull(loader.loadData("data/seahawks_transactions.csv"), "Transaction data should not be null");
                }
        );
    }

    @Test
    @DisplayName("Action (Undo) Data Loading Tests")
    void testActionLoading() {
        assertAll("Action Integrity",
                () -> {
                    DataLoader<Action> loader = new DataLoader<>(Action.class, getSupplier(Action.class, "stack"));
                    assertDoesNotThrow(() -> loader.loadData(undo50));
                },
                () -> {
                    DataLoader<Action> loader = new DataLoader<>(Action.class, getSupplier(Action.class, "array"));
                    assertDoesNotThrow(() -> loader.loadData(undo500));
                },
                () -> {
                    DataLoader<Action> loader = new DataLoader<>(Action.class, getSupplier(Action.class, "sll"));
                    assertDoesNotThrow(() -> loader.loadData(undo5000));
                }
        );
    }

    @Test
    @DisplayName("FanRequest Data Loading Tests")
    void testFanRequestLoading() {
        assertAll("FanRequest Integrity",
                () -> {
                    DataLoader<FanRequest> loader = new DataLoader<>(FanRequest.class, getSupplier(FanRequest.class, "array"));
                    assertDoesNotThrow(() -> loader.loadData(fan5000));
                },
                // Negative tests: Ensuring FanRequest loader rejects Action data
                () -> {
                    DataLoader<FanRequest> loader = new DataLoader<>(FanRequest.class, getSupplier(FanRequest.class, "stack"));
                    assertThrows(IllegalArgumentException.class, () -> loader.loadData(undo50));
                },
                () -> {
                    DataLoader<FanRequest> loader = new DataLoader<>(FanRequest.class, getSupplier(FanRequest.class, "array"));
                    assertThrows(IllegalArgumentException.class, () -> loader.loadData(undo500));
                },
                () -> {
                    DataLoader<FanRequest> loader = new DataLoader<>(FanRequest.class, getSupplier(FanRequest.class, "sll"));
                    assertThrows(IllegalArgumentException.class, () -> loader.loadData(undo5000));
                }
        );
    }

    @Test
    public void testErrorHandling() {
        DataLoader<Player> loader =
                new DataLoader<>(Player.class, getSupplier(Player.class, "array"));

        assertAll("Test Error Handling",

                () -> assertDoesNotThrow(
                        () -> loader.loadData("data/seahawks_players.csv"),
                        "Should not throw errors for valid CSV file path"),
                () -> assertThrows(IOException.class, ()-> loader.loadData("bad path")),
                () -> assertThrows(IllegalArgumentException.class, ()-> loader.loadData("test/badFormatPlayers.csv")),
                () -> assertThrows(IllegalArgumentException.class, ()-> loader.loadData("test/empty.csv")),
                () -> assertNotNull(loader.loadData("data/seahawks_players.csv"), "Application should retain valid data even after invalid data loaded"),
                () -> assertEquals(Player.class.getName(), loader.loadData("data/seahawks_players.csv").get(0).getClass().getName(),
                        "Application should retain valid data even after invalid data loaded"),
                () -> assertThrows(IllegalArgumentException.class,
                        ()-> new DataLoader<>(null, null))

        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDataIntegrity() {
        // Grouping file paths by their expected sizes
        String[][] testGroups = {
                {roster50, trans50, undo50, fan50},      // Expected size: 50
                {roster500, trans500, undo500, fan500},  // Expected size: 500
                {roster5000, trans5000, undo5000, fan5000} // Expected size: 5000
        };
        int[] expectedSizes = {50, 500, 5000};
        String[] containers = {"sll", "array", "stack", "queue"};

        assertAll("Integrity Checks", () -> {
            for (int i = 0; i < testGroups.length; i++) {
                int expected = expectedSizes[i];

                for (String path : testGroups[i]) {
                    // Cycle through containers so we don't just test 'array' every time
                    String containerType = containers[path.length() % containers.length];

                    Class<? extends DataType> targetClass = determineClassFromPath(path);

                    DataLoader loader = new DataLoader(targetClass, getSupplier(targetClass, containerType));
                    DataContainer<?> result = loader.loadData(path);

                    assertEquals(expected, result.size(),
                            String.format("Size mismatch for %s using %s", path, containerType));
                }
            }
        });
    }

    private <T extends DataType> DataLoader<T>  // return type
    createDataLoader(Class<T> theDataClass,
                     Supplier<DataContainer<T>> theSupplier) {
        return new DataLoader<>(theDataClass, theSupplier);
    }

    /** Helper to match the file path to the correct record type */
    private Class<? extends DataType> determineClassFromPath(String path) {
        if (path.contains("roster")) return Player.class;
        if (path.contains("transaction")) return Transaction.class;
        if (path.contains("undo")) return Action.class;
        if (path.contains("fan")) return FanRequest.class;
        throw new IllegalArgumentException("Unknown file type: " + path);
    }
}




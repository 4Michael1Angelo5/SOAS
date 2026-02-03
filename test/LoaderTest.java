
import loader.DataLoader;
import org.junit.jupiter.api.Test;
import types.DataType;
import types.Drill;
import types.Player;
import types.Transaction;
import util.ArrayStore;
import util.DataContainer;
import util.SinglyLinkedList;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 * Tests for loader class.
 */
public class LoaderTest {

    // remove the time stamp from the logger
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
    }

    private static final Supplier<DataContainer<Player>> playerSLL = SinglyLinkedList::new;

    private static final Supplier<DataContainer<Player>> playerArray = () -> new ArrayStore<>(Player.class,16);

    private static final Supplier<DataContainer<Transaction>> transSLL = SinglyLinkedList::new;

    private static final Supplier<DataContainer<Transaction>> transArray = () -> new ArrayStore<>(Transaction.class,16);

    private static final Supplier<DataContainer<Transaction>> drillsSLL = SinglyLinkedList::new;

    private static final Supplier<DataContainer<Transaction>> drillsArray = () -> new ArrayStore<>(Transaction.class,16);

    private final <T extends DataType> Supplier<DataContainer<T>>
    getSupplier(Class<T> theDataClass, String theContainerType){
        theContainerType = theContainerType.trim().toLowerCase();
        return switch (theContainerType) {
            case "sll" -> SinglyLinkedList::new;
            case "array" -> ()-> new ArrayStore<>(theDataClass,16);
            default -> throw new RuntimeException("unsported option");
        };
    }

    @Test
    public void testDataLoading() {
        assertAll("Loading Test",

                () -> {

                    DataLoader<Player> loader =
                            new DataLoader<>(Player.class, getSupplier(Player.class, "array"));

                    assertDoesNotThrow(() -> loader.loadData("data/seahawks_players.csv"),
                            "Should not throw errors for valid CSV file path");
                },
                () -> {

                    DataLoader<Player> loader =
                            new DataLoader<>(Player.class, getSupplier(Player.class, "sll"));

                    assertDoesNotThrow(() -> loader.loadData("data/seahawks_players.csv"),
                            "Should not throw errors for valid CSV file path");
                },
                () -> {

                    DataLoader<Drill> loader =
                            new DataLoader<>(Drill.class, getSupplier(Drill.class, "array"));

                    assertDoesNotThrow(() -> loader.loadData("data/seahawks_drills.csv"),
                            "Should not throw errors for valid CSV file path");
                },
                () -> {

                    DataLoader<Drill> loader =
                            new DataLoader<>(Drill.class, getSupplier(Drill.class, "sll"));

                    assertDoesNotThrow(() -> loader.loadData("data/seahawks_drills.csv"),
                            "Should not throw errors for valid CSV file path");
                },
                () -> {
                    DataLoader<Transaction> loader =
                            new DataLoader<>(Transaction.class, getSupplier(Transaction.class, "array"));

                    assertDoesNotThrow(() -> loader.loadData("data/seahawks_transactions.csv"),
                            "Should not throw errors for valid CSV file path");
                },
                () -> {
                    DataLoader<Transaction> loader =
                            new DataLoader<>(Transaction.class, getSupplier(Transaction.class, "sll"));

                    assertDoesNotThrow(() -> loader.loadData("data/seahawks_transactions.csv"),
                            "Should not throw errors for valid CSV file path");
                },
                () -> {
                    DataLoader<Player> loader =
                            new DataLoader<>(Player.class, getSupplier(Player.class, "array"));
                    assertNotNull(loader.loadData("data/seahawks_players.csv"),
                            "the player data should not be null");
                },
                () -> {
                    DataLoader<Drill> loader =
                            new DataLoader<>(Drill.class, getSupplier(Drill.class, "sll"));
                    assertNotNull(loader.loadData("data/seahawks_drills.csv"),
                            "the drill data should not be null");
                },
                () -> {
                    DataLoader<Drill> loader =
                            new DataLoader<>(Drill.class, getSupplier(Drill.class, "sll"));
                    assertNotNull(loader.loadData("data/seahawks_drills.csv"),
                            "the drill data should not be null");
                },
                () -> {
                    DataLoader<Transaction> loader =
                            new DataLoader<>(Transaction.class, getSupplier(Transaction.class, "sll"));
                    assertNotNull(loader.loadData("data/seahawks_transactions.csv"),
                            "the transaction data should not be null");
                }

        );

    }

    @Test
    public void testErrorHandling() {
        DataLoader<Player> loader =
                new DataLoader<Player>(Player.class, getSupplier(Player.class, "array"));

        assertAll("Test Error Handling",

                () -> assertDoesNotThrow(() -> loader.loadData("data/seahawks_players.csv"),
                        "Should not throw errors for valid CSV file path"),
                () -> assertThrows(IOException.class, ()-> loader.loadData("bad path")),
                () -> assertThrows(IllegalArgumentException.class, ()-> loader.loadData("test/badFormatPlayers.csv")),
                () -> assertThrows(IllegalArgumentException.class, ()-> loader.loadData("test/empty.csv")),
                () -> assertNotNull(loader.loadData("data/seahawks_players.csv"), "Application should retain valid data even after invalid data loaded"),
                () -> assertEquals(Player.class.getName(), loader.loadData("data/seahawks_players.csv").get(0).getClass().getName(),
                        "Application should retain valid data even after invalid data loaded")

        );
    }
}




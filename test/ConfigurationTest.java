import static org.junit.jupiter.api.Assertions.*;

import manager.TransactionFeed;
import org.junit.jupiter.api.Test;
import manager.RosterManager;
import types.Player;
import types.Transaction;
import util.ArrayStore;
import util.DataContainer;
import util.SinglyLinkedList;

import java.io.IOException;
import java.util.function.Supplier;

public class ConfigurationTest {

    // Standard Suppliers
    private final Supplier<DataContainer<Player>> playArraySup = () -> new ArrayStore<>(Player.class, 16);
    private final Supplier<DataContainer<Player>> playSllSup = SinglyLinkedList::new;

    private final Supplier<DataContainer<Transaction>> transArraySup = () -> new ArrayStore<>(Transaction.class, 16);
    private final Supplier<DataContainer<Transaction>> transSllSup = SinglyLinkedList::new;

    /**
     * Test Case: Implementation Lockdown
     * Verifies that if the Manager is set up as an ArrayStore, it stays an ArrayStore.
     */
    @Test
    void testKnowsValidContainers() {
        RosterManager rmArray = new RosterManager(playArraySup);
        RosterManager rmSll = new RosterManager(playSllSup);

        TransactionFeed tfArray = new TransactionFeed(transArraySup);
        TransactionFeed tfSll = new TransactionFeed(transSllSup);


        assertAll("Test Manager Container Instantiation",
                () -> assertFalse(rmArray.isValidContainer(playSllSup.get()),
                        "Once a manager is instantiated with a array container, " +
                                "it should not allow use of other container types."),
                () -> assertFalse(rmSll.isValidContainer(playArraySup.get()),
                        "Once a manager is instantiated with a linked list container, " +
                                "it should not allow use of other container types."),
                () -> assertFalse(tfArray.isValidContainer(transSllSup.get()),
                        "Once a manager is instantiated with a array container, " +
                                "it should not allow use of other container types."),
                () -> assertFalse(tfSll.isValidContainer(transArraySup.get()),
                        "Once a manager is instantiated with a linked list container, " +
                                "it should not allow use of other container types.")
                );
    }

    /**
     * Test Case: The Null Supplier
     * Fail-fast check for constructor parameters.
     */
    @Test
    void testNullSupplierHandling() {
        assertThrows(RuntimeException.class, () -> {
            new RosterManager(null);
        }, "Constructing with a null supplier should fail immediately.");
    }

    /**
     * Test Case: Data Reloading (Hard Reset)
     * Verifies that loading a second file completely clears the first one.
     */
    @Test
    void testDataOverwriteOnLoad() throws IOException {
        RosterManager rm = new RosterManager(playArraySup);

        // Manual add
        rm.addPlayer(new Player(1, "Old Player", "QB", 10, 100));
        assertEquals(1, rm.getPlayerData().size());

        // Now trigger a load (Assume you have a dummy small CSV)
        // This should trigger your 'Hard Reset' logic
        rm.loadCsvData("data/seahawks_players.csv");

        // The 'Old Player' should be gone.
        assertFalse(rm.getPlayerData().get(0).name().equals("Old Player"),
                "Existing data should be completely wiped upon new CSV load.");
    }

    /**
     * Test Case: Missing File
     * Ensures the manager bubbles up the IO error correctly.
     */
    @Test
    void testMissingFileThrowsIO() {
        RosterManager rm = new RosterManager(playArraySup);
        assertThrows(IOException.class, () -> {
            rm.loadCsvData("non_existent_file.csv");
        });
    }

    /**
     * Test Case: Generic Type Token Safety
     * Ensures the Manager knows what class it's supposed to hold.
     */
    @Test
    void testTypeTokenIntegrity() {
        RosterManager rm = new RosterManager(playSllSup);
        // This is a sanity check that the 'myDataClass' field was set correctly
        assertEquals(Player.class, rm.getDataClass(),
                "Manager must maintain a runtime reference to the class it manages.");
    }

    @Test
    void testDoesNotAllowNullAdd() {
        RosterManager rm = new RosterManager(playSllSup);
        assertThrows(IllegalArgumentException.class, ()-> rm.addData(null));
    }
}
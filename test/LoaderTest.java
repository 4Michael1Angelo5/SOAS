
import loader.DataLoader;
import org.junit.jupiter.api.Test;
import types.Player;

import java.io.IOException;

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


    @Test
    public void testDataLoading() {

        DataLoader loader = new DataLoader();

        assertAll("Loading Test",

                () -> assertDoesNotThrow(() -> loader.loadPlayers("data/seahawks_players.csv"),
                            "Should not throw errors for valid CSV file path"),
                () -> assertDoesNotThrow(() -> loader.loadDrills("data/seahawks_drills.csv"),
                            "Should not throw errors for valid CSV file path"),
                () -> assertDoesNotThrow(() -> loader.loadTransactions("data/seahawks_transactions.csv"),
                            "Should not throw errors for valid CSV file path"),
                () -> assertNotNull(loader.loadPlayers("data/seahawks_players.csv"), "the player data should not be null"),
                () -> assertNotNull(loader.loadDrills("data/seahawks_drills.csv"), "the drill data should not be null"),
                () -> assertNotNull(loader.loadTransactions("data/seahawks_transactions.csv"), "the transaction data should not be null")

        );

    }

    @Test
    public void testErrorHandling() {
        DataLoader loader = new DataLoader();

        assertAll("Test Error Handling",

                () -> assertDoesNotThrow(() -> loader.loadPlayers("data/seahawks_players.csv"),
                            "Should not throw errors for valid CSV file path"),
                () -> assertThrows(IOException.class, ()-> loader.loadDrills("bad path")),
                () -> assertThrows(IllegalArgumentException.class, ()-> loader.loadPlayers("test/badFormatPlayers.csv")),
                () -> assertThrows(IllegalArgumentException.class, ()-> loader.loadTransactions("test/empty.csv")),
                () -> assertNotNull(loader.loadPlayers("data/seahawks_players.csv"), "Application should retain valid data even after invalid data loaded"),
                () -> assertEquals(Player.class.getName(), loader.loadPlayers("data/seahawks_players.csv").get(0).getClass().getName(),
                            "Application should retain valid data even after invalid data loaded")

        );
    }
}





import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 * Tests for loader class.
 */
public class LoaderTest {

    @Test
    public void testDataLoading() throws IOException {
        DataLoader loader = new DataLoader();

        loader.loadPlayers("data/seahawks_players.csv");
        loader.loadDrills("data/seahawks_drills.csv");
        loader.loadTransactions("data/seahawks_transactions.csv");

        assertAll("Loading Test",
                () -> {
                    assertNotNull(loader.playerData,"the player data should not be null");
                    assertNotNull(loader.drillData, "the drill data should not be null");
                    assertNotNull(loader.transactionData, "the transaction data should not be null");
                    assertFalse(loader.playerData.isEmpty(), "the player data should not be empty");
                    assertFalse(loader.drillData.isEmpty(), "the drill data should not be empty");
                    assertFalse(loader.transactionData.isEmpty(), "the transaction data should not be empty");
                }
        );

    }

    @Test
    public void testErrorHandling() {
        DataLoader loader = new DataLoader();
        assertAll("Test Error Handling",
                () -> {
                    assertThrows(IOException.class,()-> loader.loadDrills("bad path"));
                    assertThrows(IllegalArgumentException.class, ()-> loader.loadPlayers("test/badFormatPlayers.csv"));
                }
        );
    }

}




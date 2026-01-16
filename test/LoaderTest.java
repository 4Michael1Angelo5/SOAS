
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 * Tests for loader class.
 */
public class LoaderTest {

    @Test
    public void testLoadPlayer() {
        DataLoader loader = new DataLoader();

        assertAll(
                () -> {
                    assertThrows(FileNotFoundException.class,()->loader.loadDrills("bad path"));
                    assertThrows(IOException.class, ()-> loader.loadPlayers("test/badFormatPlayers.csv"));
                }
        );

    }

}




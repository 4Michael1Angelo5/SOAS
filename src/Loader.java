import java.io.FileNotFoundException;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 * src.Loader Interface
 */
public interface Loader {
    String loadPlayers() throws FileNotFoundException;
    String loadTransactions();
    String loadDrills();
}

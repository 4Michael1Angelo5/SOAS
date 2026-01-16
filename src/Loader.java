import java.io.FileNotFoundException;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 * src.Loader Interface
 */
public interface Loader {
    String loadPlayers(String theFilePath) throws FileNotFoundException;
    String loadTransactions(String theFilePath);
    String loadDrills(String theFilePath);
}

import java.io.IOException;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 * src.Loader Interface
 */
public interface Loader {
    ArrayStore<Player> loadPlayers(String theFilePath) throws IOException, IllegalArgumentException;
    ArrayStore<Transaction> loadTransactions(String theFilePath) throws IOException, IllegalArgumentException;
    ArrayStore<Drill> loadDrills(String theFilePath)throws IOException, IllegalArgumentException;
}

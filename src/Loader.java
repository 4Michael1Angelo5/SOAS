import java.io.IOException;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 * src.Loader Interface
 */
public interface Loader {
    void loadPlayers(String theFilePath) throws IOException, IllegalArgumentException;
    void loadTransactions(String theFilePath) throws IOException, IllegalArgumentException;
    void loadDrills(String theFilePath)throws IOException, IllegalArgumentException;
}

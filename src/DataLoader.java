import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class DataLoader implements Loader {

    public DataLoader(){
        super();
    }

    List<Player> playerData;
    List<Transaction> transactionData;
    List<Drill> drillData;

    private void setPlayerData(List<Player> playerData) {
        this.playerData = playerData;
    }

    private void setTransactionData(List<Transaction> transactionData) {
        this.transactionData = transactionData;
    }

    private void setDrillData(List<Drill> drillData) {
        this.drillData = drillData;
    }

    @Override
    public String loadPlayers() {


        List<Player> playerData= new ArrayList<>();

        String nextLine;
        try (FileReader fileReader = new FileReader("data/seahawks_players.csv")) {
            BufferedReader br = new BufferedReader(fileReader);
            // skip first row of data
            nextLine = br.readLine();
            while ( (nextLine = br.readLine()) != null) {
                String[] row = nextLine.split(",");

                Player player = new Player(Integer.parseInt(row[0]),row[1],row[2],row[3], Integer.parseInt(row[4]));
                playerData.add(player);
            }

            setPlayerData(playerData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "";
    }


    @Override
    public String loadTransactions() {
        List<Transaction> transActionData= new ArrayList<>();

        String nextLine;
        try (FileReader fileReader = new FileReader("data/seahawks_transactions.csv")) {
            BufferedReader br = new BufferedReader(fileReader);
            // skip first row of data
            nextLine = br.readLine();
            while ( (nextLine = br.readLine()) != null) {
                String[] row = nextLine.split(",");

                Transaction transaction = new Transaction(Integer.parseInt(row[0]),row[1],row[2],row[3]);
                transActionData.add(transaction);
            }

            setTransactionData(transActionData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "";
    }

    @Override
    public String loadDrills() {

        List<Drill> drillsData = new ArrayList<>();

        String nextLine;
        try (FileReader fileReader = new FileReader("data/seahawks_drills.csv")) {
            BufferedReader br = new BufferedReader(fileReader);

            // skip first line
            nextLine = br.readLine();

            while ( (nextLine = br.readLine()) != null) {
                String[] row = nextLine.split(",");

                Drill drill = new Drill(Integer.parseInt(row[0]) ,row[1], Integer.parseInt(row[2]));
                drillsData.add(drill);
            }

            setDrillData(drillsData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "";
    }

    public <T> void printData(List<T> theData){
        for(T data : theData) {
            System.out.println(data.toString());
        }
    }

    public static void main() {
        DataLoader loader = new DataLoader();
        loader.loadPlayers();
        loader.loadDrills();
        loader.loadTransactions();
        loader.printData(loader.playerData);
        loader.printData(loader.drillData);
        loader.printData(loader.transactionData);

    }

}

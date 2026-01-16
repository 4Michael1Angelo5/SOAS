import java.io.*;
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
    public String loadPlayers(String theFilePath) {

        this.playerData = loadData(Player.class,theFilePath);

        return "";
    }

    @Override
    public String loadDrills(String theFilePath) {
        this.drillData = loadData(Drill.class, theFilePath);

        return "";
    }

    @Override
    public String loadTransactions(String theFilePath) {

        this.transactionData = loadData(Transaction.class, theFilePath);

        return "";
    }

    public <T> void printData(List<T> theData){
        for(T data : theData) {
            System.out.println(data.toString());
        }
    }

    private <T extends DataType> T parseData(Class<T> dataClass, String theCsvRow) {

        String[] row = theCsvRow.split(",");

        Object result = null;

       if (dataClass == Player.class) {

           result = new Player(Integer.parseInt(row[0]),row[1],row[2],row[3], Integer.parseInt(row[4]));

       }else
       if(dataClass == Drill.class) {

           result = new Drill(Integer.parseInt(row[0]) ,row[1], Integer.parseInt(row[2]));

       }else
       if (dataClass == Transaction.class) {
           result = new Transaction(Integer.parseInt(row[0]),row[1],row[2],row[3]);
        }

       return dataClass.cast(result);
    }

    private <T extends DataType> List<T> loadData(Class<T> theDataType,String theFilePath) {

        List<T> dataArray = new ArrayList<>();

        try {

            FileReader myFileReader = new FileReader(theFilePath);
            String nextLine;

            try {
                 BufferedReader br = new BufferedReader(myFileReader);

                // skip first line
                nextLine = br.readLine();

                while ( (nextLine = br.readLine()) != null) {

                    dataArray.add(parseData(theDataType, nextLine));

                }

            }catch(IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return dataArray;
    }

    public static void main() {
        DataLoader loader = new DataLoader();
        loader.loadPlayers("data/seahawks_players.csv");
        loader.loadDrills("data/seahawks_drills.csv");
        loader.loadTransactions("data/seahawks_transactions.csv");

        loader.printData(loader.playerData);
        loader.printData(loader.drillData);
        loader.printData(loader.transactionData);
    }
}
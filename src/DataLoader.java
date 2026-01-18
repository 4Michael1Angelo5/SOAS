import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class DataLoader implements Loader {

    OperationCounter myCounter = new OperationCounter();

    public DataLoader(){
        super();
    }

    private static final Logger logger = Logger.getLogger(DataLoader.class.getName());

    /**
     * A list of Player Data
     */
    List<Player> playerData;

    /**
     * A list of Transaction Data
     */
    List<Transaction> transactionData;

    /**
     * A list of drill data
     */
    List<Drill> drillData;

    @Override
    public void loadPlayers(String theFilePath)
            throws IOException, IllegalArgumentException{
        this.playerData = loadData(Player.class,theFilePath);
    }

    @Override
    public void loadDrills(String theFilePath)
            throws IOException, IllegalArgumentException {
        this.drillData = loadData(Drill.class, theFilePath);
    }

    @Override
    public void loadTransactions(String theFilePath)
            throws IOException, IllegalArgumentException {
        this.transactionData = loadData(Transaction.class, theFilePath);
    }

    /**
     * Prints data parsed from a csv file.
     * @param theData An array list of DataType objects.
     * @param <T> the Data type to print. can either be Player, Drill, Transaction
     */
    public <T> void printData(List<T> theData){
        for(T data : theData) {
            logger.info(data.toString());
        }
    }

    /**
     * Parses Data from a csv row into its corresponding data type.
     * @param dataClass can either be of type Player, Drills, or Transaction
     * @param theCsvRow a comma separated string of values.
     * @return A DataType object either: Player, Drills, or Transaction
     * @param <T> DataType either: Player, Drills, or Transaction
     */
    private <T extends DataType> T parseData(Class<T> dataClass, String theCsvRow)
            throws IllegalArgumentException {

        String[] row = theCsvRow.split(",");

        Object result;

        try {
            if (dataClass == Player.class) {

                result = new Player(Integer.parseInt(row[0]),row[1],row[2],row[3], Integer.parseInt(row[4]));
                myCounter.increment("assignments",4);
            }else
            if(dataClass == Drill.class) {

                result = new Drill(Integer.parseInt(row[0]) ,row[1], Integer.parseInt(row[2]));
                myCounter.increment("assignments",3);

            }else
            if (dataClass == Transaction.class) {

                result = new Transaction(Integer.parseInt(row[0]),row[1],row[2],row[3]);
                myCounter.increment("assignments",4);

            }else {

                throw new IllegalArgumentException(dataClass.getName() + " is not a supported data type");
            }

            myCounter.increment("lines read");

        }catch(IndexOutOfBoundsException e) {

             throw new IllegalArgumentException("Encountered malformed column input in the csv.");
        }

       return dataClass.cast(result);
    }

    /**
     * Helper method to load data from a csv file.
     * @param theDataType the data type the csv file is supposed to represent.
     *                    Can either be Player, Drill, Transaction.
     * @param theFilePath a string path to resource csv file.
     * @param <T> the data type to load: Can either be Player, Drill, Transaction.
     * @return an array list of data objects.
     */
    private <T extends DataType>
    List<T> loadData(Class<T> theDataType,String theFilePath)
            throws IllegalArgumentException, IOException{

        List<T> dataArray = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(theFilePath))){

            // skip first line
            br.readLine();

            String nextLine;
            while ( (nextLine = br.readLine()) != null) {
                try {

                    dataArray.add(parseData(theDataType, nextLine));

                } catch (IllegalArgumentException e) {
                    // Catch errors from parseData (bad columns, bad numbers)
                    logger.severe("Data error in " + theFilePath + ": " + e.getMessage());
                    throw new IllegalArgumentException("File " + theFilePath + " is malformed.", e);
                }

            }
        } catch (FileNotFoundException e) {
            logger.severe(e.getMessage());
            throw new FileNotFoundException("");
        }

        return dataArray;
    }

    static void main() throws IOException {
        DataLoader loader = new DataLoader();

        loader.loadPlayers("data/seahawks_players.csv");
        loader.loadDrills("data/seahawks_drills.csv");
        loader.loadTransactions("data/seahawks_transactions.csv");

        loader.printData(loader.playerData);
        loader.printData(loader.drillData);
        loader.printData(loader.transactionData);

    }
}
package loader;

import java.io.*;
import java.util.List;
import java.util.logging.Logger;
import util.ArrayStore;
import types.Player;
import types.Drill;
import types.Transaction;
import types.DataType;

/**
 * @author Chris Chun, Ayush
 * @version 1.2
 */
public class DataLoader implements Loader {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public DataLoader(){
        super();
    }

    private static final Logger logger = Logger.getLogger(DataLoader.class.getName());

    @Override
    public ArrayStore<Player> loadPlayers(String theFilePath)
            throws IOException, IllegalArgumentException{
        return loadData(Player.class,theFilePath);
    }

    @Override
    public ArrayStore<Drill> loadDrills(String theFilePath)
            throws IOException, IllegalArgumentException {
        return loadData(Drill.class, theFilePath);
    }

    @Override
    public ArrayStore<Transaction> loadTransactions(String theFilePath)
            throws IOException, IllegalArgumentException {
        return loadData(Transaction.class, theFilePath);
    }

    /**
     * Prints data parsed from a csv file.
     * @param theData An array list of DataType objects.
     * @param <T> the Data type to print. can either be Player, Drill, Transaction
     */
    public <T> void printData(List<T> theData){
        for(T data : theData) {
            logger.info(ANSI_GREEN + data.toString() + ANSI_RESET);
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

                result = new Player(Integer.parseInt(row[0]),row[1],row[2],Integer.parseInt(row[3]), Integer.parseInt(row[4]));
            }else
            if(dataClass == Drill.class) {

                result = new Drill(Integer.parseInt(row[0]) ,row[1], Integer.parseInt(row[2]));

            }else
            if (dataClass == Transaction.class) {

                result = new Transaction(Integer.parseInt(row[0]),row[1],row[2],row[3]);

            }else {

                throw new IllegalArgumentException(dataClass.getName() + " is not a supported data type");
            }

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
    public <T extends DataType>
    ArrayStore<T> loadData(Class<T> theDataType,String theFilePath)
            throws IllegalArgumentException, IOException{

        ArrayStore<T> dataArray = new ArrayStore<>(theDataType, 16);

        try(BufferedReader br = new BufferedReader(new FileReader(theFilePath))){

            // see if csv is empty
            String headerColumns = br.readLine();
            if (headerColumns == null) {
                throw new IllegalArgumentException("the CSV is empty");
            };

            // mark current position (second row)
            br.mark(1024);
            String secondLine = br.readLine();
            // rewind back to second row where data should be
            br.reset();

            // check if second row is empty
            if (secondLine == null || secondLine.isBlank()) {
                throw new IllegalArgumentException("CSV has no data rows");
            }

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
}
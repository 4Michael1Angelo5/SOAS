package loader;

import java.io.*;
import java.util.function.Supplier;
import java.util.logging.Logger;

import types.*;
import util.ArrayStore;
import util.DataContainer;
import util.SinglyLinkedList;

/**
 * DataLoader is a universal, type-agnostic engine responsible for parsing
 * CSV data into domain-specific objects.
 * * <p>By utilizing a {@link Supplier}, this class is decoupled from the
 * underlying storage mechanism. It can populate any structure that implements
 * {@link DataContainer}, whether it be an array-based store, a linked list,
 * or a LIFO/FIFO structure.</p>
 * * <p>The loader uses the {@code dataClass} to reflectively cast parsed
 * objects, ensuring strict type safety at runtime without the need for
 * specialized loader subclasses.</p>
 *
 * @author Chris Chun, Ayush
 * @version 1.3
 * @param <T> The {@link DataType} this loader is configured to handle.
 */
public class DataLoader <T extends DataType> {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    private final Supplier<DataContainer<T>> myContainerSupplier;
    private final Class<T> myDataClass;

    public DataLoader(Class<T> theDataClass, Supplier<DataContainer<T>> theSupplier){
        super();
        myContainerSupplier = theSupplier;
        myDataClass = theDataClass;
        validateConstructor(theDataClass, theSupplier);
    }

    private static final Logger logger = Logger.getLogger(DataLoader.class.getName());

    private void validateConstructor(Class<T> theDataClass, Supplier<DataContainer<T>> theSupplier) {
        if (theSupplier == null){
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        if (!theSupplier.get().isEmpty()){
            throw new IllegalArgumentException("Supplier returned a non empty container");
        }

    }

    /**
     * Parses Data from a csv row into its corresponding data type.
     * @param theCsvRow a comma separated string of values.
     * @return A DataType object either: Player, Drills, or Transaction
     */
    private T parseData(String theCsvRow)
            throws IllegalArgumentException {

        String[] row = theCsvRow.split(",");

        Object result;

        try {
            if (myDataClass == Player.class) {

                result = new Player(
                        Integer.parseInt(row[0]),  // player_id
                        row[1],                    // name
                        row[2],                    // position
                        Integer.parseInt(row[3]),  // jersey
                        Integer.parseInt(row[4])); // yards
            }else
            if(myDataClass == Drill.class) {

                result = new Drill(
                        Integer.parseInt(row[0]), // drill_id
                        row[1],                   // name
                        Integer.parseInt(row[2]), // urgency
                        Integer.parseInt(row[3]), // duration_min
                        Integer.parseInt(row[4]), // fatigue_cost
                        Integer.parseInt(row[5])  // install_by_day
                        );

            }else
            if (myDataClass == Transaction.class) {

                result = new Transaction(
                        Integer.parseInt(row[0]), // trans_id
                        row[1],                   // type
                        row[2],                   // player
                        row[3]);                  // timestamp

            }else
            if (myDataClass == Action.class){

                result = new Action(
                        Integer.parseInt(row[0]),   // action_id
                        ActionType.valueOf(row[1]), // action_type
                        row[2],                     // target
                        row[3]);                    // timestamp

            }else
            if (myDataClass == FanRequest.class) {
                result = new FanRequest(
                        Integer.parseInt(row[0]),  // fan_id
                        row[1],                    // name
                        row[2],                    // service_type
                        row[3]);                   // arrival_time
            }
            else {

                throw new IllegalArgumentException(myDataClass.getName() + " is not a supported data type");
            }

        }catch(IndexOutOfBoundsException e) {

            throw new IllegalArgumentException("Encountered malformed column input in the csv.");
        }

        return myDataClass.cast(result);
    }

    /**
     * Helper method to load data from a csv file.
     * @param theFilePath a string path to resource csv file.
     * @return an array list of data objects.
     */
    public DataContainer<T>
    loadData(String theFilePath)
            throws IllegalArgumentException, IOException{

        DataContainer<T> dataContainer = myContainerSupplier.get();

        try(BufferedReader br = new BufferedReader(new FileReader(theFilePath))){

            // see if csv is empty
            String headerColumns = br.readLine();
            if (headerColumns == null) {
                throw new IllegalArgumentException("the CSV is empty");
            }

            validateHeaders(headerColumns, theFilePath);

            String nextLine;
            while ( (nextLine = br.readLine()) != null) {
                try {

                    dataContainer.add(parseData(nextLine));

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

        return dataContainer;
    }

    private void validateHeaders(String theHeaderRow, String theFilePath){
        boolean isValid = false;
        theHeaderRow = theHeaderRow.trim();

        switch (myDataClass.getSimpleName()) {
            case "Player" -> isValid = theHeaderRow.equals("player_id,name,position,jersey,yards");
            case "Transaction" -> isValid = theHeaderRow.equals("trans_id,type,player,timestamp");
            case "Drill" -> isValid = theHeaderRow.equals("drill_id,name,urgency,duration_min,fatigue_cost,install_by_day");
            case "Action" -> isValid = theHeaderRow.equals("action_id,action_type,target,timestamp");
            case "FanRequest" -> isValid = theHeaderRow.equals("fan_id,name,service_type,arrival_time");
            default -> throw new RuntimeException("Unsupported DataType for loader");
        }

        if (!isValid) {
            throw new IllegalArgumentException(
                    "\nThe CSV data does match the expected header for " +
                            myDataClass.getSimpleName() + " please check the " +
                            "file path: " + theFilePath
            );
        }
    }
}
package loader;

import java.io.*;
import java.util.function.Supplier;
import java.util.logging.Logger;

import types.*;
import util.DataContainer;

/**
 * DataLoader is a universal, type-agnostic engine responsible for parsing
 * CSV data into domain-specific objects.
 * <p>By utilizing a {@link Supplier}, this class is decoupled from the
 * underlying storage mechanism. It can populate any structure that implements
 * {@link DataContainer}, whether it be an array-based store, a linked list,
 * or a LIFO/FIFO structure.</p>
 * <p>The loader uses the {@code dataClass} to reflectively cast parsed
 * objects, ensuring strict type safety at runtime without the need for
 * specialized loader subclasses.</p>
 *
 * @author Chris Chun, Ayush
 * @version 2.0
 * @param <T> The {@link DataType} this loader is configured to handle.
 */
public class DataLoader2 <T extends DataType, C extends DataContainer<T>> {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    private final Supplier<C> myContainerSupplier;
    private final T myTypeToken;

    public DataLoader2(Supplier<C> theSupplier, T theTypeToken){
        super();
        myTypeToken = theTypeToken;
        myContainerSupplier = theSupplier;
        validateConstructor(theSupplier::get);
    }

    private static final Logger logger = Logger.getLogger(DataLoader2.class.getName());

    private void validateConstructor(Supplier<DataContainer<T>> theSupplier) {
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
    @SuppressWarnings("unchecked")
    private T parseData(String theCsvRow) throws IllegalArgumentException {

        String[] row = theCsvRow.split(",");

        Object result;

        try {
            switch ((DataType) myTypeToken) {
                case Player _ -> result = new Player(
                        Integer.parseInt(row[0]),       // player_id
                        row[1],                         // name
                        row[2],                         // position
                        Integer.parseInt(row[3]),       // jersey
                        Integer.parseInt(row[4]));      // yards
                case Transaction _ ->
                    result = new Transaction(
                            Integer.parseInt(row[0]),       // trans_id
                            row[1],                         // type
                            row[2],                         // player
                            row[3]);                        // timestamp
                case Drill _ ->
                    result = new Drill(
                            Integer.parseInt(row[0]),       // drill_id
                            row[1],                         // name
                            Integer.parseInt(row[2]),       // urgency
                            Integer.parseInt(row[3]),       // duration_min
                            Integer.parseInt(row[4]),       // fatigue_cost
                            Integer.parseInt(row[5]));      // install_by_day
                case Action _ ->
                    result = new Action(
                            Integer.parseInt(row[0]),       // action_id
                            ActionType.valueOf(row[1]),     // action_type
                            row[2],                         // target
                            row[3]);                        // timestamp
                case FanRequest _ ->
                    result = new FanRequest(
                            Integer.parseInt(row[0]),       // fan_id
                            row[1],                         // name
                            row[2],                         // service_type
                            row[3]);                        // arrival_time
                case PlayerEnhanced _ ->
                    result = new PlayerEnhanced(
                            Integer.parseInt(row[0]),       // player_id
                            row[1],                         // name
                            Position.valueOf(row[2]),       // position
                            Integer.parseInt(row[3]),       // yards
                            Integer.parseInt(row[4]),       // touchdowns
                            Boolean.parseBoolean(row[5]));  // injured
                case UndoRecord _ ->
                    throw new IllegalArgumentException("UndoRecords are not currently supported for CSV file parsing");
            }

        } catch(IndexOutOfBoundsException e) {

            throw new IllegalArgumentException("Encountered malformed column input in the csv.");
        }

        return (T) result;
    }

    /**
     * Helper method to load data from a csv file.
     * @param theFilePath a string path to resource csv file.
     * @return an array list of data objects.
     */
    public C loadData(String theFilePath)
            throws IllegalArgumentException, IOException{

        C dataContainer = myContainerSupplier.get();

        try(BufferedReader br = new BufferedReader(new FileReader(theFilePath))){

            // see if csv is empty
            String headerColumns = br.readLine();
            if (headerColumns == null) {
                throw new IllegalArgumentException("the CSV is empty");
            }

            validateCsvHeaders(headerColumns, theFilePath);

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

    private void validateCsvHeaders(String theHeaderRow, String theFilePath) {
        boolean isValid;
        theHeaderRow = theHeaderRow.trim();
        switch ((DataType) myTypeToken) {
            case Player _ -> isValid = theHeaderRow.equals("player_id,name,position,jersey,yards");
            case Transaction _ -> isValid = theHeaderRow.equals("trans_id,type,player,timestamp");
            case Drill _ -> isValid = theHeaderRow.equals("drill_id,name,urgency,duration_min,fatigue_cost,install_by_day");
            case Action _ -> isValid = theHeaderRow.equals("action_id,action_type,target,timestamp");
            case FanRequest _ -> isValid = theHeaderRow.equals("fan_id,name,service_type,arrival_time");
            case UndoRecord _ -> isValid = false; // not yet implemented for csv format
            case PlayerEnhanced _ -> isValid = theHeaderRow.equals("playerId,name,position,yards,touchdowns,injured");
        }
        if (!isValid) {
            throw new IllegalArgumentException(
                    "\nThe CSV data does match the expected header for " +
                            myTypeToken.getClass().getSimpleName() + " please check the " +
                            "file path: " + theFilePath
            );
        }
    }
}
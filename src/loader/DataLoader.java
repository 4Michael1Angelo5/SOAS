package loader;

import java.io.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import types.*;
import util.ArrayStore;
import util.DataContainer;

import javax.xml.crypto.Data;

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
    }

    private static final Logger logger = Logger.getLogger(DataLoader.class.getName());

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

                result = new Player(Integer.parseInt(row[0]),row[1],row[2],Integer.parseInt(row[3]), Integer.parseInt(row[4]));
            }else
            if(myDataClass == Drill.class) {

                result = new Drill(Integer.parseInt(row[0]) ,row[1], Integer.parseInt(row[2]));

            }else
            if (myDataClass == Transaction.class) {

                result = new Transaction(Integer.parseInt(row[0]),row[1],row[2],row[3]);

            }else {

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

        DataContainer<T> dataArray = myContainerSupplier.get();

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

                    dataArray.add(parseData(nextLine));

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
package manager;

import util.DataContainer;
import types.Transaction;
import util.SinglyLinkedList;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * TransactionFeed manages Seahawks transaction records.
 * <p>While optimized for sequential data (like feeds), this manager is
 * container-agnostic. It supports high-level operations such as front/rear
 * insertion and searching by player, type, or timestamp.</p>
 * * @author Chris Chun, Ayush
 * @version 1.2
 */
public class TransactionFeed extends DataManager<Transaction> {


    public TransactionFeed(Supplier<DataContainer<Transaction>> theSupplier) {
        super(Transaction.class,  theSupplier);
    }

    // ================================= flags =================================

    @Override
    public boolean needsIndexedAccess() {
        return true;
    }

    //================================= getting =================================

    /**
     * getter
     * @return the DataContainer of transactions
     */
    public DataContainer<Transaction> getTransactionData() {
        return getData();
    }

    @Override
    public Class<TransactionFeed> getManagerClass(){
        return TransactionFeed.class;
    }

    // ================================= loading =================================

    /**
     * Loads transaction data from CSV file
     * @param theFilePath path to the CSV file
     * @throws IOException if file not found
     */
    public void loadTransactionData(String theFilePath) throws IOException {
        this.loadCsvData(theFilePath);
    }

    // ================================= adding =================================

    /**
     * Inserts a new transaction at the front of the feed
     * @param theTransaction the transaction to insert
     */
    public void insertTransaction(int theIndex,Transaction theTransaction) {

        this.getData().add(theIndex, theTransaction);
    }

    /**
     * Adds a transaction to the rear of the feed
     * @param theTransaction the transaction to add
     */
    public void addTransactionRear(Transaction theTransaction) {

        myData.add(myData.size(), theTransaction);
    }

    public void addTransactionFront(Transaction theTransaction) {

        myData.add(0,theTransaction);
    }

    // ================================= removing =================================

    /**
     * Removes transaction from the front
     * @return the removed transaction
     */
    public Transaction removeFront() {
        return removeAt(0);
    }

    /**
          * remove element by index
          * This operation can only be performed by roster managers that require indexed access.
          * @param theIndex the index of the element to remove.
          * @return the removed element.
          */
    public Transaction removeAt(int theIndex) {
        if (!this.needsIndexedAccess()) {
            throw new IllegalArgumentException("Stacks and Queues do not support indexed access");
        }
        return myData.removeAt(theIndex);
    }

    /**
     *
     * @param theId the ID of the data entry (Player, Drills, Transactions)
     *              you wish to delete.
     * @return the removed data.
     */
    public Transaction removeById(int theId) {

        int index = myData.findBy(( theDataObject) -> theDataObject.id() == theId);

        // throw exception if not found
        if (index == -1) {
            throw new NoSuchElementException("id not found");
        }

        // store it
        Transaction theRemovedData =  myData.get(index);

      // remove it, and shift everything
        myData.removeAt(index);

        return theRemovedData;
    }



    // ================================= searching =================================


    /**
     *
     * @param theId theId of the data entry (Player, Drills, Transaction)
     * @return the index of the data if present, -1 otherwise.
     */

    public int findById(int theId) {

        return myData.findBy( ( theDataObject) -> theDataObject.id() == theId);
    }

    /**
     * Finds the first index of the transaction with the matching player name and -1 otherwise.
     * @param thePlayerName name of the player to find.
     * @return the first index of the transaction with the matching player name and -1 otherwise
     */
    public int findByPlayer(String thePlayerName) {
        return myData.findBy((t)-> t.player().equals(thePlayerName));
    }

    /**
     * Finds the first index of the transaction with the matching type and -1 otherwise.
     * @param theType the type of transaction (Injury, Trade, etc.)
     * @return returns the first index of the transaction with the matching type, and -1 otherwise.
     */
    public int findByType(String theType) {
        return myData.findBy((t)-> t.type().equals(theType));
    }

    /**
     * Finds the first index of the transaction with the matching timestamp and -1 otherwise.
     * @param theTimestamp the timestamp of the transaction
     * @return returns the first index of the transaction with the matching timestamp, and -1 otherwise.
     */
    public int findByTimestamp(String theTimestamp) {

        return myData.findBy((t)-> t.timestamp().equals(theTimestamp));
    }

    //================================= printing =================================

    /**
     * Displays My Transaction
     */
    public void printTransactions() {
        printData();
    }

    public static void main(String[] args) throws IOException {
        Supplier<DataContainer<Transaction>> sup = SinglyLinkedList::new;
        TransactionFeed feed = new TransactionFeed(sup);

        feed.loadCsvData("data/seahawks_transactions.csv");

        System.out.println(feed.getData().size());
    }
}
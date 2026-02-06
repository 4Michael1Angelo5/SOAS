package manager;

import util.DataContainer;
import types.Transaction;
import util.SinglyLinkedList;

import java.io.IOException;
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
        if (!myData.supportsIndexedAccess()) {

        }
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
        return myData.remove();
    }

    // ================================= searching =================================

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
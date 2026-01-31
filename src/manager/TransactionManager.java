package manager;

import types.Transaction;
import util.ArrayStore;

import java.io.IOException;

/**
 * Responsible for
 * Storing transactions using a linked list
 * Adding transactions
 * Removing transactions
 * Searching
 * Printing
 * Uses Linked List internally
 * @author Chris Chun, Ayush
 * @version 1.1
 */
public class TransactionManager extends DataManager<Transaction> {

    // TODO: add LinkedListStore field when its ready
    // private LinkedListStore<Transaction> myTransactions;

    public TransactionManager() {
        super(Transaction.class);
    }

    //================================= getting =================================

    /**
     * getter
     * @return an ArrayStore of the transactions (will change to LinkedList)
     */
    public ArrayStore<Transaction> getTransactionData() {
        return this.getData();
    }

    // ================================= loading =================================

    /**
     * Loads transaction data from CSV file
     * @param theFilePath path to the CSV file
     * @throws IOException if file not found
     */
    public void loadTransactionData(String theFilePath) throws IOException {
        this.addCsvData(theFilePath);
        // TODO: convert to LinkedList when its added
    }

    // ================================= adding =================================

    /**
     * Inserts a new transaction at the front of the feed
     * @param theTransaction the transaction to insert
     */
    public void insertTransaction(Transaction theTransaction) {
        // TODO: myTransactions.addFront(theTransaction);
        this.addData(theTransaction); // temporary
    }

    /**
     * Adds a transaction to the rear of the feed
     * @param theTransaction the transaction to add
     */
    public void addTransaction(Transaction theTransaction) {
        this.addData(theTransaction);
    }

    // ================================= searching =================================

    /**
     * Finds transaction by player name
     * @param thePlayerName name of the player
     * @return index if found, -1 otherwise
     */
    public int findByPlayer(String thePlayerName) {
        int index = -1;

        for (int i = 0; i < this.getTransactionData().size(); i++) {
            if (getTransactionData().get(i).player().equals(thePlayerName)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Finds transaction by type
     * @param theType the type of transaction
     * @return index if found, -1 otherwise
     */
    public int findByType(String theType) {
        int index = -1;

        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).type().equals(theType)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Finds transaction by timestamp
     * @param theTimestamp the timestamp
     * @return index if found, -1 otherwise
     */
    public int findByTimestamp(String theTimestamp) {
        int index = -1;

        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).timestamp().equals(theTimestamp)) {
                index = i;
                break;
            }
        }
        return index;
    }

    //================================= printing =================================

    /**
     * Displays the transaction feed
     */
    public void displayFeed() {
        this.printData(this.getTransactionData());
    }

    /**
     * Prints all transactions
     */
    public void printTransactions() {
        displayFeed();
    }
}
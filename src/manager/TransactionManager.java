package manager;

import types.Transaction;
import util.SinglyLinkedList;

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

    private SinglyLinkedList<Transaction> myTransactions;

    public TransactionManager() {
        super(Transaction.class);
        myTransactions = new SinglyLinkedList<>();
    }

    //================================= getting =================================

    /**
     * getter
     * @return the SinglyLinkedList of transactions
     */
    public SinglyLinkedList<Transaction> getTransactionData() {
        return myTransactions;
    }

    // ================================= loading =================================

    /**
     * Loads transaction data from CSV file
     * @param theFilePath path to the CSV file
     * @throws IOException if file not found
     */
    public void loadTransactionData(String theFilePath) throws IOException {
        this.addCsvData(theFilePath);

        // convert to linked list
        myTransactions = new SinglyLinkedList<>();
        for (int i = 0; i < this.getData().size(); i++) {
            myTransactions.addRear(this.getData().get(i));
        }
    }

    // ================================= adding =================================

    /**
     * Inserts a new transaction at the front of the feed
     * @param theTransaction the transaction to insert
     */
    public void insertTransaction(Transaction theTransaction) {
        myTransactions.addFront(theTransaction);
    }

    /**
     * Adds a transaction to the rear of the feed
     * @param theTransaction the transaction to add
     */
    public void addTransaction(Transaction theTransaction) {
        myTransactions.addRear(theTransaction);
    }

    // ================================= removing =================================

    /**
     * Removes transaction from the front
     * @return the removed transaction
     */
    public Transaction removeFront() {
        return myTransactions.remove();
    }

    // ================================= searching =================================

    /**
     * Finds the first index of the transaction with the matching player name and -1 otherwise.
     * @param thePlayerName name of the player to find.
     * @return the first index of the transaction with the matching player name and -1 otherwise
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
     * Finds the first index of the transaction with the matching type and -1 otherwise.
     * @param theType the type of transaction (Injury, Trade, etc.)
     * @return returns the first index of the transaction with the matching type, and -1 otherwise.
     */
    public int findByType(String theType) {
        int index = -1;

        for (int i = 0; i < myTransactions.size(); i++) {
            if (myTransactions.get(i).type().equals(theType)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Finds the first index of the transaction with the matching timestamp and -1 otherwise.
     * @param theTimestamp the timestamp of the transaction
     * @return returns the first index of the transaction with the matching timestamp, and -1 otherwise.
     */
    public int findByTimestamp(String theTimestamp) {
        int index = -1;

        for (int i = 0; i < myTransactions.size(); i++) {
            if (myTransactions.get(i).timestamp().equals(theTimestamp)) {
                index = i;
                break;
            }
        }
        return index;
    }

    //================================= printing =================================

    /**
     * Displays My Transaction
     */
    public void displayFeed() {
        System.out.println("\n=== My Transaction  ===");
        for (int i = 0; i < myTransactions.size(); i++) {
            System.out.println(myTransactions.get(i).toString());
        }
        System.out.println("========================\n");
    }

    /**
     * Prints all transactions
     */
    public void printTransactions() {
        displayFeed();
    }
}
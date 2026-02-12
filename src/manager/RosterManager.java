package manager;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import types.Transaction;
import util.DataContainer;
import types.Player;

/**
 * Responsible for
 * Storing players in a DataContainer.
 * Adding players
 * Removing players
 * Updating stats
 * Searching
 * Printing
 * @author Chris Chun, Ayush
 * @version 1.2
 */
final public class RosterManager extends DataManager<Player> {

    public <T>RosterManager(Supplier<DataContainer<Player>> theSupplier){

        super(Player.class, theSupplier);
    }

    // ================================= flags =================================
    @Override
    public boolean needsIndexedAccess() {
        return true;
    }


    //================================= getting =================================

    /**
     * getter
     * @return a DataContainer of the player roster.
     */
    public DataContainer<Player> getPlayerData() {
        return this.getData();
    }

    @Override
    public Class<RosterManager> getManagerClass(){
        return RosterManager.class;
    }


    // ================================= adding =================================

    /**
     * Adds a player to the roster
     * @param thePlayer a Player record  to add to the Roster
     */
    public void addPlayer(Player thePlayer) {
        this.addData(thePlayer);
    }

    public void loadPlayerData(String theFilePath) throws IOException {
        this.loadCsvData(theFilePath);
    }

    // ================================= removing =================================

    /**
     * remove element by index
     * This operation can only be performed by roster managers that require indexed access.
     * @param theIndex the index of the element to remove.
     * @return the removed element.
     */
    public Player removeAt(int theIndex) {
        if (!this.needsIndexedAccess()) {
            throw new IllegalArgumentException("Stacks and Queues do not support indexed access");
        }
        return myData.removeAt(theIndex);
    }


    public Player removeById(int theId) {

        int index = myData.findBy((theDataObject) -> theDataObject.id() == theId);

        // throw exception if not found
        if (index == -1) {
            throw new NoSuchElementException("id not found");
        }

        // store it
        Player theRemovedData =  myData.get(index);

      // remove it, and shift everything
        myData.removeAt(index);

        return theRemovedData;
    }


    // ================================== updating ================================

        public void setData(int theIndex, Player theData) {

        myData.set(theIndex,theData);
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
     * Finds the first index of the player with the matching name and -1 otherwise.
     * @param theName name of the player to find.
     * @return the first index of the player with the matching name and -1 otherwise
     */
    public int findByName(String theName) {
        return myData.findBy((player)-> player.name().equals(theName));
    }

    /**
     *
     * @param thePos the Position of the player you wish to find.
     * @return returns the first index of the player with the matching position, and -1 otherwise.
     */
    public int findByPosition(String thePos) {
        return myData.findBy(player-> player.position().equals(thePos));
    }

    /**
     *
     * @param theJerseyNumber the jersey number of the player you wish to find.
     * @return returns the first index of the player with the matching jersey, and -1 otherwise.
     */
    public int findByJersey(int theJerseyNumber) {
        return myData.findBy(player-> player.jersey() == theJerseyNumber);
    }

    // ================================= updating =================================


    /**
     * Updates the yards of the player with the matching id provided
     * @param theId the id of the player
     * @param theNewYards the yards to update
     */
    public void updateStats(int theId, int theNewYards) {
        int index = myData.findBy(e -> e.player_id() == theId);

        // not found
        if (index == -1) {
            throw new RuntimeException("The Id, " + theId + ", was not found in the roster");
        }

        Player prev = getData().get(index);
        Player newPlayer = new Player(prev.id(), prev.name(), prev.position(), prev.jersey(),theNewYards);
        myData.set(index, newPlayer);
    }

    //================================= printing =================================

    /**
     * Prints the roster
     */
    public void printRoster() {
        this.printData();
    }

}

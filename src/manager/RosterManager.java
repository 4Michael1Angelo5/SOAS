package manager;

import java.io.IOException;
import java.util.function.Supplier;

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

    // ================================= searching =================================

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
        int index = findById(theId);

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



/**
 * @author Chris Chun, Ayush
 * @version 1.0
 */
public class RosterManager {

    private Player[] roster = new Player[1];
    public int size;

    public RosterManager( ) {
    }

    /**
     * Adds a player to the roster
     * @param p the player to add
     * @return true if added successfully, false if roster is full
     */
    public void addPlayer(Player p) {
        if (size == roster.length) {

            Player[] temp = roster;
            roster = new Player[size*2];

            for (int i = 0; i < size; i++) {
                roster[i] = temp[i];
            }
        }
        roster[size] = p;
        size++;
    }

    public Player getPlayer(int i) {
        return roster[i];
    }
}

/**
 * main method
 */
public void main(String[] arg) {
    RosterManager rm = new RosterManager();
    Player player = new Player(3,"bob","qb",56, 67);

    for (int i = 1; i <= 10; i++) {
        rm.addPlayer(player);
    }


    System.out.println(rm.size);
    System.out.println(rm.roster.length);
}

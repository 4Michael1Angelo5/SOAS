import manager.RosterManager;
import org.junit.jupiter.api.Test;
import types.Player;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tester for the RosterManager
 * @version 1.1
 * @author Chris Chun, Ayush
 */
public class RosterManagerTest {
    static final int THE_NUM_ADDS = 100;
    static final int TEST_TRIALS = 50;

    @Test
    public void testRosterManagerConstructor(){
        RosterManager rosterManager = new RosterManager();
        assertAll("Test Roster Manager Constructor",
                () -> assertEquals(0,rosterManager.getPlayerData().size(),
                        "The roster should have no players in it"),
                () -> assertNotNull(rosterManager.getPlayerData(), "The roster should be empty, but not null")

        );
    }

    @Test
    public void testAddPlayer() {
        RosterManager rosterManager = new RosterManager();
        final int playerId = 123;
        final String playerName = "Jerry Rice";
        final String position = "WR";
        final int jersey = 88;
        final int yards = 1000;

        Player thePlayer = new Player(playerId, playerName, position, jersey, yards);
        for(int i = 0; i < THE_NUM_ADDS; i++){
            rosterManager.addPlayer(thePlayer);
        }
        assertAll("Test addPlayer",
                () -> assertEquals(THE_NUM_ADDS, rosterManager.getPlayerData().size(),
                        "RosterManager should add the player the specified number times"),
                () -> {
                    for (int i = 0; i < THE_NUM_ADDS; i++) {
                        final int index = i;
                        assertAll(
                                () -> assertEquals(playerId, rosterManager.getPlayerData().get(index).player_id(),
                                        "RosterManager does not store the data correctly"),
                                () -> assertEquals(playerName,rosterManager.getPlayerData().get(index).name(),
                                        "RosterManager does not store the data correctly"),
                                () -> assertEquals(position, rosterManager.getPlayerData().get(index).position(),
                                        "RosterManager does not store the data correctly"),
                                () -> assertEquals(position, rosterManager.getPlayerData().get(index).position(),
                                        "RosterManager does not store the data correctly"),
                                () -> assertEquals(yards, rosterManager.getPlayerData().get(index).yards(),
                                        "RosterManager does not store the data correctly")
                        );
                    }}
        );
    }

    @Test void testRemovePlayer() {
        RosterManager rosterManager = new RosterManager();
        final int playerId = 111;
        final String playerName = "Jerry Rice";
        final String position = "WR";
        final int jersey = 88;
        final int yards = 1000;

        Player thePlayer = new Player(playerId, playerName, position, jersey, yards);

        for (int i = 0; i < THE_NUM_ADDS; i++) {
            rosterManager.addPlayer(thePlayer);
        }
        for (int i = 0; i < THE_NUM_ADDS; i++) {
            Player theRemovedPlayer = rosterManager.removeById(playerId);
            final int index = i;
            assertAll("Test removeByID" ,
                    () -> assertEquals(THE_NUM_ADDS - (index + 1), rosterManager.getPlayerData().size()),
                    () -> assertEquals(playerId, theRemovedPlayer.id()),
                    () -> assertEquals(playerName, theRemovedPlayer.name()),
                    () -> assertEquals(position, theRemovedPlayer.position()),
                    () -> assertEquals(jersey, theRemovedPlayer.jersey()),
                    () -> assertEquals(yards, theRemovedPlayer.yards())
            );

        }
    }

    @Test
    public void testUpdateStats() {
        RosterManager rosterManager = new RosterManager();
        final int playerId = 198;
        final String playerName = "Jerry Rice";
        final String position = "WR";
        final int jersey = 88;
        final int yards = 1000;
        Player thePlayer = new Player(playerId, playerName, position, jersey, yards);
        rosterManager.addPlayer(thePlayer);

        for (int i =0; i < TEST_TRIALS; i++) {
            rosterManager.updateStats(thePlayer.id(),i);
            final int index = i;
            assertAll("Test removeByID" ,
                    () -> assertEquals(thePlayer, rosterManager.getPlayerData().get(0),
                    """
                            Updating a player's stats should not change equality.
                            The Player is still the same player.
                            """),
                    () -> assertEquals(index , rosterManager.getPlayerData().get(0).yards()),
                    () -> assertEquals(playerId, rosterManager.getPlayerData().get(0).id()),
                    () -> assertEquals(playerName, rosterManager.getPlayerData().get(0).name()),
                    () -> assertEquals(position, rosterManager.getPlayerData().get(0).position()),
                    () -> assertEquals(jersey, rosterManager.getPlayerData().get(0).jersey())
            );
        }
    }
    @Test
    public void testSearchByName() {
        helperTestFindBy("name");
    }
    @Test
    public void testSearchById() {
        helperTestFindBy("id");
    }
    @Test
    public void testSearchByPosition() {
        helperTestFindBy("position");
    }

    private void helperTestFindBy(String theComparator) {
        RosterManager roster = new RosterManager();

        for (int index = 0; index < TEST_TRIALS; index++) {
            Player player = new Player(index, index + "", index + "", index, index);
            roster.addPlayer(player);
        }

        for (int i = 0; i < TEST_TRIALS; i++) {
            final int index = i;
            int found;
            switch (theComparator) {
                case "id" -> found = roster.findById(index);
                case "name" -> found = roster.findByName(index + "");
                case "position" -> found = roster.findByPosition(index +"");
                case "jersey" -> found = roster.findByJersey(index);
                default -> throw new IllegalArgumentException("comparator options are ");
            }
            int finalFound = found;
            assertAll("Test Search by ID",
                    ()-> assertEquals(index , finalFound,
                            "Roster Manager does not find player by " + theComparator
                                    + " correctly, Should be " + index + ", but returned "
                                    + roster.findByName(index + "")),
                    () -> assertEquals(index + "", roster.getPlayerData().get(finalFound).position()),
                    () -> assertEquals(index + "", roster.getPlayerData().get(finalFound).name()),
                    () -> assertEquals(index, roster.getPlayerData().get(finalFound).jersey()),
                    () -> assertEquals(index, roster.getPlayerData().get(finalFound).yards()),
                    () -> assertEquals(index, roster.getPlayerData().get(finalFound).id())

            );
        }
    }

    @Test
    void testNoHoles() {

        RosterManager rm = new RosterManager();
        Player p1 = new Player(1, "Player1", "QB", 1, 100);
        Player p2 = new Player(2, "Player2", "WR", 2, 200); // The one to remove
        Player p3 = new Player(3, "Player3", "RB", 3, 300);

        rm.addPlayer(p1);
        rm.addPlayer(p2);
        rm.addPlayer(p3);

        rm.removeById(2); // Remove the middle man

        assertAll("Verify Shifting",
                () -> assertEquals(2, rm.getPlayerData().size(),
                        "Size should be 2"),
                () -> assertEquals(1, rm.getPlayerData().get(0).id(),
                        "Index 0 should still be Player 1"),
                () -> assertEquals(3, rm.getPlayerData().get(1).id(),
                        "Player 3 should have shifted left to Index 1"),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> rm.getPlayerData().get(2),
                        "Index 2 should now be empty/invalid")
        );
    }
    @Test
    void testEdgeCases() {
        RosterManager rosterManager = new RosterManager();
        final int playerId = 198;
        final String playerName = "Jerry Rice";
        final String position = "WR";
        final int jersey = 88;
        final int yards = 1000;
        Player player = new Player(playerId, playerName, position, jersey, yards);

        assertAll("Test Edge case",
                () -> assertDoesNotThrow(()->rosterManager.findById(playerId)),
                () -> assertEquals(-1,rosterManager.findById(0),
                        """
                                RosterManager should gracefully handle searching an empty roster by
                                returning -1.
                                """),
                () -> assertEquals(-1, rosterManager.findByName(""),
                        """
                                RosterManager should gracefully handle searching an empty roster by
                                returning -1.
                                """),
                () -> assertEquals(-1, rosterManager.findByPosition(""),
                        """
                                RosterManager should gracefully handle searching an empty roster by
                                returning -1.
                                """),
                () -> {
                    // duplicate player edge case handling
                    rosterManager.addPlayer(player);
                    rosterManager.addPlayer(player);
                    assertEquals(0, rosterManager.findById(playerId),
                        """
                                RosterManger should return the first index of the matching player ID.
                                """);
                },
                () -> assertEquals(0, rosterManager.findByPosition(position),
                        "RosterManger should return the first index of the matching player position."),
                () -> assertEquals(0, rosterManager.findByName(playerName),
                        "RosterManger should return the first index of the matching player name."),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> rosterManager.getPlayerData().get(100),
                        """
                                RosterManager should throw an index out of bound exception when trying to access data
                                indexed at a position that exceeds the current roster size
                                """),
                () -> assertThrows(RuntimeException.class, () -> rosterManager.removeById(100),
                        """
                                RosterManager should throw a runtime exception when trying to remove a player that
                                does not exist.
                                """)
        );

    }


}

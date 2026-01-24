import org.junit.jupiter.api.Test;

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
                () -> assertEquals(0,rosterManager.size(),
                        "The roster should have no players in it"),
                () -> assertNotNull(rosterManager.getData(), "The roster should be empty, but not null")

        );
    }

    @Test
    public void testAddPlayer() {
        RosterManager rosterManager = new RosterManager();
        final int playerId = 198;
        final String playerName = "Jerry Rice";
        final String position = "WR";
        final int jersey = 88;
        final int yards = 1000;

        Player thePlayer = new Player(playerId, playerName, position, jersey, yards);
        for(int i = 0; i < THE_NUM_ADDS; i++){
            rosterManager.addPlayer(thePlayer);
        }
        assertAll("Test addPlayer",
                () -> assertEquals(THE_NUM_ADDS, rosterManager.size(),
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
        final int playerId = 198;
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
                    () -> assertEquals(THE_NUM_ADDS - (index + 1), rosterManager.size()),
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
                    () -> assertEquals(index , rosterManager.getPlayerData().get(0).yards()),
                    () -> assertEquals(playerId, rosterManager.getPlayerData().get(0).id()),
                    () -> assertEquals(playerName, rosterManager.getPlayerData().get(0).name()),
                    () -> assertEquals(position, rosterManager.getPlayerData().get(0).position()),
                    () -> assertEquals(jersey, rosterManager.getPlayerData().get(0).jersey())
            );
        }


    }



}

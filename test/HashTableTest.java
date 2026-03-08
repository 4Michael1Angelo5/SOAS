import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import types.Player;
import util.HashTable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for HashTable
 * @author Chris Chun, Ayush
 * @version 1.0
 */
public class HashTableTest {

    private HashTable<Integer, Player> playerTable;

    @BeforeEach
    void setup() {
        playerTable = new HashTable<>(Integer.class, Player.class);
    }


    private Player makePlayer(int id, String name, String position, int jersey, int yards) {
        return new Player(id, name, position, jersey, yards);
    }

    // ================= INSERT WITHOUT COLLISION =================

    /**
     * Tests inserting a single player with no collision
     */
    @Test
    void insertSinglePlayer() {
        Player p1 = makePlayer(101, "Jackson", "WR", 11, 850);
        playerTable.put(101, p1);

        assertAll("single insert without collision",
                () -> assertEquals(p1, playerTable.get(101), "Should retrieve the player"),
                () -> assertTrue(playerTable.containsKey(101), "Should contain the key"),
                () -> assertEquals(1, playerTable.size(), "Size should be 1")
        );
    }

    /**
     * Tests inserting multiple players with keys that don't collide
     */
    @Test
    void insertMultiplePlayers() {
        // Use keys spread far apart to avoid collision
        Player p1 = makePlayer(5, "Smith", "QB", 3, 3200);
        Player p2 = makePlayer(20, "Johnson", "RB", 24, 1150);
        Player p3 = makePlayer(100, "Williams", "TE", 87, 680);

        playerTable.put(5, p1);
        playerTable.put(20, p2);
        playerTable.put(100, p3);

        assertAll("multiple inserts without collision",
                () -> assertEquals(p1, playerTable.get(5), "Should find Smith"),
                () -> assertEquals(p2, playerTable.get(20), "Should find Johnson"),
                () -> assertEquals(p3, playerTable.get(100), "Should find Williams"),
                () -> assertEquals(3, playerTable.size(), "Size should be 3")
        );
    }

    // ================= INSERT WITH COLLISION =================

    /**
     * Tests inserting players whose keys will definitely collide (same hash bucket)
     */
    @Test
    void insertWithCollision() {
        Player p1 = makePlayer(1, "Jimmy", "LB", 50, 0);
        Player p2 = makePlayer(17, "Jone", "CB", 29, 0);
        Player p3 = makePlayer(33, "Davis", "S", 33, 0);
        Player p4 = makePlayer(49, "Adams", "OL", 72, 0);

        playerTable.put(1, p1);
        playerTable.put(17, p2);
        playerTable.put(33, p3);
        playerTable.put(49, p4);

        assertAll("deliberate collision chain",
                () -> assertEquals(p1, playerTable.get(1), "Should find Jimmy"),
                () -> assertEquals(p2, playerTable.get(17), "Should find Jone"),
                () -> assertEquals(p3, playerTable.get(33), "Should find Davis"),
                () -> assertEquals(p4, playerTable.get(49), "Should find Adams"),
                () -> assertEquals(4, playerTable.size(), "All 4 should be stored")
        );
    }

    /**
     * Tests updating a player in a collision chain
     */
    @Test
    void updateInCollisionChain() {
        Player original = makePlayer(17, "Brown", "WR", 14, 500);
        Player updated = makePlayer(17, "Brown", "WR", 14, 850);

        playerTable.put(1, makePlayer(1, "A", "QB", 3, 100));
        playerTable.put(17, original);
        playerTable.put(33, makePlayer(33, "C", "RB", 24, 200));

        playerTable.put(17, updated); // Update in collision chain

        assertAll("update in collision chain",
                () -> assertEquals(updated, playerTable.get(17), "Should get updated player"),
                () -> {
                    Player found = playerTable.get(17);
                    assertNotNull(found, "Player 17 should exist");
                    assertEquals(850, found.yards(), "Yards should be updated");
                },
                () -> assertEquals(3, playerTable.size(), "Size should stay 3")
        );
    }

    // ================= LOOKUP EXISTING PLAYER =================

    /**
     * Tests retrieving a player that exists in the table
     */
    @Test
    void lookupExisting() {
        Player player = makePlayer(201, "Ayush", "DL", 90, 0);
        playerTable.put(201, player);

        Player found = playerTable.get(201);
        assertNotNull(found, "Should find the player");

        assertAll("lookup existing player",
                () -> assertEquals("Ayush", found.name(), "Name should match"),
                () -> assertEquals("DL", found.position(), "Position should match"),
                () -> assertEquals(90, found.jersey(), "Jersey should match")
        );
    }

    /**
     * Tests using containsKey to check for existing player
     */
    @Test
    void containsExisting() {
        Player player = makePlayer(303, "Thomas", "WR", 16, 1200);
        playerTable.put(303, player);

        assertTrue(playerTable.containsKey(303), "Should contain player with ID 303");
    }

    // ================= LOOKUP MISSING PLAYER =================

    /**
     * Tests retrieving a player that doesn't exist
     */
    @Test
    void lookupMissing() {
        playerTable.put(101, makePlayer(101, "A", "QB", 3, 100));

        assertNull(playerTable.get(999), "Should return null for missing player");
    }

    /**
     * Tests containsKey for missing player
     */
    @Test
    void containsMissing() {
        playerTable.put(50, makePlayer(50, "Player", "RB", 24, 500));

        assertFalse(playerTable.containsKey(777), "Should not contain player with ID 777");
    }

    // ================= REMOVAL CORRECTNESS =================

    /**
     * Tests removing an existing player
     */
    @Test
    void removeExisting() {
        Player player = makePlayer(404, "Jones", "TE", 87, 650);
        playerTable.put(404, player);

        Player removed = playerTable.delete(404);

        assertAll("remove existing player",
                () -> assertEquals(player, removed, "Should return removed player"),
                () -> assertFalse(playerTable.containsKey(404), "Key should not exist"),
                () -> assertNull(playerTable.get(404), "Get should return null"),
                () -> assertEquals(0, playerTable.size(), "Size should be 0")
        );
    }

    /**
     * Tests removing a player that doesn't exist
     */
    @Test
    void removeMissing() {
        playerTable.put(100, makePlayer(100, "Adian", "QB", 3, 200));

        Player removed = playerTable.delete(888);

        assertAll("remove missing player",
                () -> assertNull(removed, "Should return null"),
                () -> assertEquals(1, playerTable.size(), "Size should remain 1")
        );
    }

    /**
     * Tests removing a player from a collision chain
     */
    @Test
    void removeFromCollision() {
        Player p1 = makePlayer(1, "A", "QB", 3, 100);
        Player p2 = makePlayer(17, "B", "WR", 14, 200);
        Player p3 = makePlayer(33, "C", "RB", 24, 300);

        playerTable.put(1, p1);
        playerTable.put(17, p2);
        playerTable.put(33, p3);

        playerTable.delete(17); // Remove middle

        assertAll("remove from collision chain",
                () -> assertEquals(p1, playerTable.get(1), "Should still find p1"),
                () -> assertNull(playerTable.get(17), "Should not find removed p2"),
                () -> assertEquals(p3, playerTable.get(33), "Should still find p3"),
                () -> assertEquals(2, playerTable.size(), "Size should be 2")
        );
    }

    // ================= LOAD FACTOR CALCULATION =================

    /**
     * Tests that load factor increases with inserts
     */
    @Test
    void loadFactorIncreases() {
        double load0 = playerTable.loadFactor();

        playerTable.put(1, makePlayer(1, "A", "QB", 3, 100));
        double load1 = playerTable.loadFactor();

        playerTable.put(2, makePlayer(2, "B", "WR", 14, 200));
        playerTable.put(3, makePlayer(3, "C", "RB", 24, 300));
        double load3 = playerTable.loadFactor();

        assertAll("load factor progression",
                () -> assertEquals(0.0, load0, 0.001, "Empty table has 0 load"),
                () -> assertTrue(load1 > load0, "Load increases after first insert"),
                () -> assertTrue(load3 > load1, "Load keeps increasing with more inserts")
        );
    }

    /**
     * Tests load factor after deletions
     */
    @Test
    void loadFactorDecreases() {
        playerTable.put(1, makePlayer(1, "A", "QB", 3, 100));
        playerTable.put(2, makePlayer(2, "B", "WR", 14, 200));

        double loadBefore = playerTable.loadFactor();

        playerTable.delete(1);

        double loadAfter = playerTable.loadFactor();

        assertTrue(loadAfter < loadBefore, "Load factor should decrease after deletion");
    }

    // ================= COLLISION HANDLING INTEGRITY =================

    /**
     * Tests complex operations on collision chain maintain integrity
     */
    @Test
    void collisionIntegrity() {
        // Insert keys that collide (assuming capacity 16)
        Player p1 = makePlayer(1, "A", "QB", 3, 100);
        Player p2 = makePlayer(17, "B", "WR", 14, 200);
        Player p3 = makePlayer(33, "C", "RB", 24, 300);

        playerTable.put(1, p1);
        playerTable.put(17, p2);
        playerTable.put(33, p3);

        // Update one
        Player p2Updated = makePlayer(17, "B_Updated", "WR", 14, 500);
        playerTable.put(17, p2Updated);

        // Delete one
        playerTable.delete(33);

        // Add another colliding key
        Player p4 = makePlayer(49, "D", "TE", 87, 400);
        playerTable.put(49, p4);

        assertAll("collision chain integrity",
                () -> assertEquals(p1, playerTable.get(1), "Should find original p1"),
                () -> assertEquals(p2Updated, playerTable.get(17), "Should find updated p2"),
                () -> assertNull(playerTable.get(33), "Should not find deleted p3"),
                () -> assertEquals(p4, playerTable.get(49), "Should find new p4"),
                () -> assertEquals(3, playerTable.size(), "Size should be 3")
        );
    }

    // ================= BEHAVIOR AT HIGH LOAD FACTOR =================

    /**
     * Tests table behavior when adding many players
     */
    @Test
    void highLoadBehavior() {
        // Insert many players
        for (int i = 100; i < 150; i++) {
            playerTable.put(i, makePlayer(i, "Player" + i, "WR", i % 100, i * 10));
        }

        assertAll("high load factor behavior",
                () -> assertTrue(playerTable.size() >= 50, "Should have 50+ players"),
                () -> {
                    Player found = playerTable.get(100);
                    assertNotNull(found, "Player 100 should exist");
                    assertEquals("Player100", found.name(), "Should find Player100");
                },
                () -> {
                    Player found = playerTable.get(125);
                    assertNotNull(found, "Player 125 should exist");
                    assertEquals("Player125", found.name(), "Should find Player125");
                },
                () -> {
                    Player found = playerTable.get(149);
                    assertNotNull(found, "Player 149 should exist");
                    assertEquals("Player149", found.name(), "Should find Player149");
                },
                () -> assertTrue(playerTable.loadFactor() > 0.0, "Load factor should be positive")
        );
    }

    /**
     * Tests that table still works correctly after resize
     */
    @Test
    void resizeKeepsData() {
        // Insert enough to trigger resize
        for (int i = 1; i <= 60; i++) {
            playerTable.put(i, makePlayer(i, "P" + i, "QB", i % 100, i * 50));
        }

        assertAll("resize keeps all entries",
                () -> {
                    Player found = playerTable.get(1);
                    assertNotNull(found, "Player 1 should exist");
                    assertEquals("P1", found.name(), "Should find first player");
                },
                () -> {
                    Player found = playerTable.get(30);
                    assertNotNull(found, "Player 30 should exist");
                    assertEquals("P30", found.name(), "Should find middle player");
                },
                () -> {
                    Player found = playerTable.get(60);
                    assertNotNull(found, "Player 60 should exist");
                    assertEquals("P60", found.name(), "Should find last player");
                },
                () -> assertEquals(60, playerTable.size(), "All 60 players should be stored")
        );
    }

    // ================= EDGE CASES =================


    @Test
    void deleteAndReinsert() {
        Player p1 = makePlayer(10, "A", "QB", 3, 100);
        Player p2 = makePlayer(20, "B", "WR", 14, 200);

        playerTable.put(10, p1);
        playerTable.put(20, p2);

        playerTable.delete(10);
        playerTable.delete(20);

        Player p3 = makePlayer(10, "A_New", "QB", 3, 300);
        playerTable.put(10, p3);

        assertAll("delete all and reinsert",
                () -> assertEquals(1, playerTable.size(), "Size should be 1"),
                () -> assertEquals(p3, playerTable.get(10), "Should find new player"),
                () -> {
                    Player found = playerTable.get(10);
                    assertNotNull(found, "Player 10 should exist");
                    assertEquals("A_New", found.name(), "Name should be updated");
                }
        );
    }

    @Test
    void manyOperations() {
        // Insert 30 players
        for (int i = 1; i <= 30; i++) {
            playerTable.put(i, makePlayer(i, "Player" + i, "WR", i % 100, i * 100));
        }

        // Delete every other one
        for (int i = 2; i <= 30; i += 2) {
            playerTable.delete(i);
        }

        // Update remaining ones
        for (int i = 1; i <= 30; i += 2) {
            playerTable.put(i, makePlayer(i, "Updated" + i, "RB", (i + 10) % 100, i * 200));
        }

        assertAll("many operations integrity",
                () -> assertEquals(15, playerTable.size(), "Should have 15 players"),
                () -> {
                    Player found = playerTable.get(1);
                    assertNotNull(found, "Player 1 should exist");
                    assertEquals("Updated1", found.name(), "Player 1 should be updated");
                },
                () -> assertNull(playerTable.get(2), "Player 2 should be deleted"),
                () -> {
                    Player found = playerTable.get(29);
                    assertNotNull(found, "Player 29 should exist");
                    assertEquals("Updated29", found.name(), "Player 29 should be updated");
                },
                () -> assertNull(playerTable.get(30), "Player 30 should be deleted")
        );
    }

    @Test
    void duplicateKeyDoesNotGrowSize() {
        playerTable.put(7, makePlayer(7, "A", "QB", 3, 100));
        playerTable.put(7, makePlayer(7, "A2", "QB", 3, 200));

        assertAll("duplicate key update",
                () -> assertEquals(1, playerTable.size(), "Size should stay 1"),
                () -> {
                    Player found = playerTable.get(7);
                    assertNotNull(found, "Player 7 should exist");
                    assertEquals("A2", found.name(), "Value should be replaced");
                }
        );
    }

    @Test
    void removeHeadOfCollisionChain() {
        Player p1 = makePlayer(1, "A", "QB", 3, 100);
        Player p2 = makePlayer(17, "B", "WR", 14, 200);
        Player p3 = makePlayer(33, "C", "RB", 24, 300);

        playerTable.put(1, p1);
        playerTable.put(17, p2);
        playerTable.put(33, p3);

        playerTable.delete(1);

        assertAll("remove head of collision chain",
                () -> assertNull(playerTable.get(1), "Head should be removed"),
                () -> assertEquals(p2, playerTable.get(17), "Second item should still exist"),
                () -> assertEquals(p3, playerTable.get(33), "Third item should still exist"),
                () -> assertEquals(2, playerTable.size(), "Size should be 2")
        );
    }

    @Test
    void removeTailOfCollisionChain() {
        Player p1 = makePlayer(1, "A", "QB", 3, 100);
        Player p2 = makePlayer(17, "B", "WR", 14, 200);
        Player p3 = makePlayer(33, "C", "RB", 24, 300);

        playerTable.put(1, p1);
        playerTable.put(17, p2);
        playerTable.put(33, p3);

        playerTable.delete(33);

        assertAll("remove tail of collision chain",
                () -> assertEquals(p1, playerTable.get(1), "First item should still exist"),
                () -> assertEquals(p2, playerTable.get(17), "Second item should still exist"),
                () -> assertNull(playerTable.get(33), "Tail should be removed"),
                () -> assertEquals(2, playerTable.size(), "Size should be 2")
        );
    }

    @Test
    void emptyTable() {
        assertAll("empty table",
                () -> assertTrue(playerTable.isEmpty(), "Should be empty"),
                () -> assertEquals(0, playerTable.size(), "Size should be 0"),
                () -> assertNull(playerTable.get(1), "Get should return null"),
                () -> assertFalse(playerTable.containsKey(1), "Should not contain any keys"),
                () -> assertEquals(0.0, playerTable.loadFactor(), 0.001, "Load factor should be 0")
        );
    }

    @Test
    void testReset() {
        Player p1 = makePlayer(1, "A", "QB", 3, 100);
        Player p2 = makePlayer(17, "B", "WR", 14, 200);
        Player p3 = makePlayer(33, "C", "RB", 24, 300);

        playerTable.put(1, p1);
        playerTable.put(17, p2);
        playerTable.put(33, p3);

        playerTable.clear();
        assertAll("Test reset behavior",
                ()-> assertTrue(playerTable.isEmpty()),
                ()-> assertEquals(0, playerTable.size())
        );

    }
}
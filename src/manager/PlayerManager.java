package manager;

import com.sun.jdi.Value;
import types.Drill;
import types.Player;
import types.PlayerEnhanced;
import types.Position;
import util.*;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Manages Seahawks Players -update stats yards injury status etc.
 * @author Chris Chun, Ayush.
 * @version 1.1
 */
public final class PlayerManager extends MapManager<PlayerEnhanced>{


    public PlayerManager() {
        super(PlayerEnhanced.class);
    }

    /**
     * Add a player to the map.
     * @param thePlayer the player to add.
     */
    public void addPlayer(PlayerEnhanced thePlayer) {
        addData(thePlayer);
    }

    /**
     * Search by Player ID.
     * Deprecated but required for system requirements.
     * @param theId the id of the player to search for.
     * @return theId if found and -1 otherwise.
     */
    public int searchByPlayerId(int theId) {
        return searchById(theId);
    }

    /**
     * Removes a player from the hashtable.
     * @param thePlayer the player to remove.
     * @return the player removed or null if not found.
     */
    public PlayerEnhanced removePlayer(PlayerEnhanced thePlayer) {
        return removeData(thePlayer);
    }

    /**
     * Attempts to update the hash table with
     * new player object. If the player record
     * exists in the hash table its record is updated
     * with the new one. If it is not found then a
     * NoSuchElementException is thrown.
     * @param thePlayer the new player object to update the stats with.
     */
    public void updatePlayerStats(PlayerEnhanced thePlayer) {
        if (containsRecord(thePlayer.player_id())) {
            addPlayer(thePlayer);
        } else {
            throw new NoSuchElementException(
                    "Cannot update Player because they do not exist it the HashTable");
        }
    }

    /**
     * list players position
     * @return an Array of the players sorted lexigraphically by position.
     */
    public ArrayStore<PlayerEnhanced> listPlayersByPosition() {

        final BinaryHeapPQ<PlayerEnhanced> playersByPosition =
                new BinaryHeapPQ<>(PlayerEnhanced.class,
                        Comparator.comparing(PlayerEnhanced::position));

        final ArrayStore<types.PlayerEnhanced> result =
                new ArrayStore<>(PlayerEnhanced.class,64);

        for (Entry<Integer, PlayerEnhanced> entry : getData()) {

            playersByPosition.insert(entry.value());

        }

        while (!playersByPosition.isEmpty()) {
            result.add(playersByPosition.extract());
        }

        return result;
    }

    public int countInjuredPlayers() {
        int count = 0;

        for (Entry<Integer, PlayerEnhanced> entry: getData()) {
            if (entry.value().injured()) {
                count++;
            }
        }

        return count;
    }

    public HashTable<Position, Integer> computeTotalYardsByPosition() {

        HashTable<Position, Integer> table = new HashTable<>(Position.class, Integer.class);

        for (Entry<Integer, PlayerEnhanced> entry : getData()) {

            Position position = entry.value().position();
            int yards = entry.value().yards();
            Integer currentYardsForPos = table.get(position);

            table.put(position,
                    Objects.isNull(currentYardsForPos)
                    ? yards
                    : currentYardsForPos + yards);

        }

        return table;
    }

    public int getTotalYardsByPosition(Position position) {

        HashTable<Position, Integer> table = computeTotalYardsByPosition();

        Integer result = table.get(position);

        if (Objects.isNull(result)) {
            throw new RuntimeException("Unable to compute total yards for position: " + position);
        }
        return result;
    }
}

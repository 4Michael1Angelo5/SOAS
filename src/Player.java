/**
 * A record class for storing Seahawk player data
 * @param player_id
 * @param name
 * @param position
 * @param jersey
 * @param yards
 */
public record Player (int player_id,
                     String name,
                     String position,
                     String jersey,
                     int yards) implements DataType{}
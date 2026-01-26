package types;

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
                     int jersey,
                     int yards) implements DataType {

    @Override
    public int id() {
        return player_id;
    }

    /**
     * Custom toString method
     * @return formatted string representation of Player
     */
    @Override
    public String toString() {
        return "Player[player_id =" + player_id +
                ", name =" + name +
                ", position =" + position +
                ", jersey =" + jersey +
                ", yards =" + yards + "]";
    }

    /**
     * Custom equals method
     * @param obj the object to compare with
     * @return true, if players are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return player_id == player.player_id &&
                name.equals(player.name);
    }
}
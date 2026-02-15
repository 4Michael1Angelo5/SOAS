package types;

import java.util.Objects;

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

    public Player(int player_id, String name, String position, int jersey, int yards) {
        this.player_id = player_id;
        this.name = name;
        this.position = position;
        this.jersey = jersey;
        this.yards = yards;
        validate();
    }

    @Override
    public int id() {
        return player_id;
    }

    @Override
    public void validate() {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(position, "position cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be blank");
        }

        if (position.isBlank()) {
            throw new IllegalArgumentException("Position cannot be blank");
        }

        if (player_id < 0) {
            throw new IllegalArgumentException("Player ID cannot be negative");
        }

        if (jersey < 0 || jersey > 99) {
            throw new IllegalArgumentException("Jersey number must be between 0 and 99");
        }

        if (yards < 0) {
            throw new IllegalArgumentException("Yards cannot be negative");
        }
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
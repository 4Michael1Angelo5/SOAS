package types;

import java.util.Objects;

/**
 * A record class for storing Seahawk player data
 * @param player_id
 * @param name
 * @param position
 * @param yards
 */
public record PlayerEnhanced(
                     int player_id,
                     String name,
                     Position position,
                     int yards,
                     int touchdowns,
                     boolean injured) implements DataType{

    public PlayerEnhanced(
                  int player_id,
                  String name,
                  Position position,
                  int yards,
                  int touchdowns,
                  boolean injured) {
        this.player_id = player_id;
        this.name = name;
        this.position = position;
        this.yards = yards;
        this.touchdowns = touchdowns;
        this.injured = injured;

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
//
//        if (position.isBlank()) {
//            throw new IllegalArgumentException("Position cannot be blank");
//        }
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
        PlayerEnhanced player = (PlayerEnhanced) obj;
        return player_id == player.player_id &&
                name.equals(player.name);
    }
}
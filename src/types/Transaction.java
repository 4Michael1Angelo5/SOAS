package types;

import java.util.Objects;

/**
 * A simple Record class for storing transaction Data
 * @param trans_id
 * @param type
 * @param player
 * @param timestamp
 */
public record Transaction(
        int trans_id,
        String type,
        String player,
        String timestamp) implements DataType {

    public Transaction(int trans_id, String type, String player, String timestamp) {
        this.trans_id = trans_id;
        this.type = type;
        this.player = player;
        this.timestamp = timestamp;
        validate();
    }

    @Override
    public int id() {
        return trans_id;
    }

    @Override
    public void validate() {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");

        if (type.isBlank()) {
            throw new IllegalArgumentException("Transaction type cannot be blank");
        }

        if (player.isBlank()) {
            throw new IllegalArgumentException("Player cannot be blank");
        }

        if (timestamp.isBlank()) {
            throw new IllegalArgumentException("Timestamp cannot be blank");
        }

        if (trans_id < 0) {
            throw new IllegalArgumentException("Transaction ID cannot be negative");
        }
    }

    /**
     * Custom toString
     * @return formatted string
     */
    @Override
    public String toString() {
        return "Transaction[id =" + trans_id +
                ", type =" + type +
                ", player =" + player +
                ", timestamp =" + timestamp + "]";
    }

    /**
     * Custom equals
     * @param obj object to compare
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction t = (Transaction) obj;
        return trans_id == t.trans_id &&
                type.equals(t.type) &&
                player.equals(t.player) &&
                timestamp.equals(t.timestamp);
    }
}
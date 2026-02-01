package types;

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

    @Override
    public int id() {
        return trans_id;
    }

    /**
     * Custom toString
     * @return formatted string
     */
    @Override
    public String toString() {
        return "Transaction[id=" + trans_id +
                ", type=" + type +
                ", player=" + player +
                ", timestamp=" + timestamp + "]";
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

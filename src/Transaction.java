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
        String timestamp) implements DataType{

    @Override
    public int id() {
        return trans_id;
    }
}

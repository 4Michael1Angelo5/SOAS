package types;

/**
 * A simple Record class for storing Drills Data
 * @param drill_id the drill id
 * @param name the drill name
 * @param urgency the urgency
 */
public record Drill(int drill_id,
                    String name,
                    int urgency) implements DataType {

    @Override
    public int id() {
        return drill_id;
    }
}

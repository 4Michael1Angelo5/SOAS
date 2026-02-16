package types;

import java.util.Objects;

/**
 * A simple Record class for storing Drills Data
 * @param drill_id the drill id
 * @param name the drill name
 * @param urgency the urgency
 */
public record Drill(int drill_id,
                    String name,
                    int urgency) implements DataType {

    public Drill(int drill_id, String name, int urgency) {
        this.drill_id = drill_id;
        this.name = name;
        this.urgency = urgency;
        validate();
    }

    @Override
    public int id() {
        return drill_id;
    }

    @Override
    public void validate() {
        Objects.requireNonNull(name, "name cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Drill name cannot be blank");
        }

        if (drill_id < 0) {
            throw new IllegalArgumentException("Drill ID cannot be negative");
        }

        if (urgency < 0) {
            throw new IllegalArgumentException("Urgency cannot be negative");
        }
    }
}
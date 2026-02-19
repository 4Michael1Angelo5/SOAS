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
                    int urgency,
                    int duration_min,
                    int fatigue_cost,
                    int install_by_day
) implements DataType {

    public Drill(int drill_id,
                 String name,
                 int urgency,
                 int duration_min,
                 int fatigue_cost,
                 int install_by_day){
        this.drill_id = drill_id;
        this.name = name;
        this.urgency = urgency;
        this.duration_min = duration_min;
        this.fatigue_cost = fatigue_cost;
        this.install_by_day = install_by_day;
        validate();
    }

    /**
     * Baseline comparison method used for determining
     * natural order - sorts by highest urgency.
     * @param theOther the object to be compared.
     */
    @Override
    public int compareTo(DataType theOther) {
        if (theOther instanceof Drill) {
            // Integer.compareTo(a,b)
            // performs (a-b). If negative then a should come first.
            // if positive then b should come first.
            return Integer.compare(((Drill) theOther).urgency, this.urgency);
        }
        // otherwise use default comparison between id's
        return DataType.super.compareTo(theOther);
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
    }
}
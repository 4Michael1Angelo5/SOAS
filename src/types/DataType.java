package types;

import java.util.Comparator;
import java.util.Objects;

/**
 * A sealed interface that defines all allowed DataTypes for
 * the csv data loader, and Data Manager
 */
public sealed interface DataType extends Comparable<DataType>
        permits Player, Drill, Transaction, FanRequest, Action, Undoable {
    /**
     *
     * @return the id of Data
     */
    public int id();

    public void validate();

    @Override
    default int compareTo(DataType theOther) {
        return Integer.compare(this.id(), theOther.id());
    };
}

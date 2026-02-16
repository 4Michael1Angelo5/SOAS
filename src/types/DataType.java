package types;

import java.util.Objects;

/**
 * A sealed interface that defines all allowed DataTypes for
 * the csv data loader, and Data Manager
 */
public sealed interface DataType
        permits Player, Drill, Transaction, FanRequest, Action, Undoable {
    /**
     *
     * @return the id of Data
     */
    public int id();

    public void validate();

}

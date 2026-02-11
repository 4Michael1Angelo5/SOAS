package types;

/**
 * A sealed interface that defines all allowed DataTypes for
 * the csv data loader, and Data Manager
 */
public sealed interface DataType
        permits Player, Drill, Transaction, FanRequest, Action {
    /**
     *
     * @return the id of Data
     */
    public int id();
}

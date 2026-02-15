package types;

import java.util.Objects;

public record UndoRecord(
        Action action,
        DataType previousState,
        Integer index
) implements Undoable {

    public UndoRecord(Action action, DataType previousState, Integer index) {
        this.action = action;
        this.previousState = previousState;
        this.index = index;
        validate();
    }

    @Override
    public int id() {
        return action().action_id();
    }

    @Override
    public void validate() {
        Objects.requireNonNull(action, "action cannot be null");
        Objects.requireNonNull(previousState, "previous state cannot be null");
        Objects.requireNonNull(index, "index cannot be null");

        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be less than 0");
        }
    }
}
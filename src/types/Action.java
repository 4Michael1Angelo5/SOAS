package types;

import java.util.Objects;

// represents an executable action
public record Action(
        int action_id,
        ActionType action_type,
        String target,
        String timestamp) implements DataType, Undoable {

    public Action(int action_id, ActionType action_type, String target, String timestamp) {
        this.action_id = action_id;
        this.action_type = action_type;
        this.target = target;
        this.timestamp = timestamp;
        validate();
    }

    @Override
    public int id() {
        return action_id;
    }

    @Override
    public void validate() {
        Objects.requireNonNull(action_type, "ActionType cannot be null");
        Objects.requireNonNull(target, "Target cannot be null");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");

        if (target.isBlank()) {
            throw new IllegalArgumentException("Target cannot be blank");
        }
    }

}
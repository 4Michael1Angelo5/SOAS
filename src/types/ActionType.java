package types;

import java.util.Objects;

public enum ActionType {
    ADD_PLAYER,
    REMOVE_PLAYER,
    ADD_TRANSACTION,
    REMOVE_TRANSACTION,
    UPDATE_STATS;

    public static ActionType validate(ActionType type) {
        return Objects.requireNonNull(type, "ActionType cannot be null");
    }
}

package types;

// represents an executable action
public record Action(
        int action_id,
        ActionType action_type,
        String target,
        String timestamp) implements DataType, Undoable {

    public Action {
        ActionType.validate(action_type);
    }

    @Override
    public int id() {
        return action_id;
    }

}

package types;

public record UndoAction(
        int action_id,
        ActionType action_type,
        String target,
        String timestamp) implements DataType {


    @Override
    public int id() {
        return action_id;
    }
}

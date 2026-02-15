package types;

public record UndoRecord(
        Action action, // enum
        DataType previousState,
        Integer index
) implements Undoable {
    @Override
    public int id() {
        return action().action_id();
    }
}
